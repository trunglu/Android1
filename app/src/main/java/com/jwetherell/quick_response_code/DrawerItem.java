package com.jwetherell.quick_response_code;

public class DrawerItem {

    String ItemName;
    int imgResID;
    String ItemID;

    public DrawerItem(String itemName) {
        super();
        ItemName = itemName;
        //this.imgResID = imgResID;
    }

    public DrawerItem(String itemName, String itemID) {
        ItemName = itemName;
        ItemID = itemID;
    }

    public String getItemID() { return ItemID; }

    public void setItemID(String itemID) { ItemID = itemID; }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public void setImgResID(int imgResID) {
        this.imgResID = imgResID;
    }

    public int getImgResID() {
        // TODO Auto-generated method stub
        return imgResID;
    }
}
