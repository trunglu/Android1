package com.jwetherell.quick_response_code.SQLiteService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jwetherell.quick_response_code.ModelDTO.EmployeeOffline;
import com.jwetherell.quick_response_code.ModelDTO.UserSaveAccount;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by phamvan on 12/5/2015.
 */
public class EmployeeOfflineProvider {
    private SQLiteDatabase database;
    private My_SQLiteHelper dbHelper;
    private String [] allColumns= {My_SQLiteHelper.EMP_ID, My_SQLiteHelper.CHK_DT,
            My_SQLiteHelper.IMG_URI, My_SQLiteHelper.IMG_Base64,My_SQLiteHelper.REAL_DT};

    public EmployeeOfflineProvider(Context context) {
        dbHelper = new My_SQLiteHelper(context);
    }
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }
    public void close() {
        dbHelper.close();
    }

    public EmployeeOffline CreateOfflineRecord(EmployeeOffline dto) {
        try{
            String ReealDateTime = UtilMethod.dateToString(new Date(), "MM/dd/yyyy hh:mm:ss");
            open();
            ContentValues values = new ContentValues();
            values.put(My_SQLiteHelper.EMP_ID, dto.getEMP_ID());
            values.put(My_SQLiteHelper.CHK_DT, dto.getCHK_DT());
            values.put(My_SQLiteHelper.IMG_URI, dto.getIMG_URI());
            values.put(My_SQLiteHelper.IMG_Base64, dto.getIMG_Base64());
            values.put(My_SQLiteHelper.REAL_DT, ReealDateTime);
            database.insert(My_SQLiteHelper.TABLE_OFFLINEEMP, null, values);
            Cursor cursor = database.query(My_SQLiteHelper.TABLE_OFFLINEEMP,
                    allColumns, My_SQLiteHelper.EMP_ID + " = '" + dto.getEMP_ID()+"' and "
                            +My_SQLiteHelper.CHK_DT+" = '"+dto.getCHK_DT()+"'", null,
                    null, null, null);
            cursor.moveToFirst();
            EmployeeOffline newComment = cursorToComment(cursor);
            cursor.close();
            return newComment;
        }catch (Exception exx){
            Log.e("Loi_Insert", exx.toString());
        }
        return null;
    }

    public EmployeeOffline CheckExists(String EMP_ID, String CHK_DT){
        EmployeeOffline dto = new EmployeeOffline();
        try{
            open();
            Cursor cursor = database.query(My_SQLiteHelper.TABLE_OFFLINEEMP,
                    allColumns, My_SQLiteHelper.EMP_ID + " = '" + EMP_ID+"' and "
                            +My_SQLiteHelper.CHK_DT+" = '" + CHK_DT + "'", null,
                    null, null, null);
            cursor.moveToFirst();
            dto = cursorToComment(cursor);
            cursor.close();
        }catch (Exception ex){
            return null;
        }
        return  dto;
    }

    public void DeleteOfflineRecord(EmployeeOffline dto) {
        open();
        String EMP_ID = dto.getEMP_ID();
        String CHK_DT = dto.getCHK_DT();
        database.delete(My_SQLiteHelper.TABLE_OFFLINEEMP, My_SQLiteHelper.EMP_ID + " = '" + EMP_ID + "' and " +
                My_SQLiteHelper.CHK_DT + " = '" + CHK_DT + "'", null);
    }

    public List<EmployeeOffline> GetAllOfflineRecord() {
        List<EmployeeOffline> comments = new ArrayList<EmployeeOffline>();
        try {
            open();
            Cursor cursor = database.query(My_SQLiteHelper.TABLE_OFFLINEEMP, allColumns, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                EmployeeOffline comment = cursorToComment(cursor);
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

    public int EmployeeScand(){
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + My_SQLiteHelper.TABLE_OFFLINEEMP + " WHERE " + My_SQLiteHelper.IMG_URI + "='0'", new String[]{});
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        }catch (Exception ex){
            return 0;
        }
    }

    public int EmployeeScand_WithIMG(){
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + My_SQLiteHelper.TABLE_OFFLINEEMP + " WHERE " + My_SQLiteHelper.IMG_URI + "!='0'", new String[]{});
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        }catch (Exception ex){
            return 0;
        }
    }


    private EmployeeOffline cursorToComment(Cursor cursor) {
        EmployeeOffline comment = new EmployeeOffline();
        comment.setEMP_ID(cursor.getString(0));
        comment.setCHK_DT(cursor.getString(1));
        comment.setIMG_URI(cursor.getString(2));
        comment.setIMG_Base64(cursor.getString(3));
        comment.setRealDT(cursor.getString(4));
        return comment;
    }
}
