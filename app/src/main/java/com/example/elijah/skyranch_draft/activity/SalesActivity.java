package com.example.elijah.skyranch_draft.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.elijah.skyranch_draft.AppConfig;
import com.example.elijah.skyranch_draft.BaseApp;
import com.example.elijah.skyranch_draft.Cart;
import com.example.elijah.skyranch_draft.Customer;
import com.example.elijah.skyranch_draft.CustomerAdapter;
import com.example.elijah.skyranch_draft.DatabaseHelper;
import com.example.elijah.skyranch_draft.EndlessRecyclerViewScrollListener;
import com.example.elijah.skyranch_draft.Interface.SalesHistoryAdapterListener;
import com.example.elijah.skyranch_draft.LoginActivity;
import com.example.elijah.skyranch_draft.LoginToken;
import com.example.elijah.skyranch_draft.OrderHeader;
import com.example.elijah.skyranch_draft.OrderItem;
import com.example.elijah.skyranch_draft.Product;
import com.example.elijah.skyranch_draft.ProductActivity;
import com.example.elijah.skyranch_draft.R;
import com.example.elijah.skyranch_draft.SessionManager;
import com.example.elijah.skyranch_draft.VolleySingleton;
import com.example.elijah.skyranch_draft.adapter.SalesHistoryAdapter;
import com.example.elijah.skyranch_draft.model.SalesHistory;
import com.example.elijah.skyranch_draft.utils.AidlUtil;
import com.example.elijah.skyranch_draft.utils.BluetoothUtil;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sunmi.sunmiui.edit.Edit;

public class SalesActivity extends BaseActivity {
    private RequestQueue mRequestQ; // network calls suing volley
    private DatabaseHelper mDBHelper;
    private SessionManager session;

    private static String TAG = SalesActivity.class.getSimpleName();

    private static String dateStart;
    private static String dateEnd;
    private static int isToday = 1;
    private static SalesHistory history;
    private int page = 1;
    private TextView tv_totalAmount, tv_date;
    private RecyclerView mRv_SalesHist;
    private SalesHistoryAdapter mSalesAdapter;
    ExpandableRelativeLayout expFilterContainer;

    Spinner salesStat;
    private EndlessRecyclerViewScrollListener scrollListener;
    private String custname ="";
    private long os_no = -1;
    ProgressBar pbSales;
    private RelativeLayout root_layout;
    EditText searchtxt;
    private Button refresh;

    public static BaseApp base;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        base = baseApp;
        root_layout = findViewById(R.id.pl_sales);

        mRequestQ = Volley.newRequestQueue(this);
        mDBHelper = DatabaseHelper.newInstance(this);
        session = new SessionManager(this);

        if (mDBHelper.getApiConnection()[1] != null){
            AppConfig.BASE_URL_API = mDBHelper.getApiConnection()[1];
        }

        expFilterContainer          = findViewById(R.id.exp_filter);
        Button btnFilter            = findViewById(R.id.btn_filterSales);
        searchtxt                   = findViewById(R.id.search_sales);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expFilterContainer.toggle();
            }
        });

        dateStart = "2018-12-10";
        dateEnd = "2018-12-10";
        pbSales        = findViewById(R.id.pBSales);
        tv_totalAmount = findViewById(R.id.sales_total);
        tv_date        = findViewById(R.id.sales_date);
        salesStat      = findViewById(R.id.sales_stat);
        history = new SalesHistory();

        searchtxt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || event.getAction() == KeyEvent.ACTION_DOWN){
                    if (v.getText().toString().trim().isEmpty()){
                        custname = "";
                        getSales(page,custname, -1);
                    }else{
                        custname = v.getText().toString().trim();
                        getSales(page, custname, -1);
                    }
                    return true;
                }
                return false;
            }
        });


        getTotal();
        mRv_SalesHist = findViewById(R.id.rv_sales);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRv_SalesHist.setLayoutManager(llm);
        custname = searchtxt.getText().toString().trim();
        getSales(this.page,custname, -1);
        mSalesAdapter = new SalesHistoryAdapter(SalesActivity.this, history, new SalesHistoryAdapterListener() {
            @Override
            public void viewORDialog(View v, int position) {
                showORDialog(SalesActivity.this,  history.getOrders().get(position), false);
            }

            @Override
            public void printORDialog(View v, int position) {
                SalesActivity.printReceipt(BarcodeFormat.QR_CODE, history.getOrders().get(position));
            }
        });
        mRv_SalesHist.setAdapter(mSalesAdapter);

        populateSpinnerStat();

        refresh = findViewById(R.id.refresh_sales);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getTotal();
                custname = "";
                getSales(page, custname, -1);

            }
        });

        /*
            salesStat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String item = parent.getItemAtPosition(position).toString();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        */

        /*
        TODO ADD SCROLLLISTERNER
        * */

        /*scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                custname = searchtxt.getText().toString().trim();
                getSales(page + 1, custname, os_no);

            }
        };
        mRv_SalesHist.addOnScrollListener(scrollListener);*/



    }



    public void getTotal() {
        final LoginToken user = mDBHelper.getUserToken();
        if (user == null) {
            Log.d(TAG, "getAllProducts: user is null");
            Intent intent = new Intent(SalesActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        String url = AppConfig.GET_SALES_TOTAL ;
        JsonObjectRequest jObjreq = new JsonObjectRequest(Request.Method.POST, url,
                getSalesReq(1, "", -1),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                         Log.d(TAG, "onResponse: GET_SALES_HISTORY "+response);
                         pbSales.setVisibility(View.GONE);
                        try {
                            if (response.getBoolean("success") == false) {
                                if (response.getInt("status") == 401) {
                                    /*
                                     * TODO: make a dialog that the user is not currently on duty
                                     * */

                                    finish();
                                    session.setLogin(false);
                                    mDBHelper.deleteUsers();
                                    mDBHelper.deleteAllItems();

                                    Intent intent = new Intent(SalesActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                            if (response.getInt("status") == 200) {
                                JSONObject data = response.getJSONObject("data");
                                history.setNetAmount(data.getDouble("total"));

                                tv_totalAmount.setText("PHP " +String.format("%,.2f",history.getNetAmount()));
                                tv_date.setText("As of " +data.getString("from"));
                            }


                            if (VolleySingleton.prompt !=null){
                                VolleySingleton.prompt.dismiss();
                            }

                            // notify the adapter
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON", "onResponse: " + e.getMessage());
                            Snackbar.make(root_layout, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleySingleton.showErrors(error, root_layout, refresh);
                NetworkResponse response = error.networkResponse;
                pbSales.setVisibility(View.GONE);
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    Toast.makeText(SalesActivity.this, errorString, Toast.LENGTH_LONG).show();
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", user.getToken());
                return params;
            }

        };
        mRequestQ.add(jObjreq);
    }



    public void getSales(int page,String custname, long os_no) {
        pbSales.setVisibility(View.VISIBLE);
        final LoginToken user = mDBHelper.getUserToken();
        if (user == null) {
            finish();
            Log.d(TAG, "getAllProducts: user is null");
            Intent intent = new Intent(SalesActivity.this, LoginActivity.class);
            startActivity(intent);

            return;
        }
        String url = AppConfig.GET_SALES_HISTORY;
        JsonObjectRequest jObjreq = new JsonObjectRequest(Request.Method.POST, url,
                getSalesReq(page, custname, os_no),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: GET_SALES_HISTORY "+response);
                        try {
                            if (response.getBoolean("success") == false) {
                                if (response.getInt("status") == 401) {
                                    /*
                                     * TODO: make a dialog that the user is not currently on duty
                                     * */

                                    finish();
                                    session.setLogin(false);
                                    mDBHelper.deleteUsers();
                                    mDBHelper.deleteAllItems();

                                    Intent intent = new Intent(SalesActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                            if (response.getInt("status") == 200) {

                                JSONObject data = response.getJSONObject("data");
                                JSONObject datum = data.getJSONObject("orders");
                                JSONArray salesOrders = datum.getJSONArray("data");

                                ArrayList<OrderHeader> orderlist = new ArrayList<>();

                                for (int i = 0; i < salesOrders.length(); i++) {

                                    JSONObject item = salesOrders.getJSONObject(i);
                                    Log.d(TAG, "onResponse: ites" +item);
                                    JSONObject customerObj = item.getJSONObject("customer");

                                    OrderHeader order = new OrderHeader();
                                    order.setBranch_id(item.getString("branch_id"));
                                    order.setCce_name(item.getString("cce_name"));
                                    order.setCce_number(item.getString("encoded_by"));
                                    order.setOs_date(item.getString("os_date"));
                                    order.setOr_no(item.getLong("os_no"));
                                    order.setTrans_type(item.getString("transact_type_id"));
                                    order.setTotal_amount(item.optDouble("total_amount", 0));
                                    order.setNet_amount(item.optDouble("net_amount", 0));
                                    order.setStatus(item.getString("status"));

                                    Customer customer = new Customer();
                                    customer.setId(customerObj.optInt("id", 0));
                                    Log.d(TAG, "onResponse: customer "+customerObj);
                                    String custName = customerObj.isNull("name") ? "N/A" : customerObj.getString("name");
                                    String custMob = customerObj.isNull("mobile_num") ? "N/A" : customerObj.getString("mobile_num");
                                    customer.setName(custName);
                                    customer.setMobile(custMob);

                                    order.setCustomer(customer);
                                    orderlist.add(order);
                                }

                                history.setFrom(data.getString("from"));
                                history.setTo(data.getString("to"));
                                history.setOrders(orderlist);
                                Log.d(TAG, "onResponse: orderlist " +orderlist);
                            }

                            // notify the adapter
                            pbSales.setVisibility(View.GONE);
                            mSalesAdapter.notifyDataSetChanged();
                            if (VolleySingleton.prompt !=null){
                                VolleySingleton.prompt.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Snackbar.make(root_layout, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " +error.getMessage());
                VolleySingleton.showErrors(error, root_layout, refresh);
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    Toast.makeText(SalesActivity.this, errorString, Toast.LENGTH_LONG).show();
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", user.getToken());
                return params;
            }

        };
        mRequestQ.add(jObjreq);
    }


    private JSONObject getSalesReq(int page, String custname, long or_no) {
        JSONObject params = new JSONObject();

        try {
            params.put("from", dateStart);
            params.put("to", dateEnd);
            params.put("isToday", 1);
            params.put("page", page);
            params.put("customer_name", custname);
            if(or_no > -1){
                params.put("os_header_no", or_no);
            }
            Log.d(TAG, "getSalesReq: " + params);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getSalesReq" + e.getMessage());
        }
        return params;
    }

    private void populateSpinnerStat(){
        ArrayList <String> status = new ArrayList<>();

        status.add("All");
        status.add("Pending");
        status.add("Completed");
        status.add("Served");
        status.add("Invoiced");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SalesActivity.this,
                android.R.layout.simple_spinner_item, status);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        salesStat.setAdapter(adapter);
    }



    /*======================================*/
    public static Bitmap generateBarcode(String barcode_data, BarcodeFormat format){
        Bitmap barcodeImg = null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            int width = 384;
            int height = 100;
            if (format == BarcodeFormat.QR_CODE) {
                width = 250;
                height = 250;
            }
            BitMatrix bitMatrix = multiFormatWriter.encode(barcode_data, format,width,height);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            barcodeImg = bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
        }

        return barcodeImg;
    }


    public static void printReceipt(BarcodeFormat format, OrderHeader order){
        if(base.isAidl()){
            boolean isBold = true;
            boolean isUnderLine = false;

            Customer customer_info = order.getCustomer();

            if (format == BarcodeFormat.CODE_39){
                if (generateBarcode(String.valueOf(order.getOr_no()), BarcodeFormat.CODE_39) != null){
                    Bitmap bitmap = generateBarcode(String.valueOf(order.getOr_no()), BarcodeFormat.CODE_39);
                    AidlUtil.getInstance().printText("Enchanted Kingdom", 32, isBold, isUnderLine);
                    String content = customer_info.getId()+" - "+customer_info.getName() +"\n";
                    AidlUtil.getInstance().printBitmapCust(bitmap, 1, order.getOr_no()+"\n", content,
                            customer_info.getMobile());
                }

            }else if (format == BarcodeFormat.QR_CODE){
                Log.d(TAG, "printReceipt: custmob " +customer_info.getMobile());
                String content = customer_info.getId()+"-"+customer_info.getName();
                AidlUtil.getInstance().printText("Enchanted Kingdom", 32, isBold, isUnderLine);
                AidlUtil.getInstance().printQr(String.valueOf(order.getOr_no()), 7, 3 );
                AidlUtil.getInstance().printTextCust("Order Slip No: " +order.getOr_no(), 28, isBold, isUnderLine);
                AidlUtil.getInstance().printTextCust(customer_info.getMobile(), 25, false, isUnderLine);
                AidlUtil.getInstance().printTextCust("Customer: " +content +"\n", 25, false, isUnderLine);
            }

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

    public void setPrintAlignment(){
        byte[] send;
        send = com.sunmi.printerhelper.utils.ESCUtil.alignCenter();
        if (baseApp.isAidl()) {
            AidlUtil.getInstance().sendRawData(send);
        } else {
            BluetoothUtil.sendData(send);
        }
    }

    public static void showORDialog(Context context, final OrderHeader order, boolean withPrintBtn){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater factory = LayoutInflater.from(context);
        View order_slip_dialog = factory.inflate(R.layout.sales_order, null);
        builder.setView(order_slip_dialog);
        ScrollView root_layout_dialog   = order_slip_dialog.findViewById(R.id.pl_sales_or_dialog);
        ImageView qr_img                = root_layout_dialog.findViewById(R.id.iv_qr_code);
        TextView or_num                 = root_layout_dialog.findViewById(R.id.tv_or_no);
        TextView customer_mobile        = root_layout_dialog.findViewById(R.id.cust_mobile);
        TextView customer_number        = root_layout_dialog.findViewById(R.id.cust_num);
        TextView customer_name          = root_layout_dialog.findViewById(R.id.CUST_name);
        Button btn_ok                   = root_layout_dialog.findViewById(R.id.btn_ok);
        Button btn_print                = root_layout_dialog.findViewById(R.id.btn_print);

        or_num.setText("Order Slip No: " + order.getOr_no());
        Bitmap bitmap = generateBarcode(String.valueOf(order.getOr_no()), BarcodeFormat.QR_CODE);
        if (bitmap !=null){
            qr_img.setImageBitmap(bitmap);
        }

        Customer customer_info = order.getCustomer();
        String name = "";
        if (customer_info.getName() == null){
            name = customer_info.getLname() + " " +customer_info.getFname();
        }else{
            name = customer_info.getName();
        }

        customer_mobile.setText("Mobile Number: " +customer_info.getMobile());
        customer_number.setText("No: " +String.valueOf(customer_info.getId()));
        customer_name.setText("Name: " +name);


        final AlertDialog dialog  = builder.create();
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printReceipt(BarcodeFormat.QR_CODE, order);
                dialog.dismiss();
            }
        });

        if (withPrintBtn == true){
            btn_print.setVisibility(View.VISIBLE);
        }else{
            btn_print.setVisibility(View.GONE);
        }
        dialog.show();
    }


}

