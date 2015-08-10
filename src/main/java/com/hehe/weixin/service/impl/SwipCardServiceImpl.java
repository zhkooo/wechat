package com.hehe.weixin.service.impl;

import static java.lang.Thread.sleep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hehe.common.ErrorCode;
import com.hehe.common.ErrorCode.BuzzError;
import com.hehe.common.ModelResult;
import com.hehe.utils.SerialNoBuilder;
import com.hehe.utils.XmlUtils;
import com.hehe.weixin.common.WeiXinHttpClientRequest;
import com.hehe.weixin.protocol.SwipCardQueryReqData;
import com.hehe.weixin.protocol.SwipCardQueryResData;
import com.hehe.weixin.protocol.SwipCardReqData;
import com.hehe.weixin.protocol.SwipCardResData;
import com.hehe.weixin.service.SwipCardService;
import com.hehe.weixin.vo.SwipCardPayVo;
import com.tencent.business.ScanPayBusiness.ResultListener;
import com.tencent.common.Configure;
import com.tencent.common.Signature;

public class SwipCardServiceImpl implements SwipCardService {
	private static Logger log = LoggerFactory.getLogger(SwipCardServiceImpl.class);
    //每次调用订单查询API时的等待时间，因为当出现支付失败的时候，如果马上发起查询不一定就能查到结果，所以这里建议先等待一定时间再发起查询
    private int waitingTimeBeforePayQueryServiceInvoked = 5000;
    //循环调用订单查询API的次数
    private int payQueryLoopInvokedCount = 3;
    //微信的订单号
    private String transactionID = "";

    
	@Override
	public ModelResult paySync(SwipCardPayVo swipCardPayVo) {
		SwipCardReqData swipCardReqData = new SwipCardReqData(swipCardPayVo);
		swipCardReqData.buildOutTradeNo(SerialNoBuilder.getOrderNum())
						.buildSign();
		String outTradeNo = swipCardReqData.getOut_trade_no();
        String postDataXML = XmlUtils.getXmlFromObject(swipCardReqData);
		String responseStr = new WeiXinHttpClientRequest().postXml(Configure.PAY_API, postDataXML).execute().asString();
		SwipCardResData scanPayResData = (SwipCardResData) XmlUtils.getObjectFromXML(responseStr, SwipCardResData.class);
		if (scanPayResData == null || scanPayResData.getReturn_code() == null) {
            log.error("【支付失败】支付请求逻辑错误，请仔细检测传过去的每一个参数是否合法，或是看API能否被正常访问");
            return new ModelResult().withError(ErrorCode.SystemError.NO_RESPSON);
        }
        if (scanPayResData.getReturn_code().equals("FAIL")) {
            //注意：一般这里返回FAIL是出现系统级参数错误，请检测Post给API的数据是否规范合法
            log.error("【支付失败】支付API系统返回失败，请检测Post给API的数据是否规范合法");
            return new ModelResult().withError(ErrorCode.SystemError.PARAMETER_ERROR);
        } else {
            log.info("支付API系统成功返回数据");
            //--------------------------------------------------------------------
            //收到API的返回数据的时候得先验证一下数据有没有被第三方篡改，确保安全
            //--------------------------------------------------------------------
            if (!Signature.checkIsSignValidFromResponseString(responseStr)) {
                log.error("【支付失败】支付请求API返回的数据签名验证失败，有可能数据被篡改了");
                return new ModelResult().withError(ErrorCode.SystemError.SIGN_ERROR);
            }

            //获取错误码
            String errorCode = scanPayResData.getErr_code();
            //获取错误描述
            String errorCodeDes = scanPayResData.getErr_code_des();

            if (scanPayResData.getResult_code().equals("SUCCESS")) {
                //--------------------------------------------------------------------
                //1)直接扣款成功
                //-------------------------------------------------------------------
                String transID = scanPayResData.getTransaction_id();

            }else{

                //出现业务错误注意：如果当前交易返回的支付状态是明确的错误原因造成的支付失败（支付确认失败），请重新下单支付；
            	//如果当前交易返回的支付状态是不明错误（支付结果未知），请调用查询订单接口确认状态，如果长时间（建议30秒）都得不到明确状态请调用撤销订单接口。
                //业务错误为SYSTEMERROR，BANKERROR，USERPAYING 需要重新循环查询订单状态
                if (errorCode.equals("SYSTEMERROR") || errorCode.equals("BANKERROR") || errorCode.equals("USERPAYING")) {
                	 if (doPayQueryLoop(payQueryLoopInvokedCount, outTradeNo,resultListener)) {
                         log.i("【支付扣款未知失败、查询到支付成功】");
                         resultListener.onSuccess(scanPayResData,transactionID);
                     } else {
                         log.i("【支付扣款未知失败、在一定时间内没有查询到支付成功、走撤销流程】");
                         doReverseLoop(outTradeNo,resultListener);
                         resultListener.onFail(scanPayResData);
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
                	
                	BuzzError buzzError = BuzzError.valueOf(BuzzError.class, errorCode);

                    return new ModelResult().withError(ErrorCode.SystemError.SIGN_ERROR);
                }
            }
          }
	}
        
        /**
         * 由于有的时候是因为服务延时，所以需要商户每隔一段时间（建议5秒）后再进行查询操作，多试几次（建议3次）
         *
         * @param loopCount     循环次数，至少一次
         * @param outTradeNo    商户系统内部的订单号,32个字符内可包含字母, [确保在商户系统唯一]
         * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
         * @return 该订单是否支付成功
         * @throws InterruptedException
         */
        private boolean doPayQueryLoop(int loopCount, String outTradeNo,ResultListener resultListener) throws Exception {
            //至少查询一次
            if (loopCount == 0) {
                loopCount = 1;
            }
            //进行循环查询
            for (int i = 0; i < loopCount; i++) {
            	boolean flag = false;
            	flag = doOnePayQuery(outTradeNo,resultListener);
            	log.info("商户系统内部的订单号为：【{}】，查询订单重试次数：【{}】，重试结果：【{}】",outTradeNo,i+1,flag);
                if (flag) {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * 进行一次支付订单查询操作
         *
         * @param outTradeNo    商户系统内部的订单号,32个字符内可包含字母, [确保在商户系统唯一]
         * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
         * @return 该订单是否支付成功
         * @throws Exception
         */
        private boolean doOnePayQuery(String outTradeNo,ResultListener resultListener) throws Exception {

            sleep(waitingTimeBeforePayQueryServiceInvoked);//等待一定时间再进行查询，避免状态还没来得及被更新

            String payQueryServiceResponseString;

            SwipCardQueryReqData swipCardQueryReqData = new SwipCardQueryReqData("",outTradeNo);
            String postDataXML = XmlUtils.getXmlFromObject(swipCardQueryReqData);
            payQueryServiceResponseString =  new WeiXinHttpClientRequest().postXml(Configure.PAY_API, postDataXML).execute().asString();

            log.info("支付订单查询API返回的数据如下：");
            log.info(payQueryServiceResponseString);

            //将从API返回的XML数据映射到Java对象
            SwipCardQueryResData swipCardQueryResData = (SwipCardQueryResData) XmlUtils.getObjectFromXML(payQueryServiceResponseString, SwipCardQueryResData.class);
            if (swipCardQueryResData == null || swipCardQueryResData.getReturn_code() == null) {
                log.info("支付订单查询请求逻辑错误，请仔细检测传过去的每一个参数是否合法");
                return false;
            }

            if (swipCardQueryResData.getReturn_code().equals("FAIL")) {
                //注意：一般这里返回FAIL是出现系统级参数错误，请检测Post给API的数据是否规范合法
                log.info("支付订单查询API系统返回失败，失败信息为：" + swipCardQueryResData.getReturn_msg());
                return false;
            } else {
                if (!Signature.checkIsSignValidFromResponseString(payQueryServiceResponseString)) {
                    log.info("【支付失败】支付请求API返回的数据签名验证失败，有可能数据被篡改了");
                    return false;
                }

                if (swipCardQueryResData.getResult_code().equals("SUCCESS")) {//业务层成功
                    String transID = swipCardQueryResData.getTransaction_id();
                    if(transID != null){
                        transactionID = transID;
                    }
                    if (swipCardQueryResData.getTrade_state().equals("SUCCESS")) {
                        //表示查单结果为“支付成功”
                        log.info("查询到订单支付成功");
                        return true;
                    } else {
                        //支付不成功
                        log.info("查询到订单支付不成功");
                        return false;
                    }
                } else {
                    log.info("查询出错，错误码：" + swipCardQueryResData.getErr_code() + "     错误信息：" + swipCardQueryResData.getErr_code_des());
                    return false;
                }

            }
        }

}
