package com.jwetherell.quick_response_code.ModelDTO.APIDTO;

import java.util.List;

/**
 * Created by Pham Tri on 2/16/2017.
 */

public class ReturnDTO<T> {
    T Item;
    Boolean QueryResult;
    String FalseReason;
    List<String> FalseReasons;
    public static final String ITEM="Item",QUERYRESULT="QueryResult",FALSEREASON="FalseReason",FALSEREASONS="FalseReasons";

    public T getItem() {
        return Item;
    }

    public void setItem(T item) {
        Item = item;
    }

    public Boolean getQueryResult() {
        return QueryResult;
    }

    public void setQueryResult(Boolean queryResult) {
        QueryResult = queryResult;
    }

    public String getFalseReason() {
        return FalseReason;
    }

    public void setFalseReason(String falseReason) {
        FalseReason = falseReason;
    }

    public List<String> getFalseReasons() {
        return FalseReasons;
    }

    public void setFalseReasons(List<String> falseReasons) {
        FalseReasons = falseReasons;
    }

    public ReturnDTO() {
    }

    public ReturnDTO(T item, Boolean queryResult, String falseReason, List<String> falseReasons) {
        Item = item;
        QueryResult = queryResult;
        FalseReason = falseReason;
        FalseReasons = falseReasons;
    }
}
