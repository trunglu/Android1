package com.jwetherell.quick_response_code;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.jwetherell.quick_response_code.*;
import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.ModelDTO.LiscenDTO;
import com.jwetherell.quick_response_code.ModelDTO.ServerConnectModel;
import com.jwetherell.quick_response_code.ModelDTO.UserSaveAccount;
import com.jwetherell.quick_response_code.SQLiteService.LiscenProvider;
import com.jwetherell.quick_response_code.SQLiteService.SQLiteProvider;
import com.jwetherell.quick_response_code.SQLiteService.SQLiteServerConnect;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Config extends Activity {
    Button btOk, btcheckconnect, btedit, btnADDliscen;
    EditText txtLiscenKey;
    String server1, database, user, password, databaseImage;
    Statement statementl;
    SQLiteServerConnect serverConnect;
    LiscenProvider LiscenActive;
    boolean ConnectionExists;
    LinearLayout llConfigButton, llConfigText, llLiscenkey;
    ArrayList<ServerConnectModel> listConnect;

    //ScrollView scrollViewEdit;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.config);
        listConnect = new ArrayList<ServerConnectModel>();
        btOk = (Button) findViewById(R.id.btOk);
        btcheckconnect = (Button) findViewById(R.id.btConnect);
        btedit = (Button) findViewById(R.id.btedit);
        btnADDliscen = (Button) findViewById(R.id.btnADDliscen);

        txtLiscenKey = (EditText) findViewById(R.id.txtLiscenKey);
        llConfigButton = (LinearLayout) findViewById(R.id.llConfigButton);
        llConfigText = (LinearLayout) findViewById(R.id.llConfigText);
        llLiscenkey = (LinearLayout) findViewById(R.id.llLiscenkey);
        //scrollViewEdit = (ScrollView) findViewById(R.id.scrollViewEdit);

        LiscenActive = new LiscenProvider(getApplicationContext());
        ArrayList<LiscenDTO> Liscens = new ArrayList<LiscenDTO>();
        Liscens = (ArrayList<LiscenDTO>) LiscenActive.GetAll();
        // check xem nếu chưa có licen nào thì phải add vào
        // hiển thị các view component để add
        if (Liscens.size() == 0) {
            llConfigButton.setVisibility(View.GONE);
            llConfigText.setVisibility(View.GONE);
            llLiscenkey.setVisibility(View.VISIBLE);
        }
        // nếu đã có license code trong db thì hiển thị lựa chọn kết nối
        if (Liscens.size() > 0) {
            llConfigButton.setVisibility(View.VISIBLE);
            llConfigText.setVisibility(View.VISIBLE);
            llLiscenkey.setVisibility(View.GONE);
        }


        btnADDliscen.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String LiscenString = txtLiscenKey.getText().toString();
                if (LiscenString.equals("_Glinton2017#")) {
                    LiscenDTO dtoLiscen = new LiscenDTO();
                    dtoLiscen = LiscenActive.CreateItem("true", LiscenString);
                    if (dtoLiscen != null) {
                        //scrollViewEdit.setVisibility(View.VISIBLE);
                        llConfigButton.setVisibility(View.VISIBLE);
                        llConfigText.setVisibility(View.VISIBLE);
                        llLiscenkey.setVisibility(View.GONE);
                        // Create Folder for Load Photo
                        try {
                            String dir_path = Environment.getExternalStorageDirectory() + "//GlintonPhoto//";
                            if (!UtilMethod.dir_exists(dir_path)) {
                                File directory = new File(dir_path);
                                directory.mkdirs();
                            }
                        } catch (Exception exx) {
                            Log.e("Error_Directory", exx.toString());
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Your Liscen code is not Correct, Please input again.", Toast.LENGTH_LONG).show();
                }
            }
        });


        ConnectionExists = true;
        serverConnect = new SQLiteServerConnect(getApplicationContext());

        listConnect = (ArrayList<ServerConnectModel>) serverConnect.GetALLConnect();
        if (listConnect.size() == 0) {
            ConnectionExists = false;
        } else {
            ConnectionExists = true;
        }

        btOk.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (listConnect.size() > 0) {

                    server1 = listConnect.get(0).getiPAddress();// "192.168.8.3:1433";
                    database = listConnect.get(0).getDatabaseName();// "GP8000";
                    user = listConnect.get(0).getUserName();// "gp";
                    password = listConnect.get(0).getPasswordConnect();// "P@ssw0rd";
                    databaseImage = listConnect.get(0).getDatabaseImgName();// "http://192.168.8.3:3000/api/";

                    Singleton.getInstance().setServer(server1);
                    Singleton.getInstance().setDatabase(database);
                    Singleton.getInstance().setUser(user);
                    Singleton.getInstance().setPassWord(password);
                    Singleton.getInstance().setServerAPIURL(databaseImage);
                    Config.this.finish();
                    Intent iLogin = new Intent(getApplicationContext(), Login.class);
                    startActivity(iLogin);
                }
            }
        });

        // Handle selected Event external network
        btedit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // Change Server Value
                server1 = "115.73.216.138:1433";
                database = "kieuminh";
                user = "sa";
                password = "glinton99";
                databaseImage = "http://115.73.216.138:1000/Testleaveapply/api/";

                ReChoosenConnect();
            }
        });

        // Handle selected Event internal network
        btcheckconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                server1 = "115.73.216.138:1433";
                database = "kieuminh";
                user = "sa";
                password = "glinton99";
                databaseImage = "http://115.73.216.138:1000/Testleaveapply/api/";

                ReChoosenConnect();
            }
        });


    }

    void ReChoosenConnect() {
        if (listConnect.size() == 0) {
            CheckConnect();
        } else {
            try {
                ServerConnectModel connectModel_ = new ServerConnectModel(server1, database
                        , user, password);
                connectModel_.setDatabaseImgName(databaseImage);
                connectModel_.setAllowSyncPhoto("True");
                ServerConnectModel resultAdd = serverConnect.UpdateConnect(connectModel_);
                if (resultAdd != null && !resultAdd.getUserName().equals("")) {
                    Singleton.getInstance().setServer(resultAdd.getiPAddress());
                    Singleton.getInstance().setDatabase(resultAdd.getDatabaseName());
                    Singleton.getInstance().setUser(resultAdd.getUserName());
                    Singleton.getInstance().setPassWord(resultAdd.getPasswordConnect());
                    Singleton.getInstance().setServerAPIURL(resultAdd.getDatabaseImgName());
                    GlobalData.AllowSavePhotoIntoDevice = resultAdd.getAllowSyncPhoto().equals("True") ? true : false;
                    String Message_ = getResources().getString(R.string.ChangeConnect_network);
                    if (server1.equals("10.0.0.86:1433")) {
                        Message_ += " " + getResources().getString(R.string.internal_lan);
                    } else {
                        Message_ += " " + getResources().getString(R.string.extenal_network);
                    }
                    Toast.makeText(getApplicationContext(), Message_, Toast.LENGTH_LONG).show();
                }
            } catch (Exception exInit) {
                Log.e("False_Init", exInit.toString());
            }
        }
    }

    public void CheckConnect() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        Connection conn = null;
        String connUrl = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            connUrl = "jdbc:jtds:sqlserver://" + server1 + ";" + "databaseName="
                    + database + ";user=" + user + ";password=" + password + ";";
            conn = DriverManager.getConnection(connUrl);

            statementl = getStatement(conn);
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(Config.this);
            // Setting Dialog Title
            alertDialog.setTitle(R.string.Connect_database_success)
                    // Setting OK Button
                    .setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ServerConnectModel connectModel_ = new ServerConnectModel(server1, database
                                    , user, password);
                            connectModel_.setDatabaseImgName(databaseImage);
                            ServerConnectModel resultAdd = new ServerConnectModel(); //serverConnect.CreateConnect(connectModel_);
                            if (ConnectionExists == true) {
                                resultAdd = serverConnect.UpdateConnect(connectModel_);
                                SQLiteProvider loginProvider = new SQLiteProvider(getApplicationContext());
                                ArrayList<UserSaveAccount> listSave = new ArrayList<>();
                                listSave = (ArrayList<UserSaveAccount>) loginProvider.GetAllUserSave();
                                if (listSave.size() > 0) {
                                    UserSaveAccount userDTO = new UserSaveAccount(listSave.get(0).getUserNameSave(), listSave.get(0).getPasswordSave());
                                    loginProvider.DeleteUserSave(userDTO);
                                }
                            }
                            if (ConnectionExists == false) {
                                resultAdd = serverConnect.CreateConnect(connectModel_);
                            }
                            if (resultAdd != null && !resultAdd.getUserName().equals("")) {
                                Singleton.getInstance().setServer(resultAdd.getiPAddress());
                                Singleton.getInstance().setDatabase(resultAdd.getDatabaseName());
                                Singleton.getInstance().setUser(resultAdd.getUserName());
                                Singleton.getInstance().setPassWord(resultAdd.getPasswordConnect());
                                Singleton.getInstance().setServerAPIURL(resultAdd.getDatabaseImgName());

                                // Check Table And Store Procedure Is Available in Database
                                if (ConnectionExists == false) {
                                    new InitTableAndStore().execute();
                                }
                            }
                        }
                    });
            alertDialog.show();

        } catch (SQLException se) {
            Log.e("SE", "SE");
            Log.e("ERROR1", se.getMessage());
            AlertDialog alertDialog = new AlertDialog.Builder(Config.this)
                    .create();
            // Setting Dialog Title
            alertDialog.setTitle(se.getMessage());
            // Setting OK Button
            alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });

            alertDialog.show();
            // showMessage("Error", se.getMessage());
        } catch (ClassNotFoundException cl) {
            Log.e("ER2", "ER2");
            Log.e("ERROR2", cl.getMessage());
            AlertDialog alertDialog = new AlertDialog.Builder(Config.this)
                    .create();
            // Setting Dialog Title
            alertDialog.setTitle(cl.getMessage());
            // Setting OK Button
            alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });

            alertDialog.show();
            // showMessage("Error", cl.getMessage());
        } catch (Exception e) {
            Log.e("ER3", "Couldn't get database connection");
            Log.e("ERROR3", e.getMessage());
            AlertDialog alertDialog = new AlertDialog.Builder(Config.this)
                    .create();
            // Setting Dialog Title
            alertDialog.setTitle(e.getMessage());
            // Setting OK Button
            alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });

            alertDialog.show();
            // showMessage("Error", e.getMessage());
        }
    }

    private Statement getStatement(Connection connection) {

        try {
            return connection.createStatement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    class InitTableAndStore extends AsyncTask<Void, Void, Void> {
        BaseServiceModel dataModel;
        boolean isExists;
        ResultSet rs;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataModel = new BaseServiceModel(getApplicationContext());
            isExists = false;
            rs = null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Add Simple data Success.", Toast.LENGTH_LONG).show();
            dataModel.CloseConnection();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            String sql = "select * from USER_SYSADR";
            try {
                rs = dataModel.LoadData_Simple(sql);
                if (rs == null) {
                    // Create Table and Store
                    sql = " CREATE TABLE FILC06D(EMP_ID nvarchar(10) NOT NULL, CK_O  nvarchar(10) NULL, MA_SCAN nvarchar(10) NULL, INT_DT nvarchar(10) NOT NULL CONSTRAINT pk_FILC06D PRIMARY KEY (EMP_ID,INT_DT)) ";
                    dataModel.Insert_Update_Delete(sql);
                    sql = " CREATE TABLE FILC06D_UP( EMP_ID varchar(10) NOT NULL, INT_DT datetime NOT NULL, USER_ID nvarchar(50) NOT NULL,  CK_O  nvarchar(10) NULL, MA_SCAN  nvarchar(10) NULL CONSTRAINT pk_FILC06D_UP PRIMARY KEY (EMP_ID,INT_DT,USER_ID))";
                    dataModel.Insert_Update_Delete(sql);
                    sql = " CREATE TABLE  USER_SYSADR( [USER_NAME] nvarchar(50) NOT NULL, PASS nvarchar(50) NULL, USER_ROLE varchar(50) NULL constraint pk_dboUSER_SYSADR primary key([user_name]))";
                    dataModel.Insert_Update_Delete(sql);
                    String DefaultPassword = UtilMethod.MD5("glinton123");
                    sql = " insert into USER_SYSADR values ('Admin','" + DefaultPassword + "','super')";
                    dataModel.Insert_Update_Delete(sql);
                    // Create Store Procedure
                    sql = " Create PROCEDURE spPhanTrang " +
                            " @DepName NVARCHAR(100), " +
                            " @PageNumber INT, " +
                            " @PageSize   INT " +
                            " AS " +
                            " WITH cte AS " +
                            " ( " +
                            "  SELECT F1.EMP_ID, F1.EMP_NM, F1.DEP_ID, F2.DEP_NM, F3.PIC_DR,F4.MA_SCAN, ROW_NUMBER() OVER (ORDER BY F1.EMP_ID DESC) AS Pages " +
                            "  FROM FILB01A F1 left join FILA02A F2 on F1.DEP_ID = F2.DEP_ID " +
                            " left join FILB01AB F3 on F1.EMP_ID =F3.EMP_ID " +
                            " left join FILC06D F4 on F1.EMP_ID = F4.EMP_ID and F4.INT_DT = CONVERT(nvarchar, GETDATE(),103) " +
                            "  WHERE F2.DEP_NM = @DepName " +
                            " and ISNULL(F1.VAC_BT,0)=0 and F1.DEL_BT = 0 AND F1.ATT_BT=1 ) " +
                            "  SELECT *, COUNT(*) OVER() AS totalOfPages " +
                            "  FROM cte " +
                            "  WHERE Pages BETWEEN (@PageNumber - 1) * @PageSize + 1 AND @PageNumber * @PageSize ";
                    dataModel.Insert_Update_Delete(sql);

                    sql = " Create PROCEDURE spLoadEMP  " +
                            " @DepID NVARCHAR(10),  @PageNumber INT,  @PageSize   INT  AS  " +
                            " WITH cte " +
                            " AS  (   " +
                            " SELECT F1.EMP_ID, F1.EMP_NM, F1.DEP_ID, F2.DEP_NM, F4.MA_SCAN, ROW_NUMBER() OVER (ORDER BY F1.EMP_ID DESC) AS Pages   " +
                            " FROM FILB01A F1 left join FILA02A F2 on F1.DEP_ID = F2.DEP_ID     " +
                            " left join FILC06D F4 on F1.EMP_ID = F4.EMP_ID and F4.INT_DT = CONVERT(nvarchar, GETDATE(),103)   " +
                            " WHERE F2.DEP_ID = @DepID  and ISNULL(F1.VAC_BT,0)=0 and F1.DEL_BT = 0 AND F1.ATT_BT=1 )   " +
                            " SELECT *, COUNT(*) OVER() AS totalOfPages   FROM cte   " +
                            " WHERE Pages BETWEEN (@PageNumber - 1) * @PageSize + 1 AND @PageNumber * @PageSize";
                    dataModel.Insert_Update_Delete(sql);
                    isExists = false;
                }
            } catch (Exception ErrorEx) {
                isExists = true;
            }
            return null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuchung, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit2:
                Config.this.finish();
                break;
        }
        return true;
    }

}
