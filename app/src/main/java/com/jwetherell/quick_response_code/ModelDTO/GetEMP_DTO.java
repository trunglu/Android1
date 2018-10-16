package com.jwetherell.quick_response_code.ModelDTO;

/**
 * Created by Pham Tri on 2/15/2017.
 */

public class GetEMP_DTO {
    String EMP_ID, EMP_NM, DEP_ID, DEP_NM, MA_SCAN, PIC_DR;

    public static final String EMPID = "EMP_ID", EMPNM = "EMP_NM", DEPID = "DEP_ID",
            DEPNM = "DEP_NM", MASCAN = "MA_SCAN", PICDR = "PIC_DR";

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

    public String getMA_SCAN() {
        return MA_SCAN;
    }

    public void setMA_SCAN(String MA_SCAN) {
        this.MA_SCAN = MA_SCAN;
    }

    public String getPIC_DR() {
        return PIC_DR;
    }

    public void setPIC_DR(String PIC_DR) {
        this.PIC_DR = PIC_DR;
    }

    public GetEMP_DTO() {
    }

    public GetEMP_DTO(String EMP_ID, String EMP_NM, String DEP_ID, String DEP_NM, String MA_SCAN, String PIC_DR) {
        this.EMP_ID = EMP_ID;
        this.EMP_NM = EMP_NM;
        this.DEP_ID = DEP_ID;
        this.DEP_NM = DEP_NM;
        this.MA_SCAN = MA_SCAN;
        this.PIC_DR = PIC_DR;
    }
}
