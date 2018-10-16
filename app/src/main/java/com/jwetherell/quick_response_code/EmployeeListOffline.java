package com.jwetherell.quick_response_code;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jwetherell.quick_response_code.ModelDTO.EmployeeOffline;
import com.jwetherell.quick_response_code.ModelDTO.EmployeeSyncFromDB;
import com.jwetherell.quick_response_code.MyAdapter.AdapterOfflineEMP;
import com.jwetherell.quick_response_code.MyAdapter.EmployeeOfflineAdapter;
import com.jwetherell.quick_response_code.SQLiteService.EmployeeOfflineProvider;
import com.jwetherell.quick_response_code.SQLiteService.EmployeeSyncOfflineProvider;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmployeeListOffline extends Activity {

    LinearLayout ll_offlineEMP_DepName;
    TextView lbl_OfflineEMP_DepName, lbl_OfflineEMP_NumberOfEMP;
    ListView Lv_OfflineEMP_Synced;
    Button btn_employeOffline_ClearAllPhoto ;

    EmployeeSyncOfflineProvider empSQLite;
    public static AdapterOfflineEMP empAdapter;
    public static List<EmployeeSyncFromDB> Emps;
    EmployeeOfflineProvider empOffline;
    File destination;
    private static final int REQUEST_IMAGE = 100;
    public static int IndexEMP=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_list_offline);
        getActionBar().setTitle(getResources().getString(R.string.Title_OfflineEmployeeSynced));
        //
        btn_employeOffline_ClearAllPhoto = (Button)findViewById(R.id.btn_employeOffline_ClearAllPhoto);
        ll_offlineEMP_DepName = (LinearLayout) findViewById(R.id.ll_offlineEMP_DepName);
        lbl_OfflineEMP_DepName = (TextView) findViewById(R.id.lbl_OfflineEMP_DepName);
        lbl_OfflineEMP_NumberOfEMP = (TextView) findViewById(R.id.lbl_OfflineEMP_NumberOfEMP);
        Lv_OfflineEMP_Synced = (ListView) findViewById(R.id.Lv_OfflineEMP_Synced);
        //
        empSQLite = new EmployeeSyncOfflineProvider(getApplicationContext());
        GlobalData.depList.clear();
        GlobalData.depList = empSQLite.GetAllDepartment();
        Emps = new ArrayList<EmployeeSyncFromDB>();
        empAdapter = new AdapterOfflineEMP(getApplicationContext(),R.layout.simple_emp_offline_adapter,Emps);
        Lv_OfflineEMP_Synced.setAdapter(empAdapter);
        IndexEMP  = 0;
        //
        btn_employeOffline_ClearAllPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Emps.size()==0){
                    // Show Error
                }
                else {
                    // Call AsyncTask
                    final AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeListOffline.this, R.style.MyAlertDialogStyle);
                    builder.setTitle(getResources().getString(R.string.lbl_EmployeeListOffline_Title));
                    builder.setMessage(getResources().getString(R.string.lbl_EmployeeListOffline_MessageDelete));
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new DelEmployeePhoto().execute();
                        }
                    });
                    builder.show();
                }
            }
        });
        ll_offlineEMP_DepName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent depIntent = new Intent(getApplicationContext(), DialogDepartmentActivity.class);
                startActivityForResult(depIntent, 1001);
            }
        });
        //
        Lv_OfflineEMP_Synced.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),""+position, Toast.LENGTH_LONG).show();
                empOffline = new EmployeeOfflineProvider(getApplicationContext());
                EmployeeOffline empOffCheck = empOffline.CheckExists(Emps.get(position).getEMP_ID(), UtilMethod.GetCurrentDateDMY());
                if (empOffCheck == null) {
                    EmployeeOffline empAddSQLite = new EmployeeOffline();
                    empAddSQLite.setIMG_Base64("Manual");
                    empAddSQLite.setCHK_DT(UtilMethod.GetCurrentDateDMY());
                    empAddSQLite.setIMG_URI("0");
                    empAddSQLite.setEMP_ID(Emps.get(position).getEMP_ID());
                    //
                    empOffCheck = empOffline.CreateOfflineRecord(empAddSQLite);
                    if (empOffCheck != null) {
                        Emps.get(position).setISCheck("1");
                    }
                } else {
                    try {
                        empOffline.DeleteOfflineRecord(empOffCheck);
                        Emps.get(position).setISCheck("");
                        //empAdapter.notifyDataSetChanged();
                    } catch (Exception exxxxxxx) {
                        Log.e("Loi_BoCheck", exxxxxxx.toString());
                    }
                }
                empAdapter.notifyDataSetChanged();
            }
        });
    }

    public static void Update_ListPhoto(){
        empAdapter.notifyDataSetChanged();
    }

    void capturePicture() {
        String name = UtilMethod.dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");
        //
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
        startActivityForResult(intent, REQUEST_IMAGE);
        //
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            lbl_OfflineEMP_DepName.setText(GlobalData.DepartmentNameSelected);
            if (!GlobalData.DepartmentSelected.equals("")) {
                new LoadEMPOffline().execute();
            }
        }
    }

    class DelEmployeePhoto extends AsyncTask<Void,Void,Void>{
        List<String> DelPhotos ;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DelPhotos = new ArrayList<String>();
            for(int i=0;i<Emps.size();i++){
                if(Emps.get(i).getHAVE_IMG().equals("1")){
                    DelPhotos.add(Emps.get(i).getEMP_ID()+".png");
                }
            }
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                int limitNum = DelPhotos.size();
                boolean canDel = false;
                for(int i=0;i<limitNum;i++){
                    File fDel = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/GlintonPhoto/" +DelPhotos.get(i));
                    canDel = fDel.delete();
                }
            }catch (Exception Ex){
                Log.e("Error_DelPhoto", Ex.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            empAdapter.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.lbl_EmployeeListOffline_DeleteALL),Toast.LENGTH_LONG).show();
        }
    }

    class LoadEMPOffline extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Emps.clear();
            empSQLite = new EmployeeSyncOfflineProvider(getApplicationContext());
        }

        @Override
        protected Void doInBackground(Void... params) {
            Emps = empSQLite.GetAllOfflineRecord(GlobalData.DepartmentSelected);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            empAdapter = new AdapterOfflineEMP(getApplicationContext(),R.layout.simple_emp_offline_adapter, Emps);
            Lv_OfflineEMP_Synced.setAdapter(empAdapter);
            super.onPostExecute(aVoid);
//            empAdapter.notifyDataSetChanged();
            lbl_OfflineEMP_NumberOfEMP.setText(Emps.size() + "");
        }
    }

}
