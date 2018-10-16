package com.jwetherell.quick_response_code.SQLiteService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jwetherell.quick_response_code.ModelDTO.ServerConnectModel;
import com.jwetherell.quick_response_code.ModelDTO.UserSaveAccount;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamvan on 7/30/2015.
 */
public class SQLiteServerConnect {
    private SQLiteDatabase database;
    private My_SQLiteHelper dbHelper;
    private String[] allColumns = {My_SQLiteHelper.CONFIG_ID,My_SQLiteHelper.CONFIG_IPADDRESS,My_SQLiteHelper.CONFIG_DBNAME,
            My_SQLiteHelper.CONFIG_DBUSERNAME,My_SQLiteHelper.CONFIG_DBPASSWORD,
            My_SQLiteHelper.CONFIG_ALLOWSYNCPHOTO, My_SQLiteHelper.CONFIG_API_IMG};

    public SQLiteServerConnect(Context context) {
        dbHelper = new My_SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Note:TrungLv(2018/10/12)
     * Des: Insert 1 connect vào trong DB SQLite
     * @dto param: truyền vào tham số ServerConnectModel chứa thông tin config server
     * **/
    public ServerConnectModel CreateConnect(ServerConnectModel dto) {
        open();
        ContentValues values = new ContentValues();
        values.put(My_SQLiteHelper.CONFIG_ID, "1");
        values.put(My_SQLiteHelper.CONFIG_IPADDRESS, dto.getiPAddress());
        values.put(My_SQLiteHelper.CONFIG_DBNAME, dto.getDatabaseName());
        values.put(My_SQLiteHelper.CONFIG_DBUSERNAME, dto.getUserName());
        values.put(My_SQLiteHelper.CONFIG_DBPASSWORD, dto.getPasswordConnect());
        values.put(My_SQLiteHelper.CONFIG_ALLOWSYNCPHOTO, "False");
        values.put(My_SQLiteHelper.CONFIG_API_IMG, dto.getDatabaseImgName());

        database.insert(My_SQLiteHelper.TABLE_CONFIG, null, values);
        Cursor cursor = database.query(My_SQLiteHelper.TABLE_CONFIG,
                allColumns, My_SQLiteHelper.CONFIG_ID + " = '1'", null,
                null, null, null);
        cursor.moveToFirst();
        ServerConnectModel newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
    }

    public ServerConnectModel UpdateConnect(ServerConnectModel dto) {
        open();
        ContentValues values = new ContentValues();
        values.put(My_SQLiteHelper.CONFIG_ID, "1");
        values.put(My_SQLiteHelper.CONFIG_IPADDRESS, dto.getiPAddress());
        values.put(My_SQLiteHelper.CONFIG_DBNAME, dto.getDatabaseName());
        values.put(My_SQLiteHelper.CONFIG_DBUSERNAME, dto.getUserName());
        values.put(My_SQLiteHelper.CONFIG_DBPASSWORD, dto.getPasswordConnect());
        values.put(My_SQLiteHelper.CONFIG_ALLOWSYNCPHOTO, dto.getAllowSyncPhoto());
        values.put(My_SQLiteHelper.CONFIG_API_IMG, dto.getDatabaseImgName());

        database.update(My_SQLiteHelper.TABLE_CONFIG,values,My_SQLiteHelper.CONFIG_ID+" ='1'",null);
        Cursor cursor = database.query(My_SQLiteHelper.TABLE_CONFIG,
                allColumns, My_SQLiteHelper.CONFIG_ID + " = '1'", null,
                null, null, null);
        cursor.moveToFirst();
        ServerConnectModel newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
    }

    public void DeleteConfig(ServerConnectModel comment) {
        open();
        String id = comment.getiPAddress();
        database.delete(My_SQLiteHelper.TABLE_CONFIG, My_SQLiteHelper.CONFIG_ID
                + " = '" + id+"'", null);
    }

    /***
     *  Note TrungLv(2018/10/12)
     *  Truy vấn dữ liệu connect trong SQLite
     *  Get tất cả các connection trong DB SQLite
     * */
    public List<ServerConnectModel> GetALLConnect() {
        List<ServerConnectModel> comments = new ArrayList<ServerConnectModel>();
        try {
            open();
            Cursor cursor = database.query(My_SQLiteHelper.TABLE_CONFIG,
                    allColumns, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                ServerConnectModel comment = cursorToComment(cursor);
                comments.add(comment);
                cursor.moveToNext();
            }
            // make sure to close the cursor
            cursor.close();
        } catch (Exception loadEx) {
            Log.e("ErrorLoad", loadEx.toString());
        }
        return comments;
    }

    /***
     * Note TrungLv(2018/10/12)
     *  Map raw cusor to ServerConnectModel
     * */
    private ServerConnectModel cursorToComment(Cursor cursor) {
        ServerConnectModel comment = new ServerConnectModel();
        comment.setiPAddress(cursor.getString(1));
        comment.setDatabaseName(cursor.getString(2));
        comment.setUserName(cursor.getString(3));
        comment.setPasswordConnect(cursor.getString(4));
        comment.setAllowSyncPhoto(cursor.getString(5));
        try{
            comment.setDatabaseImgName(cursor.getString(6));
        }catch (Exception ex){
            Log.e("SQLiteError", ex.getMessage());
        }
        return comment;
    }
}
