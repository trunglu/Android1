package com.jwetherell.quick_response_code;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.ModelDTO.AttendentDetailDTO;
import com.jwetherell.quick_response_code.MyAdapter.SimpleAttendentAdapter;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ActivityEMPTodayAttendent extends Activity {

    ImageView lbl_Attendent_Activity_Img;
    TextView lbl_EMPAttendent_Activity_EMP_ID, lbl_Attendent_Activity_EMP_Name, lbl_Attendent_Activity_DepName;
    ListView lbl_Attendent_Activity_LVAttendent;
    List<AttendentDetailDTO> Attendents;
    BaseServiceModel SQLService;
    SimpleAttendentAdapter adapterAttendent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_emptoday_attendent);

        lbl_Attendent_Activity_Img = (ImageView)findViewById(R.id.lbl_Attendent_Activity_Img);
        lbl_Attendent_Activity_LVAttendent = (ListView)findViewById(R.id.lbl_Attendent_Activity_LVAttendent);
        lbl_EMPAttendent_Activity_EMP_ID = (TextView)findViewById(R.id.lbl_EMPAttendent_Activity_EMP_ID);
        lbl_Attendent_Activity_EMP_Name = (TextView)findViewById(R.id.lbl_Attendent_Activity_EMP_Name);
        lbl_Attendent_Activity_DepName = (TextView)findViewById(R.id.lbl_Attendent_Activity_DepName);

        Attendents = new ArrayList<AttendentDetailDTO>();
        adapterAttendent = new SimpleAttendentAdapter(getApplicationContext(),R.layout.adapter_attendent_of_employee,Attendents);
        lbl_Attendent_Activity_LVAttendent.setAdapter(adapterAttendent);

        new LoadListAttendent().execute();
    }

    class LoadListAttendent extends AsyncTask<Void,Void,Void>{
        String sql = "", sql_Detail = "";
        Model empDTO ;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            empDTO = new Model();
            SQLService = new BaseServiceModel(getApplicationContext());
            sql = "Select F1.*,F2.USER_NAME from FILC06D_UP F1, USER_SYSADR F2 where F1.USER_ID = F2.USER_NAME and F1.INT_DT>='"+ UtilMethod.GetCurrentDateMDY()+"' and F1.EMP_ID='"+Singleton.getInstance().getEmpId()+"' ";

            sql_Detail = "select top 10 F1.DEP_NM, F1.DEP_N1, F2.EMP_ID, F2.EMP_NM, F2.EMP_N1, F3.PIC_DR " +
                    "from FILA02A F1, FILB01A F2 " +
                    "left join FILB01AB F3 on F2.EMP_ID =F3.EMP_ID " +
                    "where F1.DEP_ID = F2.DEP_ID and F2.EMP_ID='"+Singleton.getInstance().getEmpId()+"'";

            Attendents.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try{
                ResultSet rs = SQLService.LoadData_Simple(sql);
                ResultSet rsEMP = SQLService.LoadData_Simple(sql_Detail);
                if(rs!=null){
                    while(rs.next()){
                        AttendentDetailDTO dto = new AttendentDetailDTO();
                        dto.setEMP_ID(rs.getString(1));
                        dto.setINT_DT(rs.getString(2));
                        dto.setUSER_ID(rs.getString(3));
                        dto.setMA_SCAN(rs.getString(5));
                        dto.setUSER_NAME(rs.getString(6));
                        Attendents.add(dto);
                    }
                }
                if(rsEMP!=null){
                    if(rsEMP.next()){
                        empDTO.setId(rsEMP.getString(3));
                        empDTO.setName(rsEMP.getString(4));
                        empDTO.setDep_Name(rsEMP.getString(1));
                        empDTO.setImage(rsEMP.getBytes(6));
                    }
                    rsEMP.close();
                }
                rs.close();
            }catch (Exception exLoad){
                Log.e("Error_Load",exLoad.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            lbl_EMPAttendent_Activity_EMP_ID.setText(empDTO.getID());
            lbl_Attendent_Activity_EMP_Name.setText(empDTO.getName());
            lbl_Attendent_Activity_DepName.setText(empDTO.getDep_Name());
            if(empDTO.getImage()!=null){
                lbl_Attendent_Activity_Img.setImageBitmap(BitmapFactory.decodeByteArray(empDTO.getImage(),0,empDTO.getImage().length));
                System.gc();
            }
            SQLService.CloseConnection();
            adapterAttendent.notifyDataSetChanged();
        }
    }
}
