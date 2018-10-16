package com.jwetherell.quick_response_code.GlobalSetting;

import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.GlobalSetting.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.jwetherell.quick_response_code.ModelDTO.UserModel;
import com.jwetherell.quick_response_code.MyAdapter.SettingUserAdapter;
import com.jwetherell.quick_response_code.R;

import java.sql.ResultSet;
import java.util.ArrayList;

public class ForgotPassword extends Activity {

    private SystemUiHider mSystemUiHider;
    EditText activity_changepass_txtUserName;
    Button activity_changepass_btnSearchUsername;
    ListView activity_changepass_lvUser;
    public static ArrayList<UserModel> Models;
    public static SettingUserAdapter adapterUser;
    LoadListUser loadNguoiDung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forgot_password);
        getActionBar().hide();
        Models = new ArrayList<UserModel>();
        adapterUser = new SettingUserAdapter(getApplicationContext(), R.layout.list_item_manage_employee, Models);

        activity_changepass_txtUserName = (EditText) findViewById(R.id.activity_changepass_txtUserName);
        activity_changepass_btnSearchUsername = (Button) findViewById(R.id.activity_changepass_btnSearchUsername);
        activity_changepass_lvUser = (ListView) findViewById(R.id.activity_changepass_lvUser);
        activity_changepass_lvUser.setAdapter(adapterUser);
        loadNguoiDung = new LoadListUser();
        //new LoadListUser().execute();
        activity_changepass_btnSearchUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loadNguoiDung.getStatus() != AsyncTask.Status.RUNNING) {
                    loadNguoiDung = new LoadListUser();
                    loadNguoiDung.execute();
                }
            }
        });

        activity_changepass_lvUser.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(),PopupChangePassword.class);
                Bundle b = new Bundle();
                b.putString("TenDN",Models.get(i).getUserName());
                intent.putExtras(b);
                startActivity(intent);
                return false;
            }
        });

    }


    class LoadListUser extends AsyncTask<Void, Void, Void> {
        BaseServiceModel dataModel;
        ResultSet reEmp;
        String txt_SearchUsername = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataModel = new BaseServiceModel(getApplicationContext());
            txt_SearchUsername = activity_changepass_txtUserName.getText().toString();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (reEmp != null) {
                try {
                    while (reEmp.next()) {
                        UserModel itemModel = new UserModel();
                        itemModel.setUserName(reEmp.getString(1));
                        itemModel.setPassword_(reEmp.getString(2));
                        itemModel.setMode(reEmp.getString(3));
                        Models.add(itemModel);
                    }
                    dataModel.CloseConnection();
                    reEmp.close();
                } catch (Exception exErrorDis) {
                    Log.e("ErrorDisplay", exErrorDis.toString());
                }
                adapterUser.notifyDataSetChanged();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String sql_ = "select * from USER_SYSADR where USER_NAME like '%" + txt_SearchUsername + "%'";
            reEmp = dataModel.LoadData_Simple(sql_);
            return null;
        }
    }

}
