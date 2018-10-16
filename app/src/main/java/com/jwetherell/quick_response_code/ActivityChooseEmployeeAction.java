package com.jwetherell.quick_response_code;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;

public class ActivityChooseEmployeeAction extends Activity {

    TextView lbl_displayAction_EMPInfo;
    Button btn_showActionEMPDisplayDetail, btn_ChooseAction_EMPClearAttendent, btn_ChooseActionEMP_DisplayAttedent;
    int EMP_Pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_choose_employee_action);

        lbl_displayAction_EMPInfo = (TextView)findViewById(R.id.lbl_displayAction_EMPInfo);
        btn_showActionEMPDisplayDetail = (Button)findViewById(R.id.btn_showActionEMPDisplayDetail);
        btn_ChooseAction_EMPClearAttendent = (Button)findViewById(R.id.btn_ChooseAction_EMPClearAttendent);
        btn_ChooseActionEMP_DisplayAttedent = (Button)findViewById(R.id.btn_ChooseActionEMP_DisplayAttedent);

        lbl_displayAction_EMPInfo.setText(GlobalData.EMP_NAME);

        EMP_Pos = getIntent().getExtras().getInt("EmpPosition");

        btn_showActionEMPDisplayDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityChooseEmployeeAction.this.finish();
                Intent i = new Intent(ActivityChooseEmployeeAction.this, Personnel.class);
                i.putExtra("IsTakePhoto", getIntent().getExtras().getBoolean("IsTakePhoto"));
                i.putExtra("EmpPosition", getIntent().getExtras().getInt("EmpPosition"));
                startActivity(i);
            }
        });

        btn_ChooseActionEMP_DisplayAttedent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityChooseEmployeeAction.this.finish();
                Intent i = new Intent(ActivityChooseEmployeeAction.this, ActivityEMPTodayAttendent.class);
                startActivity(i);
            }
        });

        btn_ChooseAction_EMPClearAttendent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ClearTodayAttendent().execute();
            }
        });

    }

    class ClearTodayAttendent extends AsyncTask<Void,Void,Void>{
        String sql="";
        BaseServiceModel SQLService;
        Boolean kq;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            kq= false;
            sql="Delete From FILC06D where INT_DT='"+ UtilMethod.GetCurrentDateDMY()+"' and EMP_ID='"+Singleton.getInstance().getEmpId()+"' " +
                    "Delete From FILC06D_UP where INT_DT >='"+UtilMethod.GetCurrentDateMDY()+"' and EMP_ID='"+Singleton.getInstance().getEmpId()+"'";
            SQLService = new BaseServiceModel(getApplicationContext());
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                kq = SQLService.Insert_Update_Delete(sql);
                SQLService.CloseConnection();
            }catch (Exception exExcute){
                Log.e("Loi_Excute",exExcute.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(kq==true){
                FragmentContent.model.get(EMP_Pos).setScan("");
                FragmentContent.model.get(EMP_Pos).setFlag(false);
                FragmentContent.adapter.notifyDataSetChanged();
                onBackPressed();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
