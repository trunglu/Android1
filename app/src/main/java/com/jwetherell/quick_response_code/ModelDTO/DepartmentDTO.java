package com.jwetherell.quick_response_code.ModelDTO;

/**
 * Created by phamvan on 12/28/2015.
 */
public class DepartmentDTO {
    String DEP_ID, DEP_NM;
    int TotalEMP, NumberOfCheck, NumberUnCkeck;

    public int getTotalEMP() {
        return TotalEMP;
    }

    public void setTotalEMP(int totalEMP) {
        TotalEMP = totalEMP;
    }

    public int getNumberOfCheck() {
        return NumberOfCheck;
    }

    public void setNumberOfCheck(int numberOfCheck) {
        NumberOfCheck = numberOfCheck;
    }

    public int getNumberUnCkeck() {
        return NumberUnCkeck;
    }

    public void setNumberUnCkeck(int numberUnCkeck) {
        NumberUnCkeck = numberUnCkeck;
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

    public DepartmentDTO(String DEP_ID, String DEP_NM) {
        this.DEP_ID = DEP_ID;
        this.DEP_NM = DEP_NM;
    }

    public DepartmentDTO(String DEP_ID, String DEP_NM, int totalEMP, int numberOfCheck, int numberUnCkeck) {
        this.DEP_ID = DEP_ID;
        this.DEP_NM = DEP_NM;
        TotalEMP = totalEMP;
        NumberOfCheck = numberOfCheck;
        NumberUnCkeck = numberUnCkeck;
    }

    public DepartmentDTO() {
    }
}
