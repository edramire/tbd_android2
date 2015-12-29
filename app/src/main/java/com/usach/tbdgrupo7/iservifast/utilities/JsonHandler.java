package com.usach.tbdgrupo7.iservifast.utilities;

import android.util.Log;

import com.usach.tbdgrupo7.iservifast.Model.Oferta;
import com.usach.tbdgrupo7.iservifast.Model.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author: Jefferson Morales De la Parra
 * Clase que se utiliza para manipular objetos JSON
 */
public class JsonHandler {

    /**
     * Método que recibe un JSONArray en forma de String y devuelve un String[] con los actores
     */
    public String[] getUsuario(String usuario) {
        try {
            JSONArray ja = new JSONArray(usuario);
            String[] result = new String[ja.length()];
            String actor;
            for (int i = 0; i < ja.length(); i++) {
                JSONObject row = ja.getJSONObject(i);
                actor = " " + row.getString("nombre") + " " + row.getString("apellido");
                result[i] = actor;
            }
            return result;
        } catch (JSONException e) {
            Log.e("ERROR", this.getClass().toString() + " " + e.toString());
        }
        return null;
    }// getActors(String actors)

    public JSONObject setUsuario(Usuario usuario) {
        // build jsonObject
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("usuario", usuario.getUsuario());
            jsonObject.accumulate("nombre", usuario.getNombre());
            jsonObject.accumulate("apellido", usuario.getApellido());
            jsonObject.accumulate("password", usuario.getPassword());
            jsonObject.accumulate("mail", usuario.getEmail());
            jsonObject.accumulate("region", usuario.getRegion());
            jsonObject.accumulate("ciudad", usuario.getCiudad());
            jsonObject.accumulate("comuna", usuario.getComuna());
            jsonObject.accumulate("direccion", usuario.getDireccion());
            return jsonObject;

        }catch(JSONException je){
            Log.e("ERROR",this.getClass().toString()+ " - "+ je.getMessage());
        }
        return null;
    }

    public JSONObject setOferta(Oferta oferta) {
        // build jsonObject
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("titulo", oferta.getTitulo());
            jsonObject.accumulate("descripcion", oferta.getDescripcion());
            jsonObject.accumulate("categoria_idCategoria", oferta.getCategoria_idCategoria());
            jsonObject.accumulate("comunidad_idComunidad", oferta.getComunidad_idComunidad());
            jsonObject.accumulate("duracion", oferta.getDuracion());
            jsonObject.accumulate("precio", oferta.getPrecio());
            jsonObject.accumulate("fecha", oferta.getFecha().toString());
            return jsonObject;

        }catch(JSONException je){
            Log.e("ERROR",this.getClass().toString()+ " - "+ je.getMessage());
        }
        return null;
    }

}// JsonHandler