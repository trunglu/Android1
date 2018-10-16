package com.jwetherell.quick_response_code.CloudServiceDB;

import org.json.JSONObject;
import android.util.Log;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by admin on 2/14/17.
 */

public class JsonParser {
    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    public JsonParser() {
    }
    public String GetJsonStringFromURL(String url) {
        String xml = null;
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpPost = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return xml;
    }

    public String GetStringFrompostRequest(String url, JSONObject jsonObj) {
        String kq = "";
        try {
            HttpPost post = new HttpPost();
            HttpClient client = new DefaultHttpClient();
            post.setURI(new URI(url));
            StringEntity entity = new StringEntity(jsonObj.toString());
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Accept", "application/json");
//            post.setEntity(new StringEntity(entity.toString(), HTTP.UTF_8));
            post.setEntity(new StringEntity(entity.toString(), "UTF8"));
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            kq = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            Log.e("Loi:", e.toString());
        }
        return kq;
    }

    public String makeWebServiceCall(String urladdress, int requestmethod, JSONObject params) {
        URL url;
        String response = "";
        try {
            url = new URL(urladdress);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15001);
            conn.setConnectTimeout(15001);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            if (requestmethod == 2) {
                conn.setRequestMethod("POST");
            } else if (requestmethod == 1) {
                conn.setRequestMethod("GETRequest");
            }

            if (params != null) {
                OutputStream ostream = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(ostream, "UTF-8"));
                StringBuilder requestresult = new StringBuilder();
                boolean first = true;
                for(int i=0;i<params.length();i++){

                }
                //requestresult = params.toString();
                writer.write(requestresult.toString());
                writer.flush();
                writer.close();
                ostream.close();
            }
            int reqresponseCode = conn.getResponseCode();

            if (reqresponseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
