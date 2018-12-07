package com.example.elijah.skyranch_draft;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class BaseUrlActivity extends AppCompatActivity {
    private String mUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_connection);

        mUrl = AppConfig.BASE_URL_API;

        final Button bBase_Save = findViewById(R.id.bBase_Save);
        final Button bBase_edit = findViewById(R.id.bBase_Edit);
        final Button bBase_Cancel = findViewById(R.id.bBase_Cancel);
        final EditText et_url = findViewById(R.id.etBase_url);

        et_url.setText(AppConfig.BASE_URL_API);
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
                AppConfig.BASE_URL_API = et_url.getText().toString().trim();
                Toast.makeText(BaseUrlActivity.this, AppConfig.BASE_URL_API, Toast.LENGTH_SHORT).show();
            }
        });


    }
}
