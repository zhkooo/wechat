package com.hehe.common;



public interface ErrorCode  {

    
    //系统错误码
    public static class SystemError extends BaseType implements ErrorCode{
        
        private static final long serialVersionUID = -7588376604965702002L;
       
        public static final SystemError UNKNOWN_ERROR = new SystemError(10001,"您的网络忙，请稍后再试");
       
        public static final SystemError INTERFACE_NOT_EXSIT   = new SystemError(10002,"接口不存在");
        
        public static final SystemError SIGN_ERROR   = new SystemError(10002,"签名错误");
       
        public static final SystemError PARAMETER_ERROR   = new SystemError(10003,"参数错误");
        
        public static final SystemError ILLEGAL_REQUEST   = new SystemError(10004,"非法请求");
        
        public static final SystemError INTREFACE_DISCARD   = new SystemError(10005,"接口已经废弃");
        
        public static final SystemError NOT_LOGIN   = new SystemError(10006,"未登录");
        
        public static final SystemError OPTIMISTIC_LOCK_UPDATE_FAIL   = new SystemError(10007,"乐观锁更新失败");
        
        public static final SystemError HTTP_ERROR  = new SystemError(10008,"网络未知错误");
      
        private SystemError(){
            super(null, null);
        }
        
        private SystemError(int index, String description) {
            super(index, description);
        }
    }
    //业务错误码
    public static class BuzzError extends BaseType implements ErrorCode{
        
        private static final long serialVersionUID = -7588376604965702002L;
        
        //用户相关的错误中间两位固定01
        public static final BuzzError USER_EXIST = new BuzzError(20101,"用户已存在");
 
        
 
        private BuzzError(){
            super(null, null);
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
        
        private ThirdInterfaceError(int index, String description) {
            super(index, description);
        }
    }
    public String getDescription();

    public int getIndex();
}
