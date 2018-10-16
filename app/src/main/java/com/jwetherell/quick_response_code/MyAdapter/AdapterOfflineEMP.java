package com.jwetherell.quick_response_code.MyAdapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jwetherell.quick_response_code.Dialog_Offline_EMP;
import com.jwetherell.quick_response_code.EmployeeListOffline;
import com.jwetherell.quick_response_code.GlobalData;
import com.jwetherell.quick_response_code.ModelDTO.DepartmentDTO;
import com.jwetherell.quick_response_code.ModelDTO.EmployeeSyncFromDB;
import com.jwetherell.quick_response_code.R;
import com.jwetherell.quick_response_code.Singleton;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by phamvan on 1/8/2016.
 */
public class AdapterOfflineEMP extends ArrayAdapter<EmployeeSyncFromDB> {
    private LayoutInflater inflater;


    public AdapterOfflineEMP(Context context, int resource, List<EmployeeSyncFromDB> objects) {
        super(context, R.layout.simple_emp_offline_adapter, objects);
        inflater=LayoutInflater.from(context);
    }

    @Override
    public EmployeeSyncFromDB getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final EmployeeSyncFromDB item = getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.simple_emp_offline_adapter, parent, false);
        }
        ImageView img_simple_empPIC_OfflineAdapter = (ImageView)convertView.findViewById(R.id.img_simple_empPIC_OfflineAdapter);
        TextView lbl_simple_empName_OfflineAdapter = (TextView)convertView.findViewById(R.id.lbl_simple_empName_OfflineAdapter);
        TextView lbl_simple_empID_OfflineAdapter = (TextView)convertView.findViewById(R.id.lbl_simple_empID_OfflineAdapter);

        img_simple_empPIC_OfflineAdapter.setImageResource(R.drawable.noimage);
        lbl_simple_empName_OfflineAdapter.setTextColor(Color.parseColor("#000000"));
        lbl_simple_empName_OfflineAdapter.setTypeface(null, Typeface.NORMAL);
        lbl_simple_empID_OfflineAdapter.setTextColor(Color.parseColor("#000000"));
        lbl_simple_empID_OfflineAdapter.setTypeface(null, Typeface.NORMAL);

        if(item!=null){
            lbl_simple_empName_OfflineAdapter.setText(item.getEMP_NM());
            lbl_simple_empID_OfflineAdapter.setText(item.getEMP_ID());
            if(item.getHAVE_IMG().equals("1")){
                img_simple_empPIC_OfflineAdapter.setImageResource(R.drawable.ic_available);
                // Set Photo again if Allow save photo into this device
                if(GlobalData.AllowSavePhotoIntoDevice==true){
                    File fileName = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/GlintonPhoto/" + item.getEMP_ID() + ".png");
                    if(fileName.exists()){
                        Uri uri = Uri.fromFile(fileName);
                        Picasso.with(getContext()).load(uri).into(img_simple_empPIC_OfflineAdapter);
                    }
                }
            }
            if(!item.getHAVE_IMG().equals("1")&&!item.getHAVE_IMG().equals("0")){
                try {
                    File destination = new File(item.getHAVE_IMG());
                    FileInputStream in = new FileInputStream(destination);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 10;
                    Bitmap bmp = BitmapFactory.decodeStream(in, null, options);
                    img_simple_empPIC_OfflineAdapter.setImageBitmap(bmp);
                    System.gc();
//                    Picasso.with(getContext()).load(item.getHAVE_IMG()).error(R.drawable.ic_take_photo).into(img_simple_empPIC_OfflineAdapter);
                }
                catch (Exception ex){
                    Log.e("Error_displayIMG",ex.toString());
                }
            }
            if(!item.getISCheck().equals("")){
                lbl_simple_empName_OfflineAdapter.setTextColor(Color.parseColor("#51ad23"));
                lbl_simple_empName_OfflineAdapter.setTypeface(null, Typeface.BOLD);
                lbl_simple_empID_OfflineAdapter.setTextColor(Color.parseColor("#51ad23"));
                lbl_simple_empID_OfflineAdapter.setTypeface(null, Typeface.BOLD);
            }
        }

        img_simple_empPIC_OfflineAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(!item.getHAVE_IMG().equals("1")) {
                        GlobalData.EMP_NAME = item.getEMP_NM();
                        EmployeeListOffline.IndexEMP = position;
                        Intent intenPhoto = new Intent(getContext(), Dialog_Offline_EMP.class);
                        intenPhoto.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getContext().startActivity(intenPhoto);
                    }
                }catch (Exception e){
                    Log.e("Loi_Activity",e.toString());
                }
            }
        });

        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
