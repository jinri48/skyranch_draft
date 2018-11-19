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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;
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
        mDatabase = mDBHelper.getWritableDatabase();

        // Session Manager
        session = new SessionManager(this);
        Log.d(TAG, "onCreate: " + session.isLoggedIn());
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
//            Intent intent = new Intent(this, ProductActivity.class);
            Intent intent = new Intent(this, ProductActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validFields = validate(); // check if the fields are filled out
                if (validFields) {
                    login(etUsername.getText().toString(), etPassword.getText().toString());
                }
            }
        });


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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jObj = new JSONObject(response);
                            String msg = jObj.getString("message");
                            int status_code = jObj.getInt("status");

                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            // Successful logged in
                            if (status_code == 200) {
                                String name = jObj.getString("name");
                                String token = jObj.getString("token");
                                String branch_id = jObj.getString("branch_id");

                                LoginToken login_token = new LoginToken(null, token, name.trim());
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


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        Log.d(TAG, "onErrorResponse: network response - "+response);
                        Log.d(TAG, "onErrorResponse: error - " +error);
                        Log.d(TAG, "onErrorResponse: error cause - " +error.getCause());
                        String errorMsg = error.getMessage();
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            errorMsg = errorString;
                            return;
                        }

                        if(response == null) {

                            errorMsg = "Sorry can't login an account. Try again";
                        }

                        Toast.makeText(LoginActivity.this, "Error " + errorMsg, Toast.LENGTH_SHORT).show();
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
