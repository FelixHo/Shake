package com.shake.app.model;

import java.util.ArrayList;

import android.graphics.Bitmap;

public class Contact {

	/**
	 * 联系人姓名
	 */
	public String name;
	
	/**
	 * 联系人联系号码
	 */
	public ArrayList<String> numbers;
	
	/**
	 * 联系人头像
	 */
	public Bitmap avatar;
	
	/**
	 * 联系人分类key
	 */
	public String sortKey;

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
}
