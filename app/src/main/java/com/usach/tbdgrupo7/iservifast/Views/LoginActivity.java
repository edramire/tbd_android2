package com.usach.tbdgrupo7.iservifast.Views;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.usach.tbdgrupo7.iservifast.Controllers.HttpGet;
import com.usach.tbdgrupo7.iservifast.Controllers.Login;
import com.usach.tbdgrupo7.iservifast.R;
import com.usach.tbdgrupo7.iservifast.utilities.SystemUtilities;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    private BroadcastReceiver br = null;
    boolean resultadoLogin = false;
    String user_text;
    String pass_text;

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
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autentificando...");
        progressDialog.show();

        user_text = _userText.getText().toString();
        pass_text = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        SystemUtilities su = new SystemUtilities(this.getApplicationContext());

        if (su.isNetworkAvailable()) {
            IntentFilter intentFilter = new IntentFilter("httpData");
            br = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    user_text = _userText.getText().toString();
                    pass_text = _passwordText.getText().toString();
                    Login log = new Login();
                    resultadoLogin = log.getUsuarioPassword(intent.getStringExtra("data"), user_text, pass_text);
                }
            };
            this.registerReceiver(br, intentFilter);
            new HttpGet(getApplicationContext()).execute("http://10.0.2.2:8080/tbd_java_ee-master/Usuario");
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(resultadoLogin==true) {
                            onLoginSuccess();
                            progressDialog.dismiss();
                        }
                        else{
                            onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }
                }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                //this.finish();
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

        //finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Usuario y contraseña no coinciden", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        user_text = _userText.getText().toString();
        pass_text = _passwordText.getText().toString();

        if(user_text.isEmpty() || user_text.length() > 20 ) {
            _userText.setError("Ingresa un usuario válido");
            valid = false;
        } else {
            _userText.setError(null);
        }

        if (pass_text.isEmpty() || pass_text.length() < 4 || pass_text.length() > 10) {
            _passwordText.setError("Contraseña entre 4 y 10 carácteres alfanuméricos");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }
}