package com.jwetherell.quick_response_code.UtilMethod;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.jwetherell.quick_response_code.GlobalData;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by phamvan on 8/20/2015.
 */
public class UtilMethod {
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static boolean CheckInternet(Context context) {
        ConnectivityManager cm = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        if (cm == null) {
            GlobalData.InternetConnected = false;
            return false;
        }
        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected()) {
            // Send here
            GlobalData.InternetConnected = true;
            return true;
        }
        return false;
    }

    public static String GetCurrentDateDMY() {
        try {
            DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
            dateFormatter.setLenient(false);
            Date today = new Date();
            String strDate = dateFormatter.format(today);
            return strDate;
        } catch (Exception exx) {
            return "";
        }
    }

    public static String GetCurrentDateMDY() {
        try {
            DateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
            dateFormatter.setLenient(false);
            Date today = new Date();
            String strDate = dateFormatter.format(today);
            return strDate;
        } catch (Exception exx) {
            return "";
        }
    }


    public static String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static boolean dir_exists(String dir_path) {
        boolean ret = false;
        File dir = new File(dir_path);
        if (dir.exists() && dir.isDirectory())
            ret = true;
        return ret;
    }

    public Bitmap ConvertFromBase64ToBitMap(String Base64value) {
        byte[] decodedString = Base64.decode(Base64value.getBytes(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }
    public UtilMethod(){

    }

    public void CreatePNGPhotoFromBase64(String PicName, byte base64[], Context context) {
        Bitmap bitmapIMG;
        File fileName;
        try {
            String dir_path = Environment.getExternalStorageDirectory() + "//GlintonPhoto//";
            if (!UtilMethod.dir_exists(dir_path)){
                File directory = new File(dir_path);
                directory.mkdirs();
            }
            bitmapIMG = BitmapFactory.decodeByteArray(base64, 0, base64.length);
            fileName = new File(PicName + ".png");
            if(fileName.exists()){
                return;
            }
            FileOutputStream fileOutputStream = new FileOutputStream(fileName);
            bitmapIMG.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), fileName.getAbsolutePath(),
                   fileName.getName(), fileName.getName());
            // Clear File If Exists In DCIM/Camera/
            //fileName = new File(PicName.replace("GlintonPhoto","DCIM/Camera"));
        } catch (Exception ex) {
            Log.e("Error_SavePhoto", ex.toString());
        }

    }

}
