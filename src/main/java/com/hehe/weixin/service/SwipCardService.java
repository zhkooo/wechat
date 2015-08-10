package com.hehe.weixin.service;

import com.hehe.common.ModelResult;
import com.hehe.weixin.vo.SwipCardPayVo;

/**
 * 
 * 刷卡支付
 *
 */
public interface SwipCardService {
	
	public ModelResult paySync(SwipCardPayVo swipCardPayVo);
}
