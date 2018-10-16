package com.jwetherell.quick_response_code.MyAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jwetherell.quick_response_code.ModelDTO.DepartmentDTO;
import com.jwetherell.quick_response_code.R;

import java.util.List;

/**
 * Created by phamvan on 1/4/2016.
 */
public class DepartmentCheckAdapter extends ArrayAdapter<DepartmentDTO> {
    private LayoutInflater inflater;
    public DepartmentCheckAdapter(Context context, int resource, List<DepartmentDTO> objects) {
        super(context, R.layout.adapter_department_show_checknumber, objects);
        inflater=LayoutInflater.from(context);
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getPosition(DepartmentDTO item) {
        return super.getPosition(item);
    }

    @Override
    public DepartmentDTO getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DepartmentDTO item = getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_department_show_checknumber, parent, false);
        }
        TextView lbl_adapter_department_DepName = (TextView)convertView.findViewById(R.id.lbl_adapter_department_DepName);
        TextView lbl_adapter_department_TotalNum_EMP = (TextView)convertView.findViewById(R.id.lbl_adapter_department_TotalNum_EMP);
        TextView lbl_adapter_department_TotalNum_EMP_Check = (TextView)convertView.findViewById(R.id.lbl_adapter_department_TotalNum_EMP_Check);
        TextView lbl_adapter_department_TotalNum_EMP_UnCheck = (TextView)convertView.findViewById(R.id.lbl_adapter_department_TotalNum_EMP_UnCheck);
        //
        lbl_adapter_department_DepName.setText(item.getDEP_NM());
        lbl_adapter_department_TotalNum_EMP.setText(item.getTotalEMP()+"");
        lbl_adapter_department_TotalNum_EMP_Check.setText(item.getNumberOfCheck()+"");
        lbl_adapter_department_TotalNum_EMP_UnCheck.setText(item.getNumberUnCkeck()+"");
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
