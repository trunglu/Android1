package com.jwetherell.quick_response_code;

import android.content.Context;

import com.jwetherell.quick_response_code.ModelDTO.DepartmentDTO;
import com.jwetherell.quick_response_code.ModelDTO.EmployeeOffline;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ADMIN on 7/15/2015.
 */
public class GlobalData {
    public static String Name_ = "";
    public static int Select_pos = -1;
    public static String DepartmentSelected = "";
    public static String DepartmentNameSelected = "";
    public static boolean InternetConnected = false;
    public static List<EmployeeOffline> Employees_Offline = new ArrayList<EmployeeOffline>();
    public static String IMG_URI_Taken = "0";
    public static Context context_;
    public  static  List<DepartmentDTO> depList = new ArrayList<DepartmentDTO>();
    public  static String EMP_NAME = "";
    public static boolean AllowSavePhotoIntoDevice = false;
}
