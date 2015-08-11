package com.hehe.weixin.service.impl;

import static java.lang.Thread.sleep;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hehe.common.Configure;
import com.hehe.common.ErrorCode.BuzzError;
import com.hehe.common.ErrorCode.SystemError;
import com.hehe.common.ModelResult;
import com.hehe.common.Signature;
import com.hehe.common.Util;
import com.hehe.utils.XmlUtils;
import com.hehe.weixin.common.WeiXinHttpClientRequest;
import com.hehe.weixin.protocol.SwipCardResData;
import com.hehe.weixin.protocol.WeiXinOrderCloseReqData;
import com.hehe.weixin.protocol.WeiXinOrderCloseResData;
import com.hehe.weixin.protocol.WeiXinOrderQueryReqData;
import com.hehe.weixin.protocol.WeiXinOrderQueryResData;
import com.hehe.weixin.protocol.WeiXinOrderReverseReqData;
import com.hehe.weixin.protocol.WeiXinOrderReverseResData;
import com.hehe.weixin.protocol.WeiXinUnifiedOrderReqData;
import com.hehe.weixin.protocol.WeiXinUnifiedOrderResData;
import com.hehe.weixin.service.WeiXinPayOrderService;

public class WeiXinPayOrderServiceImpl implements WeiXinPayOrderService {
	private static Logger log = LoggerFactory.getLogger(WeiXinPayOrderServiceImpl.class);

    

	@Override
	public ModelResult<WeiXinOrderQueryResData> queryOrder(String outTradeNo) throws Exception {
		ModelResult<WeiXinOrderQueryResData> modelResult = null;
		String responseStr;
		WeiXinOrderQueryReqData queryReqData = new WeiXinOrderQueryReqData("",outTradeNo);
		log.info("【支付订单查询】请求数据：{}",queryReqData.toString());
		String postDataXML = XmlUtils.getXmlFromObject(queryReqData);
		responseStr =  new WeiXinHttpClientRequest().postXml(Configure.ORDER_QUERY_API, postDataXML).execute().asString();
		log.info("【支付订单查询】返回数据：{}",responseStr);
		WeiXinOrderQueryResData queryResData = (WeiXinOrderQueryResData) XmlUtils.getObjectFromXML(responseStr, WeiXinOrderQueryResData.class);
		modelResult = new ModelResult<WeiXinOrderQueryResData>(queryResData); 
		if (queryResData == null || queryResData.getReturn_code() == null) {
		     return modelResult.withError(SystemError.NO_RESPSON);
		}
		if (queryResData.getReturn_code().equals("FAIL")) {
		    return modelResult.withError(SystemError.PARAMETER_ERROR);
		 }
		if (!Signature.checkIsSignValidFromResponseString(responseStr)) {
		    log.error("【支付订单查询】参数:[outTradeNo:{}],失败原因：返回的数据签名验证失败，有可能数据被篡改了",queryResData.getOut_trade_no());
		    return modelResult.withError(SystemError.SIGN_ERROR);
		 }
		
        return modelResult;
	}

	@Override
	public ModelResult<WeiXinOrderReverseResData> reverseOrder(String outTradeNo) throws Exception {
        String responseStr;
        WeiXinOrderReverseReqData reverseReqData = new WeiXinOrderReverseReqData("",outTradeNo);
		log.info("【支付订单撤销】请求数据：{}",reverseReqData.toString());
        String postDataXML = XmlUtils.getXmlFromObject(reverseReqData);
        responseStr = new WeiXinHttpClientRequest().postXml(Configure.ORDER_REVERSE_API, postDataXML).execute().asString();
    	log.info("【支付订单撤销】返回数据：{}",responseStr);
        WeiXinOrderReverseResData reverseResData = (WeiXinOrderReverseResData) Util.getObjectFromXML(responseStr, WeiXinOrderReverseResData.class);
        ModelResult<WeiXinOrderReverseResData> modelResult = new ModelResult<WeiXinOrderReverseResData>(reverseResData); 
        if (reverseResData == null) {
		    log.error("【支付订单撤销-系统级】参数:[outTradeNo:{}],失败原因：支付订单撤销逻辑错误",reverseReqData.getOut_trade_no());
		    return modelResult.withError(SystemError.NO_RESPSON);
        }
        if (reverseResData.getReturn_code().equals("FAIL")) {
		    log.error("【支付订单撤销-系统级】参数:[outTradeNo:{}],失败原因：支付API系统返回失败",reverseReqData.getOut_trade_no());
		    return modelResult.withError(SystemError.PARAMETER_ERROR);
        } 
        if (!Signature.checkIsSignValidFromResponseString(responseStr)) {
            log.error("【支付订单撤销-系统级】参数:[outTradeNo:{}],失败原因：返回的数据签名验证失败，有可能数据被篡改了",reverseReqData.getOut_trade_no());
	        return modelResult.withError(SystemError.SIGN_ERROR);
        }
        return modelResult;
	}
	
	@Override
	public ModelResult<WeiXinOrderCloseResData> closeOrder(String outTradeNo) throws Exception {
        String responseStr;
        WeiXinOrderCloseReqData closeReqData = new WeiXinOrderCloseReqData("",outTradeNo);
		log.info("【支付订单关闭】请求数据：{}",closeReqData.toString());
        String postDataXML = XmlUtils.getXmlFromObject(closeReqData);
        responseStr = new WeiXinHttpClientRequest().postXml(Configure.ORDER_CLOSE_API, postDataXML).execute().asString();
    	log.info("【支付订单关闭】返回数据：{}",responseStr);
    	WeiXinOrderCloseResData colseResData = (WeiXinOrderCloseResData) Util.getObjectFromXML(responseStr, WeiXinOrderCloseResData.class);
        ModelResult<WeiXinOrderCloseResData> modelResult = new ModelResult<WeiXinOrderCloseResData>(colseResData); 
        if (colseResData == null) {
		    log.error("【支付订单关闭-系统级】参数:[outTradeNo:{}],失败原因：支付订单关闭逻辑错误",closeReqData.getOut_trade_no());
		    return modelResult.withError(SystemError.NO_RESPSON);
        }
        if (colseResData.getReturn_code().equals("FAIL")) {
		    log.error("【支付订单关闭-系统级】参数:[outTradeNo:{}],失败原因：支付API系统返回失败",closeReqData.getOut_trade_no());
		    return modelResult.withError(SystemError.PARAMETER_ERROR);
        } 
        if (!Signature.checkIsSignValidFromResponseString(responseStr)) {
            log.error("【支付订单关闭-系统级】参数:[outTradeNo:{}],失败原因：返回的数据签名验证失败，有可能数据被篡改了",closeReqData.getOut_trade_no());
	        return modelResult.withError(SystemError.SIGN_ERROR);
        }
        return modelResult;
	}

	@Override
	public ModelResult<WeiXinUnifiedOrderResData> unifiedOrder(WeiXinUnifiedOrderReqData unifiedOrderReqData)
			throws Exception {
		WeiXinUnifiedOrderResData unifiedOrderResData = null;
	    String outTradeNo;
		try {
			log.info("【预支付交易单支付请求】请求数据：{}",unifiedOrderReqData.toString());
			outTradeNo = unifiedOrderReqData.getOut_trade_no();
			String postDataXML = XmlUtils.getXmlFromObject(unifiedOrderReqData);
			String responseStr = new WeiXinHttpClientRequest().postXml(Configure.ORDER_UNIFIED_API, postDataXML).execute().asString();
			log.info("【预支付交易单支付返回】返回数据：{}",responseStr);
			unifiedOrderResData = (WeiXinUnifiedOrderResData) XmlUtils.getObjectFromXML(responseStr, WeiXinUnifiedOrderResData.class);
			ModelResult<WeiXinUnifiedOrderResData> modelResult = new ModelResult<WeiXinUnifiedOrderResData>(unifiedOrderResData);
			if (unifiedOrderResData == null || unifiedOrderResData.getReturn_code() == null) {
			    log.error("【预支付交易单支付失败-系统级】参数:[outTradeNo:{}],失败原因：支付请求逻辑错误",outTradeNo);
			    return modelResult.withError(SystemError.NO_RESPSON);
			}
			if (unifiedOrderResData.getReturn_code().equals("FAIL")) {
			    log.error("【预支付交易单支付失败-系统级】参数:[outTradeNo:{}],失败原因：支付API系统返回失败",outTradeNo);
			    return modelResult.withError(SystemError.PARAMETER_ERROR);
			} else {
			    log.error("【预支付交易单支付返回】参数:[outTradeNo:{}],通讯成功",outTradeNo);
			    //--------------------------------------------------------------------
			    //收到API的返回数据的时候得先验证一下数据有没有被第三方篡改，确保安全
			    //--------------------------------------------------------------------
			    if (!Signature.checkIsSignValidFromResponseString(responseStr)) {
				    log.error("【预支付交易单支付失败-系统级】参数:[outTradeNo:{}],失败原因：返回的数据签名验证失败，有可能数据被篡改了",outTradeNo);
			        return modelResult.withError(SystemError.SIGN_ERROR);
			    }
			    String errorCode = unifiedOrderResData.getErr_code();
			    String errorCodeDes = unifiedOrderResData.getErr_code_des();
			    if (unifiedOrderResData.getResult_code().equals("SUCCESS")) {
				    log.error("【预支付交易单支付成功】返回数据：",unifiedOrderResData.toString());
			        //--------------------------------------------------------------------
			        //1)预支付生成支付地址成功
			        //-------------------------------------------------------------------
			        return modelResult;
			    }else{
				    log.error("【预支付交易单支付失败-业务级-支付确认失败】参数:[authCode:{},outTradeNo:{}],失败原因：errorCode:{},errorCodeDes:{},直接返回错误结果"
				    		+ "",outTradeNo,errorCode,errorCodeDes);
		            return modelResult.withError(BuzzError.getBuzzErrorByCode(errorCode));
			        }
			    }
			  
		} catch (Exception e) {
				log.error("预支付交易单失败",e);
				return new ModelResult<SwipCardResData>().withError(SystemError.UNKNOWN_ERROR);
			}
	}
   
        
       
}
