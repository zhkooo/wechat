package com.hehe.common;



/**
 * 根据某id/no去数据表里查询，如果查不到时(没有故障)，success为true而model为null
 * 
 */
public class ModelResult<T> extends BaseResult {
	private static final long serialVersionUID = -1731185697573008846L;

	/**
	 * 根据某id/no去数据表里查询，如果查不到时(没有故障)，success为true而model为null
	 */
	private T model;
	private boolean readFromCache = true; // 默认值不能随便改

	public ModelResult() {
	}

	public ModelResult(T model) {
		this.model = model;
	}

	/**
	 * 根据某id/no去数据表里查询，如果查不到时(没有故障)，success为true而model为null
	 */
	public T getModel() {
		return model;
	}

	public void setModel(T model) {
		this.model = model;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <SubClass extends ModelResult> SubClass withModel(T model) {
		this.model = model;
		return (SubClass) this;
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <SubClass extends ModelResult> SubClass withModelFromCache(T model) {
		this.model = model;
		return (SubClass) this;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <SubClass extends ModelResult> SubClass withModelFromDb(T model) {
		this.model = model;
		this.readFromCache = false;
		return (SubClass) this;
	}

	public boolean isReadFromCache() {
		return readFromCache;
	}

	public void setReadFromCache(boolean readFromCache) {
		this.readFromCache = readFromCache;
	}
    public <SubClass extends ModelResult> SubClass withError(ErrorCode error) {
        this.getErrorList().put(String.valueOf(error.getIndex()), error.getDescription());
        return (SubClass) this;
    }
}
