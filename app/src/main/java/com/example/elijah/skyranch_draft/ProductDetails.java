package com.example.elijah.skyranch_draft;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ProductDetails extends AppCompatActivity {
    private static final String TAG = ProductDetails.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);

       Product product =  getIntent().getExtras().getParcelable("parcel_data");
        Log.d(TAG, "onCreate: " +product);
//        Toast.makeText(this, "" +product, Toast.LENGTH_SHORT).show();

        TextView tvProName = findViewById(R.id.tvProDetailName);
        ImageView ivProImg = findViewById(R.id.ivProDetailImg);
        TextView tvProPrice = findViewById(R.id.tvPrice);

        tvProName.setText(product.getName());
        Picasso.get().load(product.getImgUrl()).error(R.drawable.pro_img_placeholder).fit().centerInside().into(ivProImg);
        tvProPrice.setText(String.valueOf(product.getO_price()));


    }
}
