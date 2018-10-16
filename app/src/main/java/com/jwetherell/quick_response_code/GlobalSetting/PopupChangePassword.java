package com.jwetherell.quick_response_code.GlobalSetting;

import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.GlobalSetting.util.SystemUiHider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jwetherell.quick_response_code.R;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;

public class PopupChangePassword extends Activity {

    TextView popup_changepass_lblUsername;
    EditText popup_changepass_txtPassword, popup_changepass_txtConfirmPassword;
    Button popup_changepass_btnChangeNow;
    ChangePassword changProcess;
    String tenDn="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup_change_password);

        popup_changepass_lblUsername = (TextView)findViewById(R.id.popup_changepass_lblUsername);
        popup_changepass_txtPassword = (EditText)findViewById(R.id.popup_changepass_txtPassword);
        popup_changepass_txtConfirmPassword = (EditText)findViewById(R.id.popup_changepass_txtConfirmPassword);
        popup_changepass_btnChangeNow = (Button)findViewById(R.id.popup_changepass_btnChangeNow);
        changProcess = new ChangePassword();
        tenDn = getIntent().getExtras().getString("TenDN");
        popup_changepass_lblUsername.setText(tenDn);

        popup_changepass_btnChangeNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(popup_changepass_txtPassword.getText().toString().equals(popup_changepass_txtConfirmPassword.getText().toString())){
                    if(changProcess.getStatus()!= AsyncTask.Status.RUNNING){
                        changProcess = new ChangePassword();
                        changProcess.execute();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Confirm Password Is Not valid", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    class ChangePassword extends AsyncTask<Void,Void,Void>{
        BaseServiceModel dataModel ;
        boolean isSuccess;
        String newPassword_ = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataModel = new BaseServiceModel(getApplicationContext());
            isSuccess = false;
            newPassword_ = popup_changepass_txtPassword.getText().toString();
            newPassword_ = UtilMethod.MD5(newPassword_.trim());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                isSuccess = false;
                String sql = "Update USER_SYSADR Set PASS='" + newPassword_ + "' where USER_NAME='"+tenDn+"'";
                isSuccess = dataModel.Insert_Update_Delete(sql);
                dataModel.CloseConnection();
            }
            catch (Exception ex12){
                Log.e("Loi", ex12.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(isSuccess==true){
                Toast.makeText(getApplicationContext(),"Change Password Success",Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
