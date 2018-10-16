package com.jwetherell.quick_response_code.ModelDTO;

/**
 * Created by phamvan on 7/29/2015.
 */
public class UserSaveAccount {
    String userNameSave;
    String passwordSave;

    public String getUserMode() {
        return userMode;
    }

    public void setUserMode(String userMode) {
        this.userMode = userMode;
    }

    String userMode;

    public String getUserNameSave() {
        return userNameSave;
    }

    public void setUserNameSave(String userNameSave) {
        this.userNameSave = userNameSave;
    }

    public String getPasswordSave() {
        return passwordSave;
    }

    public void setPasswordSave(String passwordSave) {
        this.passwordSave = passwordSave;
    }

    public UserSaveAccount(String userNameSave, String passwordSave, String userMode) {
        this.userNameSave = userNameSave;
        this.passwordSave = passwordSave;
        this.userMode=userMode;
    }

    public UserSaveAccount(String userNameSave, String passwordSave) {
        this.userNameSave = userNameSave;
        this.passwordSave = passwordSave;
    }

    public UserSaveAccount() {
        this.userNameSave="";
        this.passwordSave="";
        this.userMode="";
    }
}
