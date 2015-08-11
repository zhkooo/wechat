package com.hehe.weixin.common.type;

import com.hehe.common.BaseType;

public class TradeType extends BaseType {
	private static final long serialVersionUID = 1L;
	public static final TradeType JSAPI = new TradeType(0, "JSAPI");
	public static final TradeType NATIVE = new TradeType(1, "NATIVE");
	public static final TradeType APP = new TradeType(2, "APP");
	public static final TradeType WAP = new TradeType(3, "WAP");

	
	private TradeType(){
        super(null, null);
    }
    
    private TradeType(int index, String description) {
        super(index, description);
    }
}
