package com.jwetherell.quick_response_code.ModelDTO;

/**
 * Created by phamvan on 1/6/2016.
 */
public class AttendentDetailDTO {
    String EMP_ID, INT_DT, USER_ID, USER_NAME, MA_SCAN;

    public String getEMP_ID() {
        return EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getINT_DT() {
        return INT_DT;
    }

    public void setINT_DT(String INT_DT) {
        this.INT_DT = INT_DT;
    }

    public String getUSER_ID() {
        return USER_ID;
    }

    public void setUSER_ID(String USER_ID) {
        this.USER_ID = USER_ID;
    }

    public String getUSER_NAME() {
        return USER_NAME;
    }

    public void setUSER_NAME(String USER_NAME) {
        this.USER_NAME = USER_NAME;
    }

    public String getMA_SCAN() {
        return MA_SCAN;
    }

    public void setMA_SCAN(String MA_SCAN) {
        this.MA_SCAN = MA_SCAN;
    }

    public AttendentDetailDTO(String EMP_ID, String INT_DT, String USER_ID, String USER_NAME, String MA_SCAN) {
        this.EMP_ID = EMP_ID;
        this.INT_DT = INT_DT;
        this.USER_ID = USER_ID;
        this.USER_NAME = USER_NAME;
        this.MA_SCAN = MA_SCAN;
    }
    public AttendentDetailDTO() {
    }
}
