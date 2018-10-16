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
 * Created by phamvan on 12/29/2015.
 */
public class DepartmentAdapter extends ArrayAdapter<DepartmentDTO> {
    private LayoutInflater inflater;
    public DepartmentAdapter(Context context, List<DepartmentDTO> objects) {
        super(context, R.layout.simple_adapter_choose, objects);
        inflater=LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DepartmentDTO item = getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simple_adapter_choose, parent, false);
        }
        TextView lbl_simple_adapter_choose = (TextView) convertView.findViewById(R.id.lbl_simple_adapter_choose);
        if(item!=null){
            lbl_simple_adapter_choose.setText(item.getDEP_NM());
        }
        return convertView;
    }
}
