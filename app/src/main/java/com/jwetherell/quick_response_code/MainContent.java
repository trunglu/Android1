package com.jwetherell.quick_response_code;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.method.SingleLineTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.jwetherell.quick_response_code.CloudServiceDB.BaseServiceModel;
import com.jwetherell.quick_response_code.CloudServiceDB.DataEmployee;
import com.jwetherell.quick_response_code.GlobalSetting.SettingActivity;
import com.jwetherell.quick_response_code.ModelDTO.DepartmentDTO;
import com.jwetherell.quick_response_code.ModelDTO.EmployeeSyncFromDB;
import com.jwetherell.quick_response_code.ModelDTO.UserSaveAccount;
import com.jwetherell.quick_response_code.SQLiteService.EmployeeSyncOfflineProvider;
import com.jwetherell.quick_response_code.SQLiteService.SQLiteProvider;

public class MainContent extends Activity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private Locale myLocale;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    CustomDrawerAdapter adapter;
    String locale;
    final Context context = this;
    ArrayList<DrawerItem> dataList;
    private ProgressDialog progressDialog;
    DataEmployee dataEMP; // = new DataEmployee(getApplicationContext());
    List<EmployeeSyncFromDB> resuttEMP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maincontent);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dataList = new ArrayList<DrawerItem>();
        dataEMP = new DataEmployee(getApplicationContext());
        progressDialog = new ProgressDialog(getApplicationContext());

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
                GravityCompat.START);
        adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, dataList);
        mDrawerList.setAdapter(adapter);
        new doLoadDep().execute();
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open,
                R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to
                // onPrepareOptionsMenu()
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
//        FragmentContent newFragment = new FragmentContent();
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(android.R.id.content, newFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
        Fragment fragment = new FragmentContent(MainContent.this);
        FragmentManager frgManager = getFragmentManager();
        frgManager.beginTransaction().replace(R.id.content_frame, fragment, "FragmentContent")
                .commit();
        if (!Singleton.getInstance().getDepViewing().equals("")) {
            setTitle(Singleton.getInstance().getDepViewing());
        }

        if (savedInstanceState == null) {
            //SelectItem(0);
        }

    }

    public class doLoadDep extends AsyncTask<Void, Void, Void> {
        String depid = "";
        String depname = "";
        BaseServiceModel dataModel;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dataModel = new BaseServiceModel(getApplicationContext());
        }

        @Override
        protected Void doInBackground(Void... params) {
            ResultSet rs = null;
            try {
                rs = dataModel.LoadData_Simple("select DEP_ID, DEP_NM from FILA02A");
            } catch (Exception e) {
                Log.e("LoiTruyVanSelect", e.toString());
            }
            try {
                while (rs.next()) {
                    depid = rs.getString(1);
                    depname = rs.getString(2);
                    dataList.add(new DrawerItem(depname, depid));
                }
                rs.close();
                dataModel.CloseConnection();
            } catch (SQLException e) {
                Log.e("LoiTruyVan", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menumain, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_scan:
//                MainContent.this.finish();
                Intent simple = new Intent(MainContent.this, BarcodeScanner.class);
                startActivity(simple);
                break;
            case R.id.action_deleteall:
                // Goi Method Chay Asyntask tu ben phia Fragment.
                Fragment fragment = getFragmentManager().findFragmentByTag("FragmentContent");
                if (fragment instanceof FragmentContent) {
                    ((FragmentContent) fragment).MakeDoDelete_FromActivity();
                }
                break;
            case R.id.action_Sync: {
                Intent intentSync = new Intent(MainContent.this, ActivityOfflineMainScreen.class);
                startActivity(intentSync);
                break;
            }
            case R.id.action_exit: {
                try {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            this);
                    alertDialogBuilder.setTitle(R.string.Notify);
                    // set dialog message
                    alertDialogBuilder
                            .setMessage(R.string.You_will_want_exit_program)
                            .setCancelable(false)
                            .setPositiveButton(R.string.Yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            SQLiteProvider loginProvider = new SQLiteProvider(getApplicationContext());
                                            UserSaveAccount acc_ = new UserSaveAccount(Singleton.getInstance().getUserName(), Singleton.getInstance().getPassWord());
                                            loginProvider.DeleteUserSave(acc_);
                                            //
                                            MainContent.this.finish();
                                            Intent i = new Intent(MainContent.this, Login.class);
                                            startActivity(i);
                                            Log.e("Trung Test","Exit Program.....");
                                        }
                                    })
                            .setNegativeButton(R.string.No,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,
                                                            int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } catch (Exception e) {
                    Log.e("loi", e.getMessage());
                }
                break;
            }

            case R.id.action_setting1:
                Intent i = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(i);
                break;
            case R.id.action_GetDep_for_Offline: {
                if (!GlobalData.DepartmentSelected.equals("")) {
                    try {
                        progressDialog = ProgressDialog.show(MainContent.this, getResources().getString(R.string.lbl_MainMenu_Sync_Dep_TitleWaiting), getResources().getString(R.string.lbl_MainMenu_Sync_Dep_MessageWaiting) + " : " + GlobalData.DepartmentNameSelected);
                        progressDialog.getWindow().setGravity(Gravity.BOTTOM);
                        new GetAllEMP().execute();
                        //progressDialog.dismiss();
                    } catch (Exception exxx) {
                        Log.e("LoiDialog", exxx.toString());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.lbl_MainMenu_Sync_Dep_Error), Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
        return true;
    }

    /**
     * Noted: TrungLv(2018/10/15)
     * Class xử lý việc click vào 1 item bộ phận bên left side menu
     **/
    public class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //SelectItem(position);
            String depname = dataList.get(position).getItemName();
            GlobalData.DepartmentSelected = dataList.get(position).getItemID();
            GlobalData.DepartmentNameSelected = dataList.get(position).getItemName();
            Singleton.getInstance().setDepViewing(depname);
            Singleton.getInstance().setDepID(dataList.get(position).getItemID());
//            Toast.makeText(context, "Department ID is: " + GlobalData.DepartmentSelected, Toast.LENGTH_LONG).show();
            Fragment fragment = new FragmentContent(MainContent.this);
            FragmentManager frgManager = getFragmentManager();
            frgManager.beginTransaction().replace(R.id.content_frame, fragment, "FragmentContent").commit();
            mDrawerList.setItemChecked(position, true);
            setTitle(dataList.get(position).getItemName());
            mDrawerLayout.closeDrawer(mDrawerList);
        }
    }

    class GetAllEMP extends AsyncTask<Void, Void, Void> {
        EmployeeSyncOfflineProvider empSQLite;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resuttEMP = new ArrayList<EmployeeSyncFromDB>();
            empSQLite = new EmployeeSyncOfflineProvider(getApplicationContext());
            DepartmentDTO depAdd = new DepartmentDTO(GlobalData.DepartmentSelected, GlobalData.DepartmentNameSelected);
            DepartmentDTO depCheck = empSQLite.CheckExistsDEP(GlobalData.DepartmentSelected);
            if (depCheck == null) {
                depCheck = empSQLite.CreateDepartment_Offline(depAdd);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            resuttEMP = dataEMP.GetListEMP_AddToSQLite(GlobalData.DepartmentSelected);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "Items: " + resuttEMP.size(), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }
}
