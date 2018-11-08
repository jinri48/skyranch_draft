package com.example.elijah.skyranch_draft;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Cart extends AppCompatActivity{
    private DatabaseHelper mDBHelper;

    private RecyclerView mRecyclerView;
    private ArrayList<OrderItem> mCartItems;
    private CartAdapter mAdapter;
    TextView tvCartPriceTotal;
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
//        tvCartPriceTotal.setText("Total: P" +String.format ("%,.2f", getTotalItems(mCartItems)));





//        TextView tvSampData = findViewById(R.id.tvCartData);
//        tvSampData.setText(mDBHelper.getCart_samp().toString());
    }




}
