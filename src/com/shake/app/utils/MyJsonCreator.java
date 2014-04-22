package com.shake.app.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class MyJsonCreator {

	/**
	 * 生成发送给服务器的json数据
	 * @param type 1.名片 2.联系人 3.我的图库 4.音乐
	 * @param action 1.发送  2.接收 3.传输 4.取消
	 * @param data 
	 * @param target
	 * @return
	 */
	public static String createJsonToServer(String type, String action,String data,String target)
	{
		JSONObject jso = new JSONObject();
		try {	
		if(type==null)
		{
			
			jso.put("type", "");
			
		}
		else
		{
			jso.put("type",type);
		}
		
		if(action==null)
		{
			jso.put("action","");
		}
		else
		{
			jso.put("action",action);
		}
		
		if(data==null)
		{
			jso.put("data","");
		}
		else
		{
			jso.put("data",data);
		}
		
		if(target==null)
		{
			jso.put("target", "");
		}
		else
		{
			jso.put("target",target);
		}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return  jso.toString();
	}
}
