package com.hehe.weixin.vo;


/**
 * 请求刷卡支付API前端数据
 */
public class SwipCardPayVo {

    //每个字段具体的意思请查看API文档

    private String device_info = "";
    private String body = "";
    private String detail = "";
    private String attach = "";
    private int total_fee = 0;
    private String fee_type = "";
    private String spbill_create_ip = "";
    private String goods_tag = "";
    private String limit_pay = "";
    private String auth_code = "";
    /**
     * @param authCode 这个是扫码终端设备从用户手机上扫取到的支付授权号，这个号是跟用户用来支付的银行卡绑定的，有效期是1分钟
     * @param body 要支付的商品的描述信息，用户会在支付成功页面里看到这个信息
     * @param totalFee 订单总金额，单位为“分”，只能整数
     * @param spBillCreateIP 订单生成的机器IP
     */
    public SwipCardPayVo(String authCode,String body,int totalFee,String spBillCreateIP){
        //这个是扫码终端设备从用户手机上扫取到的支付授权号，这个号是跟用户用来支付的银行卡绑定的，有效期是1分钟
        //调试的时候可以在微信上打开“钱包”里面的“刷卡”，将扫码页面里的那一串14位的数字输入到这里来，进行提交验证
        //记住out_trade_no这个订单号可以将这一笔支付进行退款
        setAuth_code(authCode);
        //要支付的商品的描述信息，用户会在支付成功页面里看到这个信息
        setBody(body);
        //订单总金额，单位为“分”，只能整数
        setTotal_fee(totalFee);
        //订单生成的机器IP
        setSpbill_create_ip(spBillCreateIP);

    }
    
    public SwipCardPayVo buildDevice_info(String device_info){
    	setDevice_info(device_info);
    	return this;
    }
    
    public SwipCardPayVo buildDetail(String detail){
    	setDetail(detail);
    	return this;
    }
  
    public SwipCardPayVo buildAttach(String attach){
    	setAttach(attach);
    	return this;
    }
    
    public SwipCardPayVo buildFee_type(String fee_type){
    	setFee_type(fee_type);
    	return this;
    }
    
    public SwipCardPayVo buildGoods_tag(String goods_tag){
    	setGoods_tag(goods_tag);
    	return this;
    }
    
    public SwipCardPayVo buildLimit_pay(String limit_pay){
    	setLimit_pay(limit_pay);
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
	public String getAuth_code() {
		return auth_code;
	}
	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}

	public String getLimit_pay() {
		return limit_pay;
	}

	public void setLimit_pay(String limit_pay) {
		this.limit_pay = limit_pay;
	}



}
