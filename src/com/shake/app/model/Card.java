package com.shake.app.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.shake.app.utils.FileUtil;

/**
 * 名片bean
 * @author Felix
 */
public class Card {

	public String name = "";
	
	public String profile = "";//职业信息
	
	public String mobile = "";
	
	public String mail = "";
	
	public String birthday = "";
	
	public String homelink = "";//主页
	
	public String avatar ="";//头像保存路径
	
	public int    id;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getHomelink() {
		return homelink;
	}

	public void setHomelink(String homelink) {
		this.homelink = homelink;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public String getJsonString()
	{
		JSONObject jso = new JSONObject();
		
		try 
		{
			jso.put("name", this.name);
			jso.put("profile", this.profile);
			jso.put("mobile",this.mobile);
			jso.put("mail", this.mail);
			jso.put("birthday", this.birthday);
			jso.put("homelink", this.homelink);
		
		if(this.avatar.equals(""))
		{
			jso.put("avatar","");
		}
		else
		{
			String data = FileUtil.fileToBase64(this.avatar);
			jso.put("avatar",data);
		}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jso.toString();
	}
}
