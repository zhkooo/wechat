package com.hehe.weixin.service;

import com.hehe.common.ModelResult;
import com.hehe.weixin.protocol.WeiXinOrderCloseResData;
import com.hehe.weixin.protocol.WeiXinOrderQueryResData;
import com.hehe.weixin.protocol.WeiXinOrderReverseResData;
import com.hehe.weixin.protocol.WeiXinUnifiedOrderReqData;
import com.hehe.weixin.protocol.WeiXinUnifiedOrderResData;

/**
 * 
 *	微信支付单相关操作
 *
 */
public interface WeiXinPayOrderService {
	/**
	 * 统一下单
	 * @param outTradeNo
	 * @return
	 * @throws Exception
	 */
	public ModelResult<WeiXinUnifiedOrderResData> unifiedOrder(WeiXinUnifiedOrderReqData unifiedOrderReqData) throws Exception;

	/**
	 * 订单查询
	 * @param outTradeNo
	 * @return
	 * @throws Exception
	 */
	public ModelResult<WeiXinOrderQueryResData> queryOrder(String outTradeNo) throws Exception;
	/**
	 * 撤销订单
	 * @param outTradeNo
	 * @return
	 * @throws Exception
	 */
	public ModelResult<WeiXinOrderReverseResData> reverseOrder(String outTradeNo)throws Exception;
	/**
	 * 关闭订单
	 * @param outTradeNo
	 * @return
	 * @throws Exception
	 */
	public ModelResult<WeiXinOrderCloseResData> closeOrder(String outTradeNo)throws Exception;

}
