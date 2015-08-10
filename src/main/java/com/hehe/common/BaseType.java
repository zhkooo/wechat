package com.hehe.common;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 *  类型父类， 模拟枚举类型规避序列化的问题
 * @project BaseType
 * @date 2013-7-2
 * Copyright (C) 2010-2016 www.2caipiao.com Inc. All rights reserved.
 */
public abstract class BaseType implements  Serializable{
    private static final long serialVersionUID = -199712849987884225L;
    
	private Integer index;	 	// 顺序
	private String  description;// 描述
	
	protected BaseType(Integer index, String description,Object empty) {
		this.index = index;
		this.description = description;
	}
	
	protected BaseType(Integer index, String description) {
		this.index = index;
		this.description = description;
	}
	
	protected BaseType(){}
	
	@SuppressWarnings("unchecked")
	public static <T extends BaseType> List<T> getAll(Class<T> clazz){
		List<T> list = new ArrayList<T>();
		try{
			Field[] fieldlist = clazz.getDeclaredFields();
			for(Field field : fieldlist){
				if(field.getType().isAssignableFrom(clazz)){
					list.add((T)field.get(null));
				}
			}
		}catch(Exception e){}
		return list;
	}
	
	public static <T extends BaseType> T valueOf(Class<T> clazz, Integer index){
		try{
			List<T> list =  (List<T>) BaseType.getAll(clazz);
			for(T t: list){
				if(t.getIndex() == index){
					return t;
				}
			}
		}catch(Exception e){}
		return null;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		BaseType baseType = BaseType.valueOf(this.getClass(), index);
		if(null != baseType){
			
			this.index = baseType.getIndex();
			this.description = baseType.getDescription();
			setOtherProp(baseType);
		}
	}
	
	public void setOtherProp(BaseType baseType){
		
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((index == null) ? 0 : index.hashCode());
		return result;
	}

	/**
	 *  此方法不能删除,使类型能直接比较
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseType other = (BaseType) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (index == null) {
			if (other.index != null)
				return false;
		} else if (!index.equals(other.index))
			return false;
		return true;
	}

	public String getDescription() {
		return description;
	}

	public String toString() {
		return JSON.toJSONString(this);
	}
}	
