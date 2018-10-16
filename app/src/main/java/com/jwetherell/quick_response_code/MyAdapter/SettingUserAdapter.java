package com.jwetherell.quick_response_code.MyAdapter;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jwetherell.quick_response_code.Model;
import com.jwetherell.quick_response_code.ModelDTO.UserModel;
import com.jwetherell.quick_response_code.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamvan on 7/17/2015.
 */
public class SettingUserAdapter extends ArrayAdapter<UserModel> {
    private LayoutInflater inflater;

    public SettingUserAdapter(Context context, int resource, ArrayList<UserModel> objects) {
        super(context, R.layout.list_item_manage_employee, objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public UserModel getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(UserModel item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final UserModel item = getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_manage_employee, parent, false);
        }
        LinearLayout llItem_Setting_Listuser = (LinearLayout) convertView.findViewById(R.id.llItem_Setting_Listuser);
        TextView txt_item_setting_listUser_UserName = (TextView) convertView.findViewById(R.id.txt_item_setting_listUser_UserName);
        ImageView img_item_setting_listUser_Role = (ImageView) convertView.findViewById(R.id.img_item_setting_listUser_Role);
        img_item_setting_listUser_Role.setImageResource(R.drawable.normal_user);
        if(item.getMode().equalsIgnoreCase("super")){
            img_item_setting_listUser_Role.setImageResource(R.drawable.super_user);
        }
        llItem_Setting_Listuser.setBackgroundColor(Color.parseColor(position % 2 == 0 ? "#8DB4E2" : "#DCE6F1"));
        txt_item_setting_listUser_UserName.setText(item.getUserName());
        return convertView;
    }
}
