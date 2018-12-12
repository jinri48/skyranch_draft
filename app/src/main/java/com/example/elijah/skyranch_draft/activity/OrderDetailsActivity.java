package com.example.elijah.skyranch_draft.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
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
import com.example.elijah.skyranch_draft.DatabaseHelper;
import com.example.elijah.skyranch_draft.LoginActivity;
import com.example.elijah.skyranch_draft.LoginToken;
import com.example.elijah.skyranch_draft.Product;
import com.example.elijah.skyranch_draft.ProductActivity;
import com.example.elijah.skyranch_draft.R;
import com.example.elijah.skyranch_draft.SessionManager;
import com.example.elijah.skyranch_draft.VolleySingleton;
import com.example.elijah.skyranch_draft.adapter.OrderDetailAdapter;
import com.example.elijah.skyranch_draft.adapter.SalesHistoryAdapter;
import com.example.elijah.skyranch_draft.model.OrderDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {

    private RecyclerView mRv_Detail;
    private ArrayList<OrderDetail> mList;
    private OrderDetailAdapter mAdapter;
    private DatabaseHelper mDBHelper;
    private String TAG =  OrderDetailsActivity.class.getSimpleName();
    private SessionManager session;
    private RequestQueue mRequestQ;
    private ProgressBar pbOrderDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);


        // establish a database connection
        mDBHelper = DatabaseHelper.newInstance(this);
        session = new SessionManager(this);
        mRequestQ = Volley.newRequestQueue(this);
        
        Intent intent = getIntent();
        long id = intent.getExtras().getLong(SalesHistoryAdapter.KEY_ORDER_ID, -1);

        pbOrderDetail = findViewById(R.id.pbOrderDetail);
        
        if (id > -1 ){
            mList =  new ArrayList<>();
            getAllOSD(0, id);
            mRv_Detail = findViewById(R.id.rv_OSD);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            mRv_Detail.setLayoutManager(llm);

            mAdapter = new OrderDetailAdapter(OrderDetailsActivity.this, mList);
            mRv_Detail.setAdapter(mAdapter);

            
        }else{
            /* show no data available*/
        }



    }



    private void getAllOSD(int page_num, long id) {
        final LoginToken user = mDBHelper.getUserToken();
        // redirect to login page
        if (user == null) {
            Intent intent = new Intent(OrderDetailsActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {

            String url = AppConfig.GET_ORDER_DETAILS + id + AppConfig.APPEND_DETAIL;
            Log.d(TAG, "getAllProducts: url" + url);
            JsonObjectRequest jObjreq = new JsonObjectRequest(Request.Method.POST, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                pbOrderDetail.setProgress(100);
                                pbOrderDetail.setVisibility(View.GONE);
                                if (response.getBoolean("success") == false) {
                                    if (response.getInt("status") == 401) {
                                        /*
                                         * TODO: make a dialog that the user is not currently on duty
                                         * */

                                        finish();
                                        session.setLogin(false);
                                        mDBHelper.deleteUsers();
                                        mDBHelper.deleteAllItems();

                                        Intent intent = new Intent(OrderDetailsActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                    }
                                }
                                if (response.getInt("status") == 200) {

                                    JSONArray data = response.getJSONArray("data");

                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject item = data.getJSONObject(i);
                                        String osd_no =  item.getString("order_detail_id");
                                        String pro_id = item.getString("product_id");
                                        String pro_name = item.getString("product_name");
                                        String pro_part_no = item.getString("part_number");
                                        String qty = item.getString("qty");
                                        String srp = item.getString("srp");
                                        String amount =  item.getString("amount");
                                        String discount = item.getString("discount");
                                        String netamount = item.getString("net_amount");
                                        String stat = item.getString("status");

                                        Product pro =  new Product();
                                        pro.setId(Long.parseLong(pro_id));
                                        pro.setName(pro_name);
                                        pro.setPart_no(pro_part_no);
                                        pro.setO_price(Double.parseDouble(srp));

                                        OrderDetail osd = new OrderDetail();
                                        osd.setId(Long.parseLong(osd_no));

                                        osd.setQty((long)Double.parseDouble(qty));
                                        osd.setDiscount(Double.parseDouble(discount));
                                        osd.setNet_amount(Double.parseDouble(netamount));
                                        osd.setStatus(stat);
                                        osd.setProduct(pro);
                                        osd.setTotal_amount(Double.parseDouble(amount));
                                        
                                        mList.add(osd);
                                    }
                                }

                                mAdapter.notifyDataSetChanged();
                                Log.d(TAG, "onResponse: listOSD : " +mList);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d(TAG, "onResponse: listerr" + e.getMessage());
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pbOrderDetail.setVisibility(View.GONE);
                    VolleySingleton.showErrors(error, OrderDetailsActivity.this);
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        String errorString = new String(response.data);
                        Toast.makeText(OrderDetailsActivity.this, errorString, Toast.LENGTH_LONG).show();
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
    }


}
