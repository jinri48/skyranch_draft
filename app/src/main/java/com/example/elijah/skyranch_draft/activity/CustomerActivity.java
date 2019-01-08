package com.example.elijah.skyranch_draft.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.elijah.skyranch_draft.AppConfig;
import com.example.elijah.skyranch_draft.Cart;
import com.example.elijah.skyranch_draft.Customer;
import com.example.elijah.skyranch_draft.CustomerAdapter;
import com.example.elijah.skyranch_draft.DatabaseHelper;
import com.example.elijah.skyranch_draft.EndlessRecyclerViewScrollListener;
import com.example.elijah.skyranch_draft.Interface.SingleClickItemListener;
import com.example.elijah.skyranch_draft.LoginActivity;
import com.example.elijah.skyranch_draft.LoginToken;
import com.example.elijah.skyranch_draft.MainActivity;
import com.example.elijah.skyranch_draft.Product;
import com.example.elijah.skyranch_draft.ProductActivity;
import com.example.elijah.skyranch_draft.R;
import com.example.elijah.skyranch_draft.SessionManager;
import com.example.elijah.skyranch_draft.VolleySingleton;
import com.google.android.gms.common.util.DataUtils;
import com.miguelcatalan.materialsearchview.MaterialSearchView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomerActivity extends AppCompatActivity implements SingleClickItemListener {
    private static final String TAG = CustomerActivity.class.getSimpleName();
    private DatabaseHelper mDBHelper;
    private SessionManager session;

    private RecyclerView mRv_Cust;
    private ArrayList<Customer> mCustList;
    private CustomerAdapter mCustAdapter;
    private Customer mSelectedCustomer;


    MaterialSearchView searchViewCust;
    ProgressDialog progressDialog;

    // custom dialog widgets
    private AlertDialog dialog;
    ProgressDialog prgDialog;
    private EditText bday, input_lname, input_fname, input_mobile, input_email;
    private TextInputLayout layout_lname, layout_fname, layout_email, layout_mobile, layout_bday;

    // isPhone Exists
    private boolean isPhoneExists;
    private RequestQueue mRequestQ;
    private String mQuery = "";
    private EndlessRecyclerViewScrollListener scrollListener;
    private ProgressBar progressBar;
    private Button btnAddCustomer;


    private LinearLayout root_layout;
    private ScrollView root_layout_dialog;
    private Button btnRefreshCustList;
    private int frompage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.customer_search_dialog);

        Toolbar myToolbar = findViewById(R.id.toolbar_cust);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDBHelper = DatabaseHelper.newInstance(this);
        session = new SessionManager(this);
        mRequestQ = Volley.newRequestQueue(this);

        if (mDBHelper.getApiConnection()[1] != null){
            AppConfig.BASE_URL_API = mDBHelper.getApiConnection()[1];
        }
        Log.d(TAG, "initObj: appconfig:  " +AppConfig.BASE_URL_API );

        root_layout = findViewById(R.id.pl_customer_search);

        btnRefreshCustList = findViewById(R.id.refresh_customers);

        mCustList = new ArrayList<>();
        progressBar = findViewById(R.id.pbCust);
        getCustList(mQuery, frompage);

        mRv_Cust = findViewById(R.id.rvCustCustom);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        mRv_Cust.setLayoutManager(llm);


        mCustAdapter = new CustomerAdapter(CustomerActivity.this, mCustList);
        mRv_Cust.setAdapter(mCustAdapter);
        mCustAdapter.setOnItemClickListener(CustomerActivity.this);

        Button confirm_selected_cust = findViewById(R.id.confirm_selected_customer);
        Button cancel = findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CustomerActivity.this, Cart.class);
                setResult(RESULT_CANCELED, i);
                finish();
            }
        });

        confirm_selected_cust.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedCustomer == null) {
                    Snackbar.make(root_layout, "Please select a customer. If you can't find his/her record then try to add an account", Snackbar.LENGTH_LONG).show();
                    return;
                }
                /*
                 * TODO: create a dialog that accepts temporary mobile assign it to selected custoner mobile
                 * */
                /*
                *  AlertDialog.Builder builder = new AlertDialog.Builder(CustomerActivity.this);
                    View customer_dialog = getLayoutInflater().inflate(R.layout.customerinfo_dialog, null);
                * */


                Toast.makeText(CustomerActivity.this, "customer " + mSelectedCustomer.getId(), Toast.LENGTH_SHORT).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("customer_id", mSelectedCustomer);
                session.setCustomer(mSelectedCustomer);
                setResult(RESULT_OK, resultIntent);
                finish();

            }
        });

        searchViewCust = findViewById(R.id.cust_search);

        searchViewCust.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                mQuery = "";
                mCustList.clear();
                mCustAdapter.notifyDataSetChanged();
                frompage = 1;
                getCustList("", 1);
            }
        });
        searchViewCust.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.isEmpty()) {
                    Log.d(TAG, "onQueryTextSubmit: not null or empty");
                    mQuery = query;
                    mCustList.clear();
                    mCustAdapter.notifyDataSetChanged();
                    frompage = 1;
                    getCustList(mQuery, frompage);
                }
                Log.d(TAG, "onQueryTextSubmit: " + query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.isEmpty()) {
                    Log.d(TAG, "onQueryTextChange: " + newText);
                }
                return true;
            }
        });
        scrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.d(TAG, "onLoadMore: " + (page + 1) + " query " + mQuery + "total" + totalItemsCount);
                showProgressView();
                frompage = page + 1;
                getCustList(mQuery, frompage);


            }
        };
        mRv_Cust.addOnScrollListener(scrollListener);

        btnRefreshCustList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCustList(mQuery, frompage);
            }
        });
    }

    @Override
    public void onItemClickListener(int position, View view) {
        mCustAdapter.selectedItem();
        mSelectedCustomer = mCustList.get(position);
    }


    public ArrayList<Customer> getCustList(String search_customer, int page) {
        final ArrayList<Customer> custList = new ArrayList<>();
        final LoginToken user = mDBHelper.getUserToken();
        String url = AppConfig.GET_CUSTOMERS;
        /*String params = "?search_value=" + search_customer + "&page=" + page;
        url = url + params;*/

        /* to create a query string
         * url + "?page=1&search_value=regine"
         * */
        Uri builtUri = Uri.parse(url)
                .buildUpon()
                .appendQueryParameter("page", String.valueOf(page))
                .appendQueryParameter("search_value", search_customer)
                .build();
        URL temp_url = null;
        try {
            temp_url = new URL(builtUri.toString());
            url = temp_url.toString();
        } catch (MalformedURLException e) {
            Toast.makeText(CustomerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

//

        JsonObjectRequest jObjreq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    hideProgressView();
                    if (response.getBoolean("success") == false) {
                        if (response.getInt("status") == 401) {
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

                    if (response.getInt("status") == 200) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray customers = data.getJSONArray("data");
                        for (int i = 0; i < customers.length(); i++) {
                            JSONObject item = customers.getJSONObject(i);
                            Log.d(TAG, "onResponse: getCustlist "+item);
                            long id = item.getLong("CUSTOMERID");
                            String name = item.getString("NAME");
                            String bday = item.optString("BIRTHDATE");

                            bday = convertStringDate(bday);
                            String mobile = item.getString("MOBILE_NUMBER");


                            Customer customer = new Customer();
                            customer.setId(id);
                            customer.setName(name);
                            customer.setBday(bday);
                            customer.setMobile(mobile);
                            JSONObject user = item.optJSONObject("user");
                            if (user != null) {
                                String email = user.isNull("email") ? "N/A" : user.getString("email");
                                customer.setEmail(email);
                            }

                            mCustList.add(customer);

                        }
                        hideProgressView();
                        mCustAdapter.notifyDataSetChanged();

                        if (VolleySingleton.prompt !=null){
                            VolleySingleton.prompt.dismiss();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressView();
                VolleySingleton.showErrors(error, root_layout, btnRefreshCustList);
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    Toast.makeText(CustomerActivity.this, errorString, Toast.LENGTH_LONG).show();
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

        VolleySingleton.getInstance(CustomerActivity.this).addToRequestQueue(jObjreq);
        return custList;
    }

    private void openAddCustomerInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CustomerActivity.this);
        View customer_dialog = getLayoutInflater()
                .inflate(R.layout.customerinfo_dialog, null);
        root_layout_dialog = customer_dialog.findViewById(R.id.pl_cust_dialog);
        btnAddCustomer = customer_dialog.findViewById(R.id.add_customer);
        Button btnCancel = customer_dialog.findViewById(R.id.cancel_addcust);
        ImageView setBday = customer_dialog.findViewById(R.id.ivCust_calendar);


        initCustDialogWidgets(customer_dialog);


        builder.setView(customer_dialog);
        dialog = builder.create();
        dialog.show();


        btnAddCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValid = validateCustDialogWidgets();
                if (isValid) {
                    btnAddCustomer.setText("Adding to Database");
                    btnAddCustomer.setEnabled(false);
                    Customer customerDetails = new Customer();
                    customerDetails.setLname(input_lname.getText().toString().trim());
                    customerDetails.setFname(input_fname.getText().toString().trim());
                    customerDetails.setEmail(input_email.getText().toString().trim());
                    customerDetails.setBday(bday.getText().toString());
                    customerDetails.setMobile(input_mobile.getText().toString().trim());
                    addCustomer(customerDetails);
                    /*isPhoneExists(input_mobile.getText().toString().trim());*/
                }
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


    private void initCustDialogWidgets(View customer_dialog) {
        bday = customer_dialog.findViewById(R.id.etCust_bday);
        bday.setEnabled(false);

        // initialize text layout fields
        layout_lname = customer_dialog.findViewById(R.id.input_layout_lname);
        layout_fname = customer_dialog.findViewById(R.id.input_layout_fname);
        layout_email = customer_dialog.findViewById(R.id.input_layout_email);
        layout_mobile = customer_dialog.findViewById(R.id.input_layout_mobile);
        layout_bday = customer_dialog.findViewById(R.id.input_layout_bday);

        // initialize edittext  fields
        input_lname = customer_dialog.findViewById(R.id.input_lname);
        input_fname = customer_dialog.findViewById(R.id.input_fname);
        input_mobile = customer_dialog.findViewById(R.id.input_mobile);
        input_email = customer_dialog.findViewById(R.id.input_email);

        prgDialog = new ProgressDialog(this);
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);

    }

    private boolean validateCustDialogWidgets() {
        //validate customer details

        /*
         * LASTNAME
         * */
        boolean isValid = true;

        if (input_lname.getText().toString().trim().isEmpty()) {
            layout_lname.setError(getString(R.string.err_lname));
            isValid = false;
        } else {
            layout_lname.setErrorEnabled(false);
        }

        /*
         * FIRST NAME
         * */


        if (input_fname.getText().toString().trim().isEmpty()) {
            layout_fname.setError(getString(R.string.err_fname));
            isValid = false;
        } else {
            layout_fname.setErrorEnabled(false);
        }


        /*
         * BIRTHDAY
         * */

        if (bday.getText().toString().trim().isEmpty()) {
            layout_bday.setError(getString(R.string.err_bday));
            isValid = false;
        } else if (calcAge(bday.getText().toString().trim()) < 3) {
            layout_bday.setError(getString(R.string.err_bday2));
            isValid = false;
        } else {
            layout_bday.setErrorEnabled(false);
        }

        /*
         * MOBILE NUMBER
         * */
        if (input_mobile.getText().toString().trim().isEmpty()) { // empty
            layout_mobile.setError(getString(R.string.err_mobile));
            isValid = false;
        } else if (validateMobile(input_mobile.getText().toString().trim()) == false) { // regex
            layout_mobile.setError(getString(R.string.err_mobile));
            isValid = false;
        } else {
            layout_mobile.setErrorEnabled(false);
        }



        if(!input_email.getText().toString().trim().isEmpty()){
            if (android.util.Patterns.EMAIL_ADDRESS.matcher
                    (input_email.getText().toString().trim()).matches() == false) {
                layout_email.setError(getString(R.string.err_email));
                isValid = false;
            } else {
                layout_email.setErrorEnabled(false);
            }
        }
        return isValid;
    }

    /*public boolean isPhoneExists(final String mobile){
        // Show Progress Dialog
        prgDialog.show();
        final LoginToken user = mDBHelper.getUserToken();
        final boolean[] isPhoneExists = new boolean[1];

        String url = AppConfig.PHONE_EXISTS;
        StringRequest stringRequest= new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Hide Progress Dialog
                        prgDialog.hide();

                        try {
                            JSONObject jObj = new JSONObject(response);

                            if (jObj.getBoolean("success") == false) {
                                if (jObj.getInt("status") == 401) {
                                    finish();
                                    session.setLogin(false);
                                    mDBHelper.deleteUsers();
                                    mDBHelper.deleteAllItems();

                                    Intent intent = new Intent(CustomerActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                            if (jObj.getInt("status") == 200) {
                                Log.d(TAG, "onResponse: existing number " +response);
                                boolean exists = jObj.getBoolean("data");
                                isPhoneExists[0] = exists;
                                Log.d(TAG, "onResponse: exist" +exists);
                                if (exists == true){
                                    layout_mobile.setError(getString(R.string.err_mobile2));
                                    return;
                                }else{
                                    layout_mobile.setErrorEnabled(false);
                                    prgDialog.setMessage("Adding to the Database ...");
                                    prgDialog.show();
//                                    Customer customerDetails = new Customer();
//                                    customerDetails.setLname(input_lname.getText().toString().trim());
//                                    customerDetails.setFname(input_fname.getText().toString().trim());
//                                    customerDetails.setEmail(input_email.getText().toString().trim());
//                                    customerDetails.setBday(bday.getText().toString());
//                                    customerDetails.setMobile(input_mobile.getText().toString().trim());
//
//                                    addCustomer(customerDetails);
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "onResponse: " + e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Hide Progress Dialog
                prgDialog.hide();

                VolleySingleton.showErrors(error, CustomerActivity.this);
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    String errorString = new String(response.data);
                    Toast.makeText(CustomerActivity.this, errorString, Toast.LENGTH_LONG).show();
                }
            }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("search_value", mobile );
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token", user.getToken());
                return params;
            }

        };
        mRequestQ.add(stringRequest);
        Log.d(TAG, "isPhoneExists: " +isPhoneExists[0]);
        return  isPhoneExists[0];
    }*/
    public boolean validateMobile(String mobile) {
        // String regex = "^\\+(?:[0-9] ?){6,14}[0-9]$";
        // String regex = "^[+]?[0-9]{10,13}$";
//        String regex = "^([ 0-9\\(\\)\\+\\-]{8,})*$";
        String regex = "^(\\+639)\\d{9}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(mobile);
        return matcher.matches();
    }

    public int calcAge(String bdate) {
        int years = 0;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date dateToday = Calendar.getInstance().getTime(); // date today
            Date birthdate = sdf.parse(bdate); // birthdate

            Long time = (dateToday.getTime() / 1000) - (birthdate.getTime() / 1000);
            years = Math.round(time) / 31536000;
            // int months = Math.round(time - years * 31536000) / 2628000;

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return years;
    }

    public void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener dateSetListener;
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = year + "-" + month + "-" + dayOfMonth;
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

    /*------------Set up Progress bar visibility -----------------*/
    void showProgressView() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressView() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.customer_menu, menu);

        MenuItem searchCustomer = menu.findItem(R.id.action_search_customer);
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

    private String convertStringDate(String date) {
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


    private void addCustomer(final Customer customer) {

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
        Log.d(TAG, "addCustomer: url"+url);
        JsonObjectRequest stringRequest = new JsonObjectRequest(
                Request.Method.POST, url, setCustomerDetails(customer),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        prgDialog.hide();
                        btnAddCustomer.setText("Add to Database");
                        btnAddCustomer.setEnabled(true);
                        Log.d(TAG, "onResponse: addCustomer " + response);
                        try {
                            int status_code = response.getInt("status");
                            if (response.getBoolean("success") == false){
                                String message = response.getString("message");
                                if (message.equals("Mobile Number Exists")){
                                    layout_mobile.setError(getString(R.string.err_mobile2));
                                    return;
                                }else if (message.toLowerCase().trim().contains("not on duty")){
                                    Toast.makeText(CustomerActivity.this, "You cannot add a customer when you are \"NOT ON DUTY\".", Toast.LENGTH_SHORT).show();
                                    return;
                                }else{
                                    Toast.makeText(CustomerActivity.this, "Cannot add customer: " +response, Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                            if (response.getBoolean("success") == true){

                                if (status_code == 200) {
                                    layout_fname.setErrorEnabled(false);
                                    JSONObject data = response.getJSONObject("data");
                                    Log.d(TAG, "onResponse: addorder " + data);
                                    long customer_id = data.getLong("customer_id");
                                    Intent resultIntent = new Intent();
                                    if (dialog.isShowing()) {
                                        customer.setId(customer_id);
                                        if (customer.getName() == null) {
                                            customer.setName(customer.getFname() + " " + customer.getLname());
                                        }
                                        resultIntent.putExtra("customer_id", customer);
                                        session.setCustomer(customer);
                                        dialog.dismiss();
                                    } else {
                                        resultIntent.putExtra("customer_id", mSelectedCustomer.getId());
                                        session.setCustomer(mSelectedCustomer);
                                    }
                                    setResult(RESULT_OK, resultIntent);
                                    finish();
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        prgDialog.hide();
                        btnAddCustomer.setText("Add to Database");
                        btnAddCustomer.setEnabled(true);
                        NetworkResponse response = error.networkResponse;
                        Log.d(TAG, "onErrorResponse: network response - " + response);
                        Log.d(TAG, "onErrorResponse: error - " + error);

                        VolleySingleton.showErrors(error, CustomerActivity.this);
                        String errorMsg = error.getMessage();
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            errorMsg = errorString;
                            Toast.makeText(CustomerActivity.this, "Error " + errorMsg, Toast.LENGTH_SHORT).show();
                            return;
                        }

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
        // to prevent multiple retries
        stringRequest.setRetryPolicy(
                new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(CustomerActivity.this).addToRequestQueue(stringRequest);
    }


    private JSONObject setCustomerDetails(Customer cus) {
        JSONObject customer = new JSONObject();
        try {
            String cust_name = cus.getFname() + " " + cus.getLname();
            if ((cus.getFname().isEmpty() || cus.getFname() == null)
                    || (cus.getLname().isEmpty() || cus.getLname() == null)) {
                cust_name = cus.getName();
            }
            customer.put("name", cust_name);
            customer.put("email", cus.getEmail());
            customer.put("mobile_number", cus.getMobile());
            customer.put("bday", cus.getBday());
            // set to true since were giving loyalty regardless the customer wants it
            customer.put("is_loyalty", true);

            Log.d(TAG, "setCustomerDetails: " +cus);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return customer;
    }


}
