package com.jwetherell.quick_response_code.MyAdapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jwetherell.quick_response_code.ModelDTO.EmployeeSyncFromDB;
import com.jwetherell.quick_response_code.R;

import java.util.List;

/**
 * Created by phamvan on 12/29/2015.
 */
public class EmployeeOfflineAdapter extends ArrayAdapter<EmployeeSyncFromDB> {
    private LayoutInflater inflater;
    public EmployeeOfflineAdapter(Context context, int resource, List<EmployeeSyncFromDB> objects) {
        super(context, R.layout.simple_emp_offline_adapter, objects);
        inflater=LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EmployeeSyncFromDB item = getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simple_emp_offline_adapter, parent, false);
        }
        ImageView img_simple_empPIC_OfflineAdapter = (ImageView)convertView.findViewById(R.id.img_simple_empPIC_OfflineAdapter);
        TextView lbl_simple_empName_OfflineAdapter = (TextView)convertView.findViewById(R.id.lbl_simple_empName_OfflineAdapter);
        TextView lbl_simple_empID_OfflineAdapter = (TextView)convertView.findViewById(R.id.lbl_simple_empID_OfflineAdapter);

        if(item!=null){
            lbl_simple_empName_OfflineAdapter.setText(item.getEMP_NM());
            lbl_simple_empID_OfflineAdapter.setText(item.getEMP_ID());
            if(item.getHAVE_IMG().equals("1")){
                img_simple_empPIC_OfflineAdapter.setImageResource(R.drawable.ic_available);
            }
            if(!item.getISCheck().equals("")){
                lbl_simple_empName_OfflineAdapter.setTextColor(Color.parseColor("#51ad23"));
                lbl_simple_empName_OfflineAdapter.setTypeface(null, Typeface.BOLD);
                lbl_simple_empID_OfflineAdapter.setTextColor(Color.parseColor("#51ad23"));
                lbl_simple_empID_OfflineAdapter.setTypeface(null, Typeface.BOLD);
            }
        }
        return convertView;
    }
}
