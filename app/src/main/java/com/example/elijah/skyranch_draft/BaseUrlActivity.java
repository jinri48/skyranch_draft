package com.example.elijah.skyranch_draft;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BaseUrlActivity extends AppCompatActivity {
    private static final String TAG = BaseUrlActivity.class.getSimpleName();
    private String mUrl;
    EditText et_url;
    private DatabaseHelper mDBHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_connection);

        mDBHelper = DatabaseHelper.newInstance(this);
        mUrl = AppConfig.BASE_URL_API;

        final Button bBase_Save = findViewById(R.id.bBase_Save);
        final Button bBase_edit = findViewById(R.id.bBase_Edit);
        final Button bBase_Cancel = findViewById(R.id.bBase_Cancel);
        et_url = findViewById(R.id.etBase_url);

        Log.d(TAG, "onCreate: url " +AppConfig.BASE_URL_API);

        if (mDBHelper.getApiConnection()[1] == null){
            et_url.setText(AppConfig.BASE_URL_API);
        }else{
            et_url.setText(mDBHelper.getApiConnection()[1]);
        }

        et_url.setEnabled(false);
        bBase_Save.setVisibility(View.GONE);
        bBase_Cancel.setVisibility(View.GONE);

        bBase_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_url.setEnabled(true);
                bBase_Save.setVisibility(View.VISIBLE);
                bBase_Cancel.setVisibility(View.VISIBLE);
                bBase_edit.setVisibility(View.GONE);
            }
        });

        bBase_Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_url.setEnabled(false);
                et_url.setText(mUrl);
                bBase_Save.setVisibility(View.GONE);
                bBase_Cancel.setVisibility(View.GONE);
                bBase_edit.setVisibility(View.VISIBLE);
            }
        });

        bBase_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* todo validate the url*/
                if (validateUrl(et_url.getText().toString()) == false){
                    return;
                }
                if (mDBHelper.deleteConnection() <= -1){
                    return;
                }
                mDBHelper.deleteConnection();
                long result = mDBHelper.createConnection(et_url.getText().toString().trim());
                if (result > -1){
                    AppConfig.BASE_URL_API = et_url.getText().toString().trim();
                    et_url.setText(AppConfig.BASE_URL_API);
                    et_url.setEnabled(false);
                    bBase_Save.setVisibility(View.GONE);
                    bBase_Cancel.setVisibility(View.GONE);
                    bBase_edit.setVisibility(View.VISIBLE);
                    Toast.makeText(BaseUrlActivity.this, AppConfig.BASE_URL_API, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BaseUrlActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });




    }


    private boolean validateUrl(String url){

        boolean isvalid_url = url.trim().matches("^(http|https|ftp)://.*$");
        if (url.trim() == null || url.trim().isEmpty()){
            Toast.makeText(BaseUrlActivity.this, "The url must not be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (isvalid_url == false){
            Toast.makeText(BaseUrlActivity.this, "Please provide a valid url. Starts with http:// or https://", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;

    }
}
