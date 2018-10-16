package com.jwetherell.quick_response_code.GlobalSetting;

import com.jwetherell.quick_response_code.AddUser;
import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.GlobalSetting.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.Toast;

import com.jwetherell.quick_response_code.Login;
import com.jwetherell.quick_response_code.ModelDTO.UserModel;
import com.jwetherell.quick_response_code.MyAdapter.SettingUserAdapter;
import com.jwetherell.quick_response_code.R;

import java.sql.ResultSet;
import java.util.ArrayList;

public class SettingListUser extends Activity {

    ListView lvSettingUser;
    public static ArrayList<UserModel> Models;
    public static SettingUserAdapter adapterUser;
    Button btn_Setting_manageUser_Call_AddNew;
    String userNameSelect_;
    int PosSelect ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_list_user);
        getActionBar().hide();
        //
        lvSettingUser = (ListView)findViewById(R.id.lvSettingUser);
        btn_Setting_manageUser_Call_AddNew = (Button)findViewById(R.id.btn_Setting_manageUser_Call_AddNew);
        //
        Models = new ArrayList<UserModel>();
        adapterUser = new SettingUserAdapter(getApplicationContext(),R.layout.list_item_manage_employee,Models);
        lvSettingUser.setAdapter(adapterUser);
        new LoadListUser().execute();
        //
        btn_Setting_manageUser_Call_AddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddUser.class);
                startActivity(i);
            }
        });

        lvSettingUser.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PosSelect = i;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingListUser.this);
                alertDialogBuilder.setTitle(R.string.Notify);
                // set dialog message
                alertDialogBuilder
                        .setMessage( getResources().getString(R.string.Setting_alert_Listuser_ConfirmDelete).replace("{user}", Models.get(i).getUserName()))
                        .setCancelable(false)
                        .setPositiveButton(R.string.Yes,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                       //
                                        userNameSelect_ = Models.get(PosSelect).getUserName();
                                        new Delete1User().execute();
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
                return false;
            }
        });
    }


    class Delete1User extends AsyncTask<Void,Void,Void>{
        BaseServiceModel dataModel;
        boolean isSuccess;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataModel = new BaseServiceModel(getApplicationContext());
            isSuccess = false;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isSuccess == true){
                Models.remove(PosSelect);
                adapterUser.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.Setting_alert_Listuser_Delete_Success).replace("{user}",userNameSelect_)
                        , Toast.LENGTH_SHORT).show();
            }
            dataModel.CloseConnection();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String sql = "Delete From USER_SYSADR where USER_NAME='"+userNameSelect_+"'";
            isSuccess = dataModel.Insert_Update_Delete(sql);
            return null;
        }
    }

    public static void AddMemberToList(String UserName, String UPassword, String userMode){
        UserModel uModel = new UserModel(UserName,UPassword,userMode);
        Models.add(uModel);
        adapterUser.notifyDataSetChanged();
    }

    class LoadListUser extends AsyncTask<Void,Void,Void>{
        BaseServiceModel dataModel;
        ResultSet reEmp;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataModel = new BaseServiceModel(getApplicationContext());
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(reEmp!=null){
                try{
                    while (reEmp.next()){
                        UserModel itemModel = new UserModel();
                        itemModel.setUserName(reEmp.getString(1));
                        itemModel.setPassword_(reEmp.getString(2));
                        itemModel.setMode(reEmp.getString(3));
                        Models.add(itemModel);
                    }
                    dataModel.CloseConnection();
                    reEmp.close();
                }
                catch (Exception exErrorDis){
                    Log.e("ErrorDisplay", exErrorDis.toString());
                }
                adapterUser.notifyDataSetChanged();
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String sql_ = "select * from USER_SYSADR";
            reEmp = dataModel.LoadData_Simple(sql_);
            return null;
        }
    }
}
