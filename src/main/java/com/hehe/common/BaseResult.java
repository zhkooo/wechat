package com.hehe.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

@SuppressWarnings("unchecked")
public class BaseResult implements Serializable {
	private static final long serialVersionUID = 1719385495455011753L;

	private transient Logger log = LoggerFactory.getLogger(getClass());

	private transient boolean loggedException = false;

	private transient boolean loggedWarn = false;

	public BaseResult() {
		loggedException = false;
		loggedWarn = false;
	}
	

	/**
	 * 给终端用户的错误提示
	 */
	private Map<String, String> errorList = new HashMap<String, String>();

	/**
	 * 给其他模块开发者的提示信息
	 */
	private Collection<String> warnList = new HashSet<String>();

	/**
	 * 出错时的重要输入参数
	 */
	private String inputParamWhereCatch;

	/**
	 * 异常时的堆栈，可网络两端都log，加大异常的知晓机会
	 */
	private String detailStack;

	/**
	 * 为false则表示业务请求没通过或发生系统异常。
	 * 为true则表示肯定没发生系统异常，但业务请求的成功与否还看具体接口定义/javadoc说明，或更多的字段
	 * 
	 * @return
	 */
	public boolean isSuccess() {
		if (!loggedException && detailStack != null) {
			log.error("{inputParamWhereCatch:'" + inputParamWhereCatch + "'}\r\n" + detailStack);
			loggedException = true;
		}
		if (!loggedWarn && !warnList.isEmpty()) {
			log.warn("{warnList:'" + warnList.toString() + "'}");
			loggedWarn = true;
		}
		boolean isEmpty = errorList.isEmpty();
		if (!isEmpty) {
			for (Map.Entry<String, String> entry : errorList.entrySet()) {
				String msg = UnknownErrorCodeConstant.getMsg(entry.getKey());
				if (msg != null) {
					entry.setValue(msg);
				}
			}
		}
		return isEmpty;
	}
	
	/**
	 * 非测试调试状态下(线上)少用这个，线上可以不log业务主动发现的问题，如密码不对
	 * @return
	 */
	public boolean isSuccessAndLogError() {
		boolean success = isSuccess();
		if(!success){
			log.error("{errorList:'" + errorList.toString() + "'}");
		}
		return success;
	}

	/**
	 * 先判断isSuccess()才能用，多重错误时可转用getErrorList()
	 * 
	 * @return
	 */
	public String getErrorMsg() {
		if (errorList.isEmpty()) {// toString()/toJson()/beanCopy等调用getter的next()会抛异常
			return "";
		}
		return errorList.values().iterator().next();
	}

	/**
	 * 不适合时，可转用getErrorList()
	 * @param seperator
	 * @param displayCount
	 * @return
	 */
	public String getErrorMsg(String seperator, int displayCount) {
		int count = 0;
		StringBuilder sb = new StringBuilder(60);
		for (String msg : errorList.values()) {
			sb.append(msg).append(seperator);
			count++;
			if (count > displayCount) {
				sb.append("还有" + (errorList.size() - displayCount) + "个错误信息未显示" + seperator);
				break;
			}
		}
		sb.delete(sb.length() - seperator.length(), sb.length());
		return sb.toString();
	}

	/**
	 * 消息本地化可看 章节“消息本地化” http://wiki.inzwc.com/wiki/index.php/AicaiAppModel#.E6.B6.88.E6.81.AF.E6.9C.AC.E5.9C.B0.E5.8C.96
	 * @return
	 */
	public Map<String, String> getErrorList() {
		return errorList;
	}

	public void setErrorList(Map<String, String> errorList) {
		this.errorList = errorList;
	}

	@Deprecated
	public Map<String, String> toLocalErrorList(Map<String, String> localMsgMap) {
		for (Map.Entry<String, String> entry : errorList.entrySet()) {
			String localMsg = localMsgMap.get(entry.getKey());
			if (localMsg != null) {
				entry.setValue(localMsg);
			}
		}
		return errorList;
	}

	/**
	 * 先判断isSuccess()才能用。
	 * 
	 * @return
	 */
	public String getErrorCode() {
		if (errorList.isEmpty()) {// 但有时toString()是直接调用，遇到没东西，next()就抛异常了
			return null;
		}
		return errorList.keySet().iterator().next();
	}

	public String getDetailStack() {
		return detailStack;
	}

	public void setDetailStack(String detailStack) {
		this.detailStack = detailStack;
	}

	public String getInputParamWhereCatch() {
		return inputParamWhereCatch;
	}

	public void setInputParamWhereCatch(String inputParamWhereCatch) {
		this.inputParamWhereCatch = inputParamWhereCatch;
	}

	/**
	 * 如果log出重要输入参数能有助事情的话，请选用带inputParamWhereCatch的方法
	 * @param errorCode 定义规范  errorCode 英文，不超过50长度，以“点”分隔，：“粗模块.细模块.错误意思"
	 * errorCode规范详见 http://wiki.inzwc.com/wiki/index.php/AicaiAppModel#errorCode.E6.A0.BC.E5.BC.8F
	 * @param errorMsg
	 */
	public <SubClass extends BaseResult> SubClass withError(String errorCode, String errorMsg) {
		errorList.put(errorCode, errorMsg);
		return (SubClass) this;
	}
	/**
	 * @param errorCode 定义规范  errorCode 英文，不超过50长度，以“点”分隔，：“粗模块.细模块.错误意思"
	 * errorCode规范详见 http://wiki.inzwc.com/wiki/index.php/AicaiAppModel#errorCode.E6.A0.BC.E5.BC.8F
	 * @param errorMsg
	 */
	public <SubClass extends BaseResult> SubClass withError(String errorCode) {
		String msg = UnknownErrorCodeConstant.getMsg(errorCode);
		if (msg == null) {
			msg = "遇到错误[" + errorCode + "]，请截屏后告知客服检查或自行重试";
		}
		errorList.put(errorCode, msg);
		return (SubClass) this;
	}
	/**
	 * @param errorCode 定义规范  errorCode 英文，不超过50长度，以“点”分隔，：“粗模块.细模块.错误意思"
	 * @see http://wiki.inzwc.com/wiki/index.php/AicaiAppModel#errorCode.E6.A0.BC.E5.BC.8F
	 * @param errorMsg
	 */
	public <SubClass extends BaseResult> SubClass withError(String errorCode, String errorMsg, String inputParamWhereCatch) {
		errorList.put(errorCode, errorMsg);
		this.inputParamWhereCatch = inputParamWhereCatch;
		return (SubClass) this;
	}

	/**
	 * 只会包含convert错误信息过去，所以命名toErrorResult()
	 * 
	 * @param result
	 * @return
	 */
	public <SubClass extends BaseResult> SubClass toErrorResult(SubClass result) {
		result.setErrorList(errorList);
		result.setWarnList(warnList);
		result.setInputParamWhereCatch(inputParamWhereCatch);
		result.setDetailStack(detailStack);
		return result;
	}

	public <SubClass extends BaseResult> SubClass withErrorAndLog(String inputParamWhereCatch, Throwable e) {
		return withErrorAndLog(UnknownErrorCodeConstant.exceptionCanRetry, UnknownErrorCodeConstant.getMsg(UnknownErrorCodeConstant.exceptionCanRetry), inputParamWhereCatch, e);
	}
	/**
	 * @param errorCode 定义规范  errorCode 英文，不超过50长度，以“点”分隔，：“粗模块.细模块.错误意思"
	 * errorCode规范详见 http://wiki.inzwc.com/wiki/index.php/AicaiAppModel#errorCode.E6.A0.BC.E5.BC.8F
	 * @param errorMsg
	 */
	public <SubClass extends BaseResult> SubClass withErrorAndLog(String errorCode, String inputParamWhereCatch, Throwable e) {
		return withErrorAndLog(errorCode, UnknownErrorCodeConstant.getMsg(errorCode), inputParamWhereCatch, e);
	}
	/**
	 * @param errorCode 定义规范  errorCode 英文，不超过50长度，以“点”分隔，：“粗模块.细模块.错误意思"
	 * errorCode规范详见 http://wiki.inzwc.com/wiki/index.php/AicaiAppModel#errorCode.E6.A0.BC.E5.BC.8F
	 * @param errorMsg
	 */
	public <SubClass extends BaseResult> SubClass withErrorAndLog(String errorCode, String errorMsg, String inputParamWhereCatch, Throwable e) {
		StringBuffer  sb = new StringBuffer(512);
		sb.append("{errorCode:'").append(errorCode)
				.append("', errorMsg:'").append(errorMsg)
				.append("', inputParamWhereCatch:'").append(inputParamWhereCatch).append("'}");
		log.error(sb.toString(), e);
		withError(errorCode, errorMsg, inputParamWhereCatch);

		for (StackTraceElement line : e.getStackTrace()) {
			sb.append("\tat ").append(line);
		}
		this.detailStack = sb.toString();
		return (SubClass) this;
	}

	@Deprecated
	/**
	 * deprecated是因为errorCode的类型改为string了
	 * @param errorCode
	 * @param errorMsg
	 * @param inputParamWhereCatch
	 */
	public <SubClass extends BaseResult> SubClass withError(int errorCode, String errorMsg, String inputParamWhereCatch) {
		return this.withError(errorCode + "", errorMsg, inputParamWhereCatch);
	}

	public <SubClass extends BaseResult> SubClass addWarn(String msg) {
		warnList.add(msg);
		return (SubClass) this;
	}

	public Collection<String> getWarnList() {
		return warnList;
	}

	public void setWarnList(Collection<String> warnList) {
		this.warnList = warnList;
	}

	public String toString() {
		return JSON.toJSONString(this);
	}
}
