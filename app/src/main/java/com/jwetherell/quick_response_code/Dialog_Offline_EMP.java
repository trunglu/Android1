package com.jwetherell.quick_response_code;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jwetherell.quick_response_code.ModelDTO.EmployeeOffline;
import com.jwetherell.quick_response_code.SQLiteService.EmployeeOfflineProvider;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Dialog_Offline_EMP extends Activity {

    TextView lbl_displayActionOFFLINE_EMPInfo;
    Button btn_Dialog_EMP_Offline_Takepicture;
    File destination;
    private static final int REQUEST_IMAGE = 100;
    String imagePath;
    EmployeeOfflineProvider empOffline;
    String PhotoFileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog__offline__emp);

        lbl_displayActionOFFLINE_EMPInfo = (TextView)findViewById(R.id.lbl_displayActionOFFLINE_EMPInfo);
        btn_Dialog_EMP_Offline_Takepicture = (Button)findViewById(R.id.btn_Dialog_EMP_Offline_Takepicture);
        lbl_displayActionOFFLINE_EMPInfo.setText(GlobalData.EMP_NAME);
        empOffline = new EmployeeOfflineProvider(getApplicationContext());

        btn_Dialog_EMP_Offline_Takepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePicture();
            }
        });
    }

    void capturePicture() {
        String name = UtilMethod.dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        PhotoFileName = name;
        destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");
        //
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
            startActivityForResult(intent, REQUEST_IMAGE);
        }
        //
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, REQUEST_IMAGE);
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {// && resultCode == Activity.RESULT_OK ){
            try {

                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                    //Toast.makeText(getApplicationContext(),"larger than 5.0", Toast.LENGTH_LONG).show();
                    if(resultCode == RESULT_OK){
                        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), "");
                        if (!mediaStorageDir.exists()){
                            if (!mediaStorageDir.mkdirs()){
                                return ;
                            }
                        }
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");

                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                        File F1 = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
                        PhotoFileName =  F1.getPath().toString();

                        String root = Environment.getExternalStorageDirectory().toString();
                        File myDir = new File(mediaStorageDir.getPath() + File.separator );
                        myDir.mkdirs();

                        String fname = "IMG_"+ timeStamp + ".jpg";
                        File file = new File (myDir, fname);
                        if (file.exists ()) file.delete ();
                        try {
                            FileOutputStream out = new FileOutputStream(file);
                            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            out.flush();
                            out.close();
                            System.gc();
                            // Update For Adapter
                            String EmpID = EmployeeListOffline.Emps.get(EmployeeListOffline.IndexEMP).getEMP_ID();
                            EmployeeOffline empOffCheck = empOffline.CheckExists(EmpID, UtilMethod.GetCurrentDateDMY());
                            if (empOffCheck == null) {
                                EmployeeOffline empAddSQLite = new EmployeeOffline();
                                empAddSQLite.setIMG_Base64("Manual");
                                empAddSQLite.setCHK_DT(UtilMethod.GetCurrentDateDMY());
                                empAddSQLite.setIMG_URI(PhotoFileName);
                                empAddSQLite.setEMP_ID(EmployeeListOffline.Emps.get(EmployeeListOffline.IndexEMP).getEMP_ID());
                                //
                                empOffCheck = empOffline.CreateOfflineRecord(empAddSQLite);
                                EmployeeListOffline.Emps.get(EmployeeListOffline.IndexEMP).setHAVE_IMG(PhotoFileName);
                                if (empOffCheck != null) {
                                    EmployeeListOffline.Emps.get(EmployeeListOffline.IndexEMP).setISCheck("2");
                                }
                            }
                            //
                            GlobalData.IMG_URI_Taken = PhotoFileName;
                            onBackPressed();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    imagePath = PhotoFileName;
                    //return;
                }
                if(Build.VERSION.SDK_INT<Build.VERSION_CODES.LOLLIPOP) {
                    imagePath = destination.getAbsolutePath();
                    String EmpID = EmployeeListOffline.Emps.get(EmployeeListOffline.IndexEMP).getEMP_ID();
                    EmployeeOffline empOffCheck = empOffline.CheckExists(EmpID, UtilMethod.GetCurrentDateDMY());
                    if (empOffCheck == null) {
                        EmployeeOffline empAddSQLite = new EmployeeOffline();
                        empAddSQLite.setIMG_Base64("Manual");
                        empAddSQLite.setCHK_DT(UtilMethod.GetCurrentDateDMY());
                        empAddSQLite.setIMG_URI(imagePath);
                        empAddSQLite.setEMP_ID(EmployeeListOffline.Emps.get(EmployeeListOffline.IndexEMP).getEMP_ID());
                        //
                        empOffCheck = empOffline.CreateOfflineRecord(empAddSQLite);
                        EmployeeListOffline.Emps.get(EmployeeListOffline.IndexEMP).setHAVE_IMG(imagePath);
                        if (empOffCheck != null) {
                            EmployeeListOffline.Emps.get(EmployeeListOffline.IndexEMP).setISCheck("2");
                        }
                    }
                }else {
                    try {
                        //empOffline.DeleteOfflineRecord(empOffCheck);
                        //Emps.get(position).setISCheck("");
                        //empAdapter.notifyDataSetChanged();
                    } catch (Exception exxxxxxx) {
                        Log.e("Loi_BoCheck", exxxxxxx.toString());
                    }
                }

            } catch (Exception e) {
                Log.e("Loi_",e.toString());
                return;
            }
        } else {
            GlobalData.IMG_URI_Taken = imagePath;
        }
        EmployeeListOffline.empAdapter.notifyDataSetChanged();
        onBackPressed();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        EmployeeListOffline.Update_ListPhoto();
    }
}
