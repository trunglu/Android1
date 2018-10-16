package com.jwetherell.quick_response_code;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.ModelDTO.EmployeeOffline;
import com.jwetherell.quick_response_code.SQLiteService.EmployeeOfflineProvider;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;
import com.jwetherell.quick_response_code.camera.CameraPreview;

import net.sourceforge.zbar.*;

import java.sql.ResultSet;

public class BarcodeScanner extends Activity {
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    String DisplayMessage_ = "";
    private Button scanButton;
    private ImageScanner scanner;
    BaseServiceModel SQLModel;
    private boolean barcodeScanned = false;
    private boolean previewing = true;
    String Current_Scan_EMPID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        initControls();
        SQLModel = new BaseServiceModel(getApplicationContext());
    }

    private void initControls() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        // Instance barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, net.sourceforge.zbar.Config.X_DENSITY, 3);
        scanner.setConfig(0, net.sourceforge.zbar.Config.Y_DENSITY, 3);

        mPreview = new CameraPreview(BarcodeScanner.this, mCamera, previewCb, autoFocusCB);
        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
        scanButton = (Button) findViewById(R.id.ScanButton);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (barcodeScanned) {
                    barcodeScanned = false;
                    mCamera.setPreviewCallback(previewCb);
                    mCamera.startPreview();
                    previewing = true;
                    mCamera.autoFocus(autoFocusCB);
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            releaseCamera();
        }
        return super.onKeyDown(keyCode, event);
    }

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    Log.i("<<<<<<Asset Code>>>>> ",
                            "<<<<Bar Code>>> " + sym.getData());
                    String scanResult = sym.getData().trim();
                    showAlertDialog(scanResult);
//                    Toast.makeText(BarcodeScanner.this, scanResult, Toast.LENGTH_SHORT).show();
                    barcodeScanned = true;
                    break;
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 100);
        }
    };

    private void showAlertDialog(final String message) {
        DisplayMessage_ = "";
        if (UtilMethod.CheckInternet(getApplicationContext()) == true) {
            Current_Scan_EMPID = message;
            new SaveEMPToDB().execute();
        } else {

            EmployeeOffline EMP_OFF = new EmployeeOffline();
            EMP_OFF.setEMP_ID(message);
            EMP_OFF.setCHK_DT(UtilMethod.GetCurrentDateDMY());
            EMP_OFF.setIMG_URI(GlobalData.IMG_URI_Taken);
            EMP_OFF.setIMG_Base64("Scan");
            //
            EmployeeOfflineProvider employeeOffline = new EmployeeOfflineProvider(getApplicationContext());
            if (employeeOffline.CheckExists(EMP_OFF.getEMP_ID(), EMP_OFF.getCHK_DT()) == null) {
                EmployeeOffline EMP_OFF1 = employeeOffline.CreateOfflineRecord(EMP_OFF);
                if (EMP_OFF1 != null) {
                    GlobalData.IMG_URI_Taken = "0";
                    GlobalData.Employees_Offline.add(EMP_OFF1);
                    if (!EMP_OFF1.getIMG_URI().equals("0")) {
                        ActivityOfflineMainScreen.img_OfflineScreen_takePhoto.setImageResource(R.drawable.ic_take_photo);
                        DisplayMessage_ = getResources().getString(R.string.Title_ScabBarcode_WithPhoto).replace("{id}", message);
                    } else {
                        DisplayMessage_ = getResources().getString(R.string.Title_ScabBarcode_NoPhoto).replace("{id}", message);
                    }
                }
            } else {
                DisplayMessage_ = getResources().getString(R.string.Title_ScabBarcode_ExistsInSQLite).replace("{id}", message);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new AlertDialog.Builder(BarcodeScanner.this)
                            .setTitle(getResources().getString(R.string.Title_ScabBarcode_))
                            .setMessage(DisplayMessage_)
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue
                                    barcodeScanned = false;
                                    mCamera.setPreviewCallback(previewCb);
                                    mCamera.startPreview();
                                    previewing = true;
                                    mCamera.autoFocus(autoFocusCB);
                                }
                            }).create().show();
                }
            });
        }

        try {

        } catch (Exception exxx) {
            Log.e("LoiDialog", exxx.toString());
        }
    }

    class SaveEMPToDB extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progressDialog;
        String sqlCheck = "", sqlInsert = "";
        ResultSet rs;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                progressDialog = new ProgressDialog(BarcodeScanner.this);
                progressDialog = ProgressDialog.show(BarcodeScanner.this,
                        getResources().getString(R.string.Message_Save_Scan_EMP_Into_DB_Title),
                        getResources().getString(R.string.Message_Save_Scan_EMP_Into_DB).replace("{id}",Current_Scan_EMPID));
                progressDialog.getWindow().setGravity(Gravity.BOTTOM);
                sqlCheck = "Select * from FILC06D F1 Where (F1.EMP_ID ='" + Current_Scan_EMPID + "') and (F1.INT_DT='" + UtilMethod.GetCurrentDateDMY() + "'))";
            }catch (Exception ex){
                Log.e("Loi_",ex.toString());
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            rs = SQLModel.LoadData_Simple(sqlCheck);
            try {
                if(rs==null){
                    sqlInsert = "INSERT INTO FILC06D (EMP_ID,CK_O, MA_SCAN,INT_DT) VALUES ('"+Current_Scan_EMPID+"','1','Scan','"+UtilMethod.GetCurrentDateDMY()+"')";
                    sqlInsert += "Insert into FILC06D_UP values('"+Current_Scan_EMPID+"',getdate(),'"+Singleton.getInstance().getUserName()+"','1','Scan')";
                }
                else {
                    if (rs.next()) {// Exists
                        sqlInsert = "Insert into FILC06D_UP values('" + Current_Scan_EMPID + "',getdate(),'" + Singleton.getInstance().getUserName() + "','1','Scan')";
                    }
                }
                Boolean kq = SQLModel.Insert_Update_Delete(sqlInsert);
            } catch (Exception exx) {
                Log.e("Loi_SQL", exx.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
            barcodeScanned = false;
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            previewing = true;
            mCamera.autoFocus(autoFocusCB);
        }
    }

    @Override
    public void onBackPressed() {
        SQLModel.CloseConnection();
        super.onBackPressed();
    }
}
