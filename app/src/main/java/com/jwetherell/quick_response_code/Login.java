package com.jwetherell.quick_response_code;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.ModelDTO.ServerConnectModel;
import com.jwetherell.quick_response_code.ModelDTO.UserSaveAccount;
import com.jwetherell.quick_response_code.SQLiteService.SQLiteProvider;
import com.jwetherell.quick_response_code.SQLiteService.SQLiteServerConnect;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;

public class Login extends Activity {

    booleanDangNhap executeDangNhap;
    EditText edituser, editpass;
    String Current_User = "";
    String locale;
    String UserMode = "";
    private Locale myLocale;
    Button btlogin, btexit, Login_btn_useOffline;
    final Context context = this;
    boolean ktdn = false;
    private String NOTES = "setLocale.txt";
    SQLiteProvider loginProvider;// SQLite
    SQLiteServerConnect connectProvider;
    boolean IsSaveLogin;
    boolean IsExistsLogin;
    LinearLayout llLogin_loginForm, ll_login_Nonetwork;
    ImageView img_Login_NoNetwork;
    ArrayList<ServerConnectModel> listConnect;


    @Override
    public void onCreate(Bundle s) {
        //TrungLv(2018/10/12)
        Log.e("Trung Test", "Login activity start.....");
        File tmpDir = new File(this.getDatabasePath("MySaveAccount.db").getPath());
        boolean exists = tmpDir.exists();
        Log.e("Path db sqlite: ", exists ? "tồn tại file db" : "ko tồn tại file db");
        Log.e("Trung Lv",this.getDatabasePath("MySaveAccount.db").getPath());

        super.onCreate(s);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        listConnect = new ArrayList<ServerConnectModel>();
        GlobalData.context_ = getApplicationContext();
        setContentView(R.layout.login);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        llLogin_loginForm = (LinearLayout) findViewById(R.id.llLogin_loginForm);
        ll_login_Nonetwork = (LinearLayout) findViewById(R.id.ll_login_Nonetwork);
        btexit = (Button) findViewById(R.id.buttonExit);
        btlogin = (Button) findViewById(R.id.buttonLogin);
        Login_btn_useOffline = (Button) findViewById(R.id.Login_btn_useOffline);
        editpass = (EditText) findViewById(R.id.editTextPass);
        edituser = (EditText) findViewById(R.id.editTextUser);
        img_Login_NoNetwork = (ImageView) findViewById(R.id.img_Login_NoNetwork);
        locale = context.getResources().getConfiguration().locale.getLanguage();
        setLocale(locale);
        executeDangNhap = new booleanDangNhap();
        IsExistsLogin = false;
        btlogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                boolean isonline = isOnline();
                if (isonline) {
                    if (executeDangNhap.getStatus() != AsyncTask.Status.RUNNING) {
                        executeDangNhap = new booleanDangNhap();
                        executeDangNhap.execute();
                    }
                }
                if (!isonline) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                    // Setting Dialog Title
                    alertDialog.setTitle(R.string.No_connect_internet)
                            // Setting OK Button
                            .setPositiveButton(R.string.Ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog,
                                                            int which) {
                                            // TODO Auto-generated method stub
                                        }
                                    });
                    alertDialog.show();
                }
            }
        });
        btexit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Login.this);
                alertDialogBuilder.setTitle(R.string.Notify);
                // set dialog message
                alertDialogBuilder
                        .setMessage(R.string.You_will_want_exit_program)
                        .setCancelable(false)
                        .setPositiveButton(R.string.Yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        Login.this.finish();
                                    }
                                })
                        .setNegativeButton(R.string.No,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        // Check all connection before Login
        // ***// Begin 141 //***
        try {
            connectProvider = new SQLiteServerConnect(getApplicationContext());
            listConnect = (ArrayList<ServerConnectModel>) connectProvider.GetALLConnect();
        } catch (Exception exSQLite) {
            Log.e("SQLite_Error", exSQLite.toString());
        }
        if (listConnect.size() == 0) {
            Intent configIntent = new Intent(getApplicationContext(), Config.class);
            startActivity(configIntent);
        }
        //***// End 141 //***//

        if (listConnect.size() != 0) {
            // Set instance đăng nhập thông tin connect vào Singleton Object
            Singleton.getInstance().setServer(listConnect.get(0).getiPAddress());
            Singleton.getInstance().setDatabase(listConnect.get(0).getDatabaseName());
            Singleton.getInstance().setUser(listConnect.get(0).getUserName());
            Singleton.getInstance().setPassWord(listConnect.get(0).getPasswordConnect());
            Singleton.getInstance().setServerAPIURL(listConnect.get(0).getDatabaseImgName());
            Singleton.getInstance().setServerURL(listConnect.get(0).getDatabaseImgName());
            GlobalData.AllowSavePhotoIntoDevice = listConnect.get(0).getAllowSyncPhoto().equals("True") ? true : false;
        }
        //
        IsSaveLogin = false;
        loginProvider = new SQLiteProvider(getApplicationContext());
        ArrayList<UserSaveAccount> listSave = new ArrayList<>();
        listSave = (ArrayList<UserSaveAccount>) loginProvider.GetAllUserSave();
        if (listSave.size() > 0) {
            IsExistsLogin = true;
            // Save Pass & User ==> go to Main Fragment
            Current_User = listSave.get(0).getUserNameSave();
            edituser.setText(listSave.get(0).getUserNameSave());
            editpass.setText(listSave.get(0).getPasswordSave());
            Singleton.getInstance().setRoleName(listSave.get(0).getUserMode());
            boolean isonline = isOnline();
            IsSaveLogin = true;
            if (isonline) {
                Login.this.finish();
                try {
                    Log.e("Trunglv test", "Đã đăng nhập thành công...");
                    //File tmpDir = new File(this.getDatabasePath("MySaveAccount.db").getPath());
                    //boolean exists = tmpDir.exists();
                    //Log.e("Path db sqlite: ", exists ? "tồn tại file db" : "ko tồn tại file db");
                    //Log.e("Trung Lv",this.getDatabasePath("MySaveAccount.db").getPath());
                    //copy(tmpDir,new File("/data1/MySaveAccount.db"));
                    Intent i = new Intent(Login.this, MainContent.class);
                    startActivity(i);
                } catch (Exception exxx) {
                    Log.e("LoiDangNhap", exxx.toString());
                }
            } else {
                Log.e("TrungLv","Show btn offline");
                Login_btn_useOffline.setVisibility(View.VISIBLE);
            }
        }

        DisplayLinearWhenOnlineAndOffline();
        Login_btn_useOffline.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentOffline = new Intent(getApplicationContext(), ActivityOfflineMainScreen.class);
                startActivity(intentOffline);
            }
        });
        img_Login_NoNetwork.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                DisplayLinearWhenOnlineAndOffline();
            }
        });

    }

    void DisplayLinearWhenOnlineAndOffline() {
        if (isOnline()) {
            llLogin_loginForm.setVisibility(View.VISIBLE);
            ll_login_Nonetwork.setVisibility(View.GONE);
        }
        if (!isOnline()) {
            //Log.e("Trunglv","Offline");
            llLogin_loginForm.setVisibility(View.GONE);
            ll_login_Nonetwork.setVisibility(View.VISIBLE);
        }
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public class booleanDangNhap extends AsyncTask<Void, Void, Void> {
        BaseServiceModel dataModel;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            UserMode = "";
            dataModel = new BaseServiceModel(getApplicationContext());
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String server = Singleton.getInstance().getServer();
            String database = Singleton.getInstance().getDatabase();
            String user = Singleton.getInstance().getUser();
            String pass = Singleton.getInstance().getPassWord();

            boolean kt = false;
            String txt_user = edituser.getText().toString();
            String txt_pass = UtilMethod.MD5(editpass.getText().toString());
            String sql = ("Select * from USER_SYSADR F1 where F1.USER_NAME='" + txt_user
                    + "' and F1.PASS ='" + txt_pass + "'");
            try {
                ResultSet rs = dataModel.LoadData_Simple(sql);
                kt = false;
                if (rs.next()) {
                    Singleton.getInstance().setUserName(txt_user);
                    UserMode = rs.getString(3);
                    kt = true;
                }
                ktdn = kt;
                rs.close();
                dataModel.CloseConnection();
            } catch (Exception ex1) {
                Log.e("LoiEX", ex1.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
//            Toast.makeText(getApplicationContext(),"Mode: "+UserMode, Toast.LENGTH_LONG).show();
            if (ktdn == true) {
                // SQLite ==> Insert To Database SQLite
                if (IsSaveLogin == false) {
                    loginProvider.CreateUserSave(edituser.getText().toString(), editpass.getText().toString(), UserMode);
                }
                Singleton.getInstance().setBooldn(ktdn);
                if (IsExistsLogin == true) {
                    if (!Current_User.equalsIgnoreCase(edituser.getText().toString())) {
                        UserSaveAccount userDTO = new UserSaveAccount(edituser.getText().toString(), "");
                        loginProvider.DeleteUserSave(userDTO);
                        loginProvider.CreateUserSave(edituser.getText().toString(), editpass.getText().toString(), UserMode);
                    }
                    loginProvider.UpdateUserSave(edituser.getText().toString(), editpass.getText().toString(), UserMode);
                }
                Singleton.getInstance().setRoleName(UserMode);
                Login.this.finish();
                try {
//                    Toast.makeText(getApplicationContext(),"Login Success",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(Login.this, MainContent.class);
                    startActivity(i);
                } catch (Exception exxx) {
                    Log.e("LoiDangNhap", exxx.toString());
                }
            } else {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        context);
                alertDialogBuilder.setTitle(R.string.Notify);

                // set dialog message
                alertDialogBuilder
                        .setMessage(R.string.Login_fail)
                        .setCancelable(false)
                        .setPositiveButton(R.string.Exit,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // if this button is clicked,
                                        // close
                                        // current activity
                                        Login.this.finish();
                                    }
                                })
                        .setNegativeButton(R.string.Ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // if this button is clicked,
                                        // just close
                                        // the dialog box and do nothing
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();
                // show it
                alertDialog.show();
            }
        }
    }

    public void setLocale(String lang) {
        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        //finish();
    }

    public void refersh() {
        Intent refresh = getIntent();
        Login.this.finish();
        startActivity(refresh);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        switch (item.getItemId()) {
            case R.id.action_exit1: {
                try {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setTitle(R.string.Notify);
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(R.string.You_will_want_exit_program)
                            .setCancelable(false)
                            .setPositiveButton(R.string.Yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            Login.this.finish();
                                        }
                                    })
                            .setNegativeButton(R.string.No,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } catch (Exception e) {
                    Log.e("loi", e.getMessage());
                }

            }
            break;
            case R.id.action_tiengviet1:
                setLocale("vi");
                refersh();
                break;
            case R.id.action_tienganh1:
                setLocale("en");
                refersh();
                break;
            case R.id.action_tienghoa1:
                setLocale("zh");
                refersh();
                break;
            case R.id.action_setting1:
                Intent i = new Intent(getApplicationContext(), Config.class);
                startActivity(i);
                break;
            case R.id.action_Sync: {
                if (IsExistsLogin == true) {
                    Intent ioffline = new Intent(getApplicationContext(), ActivityOfflineMainScreen.class);
                    startActivity(ioffline);
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Login_lblToastloginRequire), Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
    }
    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
}
