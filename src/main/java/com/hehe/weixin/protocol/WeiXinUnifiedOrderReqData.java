package com.hehe.weixin.protocol;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.hehe.common.Configure;
import com.hehe.common.Signature;
import com.hehe.utils.RandomStringGenerator;
import com.hehe.weixin.vo.ScanPayVo;

/**
 * 请求扫码支付API需要提交的数据
 */
public class WeiXinUnifiedOrderReqData {

    //每个字段具体的意思请查看API文档
    private String appid = "";
    private String mch_id = "";
    private String device_info = "";
    private String nonce_str = "";
    private String sign = "";
    private String body = "";
    private String detail = "";
    private String attach = "";
    private String out_trade_no = "";
    private int total_fee = 0;
    private String fee_type = "";
    private String spbill_create_ip = "";
    private String time_start = "";
    private String time_expire = "";
    private String goods_tag = "";
    private String limit_pay = "";
    
    private String notify_url = "";
    private String trade_type = "";
    private String product_id = "";
    private String openid = "";
    
    public WeiXinUnifiedOrderReqData(){}
    public void init(){

        //微信分配的公众号ID（开通公众号之后可以获取到）
        setAppid(Configure.getAppid());

        //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
        setMch_id(Configure.getMchid());

        //随机字符串，不长于32 位
        setNonce_str(RandomStringGenerator.getRandomStringByLength(32));
    }
    /**
     * @param authCode 这个是扫码终端设备从用户手机上扫取到的支付授权号，这个号是跟用户用来支付的银行卡绑定的，有效期是1分钟
     * @param body 要支付的商品的描述信息，用户会在支付成功页面里看到这个信息
     * @param attach 支付订单里面可以填的附加数据，API会将提交的这个附加数据原样返回
     * @param outTradeNo 商户系统内部的订单号,32个字符内可包含字母, 确保在商户系统唯一
     * @param totalFee 订单总金额，单位为“分”，只能整数
     * @param deviceInfo 商户自己定义的扫码支付终端设备号，方便追溯这笔交易发生在哪台终端设备上
     * @param spBillCreateIP 订单生成的机器IP
     * @param timeStart 订单生成时间， 格式为yyyyMMddHHmmss，如2009年12 月25 日9 点10 分10 秒表示为20091225091010。时区为GMT+8 beijing。该时间取自商户服务器
     * @param timeExpire 订单失效时间，格式同上
     * @param goodsTag 商品标记，微信平台配置的商品标记，用于优惠券或者满减使用
     */
    public WeiXinUnifiedOrderReqData(String body,String outTradeNo,int totalFee,String spBillCreateIP,String notify_url
    		,String trade_type){
    	init();
        //要支付的商品的描述信息，用户会在支付成功页面里看到这个信息
        setBody(body);
        //商户系统内部的订单号,32个字符内可包含字母, 确保在商户系统唯一
        setOut_trade_no(outTradeNo);
        //订单总金额，单位为“分”，只能整数
        setTotal_fee(totalFee);
        setSpbill_create_ip(spBillCreateIP);
        setNotify_url(notify_url);
        setTrade_type(trade_type);
    }
    
    public WeiXinUnifiedOrderReqData(ScanPayVo scanPayVo){
    	init();
    	this.attach = scanPayVo.getAttach();
    	this.body = scanPayVo.getBody();
    	this.detail = scanPayVo.getDetail();
    	this.device_info = scanPayVo.getDevice_info();
    	this.fee_type = scanPayVo.getFee_type();
    	this.goods_tag = scanPayVo.getGoods_tag();
    	this.spbill_create_ip = scanPayVo.getSpbill_create_ip();
    	this.time_start = scanPayVo.getTime_start();
    	this.time_expire = scanPayVo.getTime_expire();
    	this.total_fee = scanPayVo.getTotal_fee();
    	this.limit_pay = scanPayVo.getLimit_pay();
    	this.product_id = scanPayVo.getProduct_id();
    	this.notify_url = scanPayVo.getNotify_url();
    	this.trade_type = scanPayVo.getTrade_type();
    	this.openid = scanPayVo.getOpenid();
    }
    
    public WeiXinUnifiedOrderReqData buildDevice_info(String device_info){
    	setDevice_info(device_info);
    	return this;
    }
    
    public WeiXinUnifiedOrderReqData buildDetail(String detail){
    	setDetail(detail);
    	return this;
    }
    
    public WeiXinUnifiedOrderReqData buildAttach(String attach){
    	setAttach(attach);
    	return this;
    }
    
    public WeiXinUnifiedOrderReqData buildTime_start(String time_start){
    	setTime_start(time_start);
    	return this;
    }
    
    public WeiXinUnifiedOrderReqData buildTime_expire(String time_expire){
    	setTime_expire(time_expire);
    	return this;
    }
    
    public WeiXinUnifiedOrderReqData buildProduct_id(String product_id){
    	setProduct_id(product_id);
    	return this;
    }
    
    public WeiXinUnifiedOrderReqData buildLimit_pay(String limit_pay){
    	setLimit_pay(limit_pay);
    	return this;
    }
    
    public WeiXinUnifiedOrderReqData buildOpenid(String openid){
    	setOpenid(openid);
    	return this;
    }
    
    public WeiXinUnifiedOrderReqData buildGoods_tag(String goods_tag){
    	setGoods_tag(goods_tag);
    	return this;
    }
    
    public WeiXinUnifiedOrderReqData buildFee_type(String fee_type){
    	setAttach(attach);
    	return this;
    }
    
    public WeiXinUnifiedOrderReqData buildOutTradeNo(String outTradeNo){
    	setOut_trade_no(outTradeNo);
    	return this;
    }
    
    /**
     * 所有字段设值完成后，才调用改方法
     * @return
     */
    public WeiXinUnifiedOrderReqData buildSign(){
    	 //根据API给的签名规则进行签名
        String sign = Signature.getSign(toMap());
        setSign(sign);//把签名数据设置到Sign这个属性中
    	return this;
    }
    

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public int getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(int total_fee) {
        this.total_fee = total_fee;
    }

    public String getSpbill_create_ip() {
        return spbill_create_ip;
    }

    public void setSpbill_create_ip(String spbill_create_ip) {
        this.spbill_create_ip = spbill_create_ip;
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

    public String getGoods_tag() {
        return goods_tag;
    }

    public void setGoods_tag(String goods_tag) {
        this.goods_tag = goods_tag;
    }

    public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	
	public String getLimit_pay() {
		return limit_pay;
	}

	public void setLimit_pay(String limit_pay) {
		this.limit_pay = limit_pay;
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
	
	public Map<String,Object> toMap(){
        Map<String,Object> map = new HashMap<String, Object>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            Object obj;
            try {
                obj = field.get(this);
                if(obj!=null){
                    map.put(field.getName(), obj);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return map;
    }
	@Override
	public String toString() {
		return "WeiXinUnifiedOrderReqData [appid=" + appid + ", mch_id="
				+ mch_id + ", device_info=" + device_info + ", nonce_str="
				+ nonce_str + ", sign=" + sign + ", body=" + body + ", detail="
				+ detail + ", attach=" + attach + ", out_trade_no="
				+ out_trade_no + ", total_fee=" + total_fee + ", fee_type="
				+ fee_type + ", spbill_create_ip=" + spbill_create_ip
				+ ", time_start=" + time_start + ", time_expire=" + time_expire
				+ ", goods_tag=" + goods_tag + ", limit_pay=" + limit_pay
				+ ", notify_url=" + notify_url + ", trade_type=" + trade_type
				+ ", product_id=" + product_id + ", openid=" + openid + "]";
	}

}
