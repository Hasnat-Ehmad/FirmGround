package com.firmground.evs.firmground.webservice;

import android.os.AsyncTask;
import android.text.TextUtils;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


/**
 * Created by evs on 12/20/2016.
 */

public class WebRequestCall extends AsyncTask<String, Integer, String> {

    private TaskDelegate delegate;

    protected String doInBackground(String... uri) {

        System.out.println("WebRequestCall:doInBackground");
        System.out.println(uri);
        String responseString = null;

        System.out.println("*****WebRequestCall: "+uri[0]);
        System.out.println("*****WebRequestCall: "+uri[1]);
        System.out.println("*****WebRequestCall: "+uri[2]);


            uri[2] = uri[2].trim();
            //System.out.println("*****WebRequestCall_array_len: "+uri[2].length());

            if(uri[2].length() > 0){
                String[] array_all = uri[2].split("&");


                for(int i=0; i<array_all.length;i++){

                   // System.out.println("*****WebRequestCall_array_old: "+array_all[i]);
                    String[] array_one =   array_all[i].split("=");
                    System.out.println("*****WebRequestCall_array_all: "+array_one.length);

                    if(array_one.length > 1){
                        try {
                            array_one[1] = URLEncoder.encode(array_one[1], "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        array_all[i] =  array_one[0]+"="+array_one[1];
                    }else{

                        array_all[i] =  array_one[0]+"=";
                    }
                    /**/
                }

                String finalString = null;

                finalString = TextUtils.join("&", array_all);
                    //finalString = String.join("&", array_all);

                System.out.println("*****WebRequestCall_array_full: "+finalString);
                uri[2] = finalString;
            }

        /*try {
            uri[2] = URLEncoder.encode(uri[2], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }*/


        try {
            URL url = new URL(uri[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(uri[1]);
            urlConnection.setUseCaches (false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            if(uri[1]=="POST")
            {
                //Send request
                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream ());
                wr.writeBytes (uri[2]);
                wr.flush ();
                wr.close ();
            }



//            urlConnection.connect();
            InputStream is =urlConnection.getInputStream();
            int statuscode = urlConnection.getResponseCode();
            //System.out.println("*****WebRequestCall:totalSize "+totalSize);
            System.out.println("*****WebRequestCall:totalSize "+statuscode);
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            responseString= response.toString();
            System.out.println("*****WebRequestCall:responseString "+responseString);
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            System.out.println(statuscode);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new String("Error: " + e.getMessage());
            //TODO Handle problems..
        }

        //System.out.println(responseString);
        return responseString;
    }
    protected String doInBackground1(String... uri) {

        System.out.println("WebRequestCall:doInBackground");
        System.out.println(uri);
        String responseString = null;
        try {
            uri[2] = URLEncoder.encode(uri[2], "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("*****WebRequestCall: "+uri[0]);
        System.out.println("*****WebRequestCall: "+uri[1]);
        System.out.println("*****WebRequestCall: "+uri[2]);
        try {
            URL url = new URL(uri[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(uri[1]);
            urlConnection.setUseCaches (false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            if(uri[1]=="POST")
            {
                //Send request
                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream ());
                wr.writeBytes (uri[2]);
                wr.flush ();
                wr.close ();
            }



//            urlConnection.connect();
            InputStream is =urlConnection.getInputStream();
            int statuscode = urlConnection.getResponseCode();
            //System.out.println("*****WebRequestCall:totalSize "+totalSize);
            System.out.println("*****WebRequestCall:totalSize "+statuscode);
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            responseString= response.toString();
            System.out.println("*****WebRequestCall:responseString "+responseString);
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
            System.out.println(statuscode);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new String("Error: " + e.getMessage());
            //TODO Handle problems..
        }

        //System.out.println(responseString);
        return responseString;
    }
    public WebRequestCall(TaskDelegate delegate) {
        this.delegate = delegate;
    }
    //your code
    @Override
    protected void onPostExecute(String result) {
        try {
            delegate.TaskCompletionResult(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // if() {
        //listviewAdapter.notifyDataSetChanged();
        //listviewAdapter_win.notifyDataSetChanged();
        // }
    }
  
}