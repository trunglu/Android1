package com.jwetherell.quick_response_code.MyAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jwetherell.quick_response_code.ModelDTO.AttendentDetailDTO;
import com.jwetherell.quick_response_code.R;

import java.util.List;

/**
 * Created by phamvan on 1/6/2016.
 */
public class SimpleAttendentAdapter extends ArrayAdapter<AttendentDetailDTO> {
    private LayoutInflater inflater;

    public SimpleAttendentAdapter(Context context, int resource, List<AttendentDetailDTO> objects) {
        super(context, R.layout.adapter_attendent_of_employee, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public AttendentDetailDTO getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AttendentDetailDTO item = getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_attendent_of_employee, parent, false);
        }
        TextView lbl_adapter_attendent_AdminName = (TextView) convertView.findViewById(R.id.lbl_adapter_attendent_AdminName);
        TextView lbl_adapter_attendent_AttendentTime = (TextView) convertView.findViewById(R.id.lbl_adapter_attendent_AttendentTime);
        TextView lbl_adapter_attendent_TakenBy = (TextView) convertView.findViewById(R.id.lbl_adapter_attendent_TakenBy);
        if (item != null) {
            lbl_adapter_attendent_AdminName.setText(item.getUSER_NAME());
            lbl_adapter_attendent_AttendentTime.setText(item.getINT_DT());
            lbl_adapter_attendent_TakenBy.setText(item.getMA_SCAN());
        }
        return convertView;
    }
}
