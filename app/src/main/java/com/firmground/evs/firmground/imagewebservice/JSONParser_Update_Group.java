package com.firmground.evs.firmground.imagewebservice;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.firmground.evs.firmground.R;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Hasnat Ahmad
 */
public class JSONParser_Update_Group {

    SharedPreferences sharedPreferences;

    /**
     * Upload Image
     *
     * @param sourceImageFile
     * @return
     */
    public JSONObject uploadImage(String sourceImageFile,
                                  String data,
                                  String gId,
                                  Activity activity) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        /*
      Upload URL of your folder with php file name...
      You will find this file in php_upload folder in this project
      You can copy that folder and paste in your htdocs folder...
     */
        String URL_UPLOAD_IMAGE = activity.getResources().getString(R.string.url) + "updateGroup";
        try {

            File sourceFile = new File(sourceImageFile);

            Log.d("TAG", "File...::::" + sourceFile + " : " + sourceFile.exists());

            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

            String filename = sourceImageFile.substring(sourceImageFile.lastIndexOf("/")+1);

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("groupProfileImage", filename, RequestBody.create(MEDIA_TYPE_PNG, sourceFile))
                    .addFormDataPart("users", data)
                    .addFormDataPart( "gId", gId)
                    .build();
            Log.d("TAG", "File...:::: URL_UPLOAD_IMAGE1 = " + URL_UPLOAD_IMAGE);
            Log.d("TAG", "File...:::: users = " + data);
            Log.d("TAG", "File...:::: gId = " + gId);


            Request request = new Request.Builder()
                    .url(URL_UPLOAD_IMAGE)
                    .post(requestBody)
                    .build();


            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            Log.e("TAG", "Error: " + res);
            return new JSONObject(res);

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e("TAG", "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e("TAG", "Other Error: " + e.getLocalizedMessage());
        }
        return null;
    }
}
