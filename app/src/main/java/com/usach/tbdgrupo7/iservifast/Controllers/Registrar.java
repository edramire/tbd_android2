package com.usach.tbdgrupo7.iservifast.Controllers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: Grupo 7
 */
public class Registrar{

    private String[] usuarios;
    private String[] mails;
    boolean usuarioDisponible = false;
    boolean mailDisponible = false;

    public Registrar(){
    }

    public void getUsuariosMails(String json,String usuario,String mail) {
        try {
            JSONArray ja = new JSONArray(json);
            usuarios = new String[ja.length()];
            mails = new String[ja.length()];
            for (int i = 0; i < ja.length(); i++) {
                JSONObject row = ja.getJSONObject(i);
                usuarios[i]=row.getString("usuario");
                mails[i]=row.getString("mail");
            }
            setUsuarioDisponible(usuarioDisponible(usuarios, usuario));
            setMailDisponible(mailDisponible(mails,mail));
        } catch (JSONException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        }
    }// getActors(String actors)

    private boolean usuarioDisponible(String[] usuarios, String usuario){
        int i;
        for(i=0;i<usuarios.length;i++){
            if(usuarios[i].equals(usuario)){
                return false;//False: usuario en uso, no dispnible
            }
        }
        return true;//True: usuario disponible
    }

    private boolean mailDisponible(String[] mails, String mail){
        int i;
        for(i=0;i<mails.length;i++){
            if(mails[i].equals(mail)){
                return false;//False: mail en uso, no dispnible
            }
        }
        return true;//True: mail disponible
    }

    public boolean isUsuarioDisponible() {
        return usuarioDisponible;
    }

    public void setUsuarioDisponible(boolean usuarioDisponible) {
        this.usuarioDisponible = usuarioDisponible;
    }

    public String[] getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(String[] usuarios) {
        this.usuarios = usuarios;
    }

    public String[] getMails() {
        return mails;
    }

    public void setMails(String[] mails) {
        this.mails = mails;
    }

    public boolean isMailDisponible() {
        return mailDisponible;
    }

    public void setMailDisponible(boolean mailDisponible) {
        this.mailDisponible = mailDisponible;
    }
}