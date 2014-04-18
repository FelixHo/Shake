package com.shake.app.model;


public class Song {

	/**
	 * 歌手名
	 */
	public String artist; 
	/**
	 * 歌曲名
	 */
	public String name;
	/**
	 * 歌曲时间长度
	 */
	public String time;
	/**
	 * 歌曲路径
	 */
	public String url;
	/**
	 * 歌曲大小
	 */
	public String size;

	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

}
