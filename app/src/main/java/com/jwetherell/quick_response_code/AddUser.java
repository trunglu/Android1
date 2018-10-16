package com.jwetherell.quick_response_code;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.GlobalSetting.SettingListUser;
import com.jwetherell.quick_response_code.ModelDTO.UserModel;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by ADMIN on 6/27/2015.
 */
public class AddUser extends Activity{

    Button btadd, btexit;
    EditText txt_popupCreateUser, txt_popupCreateUserPassword, editTextPassRetype;
    Switch activity_AddNewAccount_SwitchMode;
    doAdduser AddNewUser ;
    Statement statementl;
    Connection conn = null;
    public void onCreate(Bundle s) {
        super.onCreate(s);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adduser);
        btadd = (Button)findViewById(R.id.btadduser);
        btexit=(Button)findViewById(R.id.btexitadduser);
        txt_popupCreateUser = (EditText)findViewById(R.id.txt_popupCreateUser);
        txt_popupCreateUserPassword =(EditText)findViewById(R.id.txt_popupCreateUserPassword);
        editTextPassRetype = (EditText)findViewById(R.id.editTextPassRetype);
        activity_AddNewAccount_SwitchMode = (Switch)findViewById(R.id.activity_AddNewAccount_SwitchMode);

        AddNewUser = new doAdduser();
        btadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_popupCreateUser.getText().toString().equals("")||txt_popupCreateUserPassword.getText().toString().equals("") || editTextPassRetype.getText().toString().equalsIgnoreCase(""))
                {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Activity_AddUser_EmptyUser),Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!txt_popupCreateUserPassword.getText().toString().equals(editTextPassRetype.getText().toString()))
                {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Activity_AddUser_WrongConfirmPassword),Toast.LENGTH_SHORT).show();
                    return;
                }
                if(AddNewUser.getStatus() != AsyncTask.Status.RUNNING) {
                    AddNewUser.execute();
                }
            }
        });
        btexit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddUser.this.finish();
            }
        });
    }

    public class doAdduser extends AsyncTask<Void, Void, Void>{
        BaseServiceModel dataModel ;
        boolean isSuccess;
        String UserMode = "";
        String userName_="", password_ = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataModel = new BaseServiceModel(getApplicationContext());
            UserMode = activity_AddNewAccount_SwitchMode.isChecked() == true ? "super": "nomal";
            userName_ = txt_popupCreateUser.getText().toString();
            password_ = txt_popupCreateUserPassword.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                isSuccess = false;
                String sql = "insert into USER_SYSADR values ('" + userName_ + "'," +
                        "'" + UtilMethod.MD5(password_.trim()) + "', '"+UserMode+"')";
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
            if(isSuccess){
                try {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Activity_AddUser_Alert_Success), Toast.LENGTH_SHORT).show();
                    SettingListUser.AddMemberToList(userName_,password_,UserMode);
                    //SettingListUser.Models.add(new UserModel(txt_popupCreateUser.getText().toString(), txt_popupCreateUserPassword.getText().toString()));
                    //SettingListUser.adapterUser.notifyDataSetChanged();
                    onBackPressed();
                }catch (Exception exDone){
                    Log.e("Error_Display", exDone.toString());
                }
            }
        }
    }



}
