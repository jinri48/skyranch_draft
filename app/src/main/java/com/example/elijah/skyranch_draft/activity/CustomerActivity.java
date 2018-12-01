package com.example.elijah.skyranch_draft.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.elijah.skyranch_draft.AppConfig;
import com.example.elijah.skyranch_draft.Cart;
import com.example.elijah.skyranch_draft.Customer;
import com.example.elijah.skyranch_draft.CustomerAdapter;
import com.example.elijah.skyranch_draft.DatabaseHelper;
import com.example.elijah.skyranch_draft.Interface.SingleClickItemListener;
import com.example.elijah.skyranch_draft.LoginActivity;
import com.example.elijah.skyranch_draft.LoginToken;
import com.example.elijah.skyranch_draft.MainActivity;
import com.example.elijah.skyranch_draft.R;
import com.example.elijah.skyranch_draft.SessionManager;
import com.example.elijah.skyranch_draft.VolleySingleton;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CustomerActivity extends AppCompatActivity implements SingleClickItemListener {
    private static final String TAG = CustomerActivity.class.getSimpleName();
    private DatabaseHelper mDBHelper;
    private SessionManager session;

    private RecyclerView mRv_Cust;
    private ArrayList<Customer> mCustList;
    private CustomerAdapter mCustAdapter;
    private Customer mSelectedCustomer;
    private EditText bday;

    MaterialSearchView searchViewCust;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_search_dialog);
        Toolbar myToolbar = findViewById(R.id.toolbar_cust);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDBHelper = DatabaseHelper.newInstance(this);
        session = new SessionManager(this);

        mCustList = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading... Please wait");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        getCustList("");

        mRv_Cust = findViewById(R.id.rvCustCustom);
        mRv_Cust.setLayoutManager(new LinearLayoutManager(this));

        mCustAdapter = new CustomerAdapter(CustomerActivity.this, mCustList);
        mRv_Cust.setAdapter(mCustAdapter);
        mCustAdapter.setOnItemClickListener(CustomerActivity.this);

        Button confirm_selected_cust = findViewById(R.id.confirm_selected_customer);
        confirm_selected_cust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedCustomer == null){
                    Toast.makeText(CustomerActivity.this, "Please select a customer. If you can't find his/her record then try to add an account", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(CustomerActivity.this, "customer " +mSelectedCustomer.getId(), Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("customer_id", mSelectedCustomer.getId());
                setResult(RESULT_OK, resultIntent);
                finish();

            }
        });

        searchViewCust = findViewById(R.id.cust_search);

    }

    @Override
    public void onItemClickListener(int position, View view) {
       mCustAdapter.selectedItem();
       mSelectedCustomer = mCustList.get(position);
    }


    public ArrayList<Customer> getCustList(String search_customer) {
        final ArrayList<Customer> custList = new ArrayList<>();
        final LoginToken user = mDBHelper.getUserToken();
        String url = AppConfig.GET_CUSTOMERS;
        String params = "?search_value=" +search_customer;
        url = url + params;
        JsonObjectRequest jObjreq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: getCustomers " +response);
                try {
                    if (response.getBoolean("success") == false){
                        if (response.getInt("status") == 401){
                            /*
                             * TODO: make a dialog that the user is not currently on duty
                             * */
                            finish();
                            session.setLogin(false);
                            mDBHelper.deleteUsers();
                            mDBHelper.deleteAllItems();

                            Intent intent = new Intent(CustomerActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }

                    if(response.getInt("status") == 200){
                        JSONObject data = response.getJSONObject("data");
                        JSONArray customers = data.getJSONArray("data");
                        for(int i =0; i < customers.length(); i++){
                            JSONObject item = customers.getJSONObject(i);
                            long id = item.getLong("CUSTOMERID");
                            String name = item.getString("NAME");
                            String bday = item.getString("birthdate");
                            bday = convertStringDate(bday);
                            String mobile = item.getString("mobile_number");

                            JSONObject user = item.getJSONObject("user");
                            String email = user.getString("email");

                            Log.d(TAG, "onResponse: email " +email);
                            Customer customer = new Customer();
                            customer.setId(id);
                            customer.setName(name);
                            customer.setBday(bday);
                            customer.setMobile(mobile);
                            customer.setEmail(email);
                            mCustList.add(customer);

                        }
                        progressDialog.dismiss();
                        mCustAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", user.getToken());
                return params;
            }
        };

        VolleySingleton.getInstance(CustomerActivity.this).addToRequestQueue(jObjreq);
        Log.d(TAG, "getCustList: " +custList.toString());
        return custList;
    }

    private void openAddCustomerInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerActivity.this);
        View customer_dialog = getLayoutInflater()
                .inflate(R.layout.customerinfo_dialog, null);

        Button btnAddCustomer   = customer_dialog.findViewById(R.id.add_customer);
        Button btnCancel        = customer_dialog.findViewById(R.id.cancel_addcust);
        ImageView setBday       = customer_dialog.findViewById(R.id.ivCust_calendar);
        bday                    = customer_dialog.findViewById(R.id.etCust_bday);
        bday.setEnabled(false);



        builder.setView(customer_dialog);
        final AlertDialog dialog = builder.create();
        dialog.show();

        btnAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "add customer ", Toast.LENGTH_SHORT).show();
                // if successfully added customer do something and dismiss the dialog

                Customer customerDetails = new Customer();
                customerDetails.setLname("delagon");
                customerDetails.setFname("reg");
                customerDetails.setEmail("jinri.delagon@gmail.com");
                customerDetails.setBday("2014-01-01");
                customerDetails.setMobile("09065035079");

                addCustomer(customerDetails);

                dialog.dismiss();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        setBday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }


    public void showDatePicker(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener dateSetListener;
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "-" + month + "-" +dayOfMonth;
                bday.setText(date);

            }
        };

        DatePickerDialog dialog = new DatePickerDialog(
                CustomerActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                dateSetListener,
                year, month, day
        );
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customer_menu, menu);

        MenuItem searchCustomer =  menu.findItem(R.id.action_search_customer);
        searchViewCust.setMenuItem(searchCustomer);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_customer:
                openAddCustomerInfo();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    private String convertStringDate(String date){
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat format2 = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date dateNew = format1.parse(date);
            String formatedDate = format2.format(dateNew);
            return formatedDate;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }


    private void addCustomer(Customer customer){
        final LoginToken user = mDBHelper.getUserToken();
        if (user == null) {
            finish();
            session.setLogin(false);
            mDBHelper.deleteUsers();
            mDBHelper.deleteAllItems();
            Intent intent = new Intent(CustomerActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        String url = AppConfig.ADD_CUSTOMER;
        JsonObjectRequest stringRequest = new JsonObjectRequest(
                Request.Method.POST, url, setCustomerDetails(customer),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "onResponse: addorder " +response);
                        try {
                            int status_code = response.getInt("status");
                            if (status_code == 200) {
                                  JSONObject data =  response.getJSONObject("data");
                                  long customer_id = data.getLong("customer_id");
                                  Toast.makeText(CustomerActivity.this, "added customer " +customer_id, Toast.LENGTH_SHORT).show();

//                                Intent resultIntent = new Intent();
//                                resultIntent.putExtra("customer_id", mSelectedCustomer.getId());
//                                setResult(RESULT_OK, resultIntent);
//                                finish();

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

                        VolleySingleton.showErrors(error, CustomerActivity.this);
                        String errorMsg = error.getMessage();
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            errorMsg = errorString;
                            return;
                        }
                        Toast.makeText(CustomerActivity.this, "Error " + errorMsg, Toast.LENGTH_SHORT).show();
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

        VolleySingleton.getInstance(CustomerActivity.this).addToRequestQueue(stringRequest);
    }



    private JSONObject setCustomerDetails(Customer cus){
        JSONObject customer = new JSONObject();
        try {
            String cust_name = cus.getFname() + " " + cus.getLname();
            if ( (cus.getFname().isEmpty() || cus.getFname() == null)
                    || (cus.getLname().isEmpty() || cus.getLname() == null) ){
                cust_name = cus.getName();
            }
            customer.put("name", cust_name);
            customer.put("email", cus.getEmail());
            customer.put("mobile_number", cus.getMobile());
            customer.put("bday", cus.getBday());
            customer.put("password", cus.getLname()+cus.getMobile());
            // set to true since were giving loyalty regardless the customer wants it
            customer.put("is_loyalty", true);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return customer;
    }

}
