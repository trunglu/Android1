package com.jwetherell.quick_response_code;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.sql.*;

import org.apache.http.util.ByteArrayBuffer;

import com.jwetherell.quick_response_code.CloudServiceDB.APICloudService;
import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.ModelDTO.APIDTO.PostSaveEMPPhotoDTO;
import com.jwetherell.quick_response_code.ModelDTO.APIDTO.ReturnDTO;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.YuvImage;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class Personnel extends Activity {
    String locale;
    private Locale myLocale;
    Handler handler;
    EditText editMsnv, editHoten, editNgaysinh, editBophan;
    Button btSave, btExit;
    ImageButton btCamera;
    ImageView img;
    //    TextView txtmsnv, txthoten, txtngaysinh, txtbophan;
    private Uri fileUri;
    String picturePath;
    Uri selectedUriImage;
    Bitmap selectedBitmap;
    Bitmap insertBitmap;
    Statement statement = null;
    Connection conn = null;
    String EMP_ID;
    String DEP_NM;
    ProgressDialog progressDialog;
    private WeakReference imageRef;
    boolean isTakePhoto;
    int adapterPosition;
    BaseServiceModel baseServiceModel;
    APICloudService apiCloudService;

    @Override
    public void onCreate(Bundle s) {
        super.onCreate(s);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.thongtinnhanvien);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        addControl();
        apiCloudService = new APICloudService();
        EMP_ID = Singleton.getInstance().getEmpId();
        imageRef = new WeakReference(img);
        DEP_NM = Singleton.getInstance().getDepViewing();

        isTakePhoto = getIntent().getExtras().getBoolean("IsTakePhoto");
        adapterPosition = getIntent().getExtras().getInt("EmpPosition");
        baseServiceModel = new BaseServiceModel(getApplicationContext());
        new doLoadData().execute();
        btExit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Personnel.this.finish();
//                selectedBitmap.recycle();
//                insertBitmap.recycle();
                onBackPressed();
//                Intent i = new Intent(Personnel.this, MainContent.class);
//                startActivity(i);
            }
        });
        btCamera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                capturePicture();
            }
        });
        btSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new doSaveImage().execute();
            }
        });

        handler = new Handler() {
            public void handlerMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
    }

    public void addControl() {
        editMsnv = (EditText) findViewById(R.id.editMsnv);
        editHoten = (EditText) findViewById(R.id.editHoten);
        editNgaysinh = (EditText) findViewById(R.id.editNgaysinh);
        editBophan = (EditText) findViewById(R.id.editBophan);

        btCamera = (ImageButton) findViewById(R.id.btcamera);
        btSave = (Button) findViewById(R.id.btsave);
        btExit = (Button) findViewById(R.id.btexit);
        img = (ImageView) findViewById(R.id.image);

        editMsnv.setEnabled(false);
        editHoten.setEnabled(false);
        editNgaysinh.setEnabled(false);
        editBophan.setEnabled(false);

        btSave.setEnabled(false);
//        btCamera.setEnabled(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * hàm xử lý lấy thumbnail để tối ưu bộ nhớ
     *
     * @param
     * @return
     */
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

    private void capturePicture() {
        // Kiểm tra Camera trong thiết bị
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // Mở camera mặc định
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            // Tiến hành gọi Capture Image intent
            startActivityForResult(intent, 100);
        } else {
            Toast.makeText(getApplication(), "Camera không được hỗ trợ", Toast.LENGTH_LONG).show();
        }
    }
    /**
     * Hàm hiển thị Camera folder và cho phép hiển thị hình người sử dụng chọn
     * lên giao diện, hình này sẽ được gửi lên Server nếu muốn
     */
    /**
     * public void processChonHinh() {
     * Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
     * intent.setType("image/*");
     * startActivityForResult(intent, 200);
     * }
     */


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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == 100 || requestCode == 200) && resultCode == RESULT_OK) {
            //Lấy URI hình kết quả trả về
            selectedUriImage = data.getData();
            //lấy đường dẫn hình
            picturePath = getPicturePath(selectedUriImage);
            //lấy thumbnail để tối ưu bộ nhớ
            selectedBitmap = getThumbnail(picturePath);
            selectedBitmap = rotateImageIfRequired(selectedBitmap, selectedUriImage);
            img.setImageBitmap(selectedBitmap);
            System.gc();
            // Auto Save Image to Server.
            new doSaveImage().execute();
            btSave.setEnabled(true);
        }
    }

    public void processChonHinh() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, 200);
    }

    private Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) {
// Detect rotation
        int rotation = getRotation();
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
            System.gc();
            img.recycle();
            return rotatedImg;
        } else {
            return img;
        }
    }

    //	/**
//	* Lấy Rotation của hình
//	* @return
//	*/
    private int getRotation() {
        String[] filePathColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = getContentResolver().query(selectedUriImage,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int rotation = 0;
        rotation = cursor.getInt(0);
        cursor.close();
        return rotation;
    }

    public Bitmap DownloadImage() {
        Connect();
        ResultSet rs = null;
        byte image[] = null;
        if(Singleton.getInstance().getServerURL().equals("")){
            String sql = ("Select PIC_DR from  FILB01AB Where EMP_ID ='" + EMP_ID + "'");
            Log.e("sql image ", sql);
            try {
                rs = statement.executeQuery(sql);
                while (rs.next()) {
                    image = rs.getBytes(1);
                }

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if(!Singleton.getInstance().getServerURL().equals("")){
            //String base64_ = baseServiceModel.LoadimgFromAnotherDBString(EMP_ID);
            //image = baseServiceModel.LoadimgFromAnotherDB(EMP_ID);
        }
        //ByteArrayInputStream imagestream = new ByteArrayInputStream(rs.getBytes(1));
        if (image != null) {
            insertBitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        }
        try {
            conn.close();
            statement.close();
            rs.close();
        } catch (Exception exErrorLoadBitmap) {
        }
        return insertBitmap;
    }

    public class doLoadData extends AsyncTask<Void, Void, Bitmap> {
        //		@Override
//	    protected void onPreExecute() {
//	        super.onPreExecute();
//	        progressDialog = ProgressDialog.show(Personnel.this, "Wait", "Downloading image...");
//	    }
        @Override
        protected Bitmap doInBackground(Void... params) {
            // TODO Auto-generated method stub
            return DownloadImage();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // TODO Auto-generated method stub
//			progressDialog.dismiss();
            LoadData();
            if (isCancelled()) {
                bitmap = null;
            }
            if (imageRef != null) {
                img = (ImageView) imageRef.get();
                if(!Singleton.getInstance().getServerURL().equals("")){
                    String ImgURL = Singleton.getInstance().getServerURL().replace("api","Photo/Employee") + EMP_ID +".jpg";
                    Picasso.with(getApplicationContext()).load(ImgURL).into(img);
                }
                if (img != null && bitmap != null && Singleton.getInstance().getServerURL().equals("")) {
                    img.setImageBitmap(bitmap);
                    System.gc();
                } else {
                    btCamera.setEnabled(true);
                }
            }
            super.onPostExecute(bitmap);
            if (isTakePhoto == true) {
                capturePicture();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            // TODO Auto-generated method stub
            super.onProgressUpdate(values);
        }

    }

    public void LoadData() {
        Connect();
        ResultSet rs = null;
        String sql = ("Select EMP_ID, EMP_NM, BIR_DT  from FILB01A Where EMP_ID= " + "'" + EMP_ID + "'");
        //Log.e("sql ", sql);
        try {
            rs = statement.executeQuery(sql);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            while (rs.next()) {
                editMsnv.setText(rs.getString(1));
                editHoten.setText(rs.getString(2));
                editNgaysinh.setText(rs.getString(3));
                Log.e("Ma so nhan vien", rs.getString(1));
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try { conn.close(); statement.close();rs.close(); }
        catch (Exception errorEx) {}
        editBophan.setText(DEP_NM);
    }

    public void SaveImage(Connection conn) throws SQLException {
        ResultSet rs1 = null;
        byte[] img = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        img = bos.toByteArray();
        System.gc();
        String sql = ("Insert into FILB01AB(EMP_ID,PIC_DR) values(?,?)");
        PreparedStatement sqlstatement = conn.prepareStatement(sql);
        sqlstatement.setString(1, EMP_ID);
        sqlstatement.setBytes(2, img);
        sqlstatement.executeUpdate();
    }

    public class doSaveImage extends AsyncTask<Void, Void, Void> {
        PostSaveEMPPhotoDTO dtoSave;
        ReturnDTO<Boolean> model_;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dtoSave = new PostSaveEMPPhotoDTO();
            dtoSave.setEMP_ID(EMP_ID);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String a= "100";
            if(!Singleton.getInstance().getServerURL().equals("")){
//                byte[] img = null;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
//                img = bos.toByteArray();
                System.gc();
                String Base64Value = Base64.encodeToString(bos.toByteArray(), Base64.DEFAULT);
                dtoSave.setPIC_DR(Base64Value);
                model_ = apiCloudService.SavePhotoAPI(dtoSave);
            }
            else {
                String server = Singleton.getInstance().getServer();
                String database = Singleton.getInstance().getDatabase();
                String user = Singleton.getInstance().getUser();
                String pass = Singleton.getInstance().getPassWord();
                // TODO Auto-generated method stub
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads().detectDiskWrites().detectNetwork()
                        .penaltyLog().build());
                String connUrl = null;
                try {
                    Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
                    connUrl = "jdbc:jtds:sqlserver://" + server + ";" + "databaseName=" + database + ";user=" + user + ";password=" + pass + ";";
                    Connection conn = DriverManager.getConnection(connUrl);
                    SaveImage(conn);
                } catch (SQLException se) {
                    Log.e("SE", "SE");
                    Log.e("ERROR1", se.getMessage());
                } catch (ClassNotFoundException cl) {
                    Log.e("ER2", "ER2");
                    Log.e("ERROR2", cl.getMessage());
                } catch (Exception e) {
                    Log.e("ER3", "Couldn't get database connection");
                    Log.e("ERROR3", e.getMessage());
                }
                try {
                    conn.close();
                    statement.close();
                } catch (Exception errorEx) {
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            Toast.makeText(getApplication(), R.string.Save_success, Toast.LENGTH_LONG).show();
//            if(isTakePhoto == true){
            FragmentContent.model.get(adapterPosition).setHaveImage(true);
            FragmentContent.model.get(adapterPosition).setBipMap(selectedBitmap);
            FragmentContent.model.get(adapterPosition).setSaveBipMap(selectedBitmap);
            FragmentContent.adapter.notifyDataSetChanged();
            onBackPressed();
//            }
        }

    }

    public void Connect() {
        String server = Singleton.getInstance().getServer();
        String database = Singleton.getInstance().getDatabase();
        String user = Singleton.getInstance().getUser();
        String pass = Singleton.getInstance().getPassWord();
        // TODO Auto-generated method stub
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        String connUrl = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            connUrl = "jdbc:jtds:sqlserver://" + server + ";" + "databaseName="
                    + database + ";user=" + user + ";password=" + pass + ";";
            conn = DriverManager.getConnection(connUrl);
            statement = getStatement(conn);
        } catch (SQLException se) {
            Log.e("SE", "SE");
            Log.e("ERROR1", se.getMessage());

        } catch (ClassNotFoundException cl) {
            Log.e("ER2", "ER2");
            Log.e("ERROR2", cl.getMessage());
        } catch (Exception e) {
            Log.e("ER3", "Couldn't get database connection");
            Log.e("ERROR3", e.getMessage());
        }
    }

    private Statement getStatement(Connection conn) {
        try {
            return conn.createStatement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] bitmapToByteArray(Bitmap bm) {
        // Create the buffer with the correct size
        int iBytes = bm.getWidth() * bm.getHeight() * 4;
        ByteBuffer buffer = ByteBuffer.allocate(iBytes);
        // Log.e("DBG", buffer.remaining()+""); -- Returns a correct number based on dimensions
        // Copy to buffer and then into byte array
        bm.copyPixelsToBuffer(buffer);
        // Log.e("DBG", buffer.remaining()+""); -- Returns 0
        return buffer.array();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuchung, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        switch (item.getItemId()) {
            case R.id.action_exit2: {
                Personnel.this.finish();
                Intent i = new Intent(Personnel.this, MainContent.class);
                startActivity(i);
            }
            break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}

