package com.example.elijah.skyranch_draft.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.example.elijah.skyranch_draft.DatabaseHelper;
import com.example.elijah.skyranch_draft.LoginActivity;
import com.example.elijah.skyranch_draft.OrderHeader;
import com.example.elijah.skyranch_draft.OrderItem;
import com.example.elijah.skyranch_draft.Product;
import com.example.elijah.skyranch_draft.ProductActivity;
import com.example.elijah.skyranch_draft.R;
import com.example.elijah.skyranch_draft.SessionManager;
import com.example.elijah.skyranch_draft.VolleySingleton;
import com.example.elijah.skyranch_draft.model.SalesHistory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SalesActivity extends AppCompatActivity {
    private RequestQueue mRequestQ; // network calls suing volley
    private DatabaseHelper mDBHelper;
    private SessionManager session;
    private AbstractQueue<Product> mListItems;
    private static String TAG = SalesActivity.class.getSimpleName();

    private static String dateStart;
    private static String dateEnd;
    private static int isToday = 1;
    private static SalesHistory history;
    private int page = 1;
    private TextView tv_totalAmount, tv_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        mRequestQ = Volley.newRequestQueue(this);
        mDBHelper = DatabaseHelper.newInstance(this);
        session = new SessionManager(this);

        dateStart = "2018-12-04";
        dateEnd = "2018-12-04";

        tv_totalAmount = findViewById(R.id.sales_total);
        tv_date        = findViewById(R.id.sales_date);
        getTotal();
        getSales();
    }

    public void getTotal() {
        String url = AppConfig.GET_SALES_TOTAL ;
        JsonObjectRequest jObjreq = new JsonObjectRequest(Request.Method.POST, url, getSalesReq(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Log.d(TAG, "onResponse: GET_SALES_HISTORY "+response);
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
                                history = new SalesHistory();
                                history.setNetAmount(data.getDouble("total"));
                                tv_totalAmount.setText("PHP "+history.getNetAmount());
                                tv_date.setText("As of " +data.getString("from"));
                            }

                            // notify the adapter
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON", "onResponse: " + e.getMessage());
                            Toast.makeText(SalesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleySingleton.showErrors(error, SalesActivity.this);
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
//                params.put("token", user.getToken());
                return params;
            }

        };
        mRequestQ.add(jObjreq);
    }

    public void getSales() {
        String url = AppConfig.GET_SALES_HISTORY;
        JsonObjectRequest jObjreq = new JsonObjectRequest(Request.Method.POST, url, getSalesReq(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // Log.d(TAG, "onResponse: GET_SALES_HISTORY "+response);
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

                                    OrderHeader order = new OrderHeader();
                                    order.setBranch_id(item.getString("BRANCHID"));
                                    order.setCce_name(item.getString("CCENAME"));
                                    order.setCce_number(item.getString("ENCODEDBY"));
                                    order.setOs_date(item.getString("OSDATE"));
                                    order.setOr_no(item.getLong("ORDERSLIPNO"));
                                    order.setTrans_type(item.getString("TRANSACTTYPEID"));
                                    order.setTotal_amount(item.getDouble("TOTALAMOUNT"));
                                    order.setNet_amount(item.getDouble("NETAMOUNT"));
//                                    Log.d(TAG, "onResponse: order " +order);
                                    orderlist.add(order);
                                }

                                history.setFrom(data.getString("from"));
                                history.setTo(data.getString("to"));
                                history.setOrders(orderlist);
                            }

                            // notify the adapter
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON", "onResponse: " + e.getMessage());
                            Toast.makeText(SalesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleySingleton.showErrors(error, SalesActivity.this);
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
//                params.put("token", user.getToken());
                return params;
            }

        };
        mRequestQ.add(jObjreq);
    }


    private JSONObject getSalesReq() {
        JSONObject params = new JSONObject();
        try {
            params.put("from", dateStart);
            params.put("to", dateEnd);
            params.put("cce_num", "99");
            params.put("cce_branch", "1002");
            params.put("isToday", 0);
            params.put("page", page);
            Log.d(TAG, "getSalesReq: " + params);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d(TAG, "getSalesReq" + e.getMessage());
        }
        return params;
    }
}

