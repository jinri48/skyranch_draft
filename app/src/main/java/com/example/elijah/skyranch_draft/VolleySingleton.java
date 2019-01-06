package com.example.elijah.skyranch_draft;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

    private static final String TAG = VolleySingleton.class.getSimpleName();
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    public static Snackbar prompt;

    private VolleySingleton(Context context) {
        this.mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    // synchronized only one thread process at a time
    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static void showErrors(VolleyError error, Context context){
        Log.d(TAG, "onErrorResponse: " +error);
        error.printStackTrace();

        String errorMsg = "";
        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            //This indicates that the request has either time out or there is no connection
            errorMsg = "Timeout for connection exceeded. Can't connect to " +AppConfig.BASE_URL_API;
        } else if (error instanceof AuthFailureError) {
                //Error indicating that there was an Authentication Failure while performing the request
                errorMsg = "Authentication Failed";
                Log.d(TAG, "onErrorResponse: auth" );
        } else if (error instanceof ServerError) {
            //Indicates that the server responded with a error response
            errorMsg =  "Server Error";
            Log.d(TAG, "onErrorResponse: server" );
        } else if (error instanceof NetworkError) {
            //Indicates that there was network error while performing the request
            errorMsg = "There was network error while performing the request. Please try again";
            Log.d(TAG, "onErrorResponse: network" );
        } else if (error instanceof ParseError) {
            // Indicates that the server response could not be parsed
            errorMsg = "Error encountered in loading the data. The server response could not be parsed";
            Log.d(TAG, "onErrorResponse: parse" );
        }else {
            errorMsg = error.getMessage();
            Log.d(TAG, "onErrorResponse: " +errorMsg );
        }

        Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show();

    }

    public static void showErrors(VolleyError error, View v, final Button btn){
        Log.d(TAG, "onErrorResponse: " +error);
        error.printStackTrace();

        String errorMsg = "";
        prompt = Snackbar.make(v, errorMsg, Snackbar.LENGTH_INDEFINITE);
        prompt.dismiss();

        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
            //This indicates that the request has either time out or there is no connection
            // Timeout for connection exceeded
            errorMsg = "Connection Timeout. Please check if you are connected to the portal and api";
            prompt = Snackbar.make(v, errorMsg, Snackbar.LENGTH_INDEFINITE);
            if (btn != null){
                prompt.setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn.performClick();

                    }
                });
            }


        } else if (error instanceof AuthFailureError) {
            //Error indicating that there was an Authentication Failure while performing the request
            errorMsg = "Authentication Failed";
            prompt = Snackbar.make(v, errorMsg, Snackbar.LENGTH_INDEFINITE);


        } else if (error instanceof ServerError) {
            //Indicates that the server responded with a error response
            errorMsg =  "Server Error. If error persist, feel free to contact us";
            prompt = Snackbar.make(v, errorMsg, Snackbar.LENGTH_INDEFINITE);
            prompt.setAction("Contact Us", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // todo: send a message to the tech support
                }
            });


        } else if (error instanceof NetworkError) {
            //Indicates that there was network error while performing the request
            errorMsg = "There was network error while performing the request. Please try again";
            prompt = Snackbar.make(v, errorMsg, Snackbar.LENGTH_INDEFINITE);
            if (btn!=null) {
                prompt.setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btn.performClick();

                    }
                });

            }


        } else if (error instanceof ParseError) {
            // Indicates that the server response could not be parsed
            errorMsg = "Error encountered in loading the data.The server response could not be parsed. If error persist feel free to contact us";
            prompt = Snackbar.make(v, errorMsg, Snackbar.LENGTH_INDEFINITE);
            prompt.setAction("Contact Us", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // todo: send a message to the tech support
                }
            });

        }else{

        }

        View snackbarView = prompt.getView();
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(5);  // show multiple line
        prompt.setActionTextColor(prompt.getContext().getResources().getColor(R.color.colorPrimary));

        prompt.show();
    }
}
