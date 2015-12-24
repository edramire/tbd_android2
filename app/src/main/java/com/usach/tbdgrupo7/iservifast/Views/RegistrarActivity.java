package com.usach.tbdgrupo7.iservifast.Views;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.usach.tbdgrupo7.iservifast.Controllers.HttpGet;
import com.usach.tbdgrupo7.iservifast.Controllers.HttpPost;
import com.usach.tbdgrupo7.iservifast.Controllers.Registrar;
import com.usach.tbdgrupo7.iservifast.Model.Usuario;
import com.usach.tbdgrupo7.iservifast.R;
import com.usach.tbdgrupo7.iservifast.utilities.JsonHandler;
import com.usach.tbdgrupo7.iservifast.utilities.SystemUtilities;

import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegistrarActivity extends AppCompatActivity {
    private static final String TAG = "RegistrarActivity";

    @InjectView(R.id.input_usuario)
    EditText _usuarioText;
    @InjectView(R.id.input_nombre)
    EditText _nombreText;
    @InjectView(R.id.input_apellido)
    EditText _apellidoText;
    @InjectView(R.id.input_password)
    EditText _passwordText;
    @InjectView(R.id.input_password2)
    TextView _password2Text;
    @InjectView(R.id.input_email)
    TextView _emailText;
    @InjectView(R.id.input_region)
    TextView _regionText;
    @InjectView(R.id.input_ciudad)
    TextView _ciudadText;
    @InjectView(R.id.input_comuna)
    TextView _comunaText;
    @InjectView(R.id.btn_signup)
    Button _signupButton;
    @InjectView(R.id.input_direccion)
    TextView _direccionText;
    @InjectView(R.id.link_login)
    TextView _loginLink;

    String usuario;
    String nombre;
    String apellido;
    String password;
    String password2;
    String email;
    String region;
    String ciudad;
    String comuna;
    String direccion;

    boolean usuarioDisponible;
    boolean emailDisponible;

    private BroadcastReceiver br = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });

    }

    public void signup() {

        usuario = _usuarioText.getText().toString();
        nombre = _nombreText.getText().toString();
        apellido = _apellidoText.getText().toString();
        password = _passwordText.getText().toString();
        password2 = _password2Text.getText().toString();
        email = _emailText.getText().toString();
        region = _regionText.getText().toString();
        ciudad = _ciudadText.getText().toString();
        comuna = _comunaText.getText().toString();
        direccion = _direccionText.getText().toString();

        /*
        if (!validate()) {
            onSignupFailed();
            return;
        }
        */
        
        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(RegistrarActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creando cuenta...");
        progressDialog.show();

        SystemUtilities su = new SystemUtilities(this.getApplicationContext());

        if (su.isNetworkAvailable()) {
            IntentFilter intentFilter = new IntentFilter("httpPost");

            Usuario us = new Usuario();
            us.setUsuario(usuario);
            us.setNombre(nombre);
            us.setApellido(apellido);
            us.setPassword(password);
            us.setPassword2(password2);
            us.setEmail(email);
            us.setRegion(region);
            us.setCiudad(ciudad);
            us.setComuna(comuna);
            us.setDireccion(direccion);

            JsonHandler jh = new JsonHandler();
            JSONObject jObject = jh.setUsuario(us);

            br = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String result = intent.getStringExtra("post");
                    /*
                    if(result.equals("OK")){
                        Toast.makeText(getActivity(), getResources().getString(R.string.user_created),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.error_database_connection),
                                Toast.LENGTH_LONG).show();
                    }*/
                }
            };
            this.registerReceiver(br, intentFilter);
            new HttpPost(this.getApplicationContext()).execute(getResources().getString(R.string.servidor) + "Usuario/crear", jObject.toString());
        }else{
            Toast.makeText(this, getResources().getString(R.string.error_internet), Toast.LENGTH_LONG).show();
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
                        onSignupSuccess();
                        // onSignupFailed();
                        progressDialog.dismiss();
                    }
                }, 2000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("usuario",usuario);
        intent.putExtra("password", password);
        startActivity(intent);
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Fallo al crear la cuenta", Toast.LENGTH_LONG).show();
        _signupButton.setEnabled(true);
    }

    public boolean validate() {

        boolean valid = true;
        IntentFilter intentFilter = new IntentFilter("httpData");
        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Registrar r = new Registrar();
                r.getUsuariosMails(intent.getStringExtra("data"),usuario,email);
                usuarioDisponible = r.isUsuarioDisponible();
                emailDisponible = r.isMailDisponible();
            }
        };
        this.registerReceiver(br, intentFilter);
        new HttpGet(getApplicationContext()).execute("http://10.0.2.2:8080/tbd_java_ee-master/Usuario");

        if(!usuarioDisponible){
            System.out.println("usuario en uso");
            _usuarioText.setError("Usuario ingresado en uso.");
            return false;
        }
        else{
            _usuarioText.setError(null);
        }

        if(!emailDisponible){
            System.out.println("email en uso");
            _emailText.setError("Email ingresado en uso");
            return false;
        }
        else{
            _emailText.setError(null);
        }

        if (nombre.isEmpty() || nombre.length() < 3) {
            System.out.println("por lo menos 3 caracteres");
            _nombreText.setError("por lo menos 3 carácteres");
            return false;
        }
        else{
            _nombreText.setError(null);
        }

        if(!password.equals(password2)){
            System.out.println("contraseñas no coinciden");
            _passwordText.setError("Las contraseñas no coinciden");
            return false;
        }
        else{
            _passwordText.setError(null);
            _password2Text.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            System.out.println("email no valido");
            _emailText.setError("ingresar un email válido");
            return false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            System.out.println("contrañse entre 4 y 10");
            _passwordText.setError("entre 4 y 10 carácteres alfanuméricos");
            return false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
