package com.jwetherell.quick_response_code.ModelDTO;

/**
 * Created by phamvan on 7/17/2015.
 */
public class UserModel {
    String userName, password_, Mode;

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword_() {
        return password_;
    }

    public void setPassword_(String password_) {
        this.password_ = password_;
    }

    public UserModel(String userName, String password_) {
        this.userName = userName;
        this.password_ = password_;
    }

    public UserModel(String userName, String password_, String mode) {
        this.userName = userName;
        this.password_ = password_;
        Mode = mode;
    }

    public UserModel() {
        this.userName = "";
        this.password_ = "";
    }
}
