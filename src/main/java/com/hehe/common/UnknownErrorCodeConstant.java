package com.hehe.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class UnknownErrorCodeConstant {
	@Deprecated
	public final static int errCanRetry = 1;
	@Deprecated
	public final static int errRetryAfterBizCheck = 2;
	@Deprecated
	public final static int errNeedTechCheck = 3;

	public final static String exceptionCanRetry = "exceptionCanRetry";
	public final static String exceptionRetryAfterBizCheck = "exceptionRetryAfterBizCheck";
	public final static String exceptionNeedTechCheck = "exceptionNeedTechCheck";

	@Deprecated
	protected final static Map<Integer, String> msgMap = new ConcurrentHashMap<Integer, String>();

	private final static Map<String, String> errorMap = new ConcurrentHashMap<String, String>();

	static {
		errorMap.put(exceptionCanRetry, "遇到意料之外错误，如未达到操作要求，可再重试");
		errorMap.put(exceptionRetryAfterBizCheck, "遇到意料之外错误，请检查一下业务数据，没大问题可再重试");
		errorMap.put(exceptionNeedTechCheck, "遇到错误，请打开详细消息复制/截屏后告知客服检查");// 客服意思即是客服和开发人员

		errorMap.put(errCanRetry + "", "遇到意料之外错误，如未达到操作要求，可再重试");
		errorMap.put(errRetryAfterBizCheck + "", "遇到意料之外错误，请检查一下业务数据，没大问题可再重试");
		errorMap.put(errNeedTechCheck + "", "遇到错误，请打开详细消息复制/截屏后告知客服检查");// 客服意思即是客服和开发人员
	}
	
	public static void putMsg(String errorCode, String msg) {
		errorMap.put(errorCode, msg);
	}

	@Deprecated
	public static String getMsg(int errorCode) {
		String errMsg = errorMap.get(errorCode + "");
		return errMsg;
	}

	public static String getMsg(String errorCode) {
		String errMsg = errorMap.get(errorCode);
		return errMsg;
	}

	/**
	 * Deprecated:在新result设计下，真的需要这功能吗
	 * 
	 * @return
	 */
	@Deprecated
	public static String msgCanRetry() {
		return getMsg(exceptionCanRetry);
	}

	/**
	 * Deprecated:在新result设计下，真的需要这功能吗
	 * 
	 * @return
	 */
	@Deprecated
	public static String msgNeedTechCheck() {
		return getMsg(exceptionNeedTechCheck);
	}

	/**
	 * Deprecated:在新result设计下，真的需要这功能吗
	 * 
	 * @return
	 */
	@Deprecated
	public static String msgRetryAfterBizCheck() {
		return getMsg(exceptionRetryAfterBizCheck);
	}
}
