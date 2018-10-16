package com.jwetherell.quick_response_code;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 7/14/2015.
 */
public class AdapterEmployee extends ArrayAdapter<Model> {
    private LayoutInflater inflater;
    Statement statement;
    Context cont;
    boolean flag = false;
    Connection conn = null;
    ResultSet rs = null;
    List<Model> array;
    //    Bitmap tempimage;
    String emp_tempID;
    int currentPosition;

    public AdapterEmployee(Context context, int resource, ArrayList<Model> objects) {
        super(context, R.layout.rowlishview, objects);
        inflater = LayoutInflater.from(context);
        this.array = objects;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getPosition(Model item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Model item = getItem(position);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.rowlishview, parent, false);
        }
        try {
            //Declare Controlls
            TextView txtid = (TextView) convertView.findViewById(R.id.emIdTextView);
            TextView txtname = (TextView) convertView.findViewById(R.id.nameTxt);
            ImageButton imgbtcheck = (ImageButton) convertView.findViewById(R.id.imageButton);
            ImageButton imgbtdelete = (ImageButton) convertView.findViewById(R.id.imageButton1);
            LinearLayout llRowItemEmp = (LinearLayout) convertView.findViewById(R.id.llRowItemEmp);
            LinearLayout llIsCheck = (LinearLayout) convertView.findViewById(R.id.llIsCheck);

            txtid.setTextColor(Color.BLACK);
            txtid.setTypeface(null, Typeface.NORMAL);
            txtname.setTextColor(Color.BLACK);
            txtname.setTypeface(null, Typeface.NORMAL);
            imgbtcheck.setEnabled(false);
            imgbtdelete.setEnabled(false);
            llIsCheck.setVisibility(View.GONE);

            ImageView imageview = (ImageView) convertView.findViewById(R.id.image);
            imageview.setImageResource(R.drawable.noimage);
            if (!Singleton.getInstance().getServerURL().equals("")) {
                if (item.getHaveImage()) {
                    String LocationUrl = Singleton.getInstance().getServerURL().replace("api", "Photo/Employee");
                    Picasso.with(getContext()).load(LocationUrl + item.getID() + ".jpg").error(R.drawable.noimage).into(imageview);
                }
            }
            if (Singleton.getInstance().getServerURL().equals("")) {
                if (item.getBitMap() != null) {
                    imageview.setImageBitmap(item.getBitMap());
                    System.gc();
                }
            }


            // Set Data to Controls
            if (position % 2 == 0)
                llRowItemEmp.setBackgroundColor(Color.parseColor("#8DB4E2"));
            else
                llRowItemEmp.setBackgroundColor(Color.parseColor("#DCE6F1"));

            txtid.setText(item.getID());
            txtname.setText(item.getName());

            item.setHaveImage(true);
            if (item.getImage() == null && Singleton.getInstance().getServerURL().equals("")) {
                item.setHaveImage(false);
            }
            if(!Singleton.getInstance().getServerURL().equals("")&&item.getPhotoFileName().equals("")){
                item.setHaveImage(false);
            }
            if (item.getScan().toLowerCase().equals("scan")) {
                imgbtdelete.setImageResource(R.drawable.ic_action_camera1);
            }
            if (item.getScan().toLowerCase().equals("manual")) {
                imgbtcheck.setImageResource(R.drawable.ic_action_accept1);
            }
            if (item.getScan().toLowerCase().equals("both")) {
                imgbtcheck.setImageResource(R.drawable.ic_action_accept1);
                imgbtdelete.setImageResource(R.drawable.ic_action_camera1);
            }
            if (!item.getScan().equals("")) {
                txtid.setTextColor(Color.parseColor("#51ad23"));
                txtid.setTypeface(null, Typeface.BOLD);
                txtname.setTextColor(Color.parseColor("#51ad23"));
                txtname.setTypeface(null, Typeface.BOLD);
                llIsCheck.setVisibility(View.VISIBLE);
            }

            if (item.getScan().equals("")) {
//            txtid.setTextColor(Color.parseColor("#000"));txtid.setTypeface(null, Typeface.NORMAL);
//            txtname.setTextColor(Color.parseColor("#000"));txtname.setTypeface(null, Typeface.NORMAL);
                imgbtdelete.setImageResource(R.drawable.ic_action_camera);
                imgbtcheck.setImageResource(R.drawable.ic_action_accept);
//            imageview.setImageBitmap(item.getBitMap());
//            System.gc();
            }


            llRowItemEmp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Singleton.getInstance().setEmpId(getItem(position).getID());
                    GlobalData.EMP_NAME = getItem(position).getName();
//                Intent i = new Intent(getContext(), Personnel.class);
                    Intent i = new Intent(getContext(), ActivityChooseEmployeeAction.class);
                    i.putExtra("IsTakePhoto", false);
                    i.putExtra("EmpPosition", position);
                    getContext().startActivity(i);
                }
            });

            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //byte[] hinh = getItem(position).getImage();
                    Boolean coHinh = getItem(position).getHaveImage();
                    try {
                        currentPosition = position;
                        emp_tempID = getItem(position).getID();
                        flag = getItem(position).getFlag();
                        if (flag == false) {
                            getItem(position).setScan("Manual");
                            getItem(position).setFlag(true);
                            new doSave().execute();
                            if (coHinh == false) {
                                Singleton.getInstance().setEmpId(getItem(position).getID());
                                Intent i = new Intent(getContext(), Personnel.class);
                                i.putExtra("IsTakePhoto", true);
                                i.putExtra("EmpPosition", position);
                                getContext().startActivity(i);
                            }
                        } else {
//                        item.setBipMap(item.getSaveBitMap());
                            getItem(position).setScan("");
                            getItem(position).setFlag(false);
                            new doDelete().execute();
                        }
                    } catch (Exception exx) {
                        Log.e("LoiDiemDanh", exx.toString());
                        //Toast.makeText(getContext(), "Loi: " + exx.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (Exception exAdapter) {
            Log.e("Error_Adapter", exAdapter.getMessage());
        }
        return convertView;
    }


    public void Connect() {
        try {
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
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            connUrl = "jdbc:jtds:sqlserver://" + server + ";" + "databaseName="
                    + database + ";user=" + user + ";password=" + pass + ";";
            conn = DriverManager.getConnection(connUrl);
            statement = getStatement(conn);
            Log.e("Connected Sucess QLNS Data", "OK");
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

    private Statement getStatement(Connection connection) {
        try {
            return connection.createStatement();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // Do Save
    public class doSave extends AsyncTask<Void, Void, Void> {
        boolean kt = false;
        BaseServiceModel dataModel;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataModel = new BaseServiceModel(getContext());
        }

        @Override
        protected Void doInBackground(Void... params) {
            Connect();
            String id = emp_tempID;
            String sql = ("Select * from FILC06D F1 Where (F1.EMP_ID ='" + id + "') and (F1.INT_DT=(CONVERT(nvarchar, GETDATE(),103)))");
            try {
                rs = statement.executeQuery(sql);
            } catch (SQLException e1) {
                Log.e("Loi_SQL", e1.toString());
            }
            try {
                if (!rs.next()) {
                    String ck_o = "Manual";
                    String INSERTJQueryStr = ("Insert into FILC06D values('" + id + "','1','" + ck_o + "','" + UtilMethod.GetCurrentDateDMY() + "')");
                    try {
                        dataModel.Insert_Update_Delete(INSERTJQueryStr);
                        //ResultSet rs1 = statement.executeQuery(INSERTJQueryStr);
                    } catch (Exception e) {
                        Log.e("Loi_SQL", e.toString());
                    }
                    kt = true;
                } else {
                    kt = false;
                }
                String AddDetail = "Insert into FILC06D_UP values('" + id + "',getdate(),'" + Singleton.getInstance().getUserName() + "','1','Manual')";
                dataModel.Insert_Update_Delete(AddDetail);
                //ResultSet rs2 = statement.executeQuery(AddDetail);
            } catch (SQLException e) {
                Log.e("Loi_SQL", e.toString());
            }
            try {
                conn.close();
                statement.close();
                rs.close();
            } catch (SQLException e) {
                Log.e("Loi_SQL", e.toString());
            }
            //notifyDataSetChanged();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (kt) {
                try {
                    notifyDataSetChanged();
                    Toast.makeText(getContext(), R.string.Save_success, Toast.LENGTH_SHORT).show();
                } catch (Exception ex_) {
                    Log.e("Loi_SQL", ex_.toString());
                }
            } else {
                Log.e("Loi_SQL", "Exists Item");
            }
        }
    }

    @Override
    public void remove(Model object) {
        new doDelete().execute();
    }

    // Delete Save
    public class doDelete extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                Connect();
                String id = emp_tempID;//array.get(s).getID();
                String sqldelete = "Delete from FILC06D where EMP_ID='" + id + "' and INT_DT = '" + UtilMethod.GetCurrentDateDMY() + "'";
                Boolean result_ = statement.execute(sqldelete);
                sqldelete = " Delete From FILC06D_UP where EMP_ID='" + id + "' and INT_DT >='" + UtilMethod.GetCurrentDateMDY() + "' and USER_ID='" + Singleton.getInstance().getUserName() + "'";
                result_ = statement.execute(sqldelete);
            } catch (Exception e_XX) {
                Log.e("Loi_Xoa", e_XX.toString());
            }
            try {
                conn.close();
                statement.close();
            } catch (Exception exClose) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Toast.makeText(getContext(), R.string.Delete_success, Toast.LENGTH_SHORT).show();

                notifyDataSetChanged();
            } catch (Exception ex2) {
                Log.e("Loi_EX2", ex2.toString());
            }
        }
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}

