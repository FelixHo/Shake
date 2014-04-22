package com.shake.app.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyDateUtils {

		private static SimpleDateFormat sf;
        
		/**
		 * 获取当前时间的日期格式
		 * @param format null默认 yyyy-MM-dd-HH-mm-ss
		 * @return
		 */
	    public static String getCurrentDate(String format) {
	        Date d = new Date();
	        if(format!=null)
	        {
	        	sf = new SimpleDateFormat(format);
	        }
	        else
	        {
	        	sf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	        }
	        return sf.format(d);
	    }
	                                      
	    /**
	     *  时间戳转换成字符窜
	     * @param time
	     * @param format null默认 yyyy-MM-dd-HH-mm-ss
	     * @return
	     */
	    public static String getDateToString(long time,String format) {
	        Date d = new Date(time);
	        if(format!=null)
	        {
	        	sf = new SimpleDateFormat(format);
	        }
	        else
	        {
	        	sf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	        }
	        return sf.format(d);
	    }
	                                      
	    /**
	     * 将字符串转为时间戳
	     * @param time
	     * @param format null 默认 yyyy-MM-dd-HH-mm-ss
	     * @return
	     */
	    public static long getStringToDate(String time,String format) {
	    	 if(format!=null)
		        {
		        	sf = new SimpleDateFormat(format);
		        }
		        else
		        {
		        	sf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		        }
	        Date date = new Date();
	        try{
	            date = sf.parse(time);
	        } catch(ParseException e) {
	            e.printStackTrace();
	        }
	        return date.getTime();
	    }
}
