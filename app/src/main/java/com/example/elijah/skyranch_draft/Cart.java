package com.example.elijah.skyranch_draft;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Cart extends AppCompatActivity {
    private static final String TAG = Cart.class.getSimpleName();
    private DatabaseHelper mDBHelper;

    private RecyclerView mRecyclerView;
    private ArrayList<OrderItem> mCartItems;
    private CartAdapter mAdapter;
    static TextView tvCartPriceTotal;
    private Button tvPlaceHolder;
    private JSONObject mPlacedOrders;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        mDBHelper = DatabaseHelper.newInstance(this);
        mRecyclerView = findViewById(R.id.rv_cart);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCartItems = new ArrayList<>();

        mCartItems = mDBHelper.getCart();
        mAdapter = new CartAdapter(Cart.this, mCartItems);
        mRecyclerView.setAdapter(mAdapter);

        tvCartPriceTotal = findViewById(R.id.tvCartPriceTotal);
        tvCartPriceTotal.setText("Total: P" + String.format("%,.2f", mAdapter.getTotalItems(mCartItems)));

        final TextView tvSampData = findViewById(R.id.tvCartData);
        tvPlaceHolder = findViewById(R.id.bPlaceOrder);
        tvPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Gson gson = new Gson();
//                String placedOrders = gson.toJson(mCartItems);
//                Log.d(TAG, "onClick: " + placedOrders);
//                tvSampData.setText(placedOrders);
                if(mCartItems.size() > 0){
                    // TODO: Add an alert dialog to confirm the order/s
                    addOrder();
                }

            }
        });

        Button bRemoveItems = findViewById(R.id.bRemoveItems);
        bRemoveItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCartItems.size() > 0){
                    // TODO: Add an alert dialog before deleting all items
                    int result = mDBHelper.deleteAllItems();
                    Log.d(TAG, "onClick: removeAllItems " + result);
                    if (result >= 0){

                        mCartItems.clear();
                        mAdapter.notifyDataSetChanged();
                        tvCartPriceTotal.setText("Total: P" + String.format("%,.2f", mAdapter.getTotalItems(mCartItems)));
                    }
                }

            }
        });
    }

    private void addOrder() {
        String url = AppConfig.ADD_CART_ITEMS;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, getPlacedOrders(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            int status_code = response.getInt("status");
                            if (status_code == 200){
                                mDBHelper.deleteAllItems();
                                mCartItems.clear();
                                mAdapter.notifyDataSetChanged();
                                tvCartPriceTotal.setText("Total: P" + String.format("%,.2f", mAdapter.getTotalItems(mCartItems)));
                            }
                            Log.d(TAG, "onResponse: placed order status code: "+status_code);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG, "onResponse: "+response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        Log.d(TAG, "onErrorResponse: network response - "+response);
                        Log.d(TAG, "onErrorResponse: error - " +error);

                        String errorMsg = error.getMessage();
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            errorMsg = errorString;
                            return;
                        }

                        Toast.makeText(Cart.this, "Error " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });

        VolleySingleton.getInstance(Cart.this).addToRequestQueue(stringRequest);
    }

    private JSONObject getPlacedOrders(){
        JSONObject cart = new JSONObject();

        try {

            cart.put("token", LoginActivity.mToken);
            cart.put("total_amount", mAdapter.getTotalItems(mCartItems));
            cart.put("total_no_items", mCartItems.size());

            Gson gson = new Gson();
            String placedOrders = gson.toJson(mCartItems);
            JSONArray cartItemArray = new JSONArray();

            for (OrderItem item : mCartItems) {
                JSONObject jsonItem = new JSONObject();
                jsonItem.put("product_id", item.getProduct().getId());
                jsonItem.put("subtotal", item.getAmount());
                jsonItem.put("qty", item.getQty());
                jsonItem.put("product_retail_price", item.getProduct().getO_price());
                jsonItem.put("product_group_no", item.getProduct().getGroup_no());
                jsonItem.put("product_part_no", item.getProduct().getPart_no());
                cartItemArray.put(jsonItem);
            }

            cart.put("items", cartItemArray);
            Log.d(TAG, "onClick placedOrders: " + cart);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "onClick placedOrders error " + e.getMessage());
        }

        return cart;
    }




}
