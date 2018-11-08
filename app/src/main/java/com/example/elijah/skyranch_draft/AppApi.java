package com.example.elijah.skyranch_draft;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AppApi {


    private static final String TAG = AppApi.class.getSimpleName();
    private Context mContext;
    private DatabaseHelper mDBHelper;


    public interface VolleyCallback{
        void onSuccess(Product product);
    }


    public AppApi(Context mContext) {
        this.mContext = mContext;
        mDBHelper = DatabaseHelper.newInstance(this.mContext);
    }

    // connection
    // login (usename password)
    // getProducts() returns arraylist<Product>

    // getProduct(search_value) returns product
    // getProduct(id) returns product

    public Product getProduct(final long id){

       final Product product = null;
        String url = AppConfig.GET_PRODUCT_BY_ID;
//        RequestQueue queue = VolleySingleton.getInstance(mContext).getRequestQueue();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONObject data = new JSONObject(response);
                            JSONObject products = data.getJSONObject("data");

                            Product productItem = new Product();
                            productItem.setId(products.getLong("product_id"));
                            productItem.setName(products.getString("product_name"));
                            ;
//                            product.setId(products.getLong("product_id"));
//                            product.setName(products.getString("product_name"));
//                            Log.d(TAG, "onResponse: getProduct"+product);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("search_id", String.valueOf(id));
                return params;
            }

        };

        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);

        return product;
    }


    public void getProductItem(final VolleyCallback callback, final long id) {
        String url = AppConfig.GET_PRODUCT_BY_ID;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            JSONObject products = data.getJSONObject("data");

                            Product productItem = new Product();
                            productItem.setId(products.getLong("product_id"));
                            productItem.setName(products.getString("product_name"));

                            Log.d(TAG, "onResponse: " +productItem);
                            callback.onSuccess(productItem);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " + error.getMessage());
            }
        }){

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("search_id", String.valueOf(id));
                return params;
            }

        };

        VolleySingleton.getInstance(mContext).addToRequestQueue(stringRequest);
    }


}
