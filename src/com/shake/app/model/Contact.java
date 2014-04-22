package com.shake.app.model;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.shake.app.utils.FileUtil;

import android.graphics.Bitmap;

public class Contact {

	/**
	 * 联系人姓名
	 */
	public String name="";
	
	/**
	 * 联系人联系号码
	 */
	public ArrayList<String> numbers=null;
	
	/**
	 * 联系人头像
	 */
	public Bitmap avatar=null;
	
	/**
	 * 联系人分类key
	 */
	public String sortKey="";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Bitmap getAvatar() {
		return avatar;
	}

	public void setAvatar(Bitmap avatar) {
		this.avatar = avatar;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public ArrayList<String> getNumbers() {
		return numbers;
	}

	public void setNumbers(ArrayList<String> numbers) {
		this.numbers = numbers;
	}
	
	public String getJsonString()
	{
		JSONObject jso = new JSONObject();
		try 
		{
			jso.put("name", this.name);
		
		if(this.numbers!=null)
		{
			JSONArray jsa = new JSONArray(numbers);
			jso.put("numbers", jsa);
		}
		else
		{
			jso.put("numbers", "");
		}
		if(this.avatar!=null)
		{
			jso.put("avatar", FileUtil.bitmapToBase64(this.avatar));
		}
		else
		{
			jso.put("avatar","");
		}
		jso.put("sortkey",this.sortKey);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jso.toString();
	}
}
