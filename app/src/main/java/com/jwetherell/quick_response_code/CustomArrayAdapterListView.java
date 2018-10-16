package com.jwetherell.quick_response_code;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CustomArrayAdapterListView extends ArrayAdapter<Model> implements OnItemClickListener {

    private final Activity context;
    private final MainContent content = new MainContent();
    ListView aListView;
    protected TextView txtid;
    protected TextView txtname;
    protected CheckBox checkbox;

    ArrayList<Model> array;

    ResultSet rs = null, rs1 = null, rs2 = null;
    Context context1;
    String ngaythangnam;
    java.util.Date dt = null;
    Statement statement;
    Context cont;
    Bitmap tempimage;
    boolean flag = false;
    Connection conn = null;
    String picturePath;
    Uri selectedUriImage;
    Bitmap selectedBitmap;
    private Uri fileUri;
    String EMP_ID = "";
    int posi;

    public static class ViewHolder {
        protected TextView txtid;
        protected TextView txtname;
        protected ImageButton imgbtcheck;
        protected ImageButton imgbtdelete;
        protected ImageView imageview;
    }

    ViewHolder viewHolder = null;

    public CustomArrayAdapterListView(Activity context, int textViewResourceId,
                                      ArrayList<Model> array, Activity cont) {
        super(context, R.layout.rowlishview, array);
        this.cont = cont;
        this.context = context;
        this.array = array;
    }

    @Override
    public View getView(final int position, View converView, ViewGroup parent) {
        viewHolder = new ViewHolder();
        if (converView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            converView = inflator.inflate(R.layout.rowlishview, parent, false);
            viewHolder.txtid = (TextView) converView
                    .findViewById(R.id.emIdTextView);
            viewHolder.txtname = (TextView) converView
                    .findViewById(R.id.nameTxt);
            viewHolder.imgbtcheck = (ImageButton) converView.findViewById(R.id.imageButton);
            viewHolder.imgbtdelete = (ImageButton) converView.findViewById(R.id.imageButton1);
            viewHolder.imgbtcheck.setEnabled(false);
            viewHolder.imgbtdelete.setEnabled(false);

            viewHolder.imageview = (ImageView) converView.findViewById(R.id.image);
            converView.setTag(viewHolder);
            converView.setTag(R.id.emIdTextView, viewHolder.txtid);
            converView.setTag(R.id.nameTxt, viewHolder.txtname);
            converView.setTag(R.id.image, viewHolder.imageview);
        } else {
            viewHolder = (ViewHolder) converView.getTag();
        }


        if (position % 2 == 0)
            converView.setBackgroundColor(Color.parseColor("#8DB4E2"));
        else
            converView.setBackgroundColor(Color.parseColor("#DCE6F1"));
        viewHolder.txtid.setText(array.get(position).getID());
        viewHolder.txtname.setText(array.get(position).getName());

        viewHolder.imageview.setImageBitmap(array.get(position).getBitMap());
        if (array.get(position).getScan().equals("Scan")) {
            viewHolder.imgbtdelete.setImageResource(R.drawable.ic_action_camera1);
        }
        if (array.get(position).getScan().equals("Manual")) {
            viewHolder.imgbtcheck.setImageResource(R.drawable.ic_action_accept1);
        }
        if (array.get(position).getScan().equals("")) {
            viewHolder.imgbtdelete.setImageResource(R.drawable.ic_action_camera);
            viewHolder.imgbtcheck.setImageResource(R.drawable.ic_action_accept);
        }

//
        viewHolder.imageview.setTag(position);
        converView.setTag(R.id.image,viewHolder.imageview);
        viewHolder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean isNullImg = array.get(position).getHaveImage();
                    flag = array.get(position).getFlag();
                    if (flag == false) {
                        tempimage = array.get(position).getBitMap();
                        array.get(position).setSaveBipMap(tempimage);
                        array.get(position).setScan("Manual");
                        notifyDataSetChanged();
                        new doSave().execute(position);
                        array.get(position).setFlag(true);
                        if (isNullImg == false) {
                            Singleton.getInstance().setEmpId(array.get(position).getID());
                            context.finish();
                            Intent i = new Intent(context, Personnel.class);
                            //i.putExtra("IsTakePhoto",true);
                            context.startActivity(i);
                        }
                    } else {

                        array.get(position).setBipMap(array.get(position).getSaveBitMap());
                        array.get(position).setScan("");
                        notifyDataSetChanged();
                        new doDelete().execute(position);
                        array.get(position).setFlag(false);
                        if (isNullImg == false) {
                            Singleton.getInstance().setEmpId(array.get(position).getID());
                            context.finish();
                            Intent i = new Intent(context, Personnel.class);
                            //i.putExtra("IsTakePhoto",true);
                            context.startActivity(i);
                        }
                    }
                } catch (Exception exx) {
                    Log.e("LoiDiemDanh", exx.toString());
                    Toast.makeText(getContext(), "Loi: " + exx.toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
        converView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Singleton.getInstance().setEmpId(array.get(position).getID());
                context.finish();
                Intent i = new Intent(context, Personnel.class);
                context.startActivity(i);
//					Toast.makeText(cont,""+position,Toast.LENGTH_SHORT).show();
            }
        });
        return converView;
    }

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public class imageClicListener implements OnClickListener {

        int position;

        public imageClicListener(int pos) {
            this.position = pos;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(cont, "" + position, Toast.LENGTH_SHORT).show();
        }
    }

    public String getPackageName() {
        Context context = getContext(); // this.getContext();
        // getApplicationContext(); etc.
        String sPackName = context.getPackageName();
        return sPackName;
    }

    public Resources getResources() {
        Context context = getContext(); // this.getContext();
        // getApplicationContext(); etc.
        Resources sResources = context.getResources();
        return sResources;
    }

    public class doSave extends AsyncTask<Integer, Void, Void> {

        boolean kt = false;
        int s;

        @Override
        protected Void doInBackground(Integer... params) {
            //return null;
            s = params[0];
            Connect();
            String id = array.get(s).getID();
            Log.e("sql", id + "");

            String sql = ("Select * from FILC06D F1 Where (F1.EMP_ID ='"
                    + id + "') and (F1.INT_DT=(CONVERT(nvarchar, GETDATE(),103)))");
            Log.e("cau lenh sql", sql);
            try {
                rs = statement.executeQuery(sql);
            } catch (SQLException e1) {
                // // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            try {
                if (!rs.next()) {

                    String ck_o = "Manual";

                    String INSERTJQueryStr = ("INSERT INTO FILC06D (EMP_ID,CK_O, MA_SCAN,INT_DT) VALUES ('"
                            + array.get(s).getID().toString()
                            + "',N'"
                            + 1
                            + "','"
                            + ck_o
                            + "',"
                            + "CONVERT(nvarchar, GETDATE(),103)" + ")");
                    //Log.e("cau lenh", INSERTJQueryStr);
                    try {
                        statement.executeQuery(INSERTJQueryStr);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    kt = true;
                } else {
                    kt = false;
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            //notifyDataSetChanged();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //SystemClock.sleep(10000);

            if (kt) {
                Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_accept1);
                array.get(s).setBipMap(image);
                notifyDataSetChanged();
                Toast.makeText(cont, R.string.Save_success, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(cont, R.string.Data_already_exists, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class doDelete extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... params) {

            int s = params[0];
            Connect();
            String id = array.get(s).getID();
            String sqldelete = "Delete from FILC06D where EMP_ID='" + id + "' and INT_DT = (CONVERT(nvarchar, GETDATE(),103))";
            ResultSet rsdelete = null;
            try {
                rsdelete = statement.executeQuery(sqldelete);
            } catch (SQLException e1) {
                // // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(cont, R.string.Delete_success, Toast.LENGTH_SHORT).show();
        }
    }

    public void Connect() {

        String server = Singleton.getInstance().getServer();
        String database = Singleton.getInstance().getDatabase();
        String user = Singleton.getInstance().getUser();
        String pass = Singleton.getInstance().getPassWord();
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
            Log.e("Connected Sucess QLNS Data", "OK");

        } catch (SQLException se) {
            Log.e("SE", "SE");
            Log.e("ERROR1", se.getMessage());
            Log.e("ERROR1", se.getMessage());

            // showMessage("Error", se.getMessage());
        } catch (ClassNotFoundException cl) {
            Log.e("ER2", "ER2");
            Log.e("ERROR2", cl.getMessage());
            Log.e("ERROR2", cl.getMessage());
            // showMessage("Error", cl.getMessage());
        } catch (Exception e) {
            Log.e("ER3", "Couldn't get database connection");
            Log.e("ERROR3", e.getMessage());
            Log.e("ERROR3", e.getMessage());
            // showMessage("Error", e.getMessage());
        }
    }

    private Statement getStatement(Connection connection) {

        try {
            return connection.createStatement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub

    }

}
