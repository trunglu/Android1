package com.jwetherell.quick_response_code.ModelDTO.APIDTO;

/**
 * Created by Pham Tri on 2/16/2017.
 */

public class PostSaveEMPPhotoDTO {
    String EMP_ID, PIC_DR;
    public static final String EMPID="EMP_ID",PICDR="PIC_DR";

    public String getEMP_ID() {
        return EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getPIC_DR() {
        return PIC_DR;
    }

    public void setPIC_DR(String PIC_DR) {
        this.PIC_DR = PIC_DR;
    }

    public PostSaveEMPPhotoDTO() {
    }

    public PostSaveEMPPhotoDTO(String EMP_ID, String PIC_DR) {
        this.EMP_ID = EMP_ID;
        this.PIC_DR = PIC_DR;
    }
}
