package com.hehe.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

public class XmlUtils {
	
	 public static String getXmlFromObject( Object object) {
		 	//解决XStream对出现双下划线的bug
	        XStream xStreamForRequestPostData = new XStream(new DomDriver("UTF-8", new XmlFriendlyNameCoder("-_", "_")));
	        //将要提交给API的数据对象转换成XML格式数据Post给API
	        return  xStreamForRequestPostData.toXML(object);
	    }
    public static Object getObjectFromXML(String xml, Class<?> tClass) {
        //将从API返回的XML数据映射到Java对象
        XStream xStreamForResponseData = new XStream();
        xStreamForResponseData.alias("xml", tClass);
        xStreamForResponseData.ignoreUnknownElements();//暂时忽略掉一些新增的字段
        return xStreamForResponseData.fromXML(xml);
    }

}
