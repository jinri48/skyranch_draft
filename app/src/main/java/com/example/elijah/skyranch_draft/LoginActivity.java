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
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.crashlytics.android.Crashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private DatabaseHelper mDBHelper;

    public static String mToken;
    private SessionManager session;

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;

    String[] api_url;

    private ScrollView root_layout;
    Snackbar prompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);


        root_layout = findViewById(R.id.pl_login);
        etUsername  = findViewById(R.id.input_username);
        etPassword  = findViewById(R.id.input_password);
        btnLogin    = findViewById(R.id.btn_login);

        // establish a database connection
        mDBHelper = DatabaseHelper.newInstance(this);
        // Session Manager
        session = new SessionManager(this);


        api_url  = mDBHelper.getApiConnection();
        if (api_url[1] != null){
           AppConfig.BASE_URL_API = api_url[1];
            Log.d(TAG, "onCreate: App con" +AppConfig.BASE_URL_API);
            Log.d(TAG, "onCreate: api url " +api_url[0] +" - " +api_url[1]);
        }
        AppConfig.init();
//        Toast.makeText( this, "APP CONFIG " +AppConfig.BASE_URL_API , Toast.LENGTH_SHORT).show();
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
                Snackbar.make(root_layout, "", Snackbar.LENGTH_INDEFINITE).dismiss();
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

        Log.d(TAG, "onCreate: connected " +isConnectedToServer(AppConfig.BASE_URL_API, 10));

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
        if (etPassword.getText().toString().isEmpty() && uname.trim().isEmpty()){
            prompt.make(root_layout, "Please fill up the fields", Snackbar.LENGTH_INDEFINITE).show();
            return false;
        }
        if (uname.trim().equals("")) {
            prompt.make(root_layout, "Please provide a username", Snackbar.LENGTH_INDEFINITE).show();
            return false;
        }
        if (etPassword.getText().toString().isEmpty()) {
            prompt.make(root_layout, "Please provide a password", Snackbar.LENGTH_INDEFINITE).show();
            return false;
        }
        return true;
    }

    private void login(final String username, final String password) {
//        String url = AppConfig.BASE_URL_API+"/login";
//        String url = "http://172.16.12.26:8000/api/login";
        AppConfig.init();
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

                            prompt.make(root_layout, msg, Snackbar.LENGTH_INDEFINITE).show();
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
                        String errorMsg = "";


                        VolleySingleton.showErrors(error, root_layout, btnLogin);
//                        Log.d(TAG, "onErrorResponse: login " +error);
                        NetworkResponse response = error.networkResponse;
//
                        btnLogin.setText("Login");
                        btnLogin.setEnabled(true);
                        mDBHelper.close();
//

                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Toast.makeText(LoginActivity.this, "Sorry cant login an account due to " +errorString, Toast.LENGTH_LONG).show();
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
