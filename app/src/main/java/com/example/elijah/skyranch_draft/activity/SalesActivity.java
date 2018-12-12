package com.example.elijah.skyranch_draft.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.example.elijah.skyranch_draft.Customer;
import com.example.elijah.skyranch_draft.CustomerAdapter;
import com.example.elijah.skyranch_draft.DatabaseHelper;
import com.example.elijah.skyranch_draft.EndlessRecyclerViewScrollListener;
import com.example.elijah.skyranch_draft.LoginActivity;
import com.example.elijah.skyranch_draft.OrderHeader;
import com.example.elijah.skyranch_draft.OrderItem;
import com.example.elijah.skyranch_draft.Product;
import com.example.elijah.skyranch_draft.ProductActivity;
import com.example.elijah.skyranch_draft.R;
import com.example.elijah.skyranch_draft.SessionManager;
import com.example.elijah.skyranch_draft.VolleySingleton;
import com.example.elijah.skyranch_draft.adapter.SalesHistoryAdapter;
import com.example.elijah.skyranch_draft.model.SalesHistory;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sunmi.sunmiui.edit.Edit;

public class SalesActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);
        mRequestQ = Volley.newRequestQueue(this);
        mDBHelper = DatabaseHelper.newInstance(this);
        session = new SessionManager(this);


        Button btnFilter = findViewById(R.id.btn_filterSales);
        expFilterContainer = findViewById(R.id.exp_filter);
        final EditText searchtxt = findViewById(R.id.search_sales);
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expFilterContainer.toggle();
            }
        });
        pbSales = findViewById(R.id.pBSales);

        dateStart = "2018-12-10";
        dateEnd = "2018-12-10";

        tv_totalAmount = findViewById(R.id.sales_total);
        tv_date        = findViewById(R.id.sales_date);
        salesStat      = findViewById(R.id.sales_stat);

        history = new SalesHistory();

        getTotal();

        mRv_SalesHist = findViewById(R.id.rv_sales);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRv_SalesHist.setLayoutManager(llm);

        getSales(1,"", -1);
        mSalesAdapter = new SalesHistoryAdapter(SalesActivity.this, history);
        mRv_SalesHist.setAdapter(mSalesAdapter);

        populateSpinnerStat();
        /*
        salesStat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

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
//                params.put("token", user.getToken());
                return params;
            }

        };
        mRequestQ.add(jObjreq);
    }



    public void getSales(int page,String custname, long os_no) {
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
                            mSalesAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON", "onResponse: " + e.getMessage());
                            Toast.makeText(SalesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: " +error.getMessage());
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


    private JSONObject getSalesReq(int page, String custname, long or_no) {
        JSONObject params = new JSONObject();

        try {
            params.put("from", dateStart);
            params.put("to", dateEnd);
            params.put("cce_num", "99");
            params.put("cce_branch", "1002");
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

}

