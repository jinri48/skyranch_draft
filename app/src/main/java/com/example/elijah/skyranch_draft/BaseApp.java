package com.example.elijah.skyranch_draft;

import android.app.Application;

/**
 * Created by Administrator on 2017/4/27.
 */

public class BaseApp extends Application {
    private boolean isAidl;

    public boolean isAidl() {
        return isAidl;
    }

    public void setAidl(boolean aidl) {
        isAidl = aidl;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isAidl = true;
        com.example.elijah.skyranch_draft.utils.AidlUtil.getInstance().connectPrinterService(this);
    }
}
