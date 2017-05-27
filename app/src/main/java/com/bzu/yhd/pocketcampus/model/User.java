package com.bzu.yhd.pocketcampus.model;


import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;


/**
 * 
 * TODO
 */

public class User extends BmobUser {

	public static final String TAG = "User";
	
	private String signature;
	private BmobFile file;
	private BmobRelation favorite;
	private String sex;
	private String age;
	private String star;
	private String birthday;
	private BmobGeoPoint location;
	private String area;
	private String  nickName;
	private String height;
	private String weight;
	private String note;


	public String getArea()
		{
			return area;
		}

	public void setArea(String area)
		{
			this.area = area;
		}

	public String getBirthday()
		{
			return birthday;
		}

	public void setBirthday(String birthday)
		{
			this.birthday = birthday;
		}


	public String getStar()
		{
			return star;
		}

	public void setStar(String star)
		{
			this.star = star;
		}

	public BmobFile getFile() {		
			return file;
		}

		public void setFile(BmobFile file) {
			this.file = file;
		}
	
	public BmobGeoPoint getLocation() {
		return location;
	}
	public void setLocation(BmobGeoPoint location) {
		this.location = location;
	}


	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}


	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public BmobRelation getFavorite() {
		return favorite;
	}

	public void setFavorite(BmobRelation favorite) {
		this.favorite = favorite;
	}



	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}


}
