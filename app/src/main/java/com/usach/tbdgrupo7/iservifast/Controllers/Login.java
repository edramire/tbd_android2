package com.usach.tbdgrupo7.iservifast.Controllers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by matias on 19-12-15.
 */
public class Login {

    public Login(){
    }

    public boolean getUsuarioPassword(String usuario1,String usuario,String password) {
        try {
            JSONArray ja = new JSONArray(usuario1);
            String[] usuarios = new String[ja.length()];
            String[] passwords = new String[ja.length()];
            for (int i = 0; i < ja.length(); i++) {
                JSONObject row = ja.getJSONObject(i);
                usuarios[i]=row.getString("usuario");
                passwords[i]=row.getString("password");
            }
            return matchUsuarioPassword(usuarios,passwords,usuario,password);
        } catch (JSONException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        }
        return false;
    }// getActors(String actors)

    public boolean matchUsuarioPassword(String[] usuarios, String[] passwords,String usuario, String password){

        int i;
        for(i=0;i<usuarios.length;i++){
            if(usuarios[i].equals(usuario)&&passwords[i].equals(password)){
                return true;
            }
        }
        return false;
    }
}
