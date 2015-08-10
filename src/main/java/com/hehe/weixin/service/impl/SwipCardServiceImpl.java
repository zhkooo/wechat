package com.hehe.weixin.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hehe.common.ModelResult;
import com.hehe.utils.SerialNoBuilder;
import com.hehe.utils.XmlUtils;
import com.hehe.weixin.common.WeiXinHttpClientRequest;
import com.hehe.weixin.protocol.SwipCardReqData;
import com.hehe.weixin.protocol.SwipCardResData;
import com.hehe.weixin.service.SwipCardService;
import com.hehe.weixin.vo.SwipCardPayVo;
import com.tencent.common.Configure;
import com.tencent.common.Signature;

public class SwipCardServiceImpl implements SwipCardService {
	private static Logger log = LoggerFactory.getLogger(SwipCardServiceImpl.class);
	@Override
	public ModelResult paySync(SwipCardPayVo swipCardPayVo) {
		SwipCardReqData swipCardReqData = new SwipCardReqData(swipCardPayVo);
		swipCardReqData.buildOutTradeNo(SerialNoBuilder.getOrderNum())
						.buildSign();
        String postDataXML = XmlUtils.getXmlFromObject(swipCardReqData);
		String responseStr = new WeiXinHttpClientRequest().postXml(Configure.PAY_API, postDataXML).execute().asString();
		SwipCardResData scanPayResData = (SwipCardResData) XmlUtils.getObjectFromXML(responseStr, SwipCardResData.class);
		if (scanPayResData == null || scanPayResData.getReturn_code() == null) {
            log.error("【支付失败】支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问");
            return new ModelResult();
        }
        if (scanPayResData.getReturn_code().equals("FAIL")) {
            //注意：一般这里返回FAIL是出现系统级参数错误，请检测Post给API的数据是否规范合法
            log.error("【支付失败】支付API系统返回失败，请检测Post给API的数据是否规范合法");
            return;
        } else {
            log.info("支付API系统成功返回数据");
            //--------------------------------------------------------------------
            //收到API的返回数据的时候得先验证一下数据有没有被第三方篡改，确保安全
            //--------------------------------------------------------------------
            if (!Signature.checkIsSignValidFromResponseString(responseStr)) {
                log.error("【支付失败】支付请求API返回的数据签名验证失败，有可能数据被篡改了");
                return;
            }

            //获取错误码
            String errorCode = scanPayResData.getErr_code();
            //获取错误描述
            String errorCodeDes = scanPayResData.getErr_code_des();

            if (scanPayResData.getResult_code().equals("SUCCESS")) {

                //--------------------------------------------------------------------
                //1)直接扣款成功
                //--------------------------------------------------------------------

                log.i("【一次性支付成功】");

                String transID = scanPayResData.getTransaction_id();
                if(transID != null){
                    transactionID = transID;
                }

                resultListener.onSuccess(scanPayResData,transactionID);
            }else{

                //出现业务错误
                log.i("业务返回失败");
                log.i("err_code:" + errorCode);
                log.i("err_code_des:" + errorCodeDes);

                //业务错误时错误码有好几种，商户重点提示以下几种
                if (errorCode.equals("AUTHCODEEXPIRE") || errorCode.equals("AUTH_CODE_INVALID") || errorCode.equals("NOTENOUGH")) {

                    //--------------------------------------------------------------------
                    //2)扣款明确失败
                    //--------------------------------------------------------------------

                    //对于扣款明确失败的情况直接走撤销逻辑
                    doReverseLoop(outTradeNo,resultListener);

                    //以下几种情况建议明确提示用户，指导接下来的工作
                    if (errorCode.equals("AUTHCODEEXPIRE")) {
                        //表示用户用来支付的二维码已经过期，提示收银员重新扫一下用户微信“刷卡”里面的二维码
                        log.w("【支付扣款明确失败】原因是：" + errorCodeDes);
                        resultListener.onFailByAuthCodeExpire(scanPayResData);
                    } else if (errorCode.equals("AUTH_CODE_INVALID")) {
                        //授权码无效，提示用户刷新一维码/二维码，之后重新扫码支付
                        log.w("【支付扣款明确失败】原因是：" + errorCodeDes);
                        resultListener.onFailByAuthCodeInvalid(scanPayResData);
                    } else if (errorCode.equals("NOTENOUGH")) {
                        //提示用户余额不足，换其他卡支付或是用现金支付
                        log.w("【支付扣款明确失败】原因是：" + errorCodeDes);
                        resultListener.onFailByMoneyNotEnough(scanPayResData);
                    }
                } else if (errorCode.equals("USERPAYING")) {

                    //--------------------------------------------------------------------
                    //3)需要输入密码
                    //--------------------------------------------------------------------

                    //表示有可能单次消费超过300元，或是免输密码消费次数已经超过当天的最大限制，这个时候提示用户输入密码，商户自己隔一段时间去查单，查询一定次数，看用户是否已经输入了密码
                    if (doPayQueryLoop(payQueryLoopInvokedCount, outTradeNo,resultListener)) {
                        log.i("【需要用户输入密码、查询到支付成功】");
                        resultListener.onSuccess(scanPayResData,transactionID);
                    } else {
                        log.i("【需要用户输入密码、在一定时间内没有查询到支付成功、走撤销流程】");
                        doReverseLoop(outTradeNo,resultListener);
                        resultListener.onFail(scanPayResData);
                    }
                } else {

                    //--------------------------------------------------------------------
                    //4)扣款未知失败
                    //--------------------------------------------------------------------

                    if (doPayQueryLoop(payQueryLoopInvokedCount, outTradeNo,resultListener)) {
                        log.i("【支付扣款未知失败、查询到支付成功】");
                        resultListener.onSuccess(scanPayResData,transactionID);
                    } else {
                        log.i("【支付扣款未知失败、在一定时间内没有查询到支付成功、走撤销流程】");
                        doReverseLoop(outTradeNo,resultListener);
                        resultListener.onFail(scanPayResData);
                    }
                }
            }
	}

}
