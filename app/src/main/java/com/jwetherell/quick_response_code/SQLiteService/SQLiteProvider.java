package com.jwetherell.quick_response_code.SQLiteService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jwetherell.quick_response_code.ModelDTO.UserSaveAccount;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamvan on 7/29/2015.
 */
public class SQLiteProvider {
    // Database fields
    private SQLiteDatabase database;
    private My_SQLiteHelper dbHelper;
    private String[] allColumns = {My_SQLiteHelper.COLUMN_USERID, My_SQLiteHelper.COLUMN_USERPASS, My_SQLiteHelper.COLUMN_USERMODE};
//    private  Context context_;

    public SQLiteProvider(Context context) {
        dbHelper = new My_SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public UserSaveAccount CreateUserSave(String UserID, String UPass, String UMode) {
        open();
        ContentValues values = new ContentValues();
        values.put(My_SQLiteHelper.COLUMN_USERID, UserID);
        values.put(My_SQLiteHelper.COLUMN_USERPASS, UPass);
        values.put(My_SQLiteHelper.COLUMN_USERMODE, UMode);
        database.insert(My_SQLiteHelper.TABLE_COMMENTS, null, values);
        Cursor cursor = database.query(My_SQLiteHelper.TABLE_COMMENTS,
                allColumns, My_SQLiteHelper.COLUMN_USERID + " = '" + UserID+"'", null,
                null, null, null);
        cursor.moveToFirst();
        UserSaveAccount newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
    }

    public UserSaveAccount UpdateUserSave(String UserID, String UPass,String UMode) {
        open();
        ContentValues values = new ContentValues();
        values.put(My_SQLiteHelper.COLUMN_USERID, UserID);
        values.put(My_SQLiteHelper.COLUMN_USERPASS, UPass);
        values.put(My_SQLiteHelper.COLUMN_USERMODE, UMode);
        database.update(My_SQLiteHelper.TABLE_COMMENTS,values,My_SQLiteHelper.COLUMN_USERID+" ='"+UserID+"'",null);
        Cursor cursor = database.query(My_SQLiteHelper.TABLE_COMMENTS,
                allColumns, My_SQLiteHelper.COLUMN_USERID + " = '" + UserID+"'", null,
                null, null, null);
        cursor.moveToFirst();
        UserSaveAccount newComment = cursorToComment(cursor);
        cursor.close();
        return newComment;
    }

    public void DeleteUserSave(UserSaveAccount comment) {
        open();
        String id = comment.getUserNameSave();
        System.out.println("Comment deleted with id: " + id);
        database.delete(My_SQLiteHelper.TABLE_COMMENTS, My_SQLiteHelper.COLUMN_USERID
                + " = '" + id+"'", null);
    }

    public List<UserSaveAccount> GetAllUserSave() {
        List<UserSaveAccount> comments = new ArrayList<UserSaveAccount>();
        try {
            open();
            Cursor cursor = database.query(My_SQLiteHelper.TABLE_COMMENTS,
                    allColumns, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                UserSaveAccount comment = cursorToComment(cursor);
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

    private UserSaveAccount cursorToComment(Cursor cursor) {
        UserSaveAccount comment = new UserSaveAccount();
        comment.setUserNameSave(cursor.getString(0));
        comment.setPasswordSave(cursor.getString(1));
        comment.setUserMode(cursor.getString(2));
        return comment;
    }

}
