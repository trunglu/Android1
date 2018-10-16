package com.jwetherell.quick_response_code.ModelDTO;

/**
 * Created by Pham Tri on 2/15/2017.
 */

public class PostGetListEmpDTO {
    String DEP_ID;
    int PageIndex;

    public static final String DEPID = "DEP_ID", PAGEINDEX = "PageIndex";

    public String getDEP_ID() {
        return DEP_ID;
    }

    public void setDEP_ID(String DEP_ID) {
        this.DEP_ID = DEP_ID;
    }

    public int getPageIndex() {
        return PageIndex;
    }

    public void setPageIndex(int pageIndex) {
        PageIndex = pageIndex;
    }

    public PostGetListEmpDTO() {
    }

    public PostGetListEmpDTO(String DEP_ID, int pageIndex) {
        this.DEP_ID = DEP_ID;
        PageIndex = pageIndex;
    }
}
