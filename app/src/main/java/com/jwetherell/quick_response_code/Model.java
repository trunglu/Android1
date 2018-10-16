package com.jwetherell.quick_response_code;

import android.graphics.Bitmap;

public class Model {
	String Dep_Name;
	private String name;
	private String id;
	private boolean selected;
	private String scan=null;
	private Bitmap imagebitmap=null;
	private Bitmap saveimage;
	private boolean flag =false;
	private boolean haveImage;
	public Boolean getHaveImage() {
		return haveImage;
	}
	private byte image[];
	String photoFileName;


	public String getPhotoFileName() {
		return photoFileName;
	}

	public void setPhotoFileName(String photoFileName) {
		this.photoFileName = photoFileName;
	}

	public String getDep_Name() {
		return Dep_Name;
	}

	public void setDep_Name(String dep_Name) {
		Dep_Name = dep_Name;
	}

	public void setHaveImage(Boolean haveImage) {
		this.haveImage = haveImage;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}

	String tenA;

	public String getTenA() {
		return tenA;
	}

	public void setTenA(String tenA) {
		this.tenA = tenA;
	}

	public Model() {
		this.haveImage = false;
	}
	public Model(String id, String name){
		this.id=id;
		this.name=name;
		this.haveImage = false;
	}
	public void setName(String name){
		this.name=name;
	}
	
	public void setId(String id){
		this.id=id;
	}

	public Model(String tenA) {
		this.tenA = tenA;
	}

	public String getName() {
		return this.name;
	}
	public String getID(){
		return this.id;
	}
	public void setScan(String scan){
		this.scan=scan;
	}
	public String getScan()
	{
		return this.scan;
	}
	public void setBipMap(Bitmap bitmap){
		this.imagebitmap=bitmap;
	}
	public Bitmap getBitMap(){
		return this.imagebitmap;
	}
	public void setSaveBipMap(Bitmap bitmap){
		this.saveimage=bitmap;
	}
	public Bitmap getSaveBitMap(){
		return this.saveimage;
	}
	public void setFlag(boolean values){
		this.flag=values;
	}
	public boolean getFlag(){
		return flag;
	}
}
