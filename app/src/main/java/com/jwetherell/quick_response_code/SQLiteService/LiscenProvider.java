package com.jwetherell.quick_response_code.SQLiteService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jwetherell.quick_response_code.ModelDTO.LiscenDTO;
import com.jwetherell.quick_response_code.ModelDTO.UserSaveAccount;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamvan on 8/31/2015.
 */
public class LiscenProvider {
    private SQLiteDatabase database;
    private My_SQLiteHelper dbHelper;
    private String[] allColumns = {My_SQLiteHelper.LISCEN_ACTIVE, My_SQLiteHelper.LISCEN_VALUE};

    public LiscenProvider(Context context) {
        dbHelper = new My_SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    public LiscenDTO CreateItem(String IsActive, String LiscenCode_) {
        open();
        ContentValues values = new ContentValues();
        values.put(My_SQLiteHelper.LISCEN_ACTIVE, IsActive);
        values.put(My_SQLiteHelper.LISCEN_VALUE, LiscenCode_);
        database.insert(My_SQLiteHelper.TABLE_LISCEN, null, values);
        Cursor cursor = database.query(My_SQLiteHelper.TABLE_LISCEN,
                allColumns, My_SQLiteHelper.LISCEN_ACTIVE + " = '" + IsActive+"'", null,
                null, null, null);
        cursor.moveToFirst();
        LiscenDTO newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
    }



    public void DeleteItem(LiscenDTO comment) {
        open();
        String id = comment.getActivated();
        System.out.println("Comment deleted with id: " + id);
        database.delete(My_SQLiteHelper.TABLE_LISCEN, My_SQLiteHelper.LISCEN_ACTIVE
                + " = '" + id+"'", null);
    }

    public List<LiscenDTO> GetAll() {
        List<LiscenDTO> comments = new ArrayList<LiscenDTO>();
        try {
            open();
            Cursor cursor = database.query(My_SQLiteHelper.TABLE_LISCEN,
                    allColumns, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                LiscenDTO comment = cursorToComment(cursor);
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
     * Note: TrungLv(2018/10/12)
     * Map cusor object to LiscenDTO
     * */
    private LiscenDTO cursorToComment(Cursor cursor) {
        LiscenDTO comment = new LiscenDTO();
        comment.setActivated(cursor.getString(0));
        comment.setLiscenCode(cursor.getString(1));
        return comment;
    }
}
