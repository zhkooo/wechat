package com.hehe.weixin.vo;



/**
 * 请求刷卡支付API前端数据
 */
public class ScanPayVo {

    //每个字段具体的意思请查看API文档

    private String device_info = "";
    private String body = "";
    private String detail = "";
    private String attach = "";
    private int total_fee = 0;
    private String fee_type = "";
    private String spbill_create_ip = "";
    private String time_start = "";
    private String time_expire = "";
    private String goods_tag = "";
    private String notify_url = "";
    private String trade_type = "";
    private String product_id = "";
    private String limit_pay = "";
    private String openid = "";
    
    public ScanPayVo(){}
    /**
     * @param authCode 这个是扫码终端设备从用户手机上扫取到的支付授权号，这个号是跟用户用来支付的银行卡绑定的，有效期是1分钟
     * @param body 要支付的商品的描述信息，用户会在支付成功页面里看到这个信息
     * @param totalFee 订单总金额，单位为“分”，只能整数
     * @param spBillCreateIP 订单生成的机器IP
     */
    public ScanPayVo(String body,int totalFee,String spBillCreateIP,String notify_url,
    		String trade_type ){
   
        //要支付的商品的描述信息，用户会在支付成功页面里看到这个信息
        setBody(body);
        //订单总金额，单位为“分”，只能整数
        setTotal_fee(totalFee);
        //订单生成的机器IP
        setSpbill_create_ip(spBillCreateIP);
        setNotify_url(notify_url);
        setTrade_type(trade_type);
    }
    
    public ScanPayVo buildDevice_info(String device_info){
    	setDevice_info(device_info);
    	return this;
    }
    
    public ScanPayVo buildDetail(String detail){
    	setDetail(detail);
    	return this;
    }
    
    public ScanPayVo buildAttach(String attach){
    	setAttach(attach);
    	return this;
    }
    
    public ScanPayVo buildTime_start(String time_start){
    	setTime_start(time_start);
    	return this;
    }
    
    public ScanPayVo buildTime_expire(String time_expire){
    	setTime_expire(time_expire);
    	return this;
    }
    
    public ScanPayVo buildProduct_id(String product_id){
    	setProduct_id(product_id);
    	return this;
    }
    
    public ScanPayVo buildLimit_pay(String limit_pay){
    	setLimit_pay(limit_pay);
    	return this;
    }
    
    public ScanPayVo buildOpenid(String openid){
    	setOpenid(openid);
    	return this;
    }
    
    public ScanPayVo buildGoods_tag(String goods_tag){
    	setGoods_tag(goods_tag);
    	return this;
    }
    
    public ScanPayVo buildFee_type(String fee_type){
    	setAttach(attach);
    	return this;
    }
    
    
	public String getDevice_info() {
		return device_info;
	}
	public void setDevice_info(String device_info) {
		this.device_info = device_info;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	public int getTotal_fee() {
		return total_fee;
	}
	public void setTotal_fee(int total_fee) {
		this.total_fee = total_fee;
	}
	public String getFee_type() {
		return fee_type;
	}
	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}
	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}
	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}
	public String getGoods_tag() {
		return goods_tag;
	}
	public void setGoods_tag(String goods_tag) {
		this.goods_tag = goods_tag;
	}


	public String getLimit_pay() {
		return limit_pay;
	}

	public void setLimit_pay(String limit_pay) {
		this.limit_pay = limit_pay;
	}

	public String getTime_start() {
		return time_start;
	}

	public void setTime_start(String time_start) {
		this.time_start = time_start;
	}

	public String getTime_expire() {
		return time_expire;
	}

	public void setTime_expire(String time_expire) {
		this.time_expire = time_expire;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}



}
