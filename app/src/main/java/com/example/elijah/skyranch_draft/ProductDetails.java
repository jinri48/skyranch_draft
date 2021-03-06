package com.example.elijah.skyranch_draft;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class ProductDetails extends AppCompatActivity {
    private static final String TAG = ProductDetails.class.getSimpleName();
    private DatabaseHelper mDBHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

//        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
//        setSupportActionBar(myToolbar);

       Product product =  getIntent().getExtras().getParcelable("parcel_data");
        Log.d(TAG, "onCreate: " +product);

        TextView tvProName = findViewById(R.id.tvProDetailName);
        ImageView ivProImg = findViewById(R.id.ivProDetailImg);
        TextView tvProPrice = findViewById(R.id.tvPrice);
        TextView tvProStatus = findViewById(R.id.tvProStatus);

        tvProName.setText(product.getName());
        Picasso.get().load(product.getImgUrl()).error(R.drawable.pro_img_placeholder).fit().centerInside().into(ivProImg);
        tvProPrice.setText(String.format("%,.2f",product.getO_price()));
        String status = "";
        switch (product.getStatus()){
            case 'S' : status = "Served"; break;
            case 'C' : status = "Completed"; break;
            case 'I' : status = "Invoiced"; break;
            default  : status = "Pending"; break;
        }
        tvProStatus.setText(status);
    }


}
