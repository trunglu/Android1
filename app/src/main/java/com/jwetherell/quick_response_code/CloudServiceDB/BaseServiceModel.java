package com.jwetherell.quick_response_code.CloudServiceDB;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;

import com.jwetherell.quick_response_code.Singleton;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by phamvan on 7/17/2015.
 */
public class BaseServiceModel {
    Statement statement;
    Context cont;
    Connection conn = null;
    ResultSet rs = null;

    public BaseServiceModel(Context cont) {
        this.cont = cont;
    }

    /*
    public String LoadimgFromAnotherDBString(String EMP_ID) {
        byte[] result_ = null;
        ResultSet rsImg = null;
        Connection connImg = null;
        Statement statementImg = null;
        String valueBase64 = "";
        try {
            String sql = " Select * from " + Singleton.getInstance().getTablePhoto() + " where EMP_ID='" + EMP_ID + "'";
            //
            String server = Singleton.getInstance().getServer();
            String database = Singleton.getInstance().getDbPhoto();
            String user = Singleton.getInstance().getUser();
            String pass = Singleton.getInstance().getPassWord();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build());
            String connUrl = null;
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            connUrl = "jdbc:jtds:sqlserver://" + server + ";" + "databaseName="
                    + database + ";user=" + user + ";password=" + pass + ";";
            connImg = DriverManager.getConnection(connUrl);
            statementImg = getStatement(connImg);
            // Code Load Data in Here
            rsImg = statementImg.executeQuery(sql);
            while (rsImg.next()) {
                result_ = rsImg.getBytes(1);
            }
            // Close Connection.
            connImg.close();
            statementImg.close();
            valueBase64 = Base64.encodeToString(result_,Base64.DEFAULT);
            return valueBase64;
        } catch (SQLException se) {
            Log.e("ERROR1", se.getMessage());
        } catch (ClassNotFoundException cl) {
            Log.e("ERROR2", cl.getMessage());
        } catch (Exception ex_DB) {
            Log.e("Error_ImgDB", ex_DB.getMessage());
        }
        return result_ == null ? "" : valueBase64;
    }
    */
    public void Connect() {
        try {
            String server = Singleton.getInstance().getServer();
            String database = Singleton.getInstance().getDatabase();
            String user = Singleton.getInstance().getUser();
            String pass = Singleton.getInstance().getPassWord();
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads().detectDiskWrites().detectNetwork()
                    .penaltyLog().build());
            String connUrl = null;
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            connUrl = "jdbc:jtds:sqlserver://" + server + ";" + "databaseName="
                    + database + ";user=" + user + ";password=" + pass + ";";
            conn = DriverManager.getConnection(connUrl);
            statement = getStatement(conn);
//            Log.e("Connected Sucess QLNS Data", "OK");
        } catch (SQLException se) {
            Log.e("ERROR1", se.getMessage());
        } catch (ClassNotFoundException cl) {
            Log.e("ERROR2", cl.getMessage());
        } catch (Exception e) {
            Log.e("ERROR3", e.getMessage());
        }
    }

    public Connection GetConnection() {
        Connect();
        return conn;
    }

    private Statement getStatement(Connection connection) {
        try {
            return connection.createStatement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public ResultSet LoadData_FromDB(String sqlQuery) {
        rs = null;
        try {
            Connect();
            CallableStatement ctms = conn.prepareCall(sqlQuery);
            rs = ctms.executeQuery();
            //conn.close();statement.close();
        } catch (Exception sqlErrorLoad) {
            Log.e("Error_Load", sqlErrorLoad.toString());
        }
        return rs;
    }

    public ResultSet LoadData_Simple(String sqlQuery) {
        rs = null;
        try {
            Connect();
            rs = statement.executeQuery(sqlQuery);
            //conn.close();statement.close();
        } catch (Exception ex_SQLSimpleError) {
            Log.e("SQLLoadError", ex_SQLSimpleError.toString());
        }
        return rs;
    }

    public Boolean Insert_Update_Delete(String sqlQuery) {
        boolean result = false;
        try {
            Connect();
            result = statement.execute(sqlQuery);
            //conn.close();statement.close();
            result = true;
        } catch (Exception exBaseError) {
            result = false;
            Log.e("BaseInsertError", exBaseError.toString());
        }
        return result;
    }

    public void CloseConnection() {
        try {
            conn.close();
            statement.close();
        } catch (Exception exxxxxxxxxx) {

        }
    }
}
