package com.jwetherell.quick_response_code.CloudServiceDB;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.jwetherell.quick_response_code.GlobalData;
import com.jwetherell.quick_response_code.ModelDTO.EmployeeSyncFromDB;
import com.jwetherell.quick_response_code.ModelDTO.GetEMP_DTO;
import com.jwetherell.quick_response_code.ModelDTO.PostGetListEmpDTO;
import com.jwetherell.quick_response_code.SQLiteService.EmployeeSyncOfflineProvider;
import com.jwetherell.quick_response_code.Singleton;
import com.jwetherell.quick_response_code.UtilMethod.UtilMethod;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by phamvan on 12/28/2015.
 */
public class DataEmployee {
    String getNumberEMPOfDep = "SELECT count(*) as TongSo " +
            " FROM FILB01A F1 left join FILC06D F4 on F1.EMP_ID = F4.EMP_ID and F4.INT_DT = CONVERT(nvarchar, GETDATE(),103)" +
            " WHERE F1.DEP_ID = '{depID}' and ISNULL(F1.VAC_BT,0)=0 and F1.DEL_BT = 0 AND F1.ATT_BT=1";

    String getEMP_ByDepID = "select * from " +
            "(SELECT F1.EMP_ID, F1.EMP_NM, ROW_NUMBER() OVER (ORDER BY F1.EMP_ID DESC) AS Pages, (case when F3.PIC_DR is null then 0 else 1 end) as CoHinh ,(case when F3.PIC_DR is null then '0' else F3.PIC_DR end) HinhAnh " +
            "FROM FILB01A F1 left join FILC06D F4 on F1.EMP_ID = F4.EMP_ID and F4.INT_DT = CONVERT(nvarchar, GETDATE(),103) " +
            " left join FILB01AB F3 on F1.EMP_ID =F3.EMP_ID  " +
            "WHERE F1.DEP_ID = '{depID}'  " +
            "and ISNULL(F1.VAC_BT,0)=0 and F1.DEL_BT = 0 AND F1.ATT_BT=1) temp " +
            "where temp.Pages>={fromIndex} and temp.Pages<={toIndex}";

    BaseServiceModel dataModel;
    private int TotalItem = 0, PageIndex = 0, PageSize = 20, TotalPage = 0;
    List<EmployeeSyncFromDB> result;
    String DeparmentID = "";
    ResultSet rs;
    private static EmployeeSyncOfflineProvider empSQLite;
    Context context_;
    APICloudService apiCloudService;
    List<GetEMP_DTO> listGetEMODTO;


    public DataEmployee(Context context) {
        dataModel = new BaseServiceModel(context);
        context_ = context;
        apiCloudService = new APICloudService();
        listGetEMODTO = new ArrayList<GetEMP_DTO>();
    }

    private Bitmap downloadBitmap(String url) {
        // initilize the default HTTP client object
        final DefaultHttpClient client = new DefaultHttpClient();
        //forming a HttoGet request
        final HttpGet getRequest = new HttpGet(url);
        try {
            HttpResponse response = client.execute(getRequest);
            //check 200 OK for success
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode +
                        " while retrieving bitmap from " + url);
                return null;
            }
            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    // getting contents from the stream
                    inputStream = entity.getContent();
                    // decoding stream data back into image Bitmap that android understands
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // You Could provide a more explicit error message for IOException
            getRequest.abort();
            Log.e("ImageDownloader", "Something went wrong while" +
                    " retrieving bitmap from " + url + e.toString());
        }
        return null;
    }


    public List<EmployeeSyncFromDB> GetListEMP_AddToSQLite(String DEP_ID) {
        UtilMethod utilMethod = new UtilMethod();
        empSQLite = new EmployeeSyncOfflineProvider(context_);
        result = new ArrayList<EmployeeSyncFromDB>();
        TotalItem = 0;
        PageIndex = 0;
        PageSize = 20;
        DeparmentID = DEP_ID;
        String sql = "";
        byte BasePhoto[] = null;

        try {
            sql = getNumberEMPOfDep.replace("{depID}", DeparmentID);
            rs = dataModel.LoadData_Simple(sql);
            if (rs.next()) {
                TotalItem = Integer.parseInt(rs.getString(1));
                TotalPage = TotalItem / PageSize;
                if (TotalItem % PageSize != 0) {
                    TotalPage++;
                }
                // Replace truy van cho GetDanhSach
                sql = getEMP_ByDepID.replace("{depID}", DeparmentID).replace("{fromIndex}", "0").replace("{toIndex}", "20");
            }
        } catch (Exception exx) {
            Log.e("Loi_GetItems", exx.toString());
        }
        //
        while (PageIndex <= TotalPage) {
            if(!Singleton.getInstance().getServerURL().equals("")){
                PostGetListEmpDTO dtoPost = new PostGetListEmpDTO(DEP_ID,PageIndex);
                listGetEMODTO = apiCloudService.GetListAddress(dtoPost);
                if(listGetEMODTO.size()>0){
                    for(int i=0;i<listGetEMODTO.size();i++){
                        EmployeeSyncFromDB emp = new EmployeeSyncFromDB();
                        emp.setEMP_ID(listGetEMODTO.get(i).getEMP_ID());
                        emp.setEMP_NM(listGetEMODTO.get(i).getEMP_NM());
                        emp.setDEP_ID(listGetEMODTO.get(i).getDEP_ID());
                        emp.setDEP_NM(listGetEMODTO.get(i).getDEP_NM());
                        emp.setHAVE_IMG("0");
                        if(listGetEMODTO.get(i).getPIC_DR()!=null && !listGetEMODTO.get(i).getPIC_DR().equals("") && !listGetEMODTO.get(i).getPIC_DR().equals("null")){
                            emp.setHAVE_IMG("1");
                        }
                        result.add(emp);
                        // Save To SQLite
                        EmployeeSyncFromDB empCheck = empSQLite.CheckExists_SYEMP(emp.getEMP_ID());
                        // Convert and Save Photo to SD card if have Image
                        if (emp.getHAVE_IMG().equals("1")&&GlobalData.AllowSavePhotoIntoDevice==true) {
                            String name = listGetEMODTO.get(i).getEMP_ID() + ".jpg";
                            String LocationUrl = Singleton.getInstance().getServerURL().replace("api", "Photo/Employee");
                            try {
                                Bitmap bmp = downloadBitmap(LocationUrl+name);
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                utilMethod.CreatePNGPhotoFromBase64(Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/GlintonPhoto/" + emp.getEMP_ID(), stream.toByteArray(), context_);
                            }catch (Exception exx1){
                                Log.e("Error_SavePhoto",exx1.getMessage());
                            }
                        }
                        //
                        if (empCheck != null) {
                            empCheck = empSQLite.UpdateEMP_Sync_Offline(emp);
                        } else {
                            empCheck = empSQLite.CreateEMP_Sync_Offline(emp);
                        }
                    }
                }
                PageIndex ++;
            }
            else{
                rs = dataModel.LoadData_Simple(sql);
                if (rs != null) {
                    try {
                        while (rs.next()) {
                            EmployeeSyncFromDB emp = new EmployeeSyncFromDB();
                            emp.setEMP_ID(rs.getString(1));
                            emp.setEMP_NM(rs.getString(2));
                            emp.setHAVE_IMG(rs.getInt(4) + "");
                            emp.setDEP_ID(GlobalData.DepartmentSelected);
                            emp.setDEP_NM(GlobalData.DepartmentNameSelected);
                            result.add(emp);
                            // Save To SQLite or Update SQLite
                            EmployeeSyncFromDB empCheck = empSQLite.CheckExists_SYEMP(emp.getEMP_ID());
                            // Convert and Save Photo to SD card if have Image
                            if (emp.getHAVE_IMG().equals("1")&&GlobalData.AllowSavePhotoIntoDevice==true) {
                                BasePhoto = rs.getBytes(5);
                                utilMethod.CreatePNGPhotoFromBase64(Environment.getExternalStorageDirectory().getAbsolutePath()
                                        + "/GlintonPhoto/" + emp.getEMP_ID(), BasePhoto, context_);
                            }
                            //
                            if (empCheck != null) {
                                empCheck = empSQLite.UpdateEMP_Sync_Offline(emp);
                            } else {
                                empCheck = empSQLite.CreateEMP_Sync_Offline(emp);
                            }
                        }
                    } catch (Exception exxx) {
                        Log.e("LoiTruyVan", exxx.toString());
                    }
                    PageIndex++;
                    sql = getEMP_ByDepID.replace("{depID}", DeparmentID)
                            .replace("{fromIndex}", "" + (PageIndex * PageSize + 1))
                            .replace("{toIndex}", "" + (PageIndex * PageSize + PageSize));
                }
            }
        }
        try {
            rs.close();
        } catch (Exception exrs) {
        }
        dataModel.CloseConnection();
        System.gc();
        return result;
    }

}
