package com.shake.app.model;

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
}
