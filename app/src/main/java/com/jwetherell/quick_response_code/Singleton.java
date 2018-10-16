package com.jwetherell.quick_response_code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Singleton {

	private static Singleton mInstance = null;
	public int weight, height;
    private String serverName ="115.78.227.56:1433";
    String dbName="GP_VWS";
    String ServerAPIURL="";
    String userName="sa";
    String password="glinton99";

    public int configed =1;
    public int language=1;
    public int tagCheckout;
    String depViewing="";
    String idscan;
    String dep;
    int toast=-1;
    String EMP_ID;
    boolean booldn=false;
    String RoleName="";
    String User_name="";
    boolean flag =false;
    String lange ="";
    String depID="";
    String ServerURL="http://115.78.227.56:1000/TestHRM/api/";


    public String getServerURL() {
        return ServerURL;
    }

    public void setServerURL(String serverURL) {
        ServerURL = serverURL;
    }

    Map<String, String> config = new HashMap<String, String>();
    ArrayList<String>  empIDArr,nameArr, check1Arr, check2Arr, recheckOn1Arr;
    private Singleton(){
       // mString = "Hello";
    }
	
	public static Singleton getInstance(){
        if(mInstance == null)
        {
            mInstance = new Singleton();
        }
        return mInstance;
    }
	public Map<String, String> setConfigInfo(Map<String , String> dict){
		
		config=dict;
		return config;
	}

    public String getDepID() {
        return depID;
    }

    public void setDepID(String depID) {
        this.depID = depID;
    }

    public String getRoleName() {
        return RoleName;
    }

    public void setRoleName(String roleName) {
        RoleName = roleName;
    }

    public Map<String, String> getConfigInfo() {
		return config;
	}
	
	public void setConfiged(int aValue){
    	configed = aValue;
    }
    public int getConfiged(){
    	return configed;
    }
    
    public void setLanguage1(String languageValue){
    	lange = languageValue;
    }
    public String getLanguage1(){
    	return lange;
    }
    
    public void setTagCheckOut(int aValue){
    	tagCheckout = aValue;
    }
    public int getTagCheckOut(){
    	return tagCheckout;
    }
    public ArrayList<String> setEmpIDArr( ArrayList<String> aArrayList ){
    	empIDArr = aArrayList;
    	return empIDArr;
    	
    }
    public ArrayList<String> getEmpIDArr(){
    	return empIDArr;
    }
    public ArrayList<String> setnameArr( ArrayList<String> aArrayList ){
    	nameArr = aArrayList;
    	return nameArr;
    	
    }
    
    public ArrayList<String> getnameArr(){
    	return nameArr;
    }
    
    public void  setDepViewing( String aValue){
    	depViewing = aValue;
    }
    public String  getDepViewing(){
    	return depViewing;
    }
    public void  setIdscan( String aValue){
    	idscan = aValue;
    }
    public String  getIdscan(){
    	return idscan;
    }
    
    public void  setToast( int aValue){
    	toast = aValue;
    }
    public int  getToast(){
    	return toast;
    }
    public void setEmpId(String avalue)
    {
    	this.EMP_ID = avalue;
    }
    public String getEmpId()
    {
    	return EMP_ID;
    }
    
    public void setUser(String user)
    {
    	this.userName=user;
    }
    public String getUser(){
    	return userName;
    }
    public void setServer(String server)
    {
    	this.serverName=server;
    }
    public String getServer(){
    	return serverName;
    }
    public void setDatabase(String database)
    {
    	this.dbName=database;
    }
    public String getDatabase(){
    	return dbName;
    }
    public void setPassWord(String pass)
    {
    	this.password=pass;
    }
    public String getPassWord(){
    	return password;
    }
    public void setUserName(String username){
    	this.User_name=username;
    }
    public String getUserName(){
    	return User_name;
    }
    public void setBooldn(boolean values){
    	this.booldn=values;
    }
    public boolean getBooldn()
    {
    	return this.booldn;
    }
    public boolean getFlag()
    {
    	return flag;
    }
    public void setFlag(boolean values){
    	this.flag=values;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getServerAPIURL() {
        return ServerAPIURL;
    }

    public void setServerAPIURL(String dbPhoto) {
        ServerAPIURL = dbPhoto;
    }
}
