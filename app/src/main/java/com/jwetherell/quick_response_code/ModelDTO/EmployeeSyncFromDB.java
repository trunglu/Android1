package com.jwetherell.quick_response_code.ModelDTO;

/**
 * Created by phamvan on 12/28/2015.
 */
public class EmployeeSyncFromDB {
    String EMP_ID, EMP_NM, DEP_ID, DEP_NM, HAVE_IMG, ISCheck;

    public String getISCheck() { return ISCheck; }

    public void setISCheck(String ISCheck) { this.ISCheck = ISCheck; }

    public String getEMP_ID() {
        return EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getEMP_NM() {
        return EMP_NM;
    }

    public void setEMP_NM(String EMP_NM) {
        this.EMP_NM = EMP_NM;
    }

    public String getDEP_ID() {
        return DEP_ID;
    }

    public void setDEP_ID(String DEP_ID) {
        this.DEP_ID = DEP_ID;
    }

    public String getDEP_NM() {
        return DEP_NM;
    }

    public void setDEP_NM(String DEP_NM) {
        this.DEP_NM = DEP_NM;
    }

    public String getHAVE_IMG() {
        return HAVE_IMG;
    }

    public void setHAVE_IMG(String HAVE_IMG) {
        this.HAVE_IMG = HAVE_IMG;
    }

    public EmployeeSyncFromDB(String EMP_ID, String EMP_NM, String DEP_ID, String DEP_NM, String HAVE_IMG, String ISCheck) {
        this.EMP_ID = EMP_ID;
        this.EMP_NM = EMP_NM;
        this.DEP_ID = DEP_ID;
        this.DEP_NM = DEP_NM;
        this.HAVE_IMG = HAVE_IMG;
        this.ISCheck = ISCheck;
    }

    public EmployeeSyncFromDB() {
    }
}
