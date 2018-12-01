package com.example.elijah.skyranch_draft;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.elijah.skyranch_draft.activity.BaseActivity;
import com.example.elijah.skyranch_draft.activity.CustomerActivity;
import com.example.elijah.skyranch_draft.utils.AidlUtil;
import com.example.elijah.skyranch_draft.utils.BluetoothUtil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import com.example.elijah.skyranch_draft.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;



public class Cart extends BaseActivity {
    private static final String TAG = Cart.class.getSimpleName();
    private DatabaseHelper mDBHelper;

    private RecyclerView mRecyclerView;
    private ArrayList<OrderItem> mCartItems;
    private CartAdapter mAdapter;
    static TextView tvCartPriceTotal;
    private Button tvPlaceHolder;
    private SessionManager session;
    public static LinearLayout layout;

    private boolean noReceipt = false; // paperbased
    private boolean loyalty = false; // has loyalty
    Switch switch_no_receipt, switch_no_loyalty;

    private String barcodeTxt;
    private Bitmap bitmap;
    private long customer_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        mDBHelper = DatabaseHelper.newInstance(this);
        session = new SessionManager(this);
        mRecyclerView = findViewById(R.id.rv_cart);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mCartItems = new ArrayList<>();

        mCartItems = mDBHelper.getCart();
        mAdapter = new CartAdapter(Cart.this, mCartItems);
        mRecyclerView.setAdapter(mAdapter);

        layout = findViewById(R.id.layout_linear);

        tvCartPriceTotal = findViewById(R.id.tvCartPriceTotal);
        tvCartPriceTotal.setText("Total: P" + String.format("%,.2f", mAdapter.getTotalItems(mCartItems)));
        tvPlaceHolder = findViewById(R.id.bPlaceOrder);

        tvPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCartItems.size() > 0) {
//                    // if the customer has a load wallet item  || wants to be in a loyalty || no receipt or paperless
//                    if (hasLoadWalletItem(mCartItems) || loyalty == true || noReceipt == true){
                        openSearchCustomerDialog();
//                    }
//
//                    if (noReceipt == false ){ //paper based is selected
//                        if(loyalty == false){
//                            // issue a print receipt without asking the customers info
//                            addOrder();
//                            return;
//                        }
//                    }
                }
            }
        });

        Button bRemoveItems = findViewById(R.id.bRemoveItems);
        bRemoveItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCartItems.size() > 0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);
                    builder.setTitle("Warning")
                            .setMessage("Are you sure you want to remove all the items in your cart?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    int result = mDBHelper.deleteAllItems();
                                    Log.d(TAG, "onClick: removeAllItems " + result);
                                    if (result >= 0) {
                                        mCartItems.clear();
                                        mAdapter.notifyDataSetChanged();
                                        tvCartPriceTotal.setText("Total: P" + String.format("%,.2f", mAdapter.getTotalItems(mCartItems)));

                                    }
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });
    }


    private boolean hasLoadWalletItem(ArrayList<OrderItem> orders ){
        boolean hasLoadWallet = false;

        for (OrderItem item : orders){
            Log.d(TAG, "hasLoadWalletItem: " + item);

            if (item.getProduct().getName().toLowerCase().contains("wallet load")){
                hasLoadWallet = true;

                break;
            }
        }

        Toast.makeText(Cart.this, "has wallet load " +hasLoadWallet, Toast.LENGTH_SHORT).show();
        return hasLoadWallet;

    }

    private void placeOrderWithReceipt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this);

        builder.setMessage("Are you sure you want to place your order?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        addOrder();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void openSearchCustomerDialog(){
        Intent i = new Intent(Cart.this, CustomerActivity.class);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                long result = data.getLongExtra("customer_id",  -1);
                if (result > -1){
                   customer_id = result;
                   addOrder();
                    Toast.makeText(Cart.this, "cart " +result, Toast.LENGTH_SHORT).show();
                    //clean up list
                    // print

                }

            }
            if (resultCode == RESULT_CANCELED) {
//                mTextViewResult.setText("Nothing selected");
            }
        }
    }



    public void setPrintAlignment(){
        byte[] send;
        send = com.sunmi.printerhelper.utils.ESCUtil.alignCenter();
        if (baseApp.isAidl()) {
            AidlUtil.getInstance().sendRawData(send);
        } else {
            BluetoothUtil.sendData(send);
        }
    }

    public void generateBarcode(String barcode_data){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(barcode_data, BarcodeFormat.CODE_39,384,100);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            this.bitmap = bitmap;
            this.barcodeTxt = barcode_data;
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private void printReceipt(){
        if(baseApp.isAidl()){
            boolean isBold = true;
            boolean isUnderLine = false;
            AidlUtil.getInstance().printText("Enchanted Kingdom", 32, isBold, isUnderLine);
            AidlUtil.getInstance().printBitmapCust(this.bitmap, 1, this.barcodeTxt+"\n");
        }
      else {
//            if(mytype == 0){
//                if(mCheckBox1.isChecked() && mCheckBox2.isChecked()){
//                    BluetoothUtil.sendData(com.sunmi.printerhelper.utils.ESCUtil.printBitmap(bitmap1, 3));
//                }else if(mCheckBox1.isChecked()){
//                    BluetoothUtil.sendData(com.sunmi.printerhelper.utils.ESCUtil.printBitmap(bitmap1, 1));
//                }else if(mCheckBox2.isChecked()){
//                    BluetoothUtil.sendData(com.sunmi.printerhelper.utils.ESCUtil.printBitmap(bitmap1, 2));
//                }else{
//                    BluetoothUtil.sendData(com.sunmi.printerhelper.utils.ESCUtil.printBitmap(bitmap1, 0));
//                }
//            }else if(mytype == 1){
//                BluetoothUtil.sendData(com.sunmi.printerhelper.utils.ESCUtil.selectBitmap(bitmap1, 0));
//            }else if(mytype == 2){
//                BluetoothUtil.sendData(com.sunmi.printerhelper.utils.ESCUtil.selectBitmap(bitmap1, 1));
//            }else if(mytype == 3){
//                BluetoothUtil.sendData(com.sunmi.printerhelper.utils.ESCUtil.selectBitmap(bitmap1, 32));
//            }else if(mytype == 4){
//                BluetoothUtil.sendData(com.sunmi.printerhelper.utils.ESCUtil.selectBitmap(bitmap1, 33));
//            }
//
//            BluetoothUtil.sendData(com.sunmi.printerhelper.utils.ESCUtil.nextLine(3));
        }
    }


    private void addOrder() {
        final LoginToken user = mDBHelper.getUserToken();
        if (user == null) {
            finish();
            session.setLogin(false);
            mDBHelper.deleteUsers();
            mDBHelper.deleteAllItems();

            Intent intent = new Intent(Cart.this, LoginActivity.class);
            startActivity(intent);
        }
        String url = AppConfig.ADD_CART_ITEMS;
        JsonObjectRequest stringRequest = new JsonObjectRequest(Request.Method.POST, url, getPlacedOrders(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: addorder " +response);
                        try {
                            int status_code = response.getInt("status");

                            if (status_code == 200) {
                                String order_header = response.getString("order_header");
                                mDBHelper.deleteAllItems();
                                mCartItems.clear();
                                mAdapter.notifyDataSetChanged();
                                tvCartPriceTotal.setText("Total: P" + String.format("%,.2f", mAdapter.getTotalItems(mCartItems)));
                                Toast.makeText(Cart.this, "Successfully submitted the order", Toast.LENGTH_SHORT).show();
                                if(noReceipt == false){ // can issue a receipt or order slip
                                    setPrintAlignment();
                                    generateBarcode(order_header);
                                    printReceipt();
                                }

                            }
                            Log.d(TAG, "onResponse: placed order status code: " + status_code);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG, "onResponse: " + response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        Log.d(TAG, "onErrorResponse: network response - " + response);
                        Log.d(TAG, "onErrorResponse: error - " + error);

                        VolleySingleton.showErrors(error, Cart.this);
                        String errorMsg = error.getMessage();
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            errorMsg = errorString;
                            return;
                        }
                        Toast.makeText(Cart.this, "Error " + errorMsg, Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.d(TAG, "getHeaders: token " + user.getToken());
                params.put("token", user.getToken());
                return params;
            }
        };

        VolleySingleton.getInstance(Cart.this).addToRequestQueue(stringRequest);
    }



    private JSONObject getPlacedOrders() {
        JSONObject cart = new JSONObject();

        try {
            cart.put("customer_no", customer_id);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.cart_menu, menu);

        Log.d(TAG, "onCreateOptionsMenu: "+menu);

        MenuItem item = (MenuItem) menu.findItem(R.id.switchId);
//        MenuItem itemLoyalty = (MenuItem) menu.findItem(R.id.switchLoyalty);

        item.setActionView(R.layout.switch_no_receipt);
//        itemLoyalty.setActionView(R.layout.switch_no_loyalty);


        switch_no_receipt = item
                .getActionView().findViewById(R.id.switch_noReceipt);
//        switch_no_loyalty = itemLoyalty
//                .getActionView().findViewById(R.id.switch_no_loyalty);

        if (AidlUtil.getInstance().isConnect() == false){
            noReceipt = true; // paperless
            switch_no_receipt.setChecked(true);
            switch_no_receipt.setClickable(false);
        }

        switch_no_receipt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                noReceipt = isChecked;

            }
        });
//        switch_no_loyalty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                loyalty = isChecked;
//                if (isChecked)
//                Toast.makeText(Cart.this, "joined loyalty", Toast.LENGTH_SHORT).show();
//            }
//        });

        return super.onCreateOptionsMenu(menu);
    }



}
