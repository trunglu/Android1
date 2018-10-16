package com.jwetherell.quick_response_code;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jwetherell.quick_response_code.CloudServiceDB.APICloudService;
import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.ModelDTO.GetEMP_DTO;
import com.jwetherell.quick_response_code.ModelDTO.PostGetListEmpDTO;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;
import com.jwetherell.quick_response_code.qrcode.decoder.Mode;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FragmentContent extends Fragment {
    ListView l2;
    public static AdapterEmployee adapter;
    Activity t;
    public static ArrayList<Model> model;
    private ProgressDialog progressDialog;
    Statement statementl = null;
    Connection conn = null;
    String departmentID;
    LinearLayout llFragmentContent_llHeader;
    TextView fragmentContent_lblTotal, fragmentContent_lbltotalBycard, fragmentContent_lblTotalbyApp;
    final static String ARG_POSITION = "position";
    String totalEmp = "0", totalCard = "0", TotalApp = "0";
    APICloudService apiCloudService;

    List<GetEMP_DTO> listEmps;
    int pageIndex = 1;

    public FragmentContent() {
        // TODO Auto-generated constructor stub
    }


    public FragmentContent(Activity t) {
        // TODO Auto-generated constructor stub
        super();
        this.t = t;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content, container, false);
        listEmps = new ArrayList<GetEMP_DTO>();
        l2 = (ListView) view.findViewById(R.id.mainListViewContent);
        llFragmentContent_llHeader = (LinearLayout) view.findViewById(R.id.llFragmentContent_llHeader);
        fragmentContent_lblTotal = (TextView) view.findViewById(R.id.fragmentContent_lblTotal);
        fragmentContent_lbltotalBycard = (TextView) view.findViewById(R.id.fragmentContent_lbltotalBycard);
        fragmentContent_lblTotalbyApp = (TextView) view.findViewById(R.id.fragmentContent_lblTotalbyApp);
        apiCloudService = new APICloudService();
        if (Singleton.getInstance().getRoleName().equalsIgnoreCase("super")
                && !Singleton.getInstance().getDepID().equalsIgnoreCase("")) {
            llFragmentContent_llHeader.setVisibility(View.VISIBLE);
            new LoadTopMenu().execute();
        }
//        Toast.makeText(getActivity(),"Role: "+Singleton.getInstance().getRoleName(),Toast.LENGTH_LONG).show();
        l2.setScrollingCacheEnabled(true);
        model = new ArrayList<Model>();
        adapter = new AdapterEmployee(getActivity(), R.layout.rowlishview, model);
        l2.setAdapter(adapter);
        new doLoadData().execute();
//        asyncLoadMore = new doLoadMoreData();
        l2.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                try {
                    if (model.size() >= 20) {
                        new doLoadMoreData().execute(page);
                    }
                } catch (Exception e) {

                }
            }
        });

        l2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "Long Click", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        return view;
    }

    public class doLoadMoreData extends AsyncTask<Integer, Void, Void> {
        int s;
        BaseServiceModel baseServiceModel;
        PostGetListEmpDTO dtoPost;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            baseServiceModel = new BaseServiceModel(getActivity());
            listEmps.clear();
            dtoPost = new PostGetListEmpDTO();
            dtoPost.setDEP_ID(departmentID);
            dtoPost.setPageIndex(pageIndex);
        }

        @Override
        protected Void doInBackground(Integer... params) {
            // TODO Auto-generated method stub
            if (!Singleton.getInstance().getServerURL().equals("")) {
                listEmps = apiCloudService.GetListAddress(dtoPost);
                for (int i = 0; i < listEmps.size(); i++) {
                    Model ml = new Model();
                    ml.setHaveImage(false);
                    ml.setImage(null);
                    ml.setId(listEmps.get(i).getEMP_ID());
                    ml.setName(listEmps.get(i).getEMP_NM());
                    ml.setScan("");
                    ml.setPhotoFileName("");
                    if (listEmps.get(i).getMA_SCAN() != null && !listEmps.get(i).getMA_SCAN().equals("") && !listEmps.get(i).getMA_SCAN().equals("NULL")) {
                        ml.setScan(listEmps.get(i).getMA_SCAN());
                    }
                    if (listEmps.get(i).getPIC_DR() != null && !listEmps.get(i).getPIC_DR().equals("") && !listEmps.get(i).getPIC_DR().equals("NULL")) {
                        ml.setHaveImage(true);
                        ml.setPhotoFileName(listEmps.get(i).getPIC_DR());
                    }
                    model.add(ml);
                }
            } else {
                s = params[0];
                Connect();
                String depname = Singleton.getInstance().getDepViewing();
                String dep = "N'" + depname + "'";
                ResultSet rs = null;
                byte image[] = null;
                String Scan = null;
                CallableStatement ctms;
                String sql1 = "{call spPhanTrang(?,?,?)}";
                try {
                    ctms = conn.prepareCall(sql1);
                    ctms.setString(1, depname);
                    ctms.setInt(2, s);
                    ctms.setInt(3, 3);
                    rs = ctms.executeQuery();
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                //model.clear();
                int i = 0;
                try {
                    while (rs.next()) {
//                    Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);
                        System.gc();
                        String emID = rs.getString(1);
                        String emName = rs.getString(2);
                        Scan = rs.getString(6);
                        Model ml = new Model();
                        ml.setId(emID);
                        ml.setName(emName);
                        if (Scan == null) {
                            ml.setScan("");
                        }
                        if (Scan != null) {
                            ml.setScan(Scan);
                        }
                        if (Singleton.getInstance().getServerURL().equals("")) {
                            image = rs.getBytes(5);
                        }
                        ml.setImage(image);
                        ml.setHaveImage(false);
                        if (image != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                            ml.setBipMap(bitmap);
                            ml.setHaveImage(true);
                            System.gc();
                        }
                        if (image == null) {
//                        ml.setBipMap(bitmap1);
                            ml.setHaveImage(false);
                            System.gc();
                        }
                        model.add(ml);
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    conn.close();
                    statementl.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                adapter.notifyDataSetChanged();
                //if (listEmps.size() >= 20) {
                pageIndex++;
                //}
            } catch (Exception exLoad) {
                Log.e("Error_Load", exLoad.getMessage());
            }
        }
    }


    class LoadTopMenu extends AsyncTask<Void, Void, Void> {
        BaseServiceModel MyService;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyService = new BaseServiceModel(getActivity());
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //select count(*) from FILC06A where ATT_DT = getdate() and DEP_ID = '" + Singleton.getInstance().getDepID() + "'
            try {
                Date date = new Date();
                DateFormat dateMDY = new SimpleDateFormat("MM/dd/yyyy");
                String sql = "select count(*) as TSL from FILA02A where DEP_HG = '" + Singleton.getInstance().getInstance() + "'";
                int CountNumber = 0;
                ResultSet rs = MyService.LoadData_Simple(sql);
                if (rs.next()) {
                    CountNumber = rs.getInt(1);
                }
                if (CountNumber > 0) {
                    totalCard = "0";
                    totalEmp = "0";
                    TotalApp = "0";
                    llFragmentContent_llHeader.setVisibility(View.GONE);
                } else {
                    if (Singleton.getInstance().getRoleName().equalsIgnoreCase("super")
                            && !Singleton.getInstance().getDepID().equalsIgnoreCase("")) {
                        llFragmentContent_llHeader.setVisibility(View.VISIBLE);
                    }
                    sql = "select count(t.MaNV) as TongSoL " +
                            " from (select EMP_ID as MaNV from FILC01A where SWI_DT>='" + dateMDY.format(date) + "' " +
                            " and EMP_ID in (select EMP_ID from FILB01A where DEP_ID='" + Singleton.getInstance().getDepID() + "') " +
                            " group by EMP_ID) t";

                    rs = MyService.LoadData_Simple(sql);
                    if (rs.next()) {
                        totalCard = rs.getString(1);
                    }
                    sql = "select count(*) from Filb01a where DEP_ID='" + Singleton.getInstance().getDepID() + "' and ISNULL(VAC_BT,0)=0 and DEL_BT = 0 AND ATT_BT=1";
                    rs = MyService.LoadData_Simple(sql);
                    if (rs.next()) {
                        totalEmp = rs.getString(1);
                    }
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//                System.out.println(dateFormat.format(date)); //2014/08/06 15:59:48
//                Date d = new Date();
                    sql = "select count(A.INT_DT) from FILC06D A, Filb01a B where a.EMP_ID = b.EMP_ID and b.DEP_ID='" + Singleton.getInstance().getDepID() + "' and a.INT_DT = '" + dateFormat.format(date) + "'";
                    rs = MyService.LoadData_Simple(sql);
                    if (rs.next()) {
                        TotalApp = rs.getString(1);
                    }
                }

                rs.close();
                MyService.CloseConnection();
            } catch (Exception ex1) {
                Log.e("LoiEX", ex1.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            fragmentContent_lblTotal.setText(totalEmp);
            fragmentContent_lbltotalBycard.setText(totalCard);
            fragmentContent_lblTotalbyApp.setText(TotalApp);
            if (totalEmp.equals("0") && totalCard.equals("0") && TotalApp.equals("0")) {
                llFragmentContent_llHeader.setVisibility(View.GONE);
            }
        }
    }

    public void MakeDoDelete_FromActivity() {
        doDeleteAllDialogMethod();
    }

    public class doLoadData extends AsyncTask<Void, Void, Void> {
        BaseServiceModel baseServiceModel;
        PostGetListEmpDTO dtoRequest;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(getActivity(), getResources().getString(R.string.lbl_Fragment_Waiting), getResources().getString(R.string.lbl_Fragment_LoadingData));
            progressDialog.getWindow().setGravity(Gravity.BOTTOM);
            departmentID = GlobalData.DepartmentSelected;
            baseServiceModel = new BaseServiceModel(getActivity());
            listEmps.clear();
            dtoRequest = new PostGetListEmpDTO();
            dtoRequest.setDEP_ID(departmentID);
            dtoRequest.setPageIndex(pageIndex);
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            if (!Singleton.getInstance().getServerURL().equals("")) {
                listEmps = apiCloudService.GetListAddress(dtoRequest);
                for (int i = 0; i < listEmps.size(); i++) {
                    Model ml = new Model();
                    ml.setHaveImage(false);
                    ml.setImage(null);
                    ml.setId(listEmps.get(i).getEMP_ID());
                    ml.setName(listEmps.get(i).getEMP_NM());
                    ml.setScan("");
                    ml.setPhotoFileName("");
                    if (listEmps.get(i).getMA_SCAN() != null && !listEmps.get(i).getMA_SCAN().equals("") && !listEmps.get(i).getMA_SCAN().equals("NULL")) {
                        ml.setScan(listEmps.get(i).getMA_SCAN());
                    }
                    if (listEmps.get(i).getPIC_DR() != null && !listEmps.get(i).getPIC_DR().equals("") && !listEmps.get(i).getPIC_DR().equals("NULL")) {
                        ml.setHaveImage(true);
                        ml.setPhotoFileName(listEmps.get(i).getPIC_DR());
                    }
                    model.add(ml);
                }
            } else {
                Connect();
                String depname = Singleton.getInstance().getDepViewing();
                ResultSet rs = null;
                String Scan = null;
                byte image[] = null;
                CallableStatement ctms;
                String sql1 = "{call spPhanTrang(?,?,?)}";
                try {
                    ctms = conn.prepareCall(sql1);
                    ctms.setString(1, depname);
                    ctms.setInt(2, 1);
                    ctms.setInt(3, 12);
                    rs = ctms.executeQuery();
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                model.clear();
                int i = 0;
                try {
                    while (rs.next()) {
                        System.gc();
                        String emID = rs.getString(1);
                        String emName = rs.getString(2);
                        Scan = rs.getString(6);
                        Model ml = new Model();
                        ml.setHaveImage(false);
                        ml.setId(emID);
                        ml.setName(emName);
                        if (Scan == null) {
                            ml.setScan("");
                        }
                        if (Scan != null) {
                            ml.setScan(Scan);
                        }
                        if (Singleton.getInstance().getServerURL().equals("")) {
                            image = rs.getBytes(5);
                        }
                        ml.setImage(image);
                        if (image != null) {
                            ml.setHaveImage(true);
                            ml.setBipMap(BitmapFactory.decodeByteArray(image, 0, image.length));
                            System.gc();
                        }
                        if (image == null) {
                            ml.setHaveImage(false);
                            System.gc();
                        }
                        model.add(ml);
                    }

                } catch (Exception e) {
                    // TODO: handle exception
                }
                try {
                    conn.close();
                    statementl.close();
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                progressDialog.dismiss();
                adapter.notifyDataSetChanged();
                if (listEmps.size() >= 20) {
                    pageIndex++;
                }
            } catch (Exception exLoad) {
                Log.e("Error_load", exLoad.getMessage());
            }
        }

    }

    void UpdateDeleteCheck_OfArray(String empID_UnCheck) {
        for (int i = 0; i < model.size(); i++) {
            if (model.get(i).getID().equalsIgnoreCase(empID_UnCheck)) {
                model.get(i).setScan("");
                model.get(i).setFlag(false);
//                model.get(i).setBipMap(model.get(i).getSaveBitMap());
                return;
            }
        }
    }

    public class doDeleteAll extends AsyncTask<Void, Void, Void> {
        boolean isSuccessDel;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSuccessDel = false;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Connect();
            String sql = "select emp.EMP_ID  " +
                    " from FILB01A emp, FILC06D diemdanh " +
                    " where  " +
                    " diemdanh.EMP_ID = emp.EMP_ID " +
                    " and emp.DEP_ID ='" + departmentID + "' " +
                    " and diemdanh.INT_DT ='" + UtilMethod.GetCurrentDateDMY() + "'" +

                    " delete from FILC06D where EMP_ID in " +
                    "( " +
                    "select emp.EMP_ID " +
                    " from FILB01A emp, FILC06D diemdanh " +
                    " where  " +
                    " diemdanh.EMP_ID = emp.EMP_ID " +
                    " and emp.DEP_ID ='" + departmentID + "' " +
                    " and diemdanh.INT_DT ='" + UtilMethod.GetCurrentDateDMY() + "'" +
                    ")";

            sql += " delete from FILC06D_UP where EMP_ID in " +
                    "( " +
                    "select emp.EMP_ID " +
                    "from FILB01A emp, FILC06D_UP diemdanh " +
                    "where " +
                    "diemdanh.EMP_ID = emp.EMP_ID " +
                    "and emp.DEP_ID ='" + departmentID + "' " +
                    "and diemdanh.INT_DT >='" + UtilMethod.GetCurrentDateMDY() + "' " +
                    ")";
            ResultSet rsdelete = null;
            try {
                rsdelete = statementl.executeQuery(sql);
                String empID_ = "";
                while (rsdelete.next()) {
                    empID_ = rsdelete.getString(1);
                    UpdateDeleteCheck_OfArray(empID_);
                }
                isSuccessDel = true;
            } catch (SQLException e) {
                Log.e("ErrorSQL", e.toString());
            }
            try {
                conn.close();
                statementl.close();
                rsdelete.close();
            } catch (Exception closeErr) {
                Log.e("CloseConnectionError", closeErr.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (isSuccessDel == true) {
                adapter.notifyDataSetChanged();
                Toast.makeText(t, R.string.Delete_success, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aVoid);
        }
    }

    public void doDeleteAllDialogMethod() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(t);
        alertDialogBuilder.setTitle(R.string.Warning);
        alertDialogBuilder.setMessage(R.string.Do_you_want_delete_all_of_timekeeper_datas_today)
                .setCancelable(false)
                .setPositiveButton(
                        R.string.Yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new doDeleteAll().execute();
                            }
                        })
                .setNegativeButton(
                        R.string.No,
                        new DialogInterface.OnClickListener() {
                            public void onClick(
                                    DialogInterface dialog,
                                    int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void Connect() {
        String server = Singleton.getInstance().getServer();
        String database = Singleton.getInstance().getDatabase();
        String user = Singleton.getInstance().getUser();
        String pass = Singleton.getInstance().getPassWord();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        String connUrl = null;
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            connUrl = "jdbc:jtds:sqlserver://" + server + ";" + "databaseName="
                    + database + ";user=" + user + ";password=" + pass + ";";
            conn = DriverManager.getConnection(connUrl);
            statementl = getStatement(conn);
        } catch (SQLException se) {
            Log.e("SE", "SE");
            Log.e("ERROR1", se.getMessage());
            AlertDialog alertDialog = new AlertDialog.Builder(t).create();
            alertDialog.setTitle(se.getMessage());
            alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });
            alertDialog.show();
        } catch (ClassNotFoundException cl) {
            Log.e("ER2", "ER2");
            Log.e("ERROR2", cl.getMessage());
            AlertDialog alertDialog = new AlertDialog.Builder(t).create();
            alertDialog.setTitle(cl.getMessage());
            alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });
            alertDialog.show();
        } catch (Exception e) {
            Log.e("ER3", "Couldn't get database connection");
            Log.e("ERROR3", e.getMessage());
            AlertDialog alertDialog = new AlertDialog.Builder(t).create();
            alertDialog.setTitle(e.getMessage());
            alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                }
            });
            alertDialog.show();
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
    public void onDestroy() {
        super.onDestroy();
        model.clear();
        adapter.clear();
        System.gc();
    }
}