package com.usach.tbdgrupo7.iservifast.Controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import com.usach.tbdgrupo7.iservifast.Model.Usuario;
import com.usach.tbdgrupo7.iservifast.R;
import com.usach.tbdgrupo7.iservifast.Views.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

import butterknife.InjectView;

public class Login extends AsyncTask<String, Void, String> {

    private Context context;
    private String input_usuario;
    private String input_password;
    private Usuario user;
    private ProgressDialog progressDialog;
    @InjectView(R.id.btn_login)Button _loginButton;

    public Login(Context context, String usuario, String password) {
        this.context = context;
        this.input_usuario = usuario;
        this.input_password = password;
    }// HttpGet(Context context)

    @Override
    protected void onPreExecute(){

        progressDialog = new ProgressDialog(context, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autentificando...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(10000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            return new Scanner(connection.getInputStream(), "UTF-8").useDelimiter("\\A").next();
        } catch (MalformedURLException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        } catch (ProtocolException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        } catch (IOException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        }
        return null;
    }// doInBackground(String... urls)

    @Override
    protected void onPostExecute(String result) {
        boolean resultadoValidaciones= validarLogin(result,input_usuario,input_password);
        if(resultadoValidaciones==true){
            //progressDialog = new ProgressDialog(LoginActivity.this);
            //_loginButton.setEnabled(true);
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("usuario", user.getUsuario());
            intent.putExtra("password", user.getPassword());
            intent.putExtra("nombre", user.getNombre());
            intent.putExtra("apellido", user.getApellido());
            intent.putExtra("mail", user.getEmail());
            intent.putExtra("region", user.getRegion());
            intent.putExtra("ciudad", user.getCiudad());
            intent.putExtra("comuna", user.getComuna());
            intent.putExtra("direccion", user.getDireccion());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            //progressDialog.dismiss();
        }
        else{//no hay coincidencias para el usuario y contraseña ingresados
            //
        }
    }

    public boolean validarLogin(String json, String usuario, String password) {
        try {
            JSONArray ja = new JSONArray(json);
            String[] usuarios = new String[ja.length()];
            String[] passwords = new String[ja.length()];
            String[] nombres = new String[ja.length()];
            String[] apellidos= new String[ja.length()];
            String[] emails= new String[ja.length()];
            String[] regiones= new String[ja.length()];
            String[] ciudades= new String[ja.length()];
            String[] comunas= new String[ja.length()];
            String[] direcciones= new String[ja.length()];
            int i;
            for (i = 0; i < ja.length(); i++) {
                JSONObject row = ja.getJSONObject(i);
                usuarios[i]=row.getString("usuario");
                passwords[i]=row.getString("password");
                nombres[i]=row.getString("nombre");
                apellidos[i]=row.getString("apellido");
                emails[i]=row.getString("mail");
                regiones[i]=row.getString("region");
                ciudades[i]=row.getString("ciudad");
                comunas[i]=row.getString("comuna");
                direcciones[i]=row.getString("direccion");
            }
            int resultado_match = matchUsuarioPassword(usuarios,passwords,usuario,password);//devuelve la posicion del usuario en usuarios[i]
            if(resultado_match!=-1){
                user = new Usuario();
                user.setUsuario(usuarios[resultado_match]);
                user.setNombre(nombres[resultado_match]);
                user.setApellido(apellidos[resultado_match]);
                user.setPassword(passwords[resultado_match]);
                user.setEmail(emails[resultado_match]);
                user.setRegion(regiones[resultado_match]);
                user.setCiudad(ciudades[resultado_match]);
                user.setComuna(comunas[resultado_match]);
                user.setDireccion(direcciones[resultado_match]);
                return true;
            }
            else{
                return false;
            }
        } catch (JSONException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        }
        return false;
    }

    public int matchUsuarioPassword(String[] usuarios, String[] passwords,String usuario, String password){
        int i;
        for(i=0;i<usuarios.length;i++){
            if(usuarios[i].equals(usuario)&&passwords[i].equals(password)){
                return i;
            }
        }
        return -1;
    }
}