/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jwetherell.quick_response_code;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.jwetherell.quick_response_code.ModelDTO.EmployeeOffline;
import com.jwetherell.quick_response_code.SQLiteService.EmployeeOfflineProvider;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;
import com.jwetherell.quick_response_code.result.ResultHandler;
import com.jwetherell.quick_response_code.result.ResultHandlerFactory;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Example Capture Activity.
 *
 * @author Justin Wetherell (phishman3579@gmail.com)
 */
public class CaptureActivity extends DecoderActivity {
    Statement statement;
    public Uri fileUri;
    Cursor cursor;
    ArrayList<Model> model = new ArrayList<Model>();
    ArrayList<String> listIdViewing = new ArrayList<String>();
    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet
            .of(ResultMetadataType.ISSUE_NUMBER,
                    ResultMetadataType.SUGGESTED_PRICE,
                    ResultMetadataType.ERROR_CORRECTION_LEVEL,
                    ResultMetadataType.POSSIBLE_COUNTRY);

    String ngaythangnam;
    private TextView statusView = null;
    private View resultView = null;
    private boolean inScanMode = false;
    Button btnCapturephoto_ReadQRCodeScreen;
    String emidscan;
    //	Context context;
//	String locale;
    private Locale myLocale;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.capture);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Log.v(TAG, "onCreate()");
        /*Button btcancel = (Button) findViewById(R.id.btsave);*/
        resultView = findViewById(R.id.result_view);
        statusView = (TextView) findViewById(R.id.status_view);
        inScanMode = false;
        btnCapturephoto_ReadQRCodeScreen = (Button) findViewById(R.id.btnCapturephoto_ReadQRCodeScreen);
        //btnCapturephoto_ReadQRCodeScreen.setVisibility(View.GONE);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        btnCapturephoto_ReadQRCodeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePicture();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void handleDecode(Result rawResult, Bitmap barcode) {
        drawResultPoints(barcode, rawResult);
        ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
        handleDecodeInternally(rawResult, resultHandler, barcode);
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition
                + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    protected void showScanner() {
        inScanMode = true;
        resultView.setVisibility(View.GONE);
        statusView.setText(R.string.msg_default_status);
        statusView.setVisibility(View.VISIBLE);
        viewfinderView.setVisibility(View.VISIBLE);
    }

    protected void showResults() {
        inScanMode = false;
        statusView.setVisibility(View.GONE);
        viewfinderView.setVisibility(View.GONE);
        resultView.setVisibility(View.VISIBLE);
    }

    // Put up our own UI for how to handle the decodBarcodeFormated contents.
    private void handleDecodeInternally(Result rawResult, ResultHandler resultHandler, Bitmap barcode) {
        onPause();

        ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
        if (barcode == null) {
            barcodeImageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon));
        } else {
            barcodeImageView.setImageBitmap(barcode);
        }
        //TextView contentsTextView = (TextView) findViewById(R.id.contents_text_view);
        CharSequence displayContents = resultHandler.getDisplayContents();
        //contentsTextView.setText(displayContents);
        emidscan = displayContents.toString();
        // Save Scan info to sqlite. ==> using for Offline Sync
        if (UtilMethod.CheckInternet(getApplicationContext()) == false) {
            DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            dateFormatter.setLenient(false);
            Date today = new Date();
            String strDate = dateFormatter.format(today);
            EmployeeOffline EMP_OFF = new EmployeeOffline();
            EMP_OFF.setEMP_ID(displayContents.toString());
            EMP_OFF.setCHK_DT(strDate);
            EMP_OFF.setIMG_URI(GlobalData.IMG_URI_Taken);
            EMP_OFF.setIMG_Base64("Scan");
            EmployeeOfflineProvider employeeOffline = new EmployeeOfflineProvider(getApplicationContext());
            if (employeeOffline.CheckExists(EMP_OFF.getEMP_ID(), EMP_OFF.getCHK_DT()) == null) {
                EmployeeOffline EMP_OFF1 = employeeOffline.CreateOfflineRecord(EMP_OFF);
                if (EMP_OFF1 != null) {
                    GlobalData.IMG_URI_Taken = "0";
                    GlobalData.Employees_Offline.add(EMP_OFF1);
                    if (!EMP_OFF1.getIMG_URI().equals("0")) {
                        Toast.makeText(getApplicationContext(), EMP_OFF1.getEMP_ID() + " Saved To Device. With Picture.", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), EMP_OFF1.getEMP_ID() + " Saved To Device.", Toast.LENGTH_LONG).show();
                    }
                    onResume();
                }
            } else {
                Toast.makeText(getApplicationContext(), EMP_OFF.getEMP_ID() + " Is Exists in this Device.", Toast.LENGTH_SHORT).show();
                onResume();
            }
            //btnCapturephoto_ReadQRCodeScreen.setVisibility(View.GONE);
        }
        if (UtilMethod.CheckInternet(getApplicationContext()) == true) {
            saveToSQLDataAction(displayContents.toString());
        }
        // Crudely scale betweeen 22 and 32 -- bigger font for shorter text
        //int scaledSize = Math.max(22, 32 - displayContents.length() / 4);
        //contentsTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, scaledSize);
    }

    // Save To SQL Data
    public void saveToSQLDataAction(String empIdScaning) {
        // Save Scan info to SQLServer
        String server = Singleton.getInstance().getServer();
        String db = Singleton.getInstance().getDatabase();
        String user = Singleton.getInstance().getUser();
        String pass = Singleton.getInstance().getPassWord();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        Connection conn = null;
        String connUrl = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            connUrl = "jdbc:jtds:sqlserver://" + server + ";" + "databaseName="
                    + db + ";user=" + user + ";password=" + pass + ";";
            conn = DriverManager.getConnection(connUrl);
            Log.e("Connected Sucess QLNS Data", "OK");
            statement = getStatement(conn);
            saveDataMethod(empIdScaning);
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Save Data Method
    public void saveDataMethod(String empIdScaning) {
        ResultSet rs = null;
        //TextView t = (TextView) findViewById(R.id.contents_text_view);
        //String empIdScaning = (String) t.getText();
        String ck_o = "Scan";
        String id;
        String sql = ("Select * from FILC06D F1 Where (F1.EMP_ID ='" + empIdScaning + "') and (F1.INT_DT=(CONVERT(nvarchar, GETDATE(),103)))");
        try {
            rs = statement.executeQuery(sql);
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            String l = Singleton.getInstance().getLanguage1();
            if (!rs.next()) {
                for (int i = 0; i < model.size(); i++) {
                    id = model.get(i).getID();
                    if (id == empIdScaning) {
                        model.get(i).setScan("scan");
                    }
                }
                String UPDATEJQueryStr = ("INSERT INTO FILC06D (EMP_ID,CK_O, MA_SCAN,INT_DT) VALUES ('"
                        + empIdScaning
                        + "',N'"
                        + 1
                        + "','"
                        + ck_o
                        + "',"
                        + "CONVERT(nvarchar, GETDATE(),103)" + ")");
                Log.w("UPDATEJQueryStr: ", UPDATEJQueryStr);
                try {
                    statement.executeUpdate(UPDATEJQueryStr);
                    onResume();
                    Context context = getApplicationContext();
                    Toast toast = Toast.makeText(context, getResources().getString(R.string.Save_success) + "           " + emidscan, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER_HORIZONTAL
                            | Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();
                } catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    onResume();
                }
            } else {
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, getResources().getString(R.string.Data_already_exists) + "           " + emidscan, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_HORIZONTAL
                        | Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
                onResume();
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    //
    // getStatement
    private Statement getStatement(Connection connection) {

        try {
            return connection.createStatement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // showMessage
    public void showMessage(String title, String message) {
        Builder builder = new Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private void capturePicture() {
        // Kiểm tra Camera trong thiết bị
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, 100);
        } else {
            Toast.makeText(getApplication(), "Camera không được hỗ trợ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data==null){
            Toast.makeText(getApplicationContext(),"Null Value",Toast.LENGTH_LONG);
            Log.e("Null_Error","Intent data is Null");
        }
        if (requestCode == 100) {
            try {
                fileUri = data.getData();
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                cursor = getContentResolver().query(fileUri,filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                GlobalData.IMG_URI_Taken = cursor.getString(columnIndex);
            }catch (Exception exx){
                Log.e("Loi_LoadHinh", exx.toString());
            }
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            cursor.close();
        } catch (Exception exx) {
            Log.e("CursorError", exx.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuchung, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        switch (item.getItemId()) {
            case R.id.action_exit2:
                onBackPressed();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return true;
    }

}
