package com.example.elijah.skyranch_draft;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SecondScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_screen);

        final TextView mTextView = (TextView) findViewById(R.id.tvSampData);
        Button bSend = findViewById(R.id.bSend);
        final EditText etName = findViewById(R.id.etName);
        bSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = VolleySingleton.getInstance(SecondScreen.this.getApplicationContext()).getRequestQueue();

                //RequestQueue queue = Volley.newRequestQueue(SecondScreen.this);
                String url = "http://172.16.12.26:8000/api/test";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jObj = new JSONObject(response);
                            JSONObject data =  jObj.getJSONObject("data");
                                    mTextView.setText("Response is: "+data.getString("data"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTextView.setText(error.toString());
                    }
                }){

                    @Override
                    protected Map<String, String> getParams() {
                        // Posting parameters to login url
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("myname", String.valueOf(etName.getText()));
                        return params;
                    }

                };

                VolleySingleton.getInstance(SecondScreen.this).addToRequestQueue(stringRequest);
//                queue.add(stringRequest);
            }
        });


    }

}
