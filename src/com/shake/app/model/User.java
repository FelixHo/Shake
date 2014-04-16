package com.shake.app.model;

import android.widget.ImageView;

public class User {
	
	public String name ="";
	
	public String mobile ="";
	
	public String mail = "";
	
	public ImageView avatar ;
	
	public String profile ;
	
	public int age;
	
	public String birth ;
	
	public String homesite;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public ImageView getAvatar() {
		return avatar;
	}

	public void setAvatar(ImageView avatar) {
		this.avatar = avatar;
	}

	public String getProfile() {
		return profile;
	}

	public void setProfile(String profile) {
		this.profile = profile;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getBirth() {
		return birth;
	}

	public void setBirth(String birth) {
		this.birth = birth;
	}

	public String getHomesite() {
		return homesite;
	}

	public void setHomesite(String homesite) {
		this.homesite = homesite;
	}
}
