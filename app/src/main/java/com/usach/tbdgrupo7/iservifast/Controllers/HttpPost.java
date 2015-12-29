package com.usach.tbdgrupo7.iservifast.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author: Grupo 7
 */
public class HttpPost extends AsyncTask<String, Void, String> {


    private Context context;

    public HttpPost(Context context) {
        this.context = context;
    }// HttpPost(Context context)

    /***
     * Envia consulta POST.
     * @param params [0] URL, [1] parametros de envio (formato JSON)
     */
    public String sendData(String... params){
        try {

            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setFixedLengthStreamingMode(params[1].getBytes().length);
            connection.connect();

            OutputStream os = new BufferedOutputStream(connection.getOutputStream());
            os.write(params[1].getBytes());
            os.flush();
            /*OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(params[1]);
            out.close();*/
            return "OK";
        }catch(Exception e){
            Log.e("ERROR",this.getClass().toString() + " " + e.toString());
        }
        return "ERROR";
    }

    @Override
    protected String doInBackground(String... data) {
        return sendData(data);
    }

    @Override
    protected void onPostExecute(String result) {
        Intent intent = new Intent("httpPost").putExtra("post", result);
        context.sendBroadcast(intent);
    }// onPostExecute(String result)

}// HttpPost