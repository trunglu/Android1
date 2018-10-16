package com.jwetherell.quick_response_code.ModelDTO;

/**
 * Created by phamvan on 12/5/2015.
 */
public class EmployeeOffline {
    String EMP_ID, IMG_URI, IMG_Base64, CHK_DT, RealDT;


    public String getRealDT() {
        return RealDT;
    }

    public void setRealDT(String realDT) {
        RealDT = realDT;
    }

    public String getCHK_DT() {
        return CHK_DT;
    }

    public void setCHK_DT(String CHK_DT) {
        this.CHK_DT = CHK_DT;
    }

    public String getEMP_ID() {
        return EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getIMG_URI() {
        return IMG_URI;
    }

    public void setIMG_URI(String IMG_URI) {
        this.IMG_URI = IMG_URI;
    }

    public String getIMG_Base64() {
        return IMG_Base64;
    }

    public void setIMG_Base64(String IMG_Base64) {
        this.IMG_Base64 = IMG_Base64;
    }

    public EmployeeOffline(String EMP_ID, String IMG_URI, String IMG_Base64, String CHK_DT) {
        this.EMP_ID = EMP_ID;
        this.IMG_URI = IMG_URI;
        this.IMG_Base64 = IMG_Base64;
        this.CHK_DT = CHK_DT;
    }

    public EmployeeOffline() {
    }
}
