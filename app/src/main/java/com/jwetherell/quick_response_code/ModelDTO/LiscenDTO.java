package com.jwetherell.quick_response_code.ModelDTO;

/**
 * Created by phamvan on 8/31/2015.
 */
public class LiscenDTO {
    String Activated,LiscenCode;

    public LiscenDTO() {
    }

    public LiscenDTO(String activated, String liscenCode) {
        Activated = activated;
        LiscenCode = liscenCode;
    }

    public String getActivated() {
        return Activated;
    }

    public void setActivated(String activated) {
        Activated = activated;
    }

    public String getLiscenCode() {
        return LiscenCode;
    }

    public void setLiscenCode(String liscenCode) {
        LiscenCode = liscenCode;
    }
}
