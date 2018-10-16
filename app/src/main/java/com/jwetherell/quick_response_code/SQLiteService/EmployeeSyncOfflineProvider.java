package com.jwetherell.quick_response_code.SQLiteService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jwetherell.quick_response_code.ModelDTO.DepartmentDTO;
import com.jwetherell.quick_response_code.ModelDTO.EmployeeOffline;
import com.jwetherell.quick_response_code.ModelDTO.EmployeeSyncFromDB;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamvan on 12/28/2015.
 */
public class EmployeeSyncOfflineProvider {
    private SQLiteDatabase database;
    private My_SQLiteHelper dbHelper;
    private String[] DEP_COLS = {My_SQLiteHelper.DEP_ID_COL, My_SQLiteHelper.DEP_NAME_COL};
    private String[] EMP_SYNC = {My_SQLiteHelper.SY_EMP_ID, My_SQLiteHelper.SY_DEP_ID, My_SQLiteHelper.SY_DEP_NAME,
            My_SQLiteHelper.SY_EMP_NAME, My_SQLiteHelper.SY_HAVE_IMG};
    private static Context context_;
    public EmployeeSyncOfflineProvider(Context context) {
        dbHelper = new My_SQLiteHelper(context);context_=context;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    private DepartmentDTO cursorToComment_DEP(Cursor cursor) {
        DepartmentDTO comment = new DepartmentDTO();
        comment.setDEP_ID(cursor.getString(0));
        comment.setDEP_NM(cursor.getString(1));
        return comment;
    }

    private EmployeeSyncFromDB cursorToComment_EMP(Cursor cursor) {
        EmployeeSyncFromDB comment = new EmployeeSyncFromDB();
        comment.setEMP_ID(cursor.getString(0));
        comment.setDEP_ID(cursor.getString(1));
        comment.setDEP_NM(cursor.getString(2));
        comment.setEMP_NM(cursor.getString(3));
        comment.setHAVE_IMG(cursor.getString(4));
        return comment;
    }

    // Create Record in SQLite
    public DepartmentDTO CreateDepartment_Offline(DepartmentDTO dto) {
        try {
            open();
            ContentValues values = new ContentValues();
            values.put(My_SQLiteHelper.DEP_ID_COL, dto.getDEP_ID());
            values.put(My_SQLiteHelper.DEP_NAME_COL, dto.getDEP_NM());
            database.insert(My_SQLiteHelper.TABLE_DEPARTMENT, null, values);
            Cursor cursor = database.query(My_SQLiteHelper.TABLE_DEPARTMENT,
                    DEP_COLS, My_SQLiteHelper.DEP_ID_COL + " = '" + dto.getDEP_ID() + "'", null,
                    null, null, null);
            cursor.moveToFirst();
            DepartmentDTO newComment = cursorToComment_DEP(cursor);
            cursor.close();
            return newComment;
        } catch (Exception exx) {
            Log.e("Loi_Insert", exx.toString());
        }
        return null;
    }

    public EmployeeSyncFromDB CreateEMP_Sync_Offline(EmployeeSyncFromDB dto) {
        try {
            open();
            ContentValues values = new ContentValues();
            values.put(My_SQLiteHelper.SY_EMP_ID, dto.getEMP_ID());
            values.put(My_SQLiteHelper.SY_DEP_ID, dto.getDEP_ID());
            values.put(My_SQLiteHelper.SY_EMP_NAME, dto.getEMP_NM());
            values.put(My_SQLiteHelper.SY_DEP_NAME, dto.getDEP_NM());
            values.put(My_SQLiteHelper.SY_HAVE_IMG, dto.getHAVE_IMG());

            database.insert(My_SQLiteHelper.TABLE_SYNCEMP, null, values);
            Cursor cursor = database.query(My_SQLiteHelper.TABLE_SYNCEMP,
                    EMP_SYNC, My_SQLiteHelper.SY_EMP_ID + " = '" + dto.getEMP_ID() + "'", null,
                    null, null, null);
            cursor.moveToFirst();
            EmployeeSyncFromDB newComment = cursorToComment_EMP(cursor);
            cursor.close();
            return newComment;
        } catch (Exception exx) {
            Log.e("Loi_Insert", exx.toString());
        }
        return null;
    }

    // Update Record
    public EmployeeSyncFromDB UpdateEMP_Sync_Offline(EmployeeSyncFromDB dto) {
        try {
            open();
            ContentValues values = new ContentValues();
            values.put(My_SQLiteHelper.SY_EMP_ID, dto.getEMP_ID());
            values.put(My_SQLiteHelper.SY_DEP_ID, dto.getDEP_ID());
            values.put(My_SQLiteHelper.SY_EMP_NAME, dto.getEMP_NM());
            values.put(My_SQLiteHelper.SY_DEP_NAME, dto.getDEP_NM());
            values.put(My_SQLiteHelper.SY_HAVE_IMG, dto.getHAVE_IMG());
            database.update(My_SQLiteHelper.TABLE_SYNCEMP, values, My_SQLiteHelper.SY_EMP_ID + "='" + dto.getEMP_ID() + "'", null);
            //database.insert(My_SQLiteHelper.TABLE_SYNCEMP, null, values);
            Cursor cursor = database.query(My_SQLiteHelper.TABLE_SYNCEMP,
                    EMP_SYNC, My_SQLiteHelper.SY_EMP_ID + " = '" + dto.getEMP_ID() + "'", null,
                    null, null, null);
            cursor.moveToFirst();
            EmployeeSyncFromDB newComment = cursorToComment_EMP(cursor);
            cursor.close();
            return newComment;
        } catch (Exception exx) {
            Log.e("Loi_Update", exx.toString());
        }
        return null;
    }

    // Check value Is Exists
    public DepartmentDTO CheckExistsDEP(String DEP_ID) {
        DepartmentDTO dto = new DepartmentDTO();
        try {
            open();
            Cursor cursor = database.query(My_SQLiteHelper.TABLE_DEPARTMENT,
                    DEP_COLS, My_SQLiteHelper.DEP_ID_COL + " = '" + DEP_ID + "'", null,
                    null, null, null);
            cursor.moveToFirst();
            dto = cursorToComment_DEP(cursor);
            cursor.close();
        } catch (Exception ex) {
            return null;
        }
        return dto;
    }

    public EmployeeSyncFromDB CheckExists_SYEMP(String EMP_ID) {
        EmployeeSyncFromDB dto = new EmployeeSyncFromDB();
        try {
            open();
            Cursor cursor = database.query(My_SQLiteHelper.TABLE_SYNCEMP,
                    EMP_SYNC, My_SQLiteHelper.SY_EMP_ID + " = '" + EMP_ID + "'", null,
                    null, null, null);
            cursor.moveToFirst();
            dto = cursorToComment_EMP(cursor);
            cursor.close();
        } catch (Exception ex) {
            return null;
        }
        return dto;
    }

    // Get List Value
    public List<EmployeeSyncFromDB> GetAllOfflineRecord(String dep_ID) {
        EmployeeOfflineProvider empOffline = new EmployeeOfflineProvider(context_);
        List<EmployeeSyncFromDB> comments = new ArrayList<EmployeeSyncFromDB>();
        try {
            open();
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + My_SQLiteHelper.TABLE_SYNCEMP + " WHERE " + My_SQLiteHelper.SY_DEP_ID + "=?", new String[]{dep_ID});
             //database.query(My_SQLiteHelper.TABLE_SYNCEMP, EMP_SYNC, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                EmployeeSyncFromDB comment = cursorToComment_EMP(cursor);
                comment.setISCheck("");
                try {
                    EmployeeOffline empCheck = empOffline.CheckExists(comment.getEMP_ID(), UtilMethod.GetCurrentDateDMY());
                    if(empCheck!=null){
                        comment.setISCheck("1");
                        if(!empCheck.getIMG_URI().equals("0")){
                            comment.setHAVE_IMG(empCheck.getIMG_URI());
                        }
                    }
                }catch (Exception exxxxx){
                    comment.setISCheck("");
                }
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

    public List<DepartmentDTO> GetAllDepartment() {
        List<DepartmentDTO> comments = new ArrayList<DepartmentDTO>();
        try {
            open();
            Cursor cursor = database.query(My_SQLiteHelper.TABLE_DEPARTMENT, DEP_COLS, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                DepartmentDTO comment = cursorToComment_DEP(cursor);
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

    public List<DepartmentDTO> GetAllDepartment_WithCheckNumber() {
        List<DepartmentDTO> comments = new ArrayList<DepartmentDTO>();
        try {
            open();
            Cursor cursor = database.query(My_SQLiteHelper.TABLE_DEPARTMENT, DEP_COLS, null, null, null, null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                DepartmentDTO comment = cursorToComment_DEP(cursor);
                // Add Check Number in here
                try{
                    int TotalEMP = TotalEMP_Of_DEP(comment.getDEP_ID());
                    int TotalOfCheck = TotalEMP_Of_DEP_CheckAtten(comment.getDEP_ID());
                    int TotalUnCheck = TotalEMP - TotalOfCheck;
                    comment.setTotalEMP(TotalEMP);
                    comment.setNumberOfCheck(TotalOfCheck);
                    comment.setNumberUnCkeck(TotalUnCheck);
                }catch (Exception exx){
                    Log.e("Error_DEPDetail",exx.toString());
                }
                comments.add(comment);
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception loadEx) {
            Log.e("ErrorLoad", loadEx.toString());
        }
        return comments;
    }

    // For Check Number
    public int TotalEMP_Of_DEP(String Dep_ID){
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT (*) FROM " + My_SQLiteHelper.TABLE_SYNCEMP + " WHERE " + My_SQLiteHelper.SY_DEP_ID + "=?", new String[]{Dep_ID});
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        }catch (Exception ex){
            return 0;
        }
    }
    //
    public int TotalEMP_Of_DEP_CheckAtten(String Dep_ID){
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery("Select Count(*) from OfflineUserCheck , SyncEmployee where OfflineUserCheck.EMPID=SyncEmployee.EMP_ID and SyncEmployee.DEP_ID=?", new String[]{Dep_ID});
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();
            return count;
        }catch (Exception ex){
            return 0;
        }
    }
}
