package com.jwetherell.quick_response_code;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jwetherell.quick_response_code.ModelDTO.DepartmentDTO;
import com.jwetherell.quick_response_code.MyAdapter.DepartmentAdapter;
import com.jwetherell.quick_response_code.SQLiteService.EmployeeSyncOfflineProvider;

import java.util.List;

public class DialogDepartmentActivity extends Activity {
    ListView lv_opoup_Dep;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_department);
        lv_opoup_Dep=(ListView)findViewById(R.id.lv_opoup_Dep);
        //
        DepartmentAdapter adapter = new DepartmentAdapter(getApplicationContext(),GlobalData.depList);
        lv_opoup_Dep.setAdapter(adapter);

        lv_opoup_Dep.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GlobalData.DepartmentNameSelected = GlobalData.depList.get(position).getDEP_NM();
                GlobalData.DepartmentSelected = GlobalData.depList.get(position).getDEP_ID();
                setResult(1001,new Intent().putExtra("ID",GlobalData.DepartmentSelected).putExtra("NAME",GlobalData.DepartmentNameSelected));
                onBackPressed();
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
