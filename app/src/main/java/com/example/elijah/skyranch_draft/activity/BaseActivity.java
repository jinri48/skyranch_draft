package com.example.elijah.skyranch_draft.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.elijah.skyranch_draft.BaseApp;

public abstract class BaseActivity extends AppCompatActivity {

    public BaseApp baseApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseApp = (BaseApp) getApplication();
    }

}
