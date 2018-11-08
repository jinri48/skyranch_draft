package com.example.elijah.skyranch_draft;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity{
    private static final String TAG = Cart.class.getSimpleName();
    private DatabaseHelper mDBHelper;

    private RecyclerView mRecyclerView;
    private ArrayList<OrderItem> mCartItems;
    private CartAdapter mAdapter;
    static TextView tvCartPriceTotal;
    private Button tvPlaceHolder;


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
        tvCartPriceTotal.setText("Total: P" +String.format ("%,.2f", mAdapter.getTotalItems(mCartItems)));

        final TextView tvSampData = findViewById(R.id.tvCartData);
        tvPlaceHolder = findViewById(R.id.bPlaceOrder);
        tvPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                String placedOrders = gson.toJson(mCartItems);
                Log.d(TAG, "onClick: " +placedOrders);
//                tvSampData.setText(placedOrders);
            }
        });

    }






}
