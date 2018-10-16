package com.jwetherell.quick_response_code.CloudServiceDB;

import android.util.Log;

import com.jwetherell.quick_response_code.ModelDTO.APIDTO.PostSaveEMPPhotoDTO;
import com.jwetherell.quick_response_code.ModelDTO.APIDTO.ReturnDTO;
import com.jwetherell.quick_response_code.ModelDTO.GetEMP_DTO;
import com.jwetherell.quick_response_code.ModelDTO.PostGetListEmpDTO;
import com.jwetherell.quick_response_code.Singleton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2/14/17.
 */

public class APICloudService {

    public APICloudService() {
    }

    public List<GetEMP_DTO> GetListAddress(PostGetListEmpDTO dto){
        List<GetEMP_DTO> model = new ArrayList<GetEMP_DTO>();
        String UrlHeader = "getListDepartmentForMobile";
        String URL = Singleton.getInstance().getServerURL() + UrlHeader;
        JsonParser parser = new JsonParser();
        try{
            JSONObject putParams = new JSONObject();
            putParams.put(dto.DEPID, dto.getDEP_ID());
            putParams.put(dto.PAGEINDEX, dto.getPageIndex());
            //
            String JsonStr = parser.GetStringFrompostRequest(URL, putParams);
            JSONObject jsonResponse = new JSONObject(JsonStr);
            int TotalItem = (jsonResponse.getInt("TotalItem"));
            String Message_ = (jsonResponse.getString("Message"));
            if (TotalItem>0){
                JSONArray jsonArray = new JSONArray();
                jsonArray= jsonResponse.getJSONArray("Items");
                for (int i=0;i<jsonArray.length();i++){
                    JSONObject jAddress = new JSONObject();
                    jAddress=jsonArray.getJSONObject(i);
                    GetEMP_DTO globalAddressDTO = new GetEMP_DTO();
                    globalAddressDTO.setDEP_ID(jAddress.getString(globalAddressDTO.DEPID));
                    globalAddressDTO.setDEP_NM(jAddress.getString(globalAddressDTO.DEPNM));
                    globalAddressDTO.setEMP_ID(jAddress.getString(globalAddressDTO.EMPID));
                    globalAddressDTO.setEMP_NM(jAddress.getString(globalAddressDTO.EMPNM));
                    globalAddressDTO.setMA_SCAN(jAddress.getString(globalAddressDTO.MASCAN));
                    globalAddressDTO.setPIC_DR(jAddress.getString(globalAddressDTO.PICDR));
                    model.add(globalAddressDTO);
                }
            }
        }catch (Exception exx){
            Log.e("ErrorParseInvite",exx.toString());
        }
        return  model;
    }


    public ReturnDTO<Boolean> SavePhotoAPI(PostSaveEMPPhotoDTO dto) {
        ReturnDTO<Boolean> kq = new ReturnDTO<Boolean>();
        String UrlHeader = "SavePhotoMobile";
        String URL = Singleton.getInstance().getServerURL() + UrlHeader;
        JsonParser parser = new JsonParser();
        try {
            JSONObject putParams = new JSONObject();
            putParams.put(dto.EMPID, dto.getEMP_ID());
            putParams.put(dto.PICDR,dto.getPIC_DR());
            //
            String JsonStr = parser.GetStringFrompostRequest(URL, putParams);
            JSONObject jsonResponse = new JSONObject(JsonStr);
            kq.setFalseReason(jsonResponse.getString(kq.FALSEREASON));
            kq.setQueryResult(jsonResponse.getBoolean(kq.QUERYRESULT));
            List<String> falseReasons = new ArrayList<String>();
            JSONArray jfalseReason = jsonResponse.getJSONArray(kq.FALSEREASONS);
            if(jfalseReason.length()>0){
                for (int i=0;i<jfalseReason.length();i++){
                    falseReasons.add(jfalseReason.getString(i));
                }
                kq.setFalseReasons(falseReasons);
            }
            if (jsonResponse.getBoolean(kq.QUERYRESULT) == true) {
                kq.setItem(jsonResponse.getBoolean(kq.ITEM));
            }
        } catch (Exception exx) {
            Log.e("ErrorParseLogin", exx.toString());
        }
        return kq;
    }

}
