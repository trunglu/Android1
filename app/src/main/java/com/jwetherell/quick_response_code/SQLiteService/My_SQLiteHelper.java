package com.jwetherell.quick_response_code.SQLiteService;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by phamvan on 7/29/2015.
 * Noted: TrungLv(2018/10/12)
 * Helper class này giúp tạo SQLite DB lưu trữ thông tin connect server trên device
 * thông tin chấm công khi offline
 */
public class My_SQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE_COMMENTS = "SaveAccount";
    public static final String COLUMN_USERID = "userNameSave";
    public static final String COLUMN_USERPASS = "passwordSave";
    public static final String COLUMN_USERMODE = "userMode";

    public static final String TABLE_CONFIG = "SaveConnect";
    public static final String CONFIG_ID = "IDConfig";
    public static final String CONFIG_IPADDRESS = "ipaddress";
    public static final String CONFIG_DBNAME = "databasename";
    public static final String CONFIG_DBUSERNAME = "username";
    public static final String CONFIG_DBPASSWORD = "password";
    public static final String CONFIG_ALLOWSYNCPHOTO = "allowSyncPhoto";
    public static final String CONFIG_API_IMG = "DatabaseAPIURL";

    public static final String TABLE_LISCEN = "LiscenTable";
    public static final String LISCEN_ACTIVE = "ACTIVE";
    public static final String LISCEN_VALUE = "VALUE";

    public static final String TABLE_OFFLINEEMP = "OfflineUserCheck";
    public static final String EMP_ID = "EMPID";
    public static final String CHK_DT = "CHK_DT";
    public static final String IMG_URI = "IMG_URI";
    public static final String IMG_Base64 = "IMG_Base64";
    public static final String REAL_DT = "REAL_DT";

    public static final String TABLE_SYNCEMP = "SyncEmployee";
    public static final String SY_EMP_ID = "EMP_ID";
    public static final String SY_DEP_ID = "DEP_ID";
    public static final String SY_DEP_NAME = "DEP_NM";
    public static final String SY_EMP_NAME = "EMP_NM";
    public static final String SY_HAVE_IMG = "HAVE_IMG";

    public static final String TABLE_DEPARTMENT = "DepartmentTB";
    public static final String DEP_ID_COL = "DEP_ID";
    public static final String DEP_NAME_COL = "DEP_NM";

    private static final String DATABASE_NAME = "MySaveAccount.db";//commments.db
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_COMMENTS + "(" + COLUMN_USERID
            + " text primary key, " + COLUMN_USERPASS
            + " text not null, "+COLUMN_USERMODE+" text not null);";
    private static final String DATABASE_CONFIG_TABLE = " create table "
            + TABLE_CONFIG + " ("+CONFIG_ID+" Interger primary key, "+CONFIG_IPADDRESS
            +" text not null,"+CONFIG_DBNAME+" text not null,"+CONFIG_DBUSERNAME+" text not null,"
            +CONFIG_DBPASSWORD+" text not null,"+CONFIG_ALLOWSYNCPHOTO+" text not null, "+CONFIG_API_IMG+" text)";

    private static final String DATABASE_LISCEN_TABLE = " create table "
            + TABLE_LISCEN + " ("+LISCEN_ACTIVE
            +" text primary key,"+LISCEN_VALUE+" text not null)";

    private static final String DATABASE_CREATE_TABLE_OFFLINE_EMP = " create table "
            + TABLE_OFFLINEEMP + "(" + EMP_ID + " text not null, "
            + CHK_DT + " text not null, "
            + IMG_URI + " text not null,"
            + IMG_Base64 + " text not null , " + REAL_DT +" text not null, "
            + "PRIMARY KEY ("+EMP_ID+", "+CHK_DT+") "+") ";

    private static final String DATABASE_CREATE_TABLE_EMPLOYEE_SYNC = "create table "+TABLE_SYNCEMP+ "("
            +SY_EMP_ID +" text primary key, "+SY_DEP_ID+" text not null, "+SY_DEP_NAME+" text not null, "
            +SY_EMP_NAME +" text not null, "+SY_HAVE_IMG +" text not null)";

    private static final String DATABASE_CREATE_TABLE_DEPARTMENT = "create table "+ TABLE_DEPARTMENT
            +"("+DEP_ID_COL+" text primary key, "+DEP_NAME_COL+" text not null)";

    public My_SQLiteHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }


    public My_SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(DATABASE_CONFIG_TABLE);
        sqLiteDatabase.execSQL(DATABASE_CREATE);
        sqLiteDatabase.execSQL(DATABASE_LISCEN_TABLE);
        sqLiteDatabase.execSQL(DATABASE_CREATE_TABLE_OFFLINE_EMP);
        sqLiteDatabase.execSQL(DATABASE_CREATE_TABLE_EMPLOYEE_SYNC);
        sqLiteDatabase.execSQL(DATABASE_CREATE_TABLE_DEPARTMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.w(My_SQLiteHelper.class.getName(),
                "Upgrading database from version " + i + " to "
                        + i1 + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_COMMENTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_CONFIG);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_LISCEN_TABLE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_TABLE_OFFLINE_EMP);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_TABLE_EMPLOYEE_SYNC);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_CREATE_TABLE_DEPARTMENT);
        onCreate(sqLiteDatabase);
    }
}
