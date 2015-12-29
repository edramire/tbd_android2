package com.usach.tbdgrupo7.iservifast.Views;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.usach.tbdgrupo7.iservifast.Controllers.Login;
import com.usach.tbdgrupo7.iservifast.R;
import com.usach.tbdgrupo7.iservifast.utilities.SystemUtilities;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_SIGNUP = 0;

    private String user_text;
    private String pass_text;
    private final short largoMinUsuario = 6;
    private final short largoMaxUsuario = 12;
    private final short largoMinPassword = 6;
    private final short largoMaxPassword = 12;
    private final ProgressDialog dialogAutentificando = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);

    @InjectView(R.id.input_user)EditText _userText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.btn_login)Button _loginButton;
    @InjectView(R.id.link_signup)TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), RegistrarActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }// onCreate()


    private void login() {

        user_text = _userText.getText().toString();
        pass_text = _passwordText.getText().toString();

        if (validarCampos()==false) {
            onLoginFailed();
            return;
        }

        SystemUtilities su = new SystemUtilities(this.getApplicationContext());
        if (su.isNetworkAvailable()) {
            new Login(this,getApplicationContext(),user_text,pass_text).execute(getResources().getString(R.string.servidor) + "Usuario");
        }
        else{
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.error_internet),Toast.LENGTH_LONG);
        }

    }

    public boolean validarCredenciales(String usuario, String password){

        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("usuario",user_text);
        intent.putExtra("password", pass_text);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Usuario y contraseña no coinciden", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    private boolean validarCampos() {

        boolean valid = true;

        if(user_text.isEmpty() || user_text.length() < largoMinUsuario ||user_text.length() > largoMaxUsuario ) {
            _userText.setError("Largo de usuario entre "+largoMinUsuario+" y "+largoMaxUsuario);
            valid = false;
        } else {
            _userText.setError(null);
        }

        if (pass_text.isEmpty() || pass_text.length() < largoMinPassword || pass_text.length() > largoMaxPassword) {
            _userText.setError("Largo de contraseña entre "+largoMinPassword+" y "+largoMaxPassword);
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }

    public void abrirProgressDialog() {
        _loginButton.setEnabled(false);
        dialogAutentificando.setIndeterminate(true);
        dialogAutentificando.setMessage("Autentificando...");
        dialogAutentificando.show();
    }

    public void cerrarProgressDialog(){
        dialogAutentificando.dismiss();
        _loginButton.setEnabled(true);
    }

}