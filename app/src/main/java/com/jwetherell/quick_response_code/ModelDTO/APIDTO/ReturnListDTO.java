package com.jwetherell.quick_response_code.ModelDTO.APIDTO;

/**
 * Created by Pham Tri on 2/16/2017.
 */

public class ReturnListDTO<T> {
    T Items;
    int TotalItem;
    String Message;

    public static final String ITEMS = "Items", TOTALITEM = "TotalItem", MESSAGE = "Message";

    public T getItems() {
        return Items;
    }

    public void setItems(T items) {
        Items = items;
    }

    public int getTotalItem() {
        return TotalItem;
    }

    public void setTotalItem(int totalItem) {
        TotalItem = totalItem;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public ReturnListDTO() {
    }

    public ReturnListDTO(T items, int totalItem, String message) {
        Items = items;
        TotalItem = totalItem;
        Message = message;
    }
}
