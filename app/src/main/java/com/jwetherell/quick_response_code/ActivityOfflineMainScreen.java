package com.jwetherell.quick_response_code;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;

import com.jwetherell.quick_response_code.CloudServiceDB.APICloudService;
import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.ModelDTO.APIDTO.PostSaveEMPPhotoDTO;
import com.jwetherell.quick_response_code.ModelDTO.APIDTO.ReturnDTO;
import com.jwetherell.quick_response_code.ModelDTO.DepartmentDTO;
import com.jwetherell.quick_response_code.ModelDTO.EmployeeOffline;
import com.jwetherell.quick_response_code.MyAdapter.DepartmentCheckAdapter;
import com.jwetherell.quick_response_code.SQLiteService.EmployeeOfflineProvider;
import com.jwetherell.quick_response_code.SQLiteService.EmployeeSyncOfflineProvider;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by phamvan on 12/4/2015.
 */
public class ActivityOfflineMainScreen extends Activity {
    private static final int IMG_REQUEST_CODE_=100;
    public Uri fileUri;
    TextView OfflineScreen_LabelIntroduce,OfflineScreen_lblImage, OfflineScreen_lblEmployee;
    ImageView img_OfflineScreen_qrcode,img_OfflineScreen_sync;
    public static ImageView img_OfflineScreen_takePhoto;
    LinearLayout OfflineScreen_llWaitingforSync, OfflineScreen_llBtnOffline;
    Button btn_CheckByListSynced;
    ListView lv_offlineScreen_Department;

    private SwipeRefreshLayout Sf_ReloadOffline;
    String picturePath;
    Uri selectedUriImage;
    Bitmap selectedBitmap;

    File destination;
    String imagePath;
    private static final int REQUEST_IMAGE = 100;
    private static final String TAG = "MainActivity";
    List<DepartmentDTO> Departments;
    DepartmentCheckAdapter depAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_main_screen);
        getActionBar().setTitle(getResources().getString(R.string.OfflineScreenTitle));
        // Declare Controls
        OfflineScreen_LabelIntroduce = (TextView)findViewById(R.id.OfflineScreen_LabelIntroduce);
        OfflineScreen_lblImage = (TextView)findViewById(R.id.OfflineScreen_lblImage);
        OfflineScreen_lblEmployee = (TextView)findViewById(R.id.OfflineScreen_lblEmployee);

        btn_CheckByListSynced = (Button)findViewById(R.id.btn_CheckByListSynced);

        img_OfflineScreen_qrcode = (ImageView)findViewById(R.id.img_OfflineScreen_qrcode);
        img_OfflineScreen_sync = (ImageView)findViewById(R.id.img_OfflineScreen_sync);
        img_OfflineScreen_takePhoto = (ImageView)findViewById(R.id.img_OfflineScreen_takePhoto);

        OfflineScreen_llWaitingforSync = (LinearLayout)findViewById(R.id.OfflineScreen_llWaitingforSync);
        OfflineScreen_llBtnOffline = (LinearLayout)findViewById(R.id.OfflineScreen_llBtnOffline);

        lv_offlineScreen_Department = (ListView)findViewById(R.id.lv_offlineScreen_Department);

        Sf_ReloadOffline = (SwipeRefreshLayout)findViewById(R.id.Sf_ReloadOffline);
        // Declare Events and Process for controls.
        CheckNetworkDisplay();

        Departments = new ArrayList<DepartmentDTO>();
        depAdapter = new DepartmentCheckAdapter(getApplicationContext(),R.layout.adapter_department_show_checknumber,Departments);
        lv_offlineScreen_Department.setAdapter(depAdapter);

        img_OfflineScreen_qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent simple = new Intent(getApplicationContext(), BarcodeScanner.class);
                    startActivity(simple);
                }catch (Exception exxxx){
                    Log.e("LoiMoScreen",exxxx.toString());
                }
            }
        });

        img_OfflineScreen_takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePicture();
            }
        });
        Sf_ReloadOffline.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Sf_ReloadOffline.setRefreshing(true);
                Update_NumberOffline();
                CheckNetworkDisplay();
                new LoadDep_withCheck().execute();
                Sf_ReloadOffline.setRefreshing(false);
            }
        });

        img_OfflineScreen_sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Sync Request...", Toast.LENGTH_LONG).show();
                ProcessSyncToServer();
            }
        });

        btn_CheckByListSynced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent empIntent = new Intent(getApplicationContext(),EmployeeListOffline.class);
                startActivity(empIntent);
            }
        });

        OfflineScreen_LabelIntroduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Update_NumberOffline();
                CheckNetworkDisplay();
                new LoadDep_withCheck().execute();
            }
        });

        Update_NumberOffline();
        new LoadDep_withCheck().execute();
    }

    void CheckNetworkDisplay(){
        if(UtilMethod.CheckInternet(getApplicationContext())==true){
            OfflineScreen_llBtnOffline.setVisibility(View.GONE);
            img_OfflineScreen_sync.setVisibility(View.VISIBLE);
            OfflineScreen_LabelIntroduce.setText(getResources().getString(R.string.OfflineScreen_labelSyncReady));
        }
        if(UtilMethod.CheckInternet(getApplicationContext())==false){
            OfflineScreen_llBtnOffline.setVisibility(View.VISIBLE);
            img_OfflineScreen_sync.setVisibility(View.GONE);
            OfflineScreen_LabelIntroduce.setText(getResources().getString(R.string.OfflineScreen_LabelIntroduce));
        }
    }

    private void capturePicture() {
        String name = dateToString(new Date(), "yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");
        //
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
        startActivityForResult(intent, REQUEST_IMAGE);
        //
    }

    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    class LoadDep_withCheck extends AsyncTask<Void,Void,Void>{
        EmployeeSyncOfflineProvider ModelSQLite;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Departments = new ArrayList<DepartmentDTO>();
            ModelSQLite = new EmployeeSyncOfflineProvider(getApplicationContext());
        }

        @Override
        protected Void doInBackground(Void... params) {
            Departments = ModelSQLite.GetAllDepartment_WithCheckNumber();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            depAdapter.notifyDataSetChanged();
            depAdapter = new DepartmentCheckAdapter(getApplicationContext(),R.layout.adapter_department_show_checknumber,Departments);
            lv_offlineScreen_Department.setAdapter(depAdapter);
            super.onPostExecute(aVoid);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE) {// && resultCode == Activity.RESULT_OK ){
            try {
                FileInputStream in = new FileInputStream(destination);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                imagePath = destination.getAbsolutePath();
                GlobalData.IMG_URI_Taken=(imagePath);
                //Toast.makeText(getApplicationContext(),"URI: "+GlobalData.IMG_URI_Taken,Toast.LENGTH_LONG).show();
                Bitmap bmp = BitmapFactory.decodeStream(in, null, options);
                img_OfflineScreen_takePhoto.setImageBitmap(bmp);
                System.gc();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            GlobalData.IMG_URI_Taken="0";
        }
    }

    public String getPicturePath(Uri uriImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uriImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }
    public Bitmap getThumbnail(String pathHinh) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathHinh, bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;
        int originalSize = (bounds.outHeight > bounds.outWidth) ?
                bounds.outHeight
                : bounds.outWidth;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = originalSize / 500;
        return BitmapFactory.decodeFile(pathHinh, opts);

    }

    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) {
        // Detect rotation
        int rotation = getRotation(selectedImage);
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            System.gc();
            //img.recycle();
            return rotatedImg;
        } else {
            return img;
        }
    }
    private int getRotation(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = getContentResolver().query(selectedImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int rotation = 0;
        rotation = cursor.getInt(0);
        cursor.close();
        return rotation;
    }

    public void Update_NumberOffline(){
        EmployeeOfflineProvider dblite = new EmployeeOfflineProvider(GlobalData.context_);
        int NumberScan = dblite.EmployeeScand();
        int NumberScanWithIMG = dblite.EmployeeScand_WithIMG();
        OfflineScreen_lblEmployee.setText("" + NumberScan);
        OfflineScreen_lblImage.setText("" + NumberScanWithIMG);
    }

    void ProcessSyncToServer(){
        try{
            new Process_SyncToDatabase().execute();
            new LoadDep_withCheck().execute();
        }catch (Exception Exx){
            Log.e("LoiSync",Exx.toString());
        }
    }

    public class Process_SyncToDatabase extends AsyncTask<Void,Void,Void>{
        EmployeeOfflineProvider dblite = new EmployeeOfflineProvider(GlobalData.context_);
        APICloudService apiCloudService;
        private ProgressDialog progress;
        private int Current=0;
        private List<EmployeeOffline> EMP_IDDel;
        private BaseServiceModel DBdata;
        @Override
        protected void onPreExecute() {
            DBdata = new BaseServiceModel(getApplicationContext());
            EMP_IDDel = new ArrayList<EmployeeOffline>();
            super.onPreExecute();
            GlobalData.Employees_Offline = dblite.GetAllOfflineRecord();
            progress = new ProgressDialog(ActivityOfflineMainScreen.this);
            progress.setMessage(getResources().getString(R.string.Title_SyncOfflineIntoSQL));
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setIndeterminate(true);
            progress.setMax(GlobalData.Employees_Offline.size());
            progress.show();
            apiCloudService = new APICloudService();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.dismiss();
            Update_NumberOffline();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String sqlItem = "", sqlHinh = "";
            Boolean result_item = false, IsExists = false;
            for(int i=0;i<GlobalData.Employees_Offline.size();i++){
                Current = i+1;
                result_item = false;
                EmployeeOffline emp = GlobalData.Employees_Offline.get(i);
                if(!emp.getIMG_URI().equals("0")){// Offline co Hinh
                    try {
                        picturePath = emp.getIMG_URI();
                        selectedUriImage = Uri.parse(emp.getIMG_URI());
                        selectedBitmap = getThumbnail(picturePath);
                        //selectedBitmap = rotateImageIfRequired(selectedBitmap, selectedUriImage);
                        byte[] img = null;
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                        img = bos.toByteArray();
                        System.gc();
                        if(!Singleton.getInstance().getServerURL().equals("")){
                            PostSaveEMPPhotoDTO dtoSaveImg = new PostSaveEMPPhotoDTO();
                            dtoSaveImg.setEMP_ID(emp.getEMP_ID());
                            String Base64Value = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
                            dtoSaveImg.setPIC_DR(Base64Value);
                            ReturnDTO<Boolean> model_ = apiCloudService.SavePhotoAPI(dtoSaveImg);
                        }
                        else{
                            sqlHinh = "Insert into FILB01AB(EMP_ID,PIC_DR) values(?,?)";
                            PreparedStatement sqlstatement = DBdata.GetConnection().prepareStatement(sqlHinh);
                            sqlstatement.setString(1, emp.getEMP_ID());
                            sqlstatement.setBytes(2, img);
                            sqlstatement.executeUpdate();
                        }
                    }catch (Exception exxx){
                        Log.e("LoiDecodeHinh",exxx.toString());
                    }
                }
                IsExists = false;
                sqlItem = "select * from FILC06D where INT_DT='"+emp.getCHK_DT()+"' and EMP_ID='"+emp.getEMP_ID()+"'";
                try {
                    ResultSet rs = DBdata.LoadData_Simple(sqlItem);
                    if(rs.next()){
                        IsExists = true;
                    }
                }catch (Exception sqlEx){
                    Log.e("Error_SQL",sqlEx.toString());
                }
                sqlItem = "INSERT INTO FILC06D (EMP_ID,CK_O, MA_SCAN,INT_DT) VALUES ('"
                        + emp.getEMP_ID()
                        + "',N'1','"+emp.getIMG_Base64()+"',"
                        + "'"+emp.getCHK_DT()+"') ";
                if(IsExists==false) {
                    sqlItem += "Insert into FILC06D_UP values('"+emp.getEMP_ID()+"','"+emp.getRealDT()+"','"+Singleton.getInstance().getUserName()+"','1','"+emp.getIMG_Base64()+"')";
                    result_item = DBdata.Insert_Update_Delete(sqlItem);
                    DBdata.CloseConnection();
                }
                if(IsExists == true){
                    sqlItem = "Insert into FILC06D_UP values('"+emp.getEMP_ID()+"','"+emp.getRealDT()+"','"+Singleton.getInstance().getUserName()+"','1','"+emp.getIMG_Base64()+"')";
                    result_item = DBdata.Insert_Update_Delete(sqlItem);
                    DBdata.CloseConnection();
                }
                if(result_item == true || IsExists == true){
                    EMP_IDDel.add(emp);
                }
            }
            // Clear in SQLite
            for (int i=0; i<EMP_IDDel.size(); i++){
                dblite.DeleteOfflineRecord(EMP_IDDel.get(i));
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            progress.setProgress(Current);
        }
    }
}
