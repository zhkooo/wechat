package com.hehe.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

import com.tencent.common.MD5;

/**
 * 业务编号统一生成器
 * 
 */
public class SerialNoBuilder {
    public static String genOperLogNo(long sequence) {
    	DecimalFormat df = new DecimalFormat("00000000"); //8位
        return DateFormatUtils.format(Calendar.getInstance(), "yyyyMMdd")+df.format(sequence) ;
    }
    /**
     * 根据给定的id和前缀生成业务编号
     * @param prefix
     * @param id
     * @return
     * @create_time 2011-6-26 下午06:01:07
     */
    public static String genNoById(String prefix, long id) {
    	DecimalFormat df = new DecimalFormat("000000000"); //9位
        return prefix +  df.format(id);
    }
    /**
     * 返回21位数字型随机数
     * 
     * @return
     */
    public static String getOrderNum() {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return df.format(new Date()) + RandomStringUtils.randomNumeric(4);
    }
    
}
