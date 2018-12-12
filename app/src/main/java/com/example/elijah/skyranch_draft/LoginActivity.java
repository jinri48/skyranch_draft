package com.example.elijah.skyranch_draft;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private DatabaseHelper mDBHelper;

    public static String mToken;
    private SessionManager session;

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.input_username);
        etPassword = findViewById(R.id.input_password);
        btnLogin = findViewById(R.id.btn_login);

        // establish a database connection
        mDBHelper = DatabaseHelper.newInstance(this);
        // Session Manager
        session = new SessionManager(this);
        Log.d(TAG, "onCreate: " + session.isLoggedIn());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(this, ProductActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validFields = validate(); // check if the fields are filled out
                if (validFields) {
                    btnLogin.setText("Logging in");
                    btnLogin.setEnabled(false);
                    login(etUsername.getText().toString(), etPassword.getText().toString());
                }
            }
        });

        ImageView apiSetting =  findViewById(R.id.setting_connection);
        apiSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, BaseUrlActivity.class);
                startActivity(intent);
            }
        });

//        Log.d(TAG, "onCreate: connected " +isConnectedToServer(AppConfig.BASE_URL_API, 10));

    }

    public boolean isConnectedToServer(String url, int timeout) {
        Log.d(TAG, "isConnectedToServer: " +url);
        try{
            URL myUrl = new URL(url);
            URLConnection connection = myUrl.openConnection();
            connection.setConnectTimeout(timeout);
            connection.connect();
            return true;
        } catch (Exception e) {
            // Handle your exceptions
            return false;
        }
    }

    private boolean validate() {
        String uname = etUsername.getText().toString();
        if (uname.trim().equals("")) {
            Toast.makeText(LoginActivity.this, "Please provide a username", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPassword.getText().toString().isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please provide a password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void login(final String username, final String password) {
//        String url = AppConfig.BASE_URL_API+"/login";
//        String url = "http://172.16.12.26:8000/api/login";
        String url = AppConfig.LOGIN;
        RequestQueue queue = VolleySingleton
                .getInstance(LoginActivity.this.getApplicationContext())
                .getRequestQueue();
        Log.d(TAG, "login: url " +url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String msg = jObj.getString("message");
                            int status_code = jObj.getInt("status");

                            btnLogin.setText("Login");
                            btnLogin.setEnabled(true);

                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            // Successful logged in
                            if (status_code == 200) {

                                String name = jObj.getString("name");
                                String token = jObj.getString("token");
                                String branch_id = jObj.getString("branch_id");

                                LoginToken login_token = new LoginToken(null, token, name.trim(), Long.valueOf(branch_id));
                                mDBHelper.addUserToken(login_token);
                                Log.d(TAG, "onResponse: " + login_token.toString());
                                mToken = token;

                                session.setLogin(true);
                                Log.d(TAG, "onResponse: isLoggedIn" + session.isLoggedIn());
                                finish();
                                Intent intent = new Intent(LoginActivity.this, ProductActivity.class);
                                intent.putExtra("branch_id", branch_id);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse:for login " + e.getMessage());
                        }

                        mDBHelper.close();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleySingleton.showErrors(error, LoginActivity.this);
                        Log.d(TAG, "onErrorResponse: login " +error);
                        NetworkResponse response = error.networkResponse;

                        btnLogin.setText("Login");
                        btnLogin.setEnabled(true);
                        mDBHelper.close();

                        if (response == null){
                            Toast.makeText(LoginActivity.this, "Sorry can't login an account. Try again", Toast.LENGTH_LONG).show();
                        }
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Toast.makeText(LoginActivity.this, errorString, Toast.LENGTH_LONG).show();
                        }
                    }
                }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }
        };
        // Add the realibility on the connection.
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        VolleySingleton.getInstance(LoginActivity.this).addToRequestQueue(stringRequest);
    }
}
