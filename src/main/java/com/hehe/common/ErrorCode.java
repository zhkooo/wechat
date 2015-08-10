package com.hehe.common;



public interface ErrorCode  {

    
    //系统错误码
    public static class SystemError extends BaseType implements ErrorCode{
        
        private static final long serialVersionUID = -7588376604965702002L;
       
        public static final SystemError UNKNOWN_ERROR = new SystemError(10001,"您的网络忙，请稍后再试");
       
        public static final SystemError INTERFACE_NOT_EXSIT   = new SystemError(10002,"接口不存在");
        
        public static final SystemError SIGN_ERROR   = new SystemError(10002,"签名错误");
       
        public static final SystemError PARAMETER_ERROR   = new SystemError(10003,"请求参数错误");
        
        public static final SystemError ILLEGAL_REQUEST   = new SystemError(10004,"非法请求");
        
        public static final SystemError NO_RESPSON   = new SystemError(10005,"请求接口未响应");
        
        public static final SystemError NOT_LOGIN   = new SystemError(10006,"未登录");
        
        public static final SystemError OPTIMISTIC_LOCK_UPDATE_FAIL   = new SystemError(10007,"乐观锁更新失败");
        
        public static final SystemError HTTP_ERROR  = new SystemError(10008,"网络未知错误");
      
        private SystemError(){
            super(null, null);
        }
        
        private SystemError(int index,String code, String description) {
            super(index,code,description);
        }
        
        private SystemError(int index, String description) {
            super(index, description);
        }
    }
    //业务错误码
    public static class BuzzError extends BaseType implements ErrorCode{
        
        private static final long serialVersionUID = -7588376604965702002L;
        
        //支付返回状态-->支付确认失败(20001-20199) 直接提示
        public static final BuzzError ORDERPAID = new BuzzError(20001,"ORDERPAID","该订单号已支付，如果是新单，请重新下单");
        public static final BuzzError AUTHCODEEXPIRE = new BuzzError(20002,"AUTHCODEEXPIRE","二维码已过期，请用户在微信上刷新后再试");
        public static final BuzzError NOTENOUGH = new BuzzError(20003,"NOTENOUGH","用户余额不足，提示用户换卡支付 ");
        public static final BuzzError NOTSUPORTCARD = new BuzzError(20004,"NOTSUPORTCARD","该卡不支持当前支付，提示用户换卡支付或绑新卡支付");
        public static final BuzzError ORDERCLOSED = new BuzzError(20005,"ORDERCLOSED ","商户订单号异常，请重新下单支付");
        public static final BuzzError ORDERREVERSED = new BuzzError(20006,"ORDERREVERSED ","商户订单号异常，请重新下单支付");
        public static final BuzzError AUTH_CODE_ERROR = new BuzzError(20007,"AUTH_CODE_ERROR ","每个二维码仅限使用一次，请刷新再试");
        public static final BuzzError AUTH_CODE_INVALID = new BuzzError(20008,"AUTH_CODE_INVALID ","请扫描微信支付被扫条码/二维码");
        public static final BuzzError BUYER_MISMATCH = new BuzzError(20009,"BUYER_MISMATCH ","暂不支持同一笔订单更换支付方");
        public static final BuzzError OUT_TRADE_NO_USED = new BuzzError(20010,"OUT_TRADE_NO_USED ","商户订单号重复，同一笔交易不能多次提交");
        public static final BuzzError PARAM_ERROR = new BuzzError(20011,"PARAM_ERROR ","请重新再试，如重试多次失败，请联系技术人员");
        public static final BuzzError NOAUTH = new BuzzError(20012,"NOAUTH ","请重新再试，如重试多次失败，请联系技术人员");
        public static final BuzzError XML_FORMAT_ERROR = new BuzzError(20013,"XML_FORMAT_ERROR ","请重新再试，如重试多次失败，请联系技术人员");
        public static final BuzzError REQUIRE_POST_METHOD = new BuzzError(20014,"REQUIRE_POST_METHOD ","请重新再试，如重试多次失败，请联系技术人员");
        public static final BuzzError SIGNERROR = new BuzzError(20015,"SIGNERROR ","请重新再试，如重试多次失败，请联系技术人员");
        public static final BuzzError LACK_PARAMS = new BuzzError(20016,"LACK_PARAMS ","请重新再试，如重试多次失败，请联系技术人员");
        public static final BuzzError NOT_UTF8 = new BuzzError(20017,"NOT_UTF8 ","请重新再试，如重试多次失败，请联系技术人员");
        public static final BuzzError APPID_NOT_EXIST = new BuzzError(20018,"APPID_NOT_EXIST ","请重新再试，如重试多次失败，请联系技术人员");
        public static final BuzzError MCHID_NOT_EXIST = new BuzzError(20019,"MCHID_NOT_EXIST ","请重新再试，如重试多次失败，请联系技术人员");
        public static final BuzzError APPID_MCHID_NOT_MATCH = new BuzzError(20020,"APPID_MCHID_NOT_MATCH ","请重新再试，如重试多次失败，请联系技术人员");
        //支付返回状态-->支付结果未知 请立即调用被扫订单结果查询API，查询当前订单的不同状态，决定下一步的操作。
        public static final BuzzError SYSTEMERROR = new BuzzError(20200,"SYSTEMERROR ","系统超时");
        public static final BuzzError BANKERROR = new BuzzError(20201,"BANKERROR ","银行系统异常");
        public static final BuzzError USERPAYING = new BuzzError(20202,"USERPAYING ","用户支付中，需要输入密码");















        
 
        private BuzzError(){
            super(null, null);
        }
        
        private BuzzError(int index,String code, String description) {
            super(index,code,description);
        }
        
        private BuzzError(int index, String description) {
            super(index, description);
        }
    }
   
    
   //三方接口错误码
    public static class ThirdInterfaceError extends BaseType implements ErrorCode{
        
        private static final long serialVersionUID = -7588376604965702002L;
       
        public static final ThirdInterfaceError UNKNOWN_ERROR = new ThirdInterfaceError(10000,"未知错误");
        
       
        
        
        
        private ThirdInterfaceError(){
            super(null, null);
        }
        private ThirdInterfaceError(int index,String code, String description) {
            super(index,code,description);
        }
        
        private ThirdInterfaceError(int index, String description) {
            super(index, description);
        }
    }
    public String getDescription();

    public int getIndex();
}
