package com.example.elijah.skyranch_draft;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

public class SessionManager {

    private static final String TAG = SessionManager.class.getSimpleName();
    private static final String PREFS = "AppsPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_CUSTOMER = "customer_info";
    // Shared Preferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context mContext;

    int PRIVATE_MODE = 0;


    public SessionManager(Context context) {
        this.mContext = context;
        this.pref = mContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        this.editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        // commit changes
        editor.commit();
        Log.d(TAG, "User login session modified! ");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setCustomer(Customer customer){
        Gson gson = new Gson();
        String json = gson.toJson(customer);
        this.editor.putString(KEY_CUSTOMER, json);
        this.editor.commit();
    }

    public Customer getCustomer(){
        Gson gson = new Gson();
        String json = pref.getString(KEY_CUSTOMER, "");
        Customer obj = gson.fromJson(json, Customer.class);
        return obj;
    }


}
