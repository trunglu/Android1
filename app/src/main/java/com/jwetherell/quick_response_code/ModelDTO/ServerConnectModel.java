package com.jwetherell.quick_response_code.ModelDTO;

/**
 * Created by phamvan on 7/30/2015.
 */
public class ServerConnectModel {
    String iPAddress, databaseName, userName, passwordConnect, allowSyncPhoto;
    String databaseImgName, tableImg;

    public String getAllowSyncPhoto() {
        return allowSyncPhoto;
    }

    public void setAllowSyncPhoto(String allowSyncPhoto) {
        this.allowSyncPhoto = allowSyncPhoto;
    }

    public String getiPAddress() {
        return iPAddress;
    }

    public void setiPAddress(String iPAddress) {
        this.iPAddress = iPAddress;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswordConnect() {
        return passwordConnect;
    }

    public void setPasswordConnect(String passwordConnect) {
        this.passwordConnect = passwordConnect;
    }

    public String getDatabaseImgName() {
        return databaseImgName;
    }

    public void setDatabaseImgName(String databaseImgName) {
        this.databaseImgName = databaseImgName;
    }

    public String getTableImg() {
        return tableImg;
    }

    public void setTableImg(String tableImg) {
        this.tableImg = tableImg;
    }

    public ServerConnectModel() {
    }


    public ServerConnectModel(String iPAddress, String databaseName, String userName, String passwordConnect, String allowSyncPhoto) {
        this.iPAddress = iPAddress;
        this.databaseName = databaseName;
        this.userName = userName;
        this.passwordConnect = passwordConnect;
        this.allowSyncPhoto = allowSyncPhoto;
    }

    public ServerConnectModel(String iPAddress, String databaseName, String userName, String passwordConnect) {
        this.iPAddress = iPAddress;
        this.databaseName = databaseName;
        this.userName = userName;
        this.passwordConnect = passwordConnect;
    }
}
