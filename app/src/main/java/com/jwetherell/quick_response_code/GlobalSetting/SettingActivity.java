package com.jwetherell.quick_response_code.GlobalSetting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.jwetherell.quick_response_code.Config;
import com.jwetherell.quick_response_code.GlobalData;
import com.jwetherell.quick_response_code.ModelDTO.ServerConnectModel;
import com.jwetherell.quick_response_code.R;
import com.jwetherell.quick_response_code.SQLiteService.SQLiteServerConnect;
import com.jwetherell.quick_response_code.Singleton;

import java.util.ArrayList;

public class SettingActivity extends Activity {

    LinearLayout llSetting_EditConnection, llSetting_UserNamager, llSetting_ChangePassword,
            llSetting_Exit,llSetting_ChangePasswordManual;
    Switch sw_setting_allowSyncPhoto;

    SQLiteServerConnect connectProvider;
    ArrayList<ServerConnectModel> listConnect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getActionBar().hide();

        llSetting_EditConnection = (LinearLayout) findViewById(R.id.llSetting_EditConnection);
        llSetting_UserNamager = (LinearLayout) findViewById(R.id.llSetting_UserNamager);
        llSetting_ChangePassword = (LinearLayout) findViewById(R.id.llSetting_ChangePassword);
        llSetting_Exit = (LinearLayout) findViewById(R.id.llSetting_Exit);
        llSetting_ChangePasswordManual = (LinearLayout)findViewById(R.id.llSetting_ChangePasswordManual);

        sw_setting_allowSyncPhoto = (Switch)findViewById(R.id.sw_setting_allowSyncPhoto);
        connectProvider = new SQLiteServerConnect(getApplicationContext());
        listConnect = new ArrayList<ServerConnectModel>();
        listConnect = (ArrayList<ServerConnectModel>) connectProvider.GetALLConnect();
        if(listConnect.size()>0){
            boolean daCheck = listConnect.get(0).getAllowSyncPhoto().equals("True")?true:false;
            //Log.e("DaCheck",daCheck+"");
            sw_setting_allowSyncPhoto.setChecked(daCheck);
        }
        // Display Or Gone Menu
        llSetting_UserNamager.setVisibility(View.GONE);
        llSetting_EditConnection.setVisibility(View.GONE);
        if(Singleton.getInstance().getUserName().trim().equalsIgnoreCase("admin") || Singleton.getInstance().getRoleName().equalsIgnoreCase("super")
                || Singleton.getInstance().getUserName().trim().equalsIgnoreCase("vinhtuyen")){
            llSetting_UserNamager.setVisibility(View.VISIBLE);
            llSetting_EditConnection.setVisibility(View.VISIBLE);
        }

        // Switch Events
        sw_setting_allowSyncPhoto.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                boolean isCheck = sw_setting_allowSyncPhoto.isChecked();
                GlobalData.AllowSavePhotoIntoDevice = isCheck;
                if(listConnect.size() != 0){
                    ServerConnectModel littleModel = listConnect.get(0);
                    littleModel.setAllowSyncPhoto(isCheck==true?"True":"False");
                    littleModel = connectProvider.UpdateConnect(littleModel);
                }
            }
        });

        // Events
        llSetting_ChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent config = new Intent(getApplicationContext(), SettingChangePassword.class);
                startActivity(config);
            }
        });

        llSetting_UserNamager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),SettingListUser.class);
                startActivity(i);
            }
        });

        llSetting_EditConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Config.class);
                startActivity(i);
            }
        });

        llSetting_ChangePasswordManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent i = new Intent(getApplicationContext(), ForgotPassword.class);
                    startActivity(i);
                }catch (Exception ex){
                    Log.e("OpenScreen",ex.toString());
                }
//                Toast.makeText(getApplicationContext(),"Forgot password", Toast.LENGTH_LONG).show();
            }
        });

        llSetting_Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
