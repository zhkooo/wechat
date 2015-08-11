package com.hehe.weixin.service.impl;

import static java.lang.Thread.sleep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hehe.common.Configure;
import com.hehe.common.ErrorCode.BuzzError;
import com.hehe.common.ErrorCode.SystemError;
import com.hehe.common.ModelResult;
import com.hehe.common.Signature;
import com.hehe.utils.SerialNoBuilder;
import com.hehe.utils.XmlUtils;
import com.hehe.weixin.common.WeiXinHttpClientRequest;
import com.hehe.weixin.protocol.SwipCardReqData;
import com.hehe.weixin.protocol.SwipCardResData;
import com.hehe.weixin.protocol.WeiXinOrderQueryResData;
import com.hehe.weixin.protocol.WeiXinOrderReverseResData;
import com.hehe.weixin.service.SwipCardService;
import com.hehe.weixin.vo.SwipCardPayVo;

public class SwipCardServiceImpl implements SwipCardService {
	private static Logger log = LoggerFactory.getLogger(SwipCardServiceImpl.class);
    //循环调用订单查询API的次数
    private int payQueryLoopInvokedCount = 3;
    //微信的订单号
    //private String transactionID = "";
    //是否需要再调一次撤销，这个值由撤销API回包的recall字段决定
    private boolean needRecallReverse = false;
    private String outTradeNo;
    //每次调用订单查询API时的等待时间，因为当出现支付失败的时候，如果马上发起查询不一定就能查到结果，所以这里建议先等待一定时间再发起查询
    private int waitingTimeBeforePayQueryServiceInvoked = 5000;
    //每次调用撤销API的等待时间
    private int waitingTimeBeforeReverseServiceInvoked = 5000;
    
    private WeiXinPayOrderServiceImpl weiXinPayOrderServiceImpl = new WeiXinPayOrderServiceImpl();
    
	@Override
	public ModelResult<SwipCardResData> paySync(SwipCardPayVo swipCardPayVo) {
		SwipCardResData scanPayResData = null;
		try {
			SwipCardReqData swipCardReqData = new SwipCardReqData(swipCardPayVo);
			swipCardReqData.buildOutTradeNo(SerialNoBuilder.getOrderNum()).buildSign();
			outTradeNo = swipCardReqData.getOut_trade_no();
			log.info("【刷卡支付-支付请求】请求数据：{}",swipCardReqData.toString());
			String postDataXML = XmlUtils.getXmlFromObject(swipCardReqData);
			String responseStr = new WeiXinHttpClientRequest().postXml(Configure.SWIPCARD_PAY_API, postDataXML).execute().asString();
			log.info("【刷卡支付-支付返回】返回数据：{}",responseStr);
			scanPayResData = (SwipCardResData) XmlUtils.getObjectFromXML(responseStr, SwipCardResData.class);
			ModelResult<SwipCardResData> modelResult = new ModelResult<SwipCardResData>(scanPayResData);
			if (scanPayResData == null || scanPayResData.getReturn_code() == null) {
			    log.error("【刷卡支付-支付失败-系统级】参数:[authCode:{},outTradeNo:{}],失败原因：支付请求逻辑错误",swipCardReqData.getAuth_code(),swipCardReqData.getOut_trade_no());
			    return modelResult.withError(SystemError.NO_RESPSON);
			}
			if (scanPayResData.getReturn_code().equals("FAIL")) {
			    log.error("【刷卡支付-支付失败-系统级】参数:[authCode:{},outTradeNo:{}],失败原因：支付API系统返回失败",swipCardReqData.getAuth_code(),swipCardReqData.getOut_trade_no());
			    return modelResult.withError(SystemError.PARAMETER_ERROR);
			} else {
			    log.error("【刷卡支付-支付返回】参数:[authCode:{},outTradeNo:{}],通讯成功",swipCardReqData.getAuth_code(),swipCardReqData.getOut_trade_no());
			    //--------------------------------------------------------------------
			    //收到API的返回数据的时候得先验证一下数据有没有被第三方篡改，确保安全
			    //--------------------------------------------------------------------
			    if (!Signature.checkIsSignValidFromResponseString(responseStr)) {
				    log.error("【刷卡支付-支付失败-系统级】参数:[authCode:{},outTradeNo:{}],失败原因：返回的数据签名验证失败，有可能数据被篡改了",swipCardReqData.getAuth_code(),swipCardReqData.getOut_trade_no());
			        return modelResult.withError(SystemError.SIGN_ERROR);
			    }
			    String errorCode = scanPayResData.getErr_code();
			    String errorCodeDes = scanPayResData.getErr_code_des();
			    if (scanPayResData.getResult_code().equals("SUCCESS")) {
				    log.error("【刷卡支付-支付成功】返回数据：",scanPayResData.toString());
			        //--------------------------------------------------------------------
			        //1)直接扣款成功
			        //-------------------------------------------------------------------
			        //String transID = scanPayResData.getTransaction_id();
			        return modelResult;
			    }else{
			        //出现业务错误注意：如果当前交易返回的支付状态是明确的错误原因造成的支付失败（支付确认失败），请重新下单支付；
			    	//如果当前交易返回的支付状态是不明错误（支付结果未知），请调用查询订单接口确认状态，如果长时间（建议30秒）都得不到明确状态请调用撤销订单接口。
			        //业务错误为SYSTEMERROR，BANKERROR，USERPAYING 需要重新循环查询订单状态
			        if (BuzzError.isUnknownResult(errorCode)) {
					     log.error("【刷卡支付-支付失败-业务级-支付结果未知】参数:[authCode:{},outTradeNo:{},走重新循环查询订单状态流程],失败原因：errorCode:{},errorCodeDes:{}",swipCardReqData.getAuth_code(),swipCardReqData.getOut_trade_no(),errorCode,errorCodeDes);
			        	 if (doPayQueryLoop(payQueryLoopInvokedCount, outTradeNo)) {
							 log.error("【刷卡支付-支付成功】走重新循环查询订单状态流程,查询到支付成功,返回数据：",scanPayResData.toString());
			                 return modelResult;
			             } else {
							 log.error("【刷卡支付-支付失败-业务级】参数:[authCode:{},outTradeNo:{}],失败原因：errorCode:{},errorCodeDes:{},走重新循环查询订单状态流程,没有查询到支付成功,走撤销流程",swipCardReqData.getAuth_code(),swipCardReqData.getOut_trade_no(),errorCode,errorCodeDes);
			                 doReverseLoop(outTradeNo);
			             }
			        }else {
					    log.error("【刷卡支付-支付失败-业务级-支付确认失败】参数:[authCode:{},outTradeNo:{}],失败原因：errorCode:{},errorCodeDes:{},直接返回错误结果"
					    		+ "",swipCardReqData.getAuth_code(),swipCardReqData.getOut_trade_no(),errorCode,errorCodeDes);
			            return modelResult.withError(BuzzError.getBuzzErrorByCode(errorCode));
			        }
			    }
			  }
		} catch (Exception e) {
			try {
				if(isNeedReverse(outTradeNo)){
					doReverseLoop(outTradeNo);
				}
				return new ModelResult<SwipCardResData>(scanPayResData);
			} catch (Exception e1) {
				 return new ModelResult<SwipCardResData>().withError(SystemError.UNKNOWN_ERROR);
			}
		}
		return null;
	}
        
        /**
         * 由于有的时候是因为服务延时，所以需要商户每隔一段时间（建议5秒）后再进行查询操作，多试几次（建议3次）
         *
         * @param loopCount     循环次数，至少一次
         * @param outTradeNo    商户系统内部的订单号,32个字符内可包含字母, [确保在商户系统唯一]
         * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
         * @return 该订单是否支付成功
         * @throws Exception 
         * @throws InterruptedException
         */
        private boolean doPayQueryLoop(int loopCount, String outTradeNo) throws Exception {
            //至少查询一次
            if (loopCount == 0) {
                loopCount = 1;
            }
            //进行循环查询
            for (int i = 0; i < loopCount; i++) {
        		sleep(waitingTimeBeforePayQueryServiceInvoked);//等待一定时间再进行查询，避免状态还没来得及被更新
            	boolean flag = false;
            	flag = isPaySuccuss(outTradeNo);
            	log.info("商户系统内部的订单号为：【{}】，查询订单重试次数：【{}】，重试结果：【{}】",outTradeNo,i+1,flag);
                if (flag) {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * return_code、result_code、 trade_state 都为SUCCESS
         * @param outTradeNo
         * @return 订单是否支付成功
         * @throws Exception 
         */
        public boolean isPaySuccuss(String outTradeNo) throws Exception{
        	 ModelResult<WeiXinOrderQueryResData> modelResult = weiXinPayOrderServiceImpl.queryOrder(outTradeNo);
        	 if(modelResult.isSuccess()){
        		 WeiXinOrderQueryResData swipCardQueryResData = modelResult.getModel();
        		String errorCode = swipCardQueryResData.getErr_code();
     		    String errorCodeDes = swipCardQueryResData.getErr_code_des();
                  if (swipCardQueryResData.getResult_code().equals("SUCCESS")) {//业务层成功
                      if (swipCardQueryResData.getTrade_state().equals("SUCCESS")) {
                      	log.error("【刷卡支付-支付订单查询-支付成功】返回数据：",swipCardQueryResData.toString());
       			        return true;
                      } else {
                      	log.error("【刷卡支付-支付订单查询-支付不成功】返回数据：",swipCardQueryResData.toString());
                      	return false;
                      }
                  } else {
                  	log.error("【刷卡支付-支付订单查询-支付确认失败】参数:[outTradeNo:{}],失败原因：errorCode:{},errorCodeDes:{},直接返回错误结果"
     			    		+ "",swipCardQueryResData.getOut_trade_no(),errorCode,errorCodeDes);                   
                  	return false;
                  }
        	 }
        	 return false;
        }
        
       
		/**
		 * 查询支付订单,如果业务返回成功而交易状态不为USERPAYING--用户支付中 则不进行撤销返回false,其他情况都进行撤销操作true
		 * @param outTradeNo
		 * @return
		 * @throws Exception 
		 */
		public boolean isNeedReverse(String outTradeNo) throws Exception{
			ModelResult<WeiXinOrderQueryResData> modelResult = weiXinPayOrderServiceImpl.queryOrder(outTradeNo);
			if(modelResult.isSuccess()){
				 WeiXinOrderQueryResData swipCardQueryResData = modelResult.getModel();
				 if (swipCardQueryResData.getResult_code().equals("SUCCESS") && !swipCardQueryResData.getTrade_state().equals("USERPAYING")) {
                     return false;
                 } else {
                 	return true;
                 }
			}
			return true;
		}
		
		/**
		 * 当且仅当 modelResult返回Success 且 Result_code为SUCCESS 或者 为FAIL而Recall为Y时  说明撤销成功
		 * @param modelResult
		 * @return
		 */
		public boolean isReverseSuccess(ModelResult<WeiXinOrderReverseResData> modelResult){
				if(!modelResult.isSuccess()){
					return false;
				}
				WeiXinOrderReverseResData reverseResData = modelResult.getModel();
                if (reverseResData.getResult_code().equals("FAIL") && !reverseResData.getRecall().equals("Y")) {
                   return false;
                } 
                return true; 
			}
        
        /**
         * 由于有的时候是因为服务延时，所以需要商户每隔一段时间（建议5秒）后再进行查询操作，是否需要继续循环调用撤销API由撤销API回包里面的recall字段决定。
         *
         * @param outTradeNo    商户系统内部的订单号,32个字符内可包含字母, [确保在商户系统唯一]
         * @param resultListener 商户需要自己监听被扫支付业务逻辑可能触发的各种分支事件，并做好合理的响应处理
         * @throws InterruptedException
         */
        private void doReverseLoop(String outTradeNo) throws Exception {
        	int loopCount = 0;
            //初始化这个标记
            needRecallReverse = isNeedReverse(outTradeNo);
            //进行循环撤销，直到撤销成功，或是API返回recall字段为"Y"
            while (needRecallReverse) {
        		sleep(waitingTimeBeforeReverseServiceInvoked);//等待一定时间再进行查询，避免状态还没来得及被更新
            	loopCount++;
            	log.info("【刷卡支付-支付订单撤销】次数：第{}次",loopCount);
                if (isReverseSuccess(weiXinPayOrderServiceImpl.reverseOrder(outTradeNo))) {
                    return;
                }
            }
        }
        
    

}
