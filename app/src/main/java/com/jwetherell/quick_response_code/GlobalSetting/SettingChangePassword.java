package com.jwetherell.quick_response_code.GlobalSetting;

import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.GlobalSetting.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jwetherell.quick_response_code.R;
import com.jwetherell.quick_response_code.Singleton;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;

public class SettingChangePassword extends Activity {

    EditText settingChangepass_txt_Oldpass, settingChangepass_txt_Newpass, settingChangepass_txt_Retype;
    Button btn_Setting_changepassword, btn_Setting_Goback;
    TextView Setting_lbl_UserName_ChangePass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_change_password);
        getActionBar().hide();
        //Declare Controls
        btn_Setting_changepassword = (Button)findViewById(R.id.btn_Setting_changepassword);
        btn_Setting_Goback = (Button)findViewById(R.id.btn_Setting_Goback);
        Setting_lbl_UserName_ChangePass = (TextView)findViewById(R.id.Setting_lbl_UserName_ChangePass);
        settingChangepass_txt_Oldpass = (EditText) findViewById(R.id.settingChangepass_txt_Oldpass);
        settingChangepass_txt_Newpass = (EditText) findViewById(R.id.settingChangepass_txt_Newpass);
        settingChangepass_txt_Retype = (EditText) findViewById(R.id.settingChangepass_txt_Retype);
        //Declare Events
        Setting_lbl_UserName_ChangePass.setText(Singleton.getInstance().getUserName());
        btn_Setting_Goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_Setting_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(settingChangepass_txt_Oldpass.getText().toString().equalsIgnoreCase("") ||
                        settingChangepass_txt_Newpass.getText().toString().equalsIgnoreCase("")||
                        settingChangepass_txt_Retype.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.Setting_alert_ChangePassword_input),Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!settingChangepass_txt_Newpass.getText().toString().equals(settingChangepass_txt_Retype.getText().toString())){
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.Setting_alert_ChangePassword_inputConfirm),Toast.LENGTH_SHORT).show();
                    return;
                }
                new ChangeuserPassword().execute();
            }
        });
    }


    class ChangeuserPassword extends AsyncTask<Void,Void,Void>{
        BaseServiceModel dataModel;
        boolean isSuccess;
        String newPassword_ = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataModel = new BaseServiceModel(getApplicationContext());
            isSuccess = false;
            newPassword_ = settingChangepass_txt_Newpass.getText().toString();
            newPassword_ = UtilMethod.MD5(newPassword_);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext()
                    ,getResources().getString(R.string.Setting_alert_ChangePassword_Success)
                    ,Toast.LENGTH_SHORT).show();
            settingChangepass_txt_Oldpass.setText("");
            settingChangepass_txt_Newpass.setText("");
            settingChangepass_txt_Retype.setText("");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String sql = "Update USER_SYSADR Set PASS ='" + newPassword_ + "' where USER_NAME='"+Singleton.getInstance().getUserName()+"'";
            isSuccess = dataModel.Insert_Update_Delete(sql);
            return null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
