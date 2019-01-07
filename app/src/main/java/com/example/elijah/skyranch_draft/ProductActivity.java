package com.example.elijah.skyranch_draft;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.android.volley.toolbox.Volley;
import com.example.elijah.skyranch_draft.activity.PrintActivity;
import com.example.elijah.skyranch_draft.activity.SalesActivity;
import com.example.elijah.skyranch_draft.activity.SettingActivity;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
//import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProductActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = ProductActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private ArrayList<Product> mListItems; // storage var for our JSON
    private RequestQueue mRequestQ; // network calls suing volley
    private DatabaseHelper mDBHelper;
    private SessionManager session;

    private EndlessRecyclerViewScrollListener scrollListener;
    private ProgressBar progressBar;
    GridLayoutManager gridLayoutManager;

    private String mQuery = ""; // query for search view
    private String mGroupCode = "";
//    private SearchView mSearchView;
    MaterialSearchView materialSearchView;

    ArrayAdapter<ProductGroup> groupAdapter;
    private Spinner spinner_procategory;
    private List<ProductGroup> productGroups;

    private LinearLayout root_layout;
    private Button btnRefresh;
    TextView tv_noresults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_activity);
        initObj();

    }


    private void initObj() {
        final Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.section_shop);
        setSupportActionBar(myToolbar);

        mRequestQ = Volley.newRequestQueue(this);
        mDBHelper = DatabaseHelper.newInstance(this);
        session = new SessionManager(this);

        if (mDBHelper.getApiConnection()[1] != null){
            AppConfig.BASE_URL_API = mDBHelper.getApiConnection()[1];
        }


        Log.d(TAG, "initObj: appconfig:  " +AppConfig.BASE_URL_API );
        root_layout = findViewById(R.id.pl_products);

        btnRefresh  = findViewById(R.id.refresh_products);
        progressBar = findViewById(R.id.progressBar);
        mRecyclerView =findViewById(R.id.rv_products);
        mListItems = new ArrayList<>();

        tv_noresults = findViewById(R.id.no_res);

        // make your container displayed in a linear way
        // LinearLayoutManager llm = new LinearLayoutManager(this);
        // llm.setOrientation(LinearLayoutManager.VERTICAL);
        // mRecyclerView.setLayoutManager(llm);

        showProgressView();
        gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
//        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mAdapter = new ProductAdapter(ProductActivity.this, mListItems);
        mRecyclerView.setAdapter(mAdapter);


        spinner_procategory = findViewById(R.id.spinner_proCat);
        productGroups = new ArrayList<>();
        getAllProductGroups();

        groupAdapter = new ArrayAdapter<ProductGroup>(ProductActivity.this,
                android.R.layout.simple_spinner_dropdown_item, productGroups);
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_procategory.setAdapter(groupAdapter);
        spinner_procategory.setOnItemSelectedListener(this);


        getAllProducts(1, mQuery, mGroupCode);

        materialSearchView = findViewById(R.id.sampleSearch);
        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                mQuery = "";
                mListItems.clear();
                mAdapter.notifyDataSetChanged();
                getAllProducts(1, "", mGroupCode);
            }
        });
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null){
                    Log.d(TAG, "onQueryTextSubmit: not null or empty" );
                    mQuery = query;
                    mListItems.clear();
                    mAdapter.notifyDataSetChanged();
                    getAllProducts(1, mQuery, mGroupCode);
                }


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && !newText.isEmpty()){

                }
                return true;
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                showProgressView();
                getAllProducts(page + 1, mQuery, mGroupCode);
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllProductGroups();
                getAllProducts(1, mQuery, mGroupCode);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shop_menu, menu);
        MenuItem searchingSamp =  menu.findItem(R.id.action_search_sample);
        materialSearchView.setMenuItem(searchingSamp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_cart:
                intent = new Intent(this, Cart.class);
                startActivity(intent);
                return true;

            case R.id.action_signout:
                signingOut();
                return true;

            case R.id.action_settings:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_testprint:
                intent = new Intent(this, PrintActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sales:
                intent = new Intent(this, SalesActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_refresh:
                mListItems.clear();
                mAdapter.notifyDataSetChanged();
                btnRefresh.performClick();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

    }

    /*--------dialog before signing out--------------*/
    private void signingOut() {
        long itemsCountInCart = mDBHelper.getCartCount();
        Log.d(TAG, "onOptionsItemSelected:  itemsCountInCart " + itemsCountInCart);
        if (itemsCountInCart > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
            builder.setTitle("Warning")
                    .setMessage("All items in your cart will be deleted when signing out without placing your order. Do you want to proceed?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDBHelper.deleteUsers(); // remove the current logged in user
                            mDBHelper.deleteAllItems(); // delete the temp cart items
                            session.setLogin(false);
                            finish();
                            Intent intent = new Intent(ProductActivity.this, LoginActivity.class);
                            startActivity(intent);
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
        } else {
            mDBHelper.deleteUsers();
            session.setLogin(false);
            finish();
            Intent intent = new Intent(ProductActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }


    /*------------Set up Progress bar visibility -----------------*/
    void showProgressView() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressView() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void getAllProducts(int page_num, String search_item, String group_code) {

        final LoginToken user = mDBHelper.getUserToken();
        // redirect to login page
        if (user == null) {
            Log.d(TAG, "getAllProducts: user is null");
            Intent intent = new Intent(ProductActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            Log.d(TAG, "getAllProducts: user " + user);
            showProgressView();

            String url = AppConfig.GET_PRODUCTS_BY_BRANCH;
            /*to create a query string
            * url + "?page=1&arnoc=1002&group_cat=1&search_value=tumbler%20dwall"
            * */
            Uri builtUri = Uri.parse(url)
                    .buildUpon()
                    .appendQueryParameter("page", String.valueOf(page_num))
                    .appendQueryParameter("arnoc",String.valueOf(user.getBranch()))
                    .appendQueryParameter("group_cat", group_code.trim())
                    .appendQueryParameter("search_value", search_item)
                    .build();

            URL temp_url = null;
            try {
                temp_url = new URL(builtUri.toString());
                url = temp_url.toString();
            } catch (MalformedURLException e) {
                Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

            Log.d(TAG, "getAllProducts: ");
//            String params = "?page=" + page_num
//                    + "&arnoc=" + String.valueOf(user.getBranch())
//                    + "&group_cat=" +group_code.trim()
//                    + "&search_value=" + search_item
//                    ;
//            url = url + params;

            Log.d(TAG, "getAllProducts: url" + url);
            JsonObjectRequest jObjreq = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hideProgressView();
                            tv_noresults.setVisibility(View.GONE);

                            try {
                                if (response.getBoolean("success") == false) {

                                    if (response.getInt("status") == 401) {
                                        /*
                                         * TODO: make a dialog that the user is not currently on duty
                                         * */


                                        session.setLogin(false);
                                        mDBHelper.deleteUsers();
                                        mDBHelper.deleteAllItems();

                                        Intent intent = new Intent(ProductActivity.this, LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                    String message = response.optString("message");
                                    Toast.makeText(ProductActivity.this, message, Toast.LENGTH_SHORT).show();
                                }else if (response.getBoolean("success") == true){
                                    if (response.getInt("status") == 200) {
                                        JSONObject data = response.getJSONObject("data");
                                        JSONArray products = data.getJSONArray("data");
                                            for (int i = 0; i < products.length(); i++) {
                                                JSONObject item = products.getJSONObject(i);

                                                long id = item.getLong("product_id");
                                                String pro_name = item.getString("product_name");
                                                String pro_imgUrl = item.getString("img_url");
                                                Double price = item.getDouble("retail_price");

                                                long arnoc = item.getLong("arnoc");
                                                String part_no = item.getString("part_no");
                                                String group_no = item.getString("group");
                                                String category = item.getString("category");
                                                char status = item.getString("status").charAt(0);
                                                Product product = new Product(id, pro_name, pro_imgUrl, price
                                                        , arnoc, part_no, group_no, category, status
                                                );
                                                mListItems.add(product);
                                            }
                                    }

                                }

                                mAdapter.notifyDataSetChanged();

                                Log.d(TAG, "onResponse: listpro : " +mListItems);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("JSON", "onResponse: " + e.getMessage());
                                Toast.makeText(ProductActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            if (VolleySingleton.prompt !=null){
                                VolleySingleton.prompt.dismiss();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleySingleton.showErrors(error, root_layout, btnRefresh);
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        String errorString = new String(response.data);
                        Toast.makeText(ProductActivity.this, errorString, Toast.LENGTH_LONG).show();
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

//            jObjreq.setRetryPolicy(new DefaultRetryPolicy(
//                    30000,
//                    5,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
            jObjreq.setRetryPolicy(
                    new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            mRequestQ.add(jObjreq);

        }
    }

    private void getAllProductGroups(){
        showProgressView();
        String url = AppConfig.GET_PRODUCT_GROUPS;
        JsonObjectRequest jObjreq =  new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideProgressView();
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

                                    Intent intent = new Intent(ProductActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                            if (response.getInt("status") == 200) {
                                productGroups.add(new ProductGroup("", "All"));
                                JSONArray data = response.getJSONArray("data");

                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject item = data.getJSONObject(i);
                                    String code = item.getString("GROUPCODE");
                                    String desc = item.getString("DESCRIPTION").trim();
//                                    if (code == "" || code.trim().isEmpty()){
//                                        desc = "ALL";
//                                        Log.d(TAG, "onResponse: desc " +desc);
//                                    }
                                    Log.d(TAG, "onResponse: getAllProductGroups " +desc);
                                    ProductGroup productGroup = new ProductGroup(code, desc);
                                    productGroups.add(productGroup);
                                }
                                groupAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON", "onResponse: " + e.getMessage());
                            Toast.makeText(ProductActivity.this,  e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressView();
                VolleySingleton.showErrors(error, ProductActivity.this);
                NetworkResponse response = error.networkResponse;

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




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ProductGroup group = (ProductGroup) spinner_procategory.getSelectedItem();
        mListItems.clear();
        mAdapter.notifyDataSetChanged();
        mGroupCode = group.getGroupNo();


        getAllProducts(1, mQuery, mGroupCode);
        mRecyclerView.removeOnScrollListener(scrollListener);

        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                showProgressView();
                getAllProducts(page + 1, mQuery, mGroupCode);
                Log.d(TAG, "onLoadMore: "+mGroupCode);
            }
        };
        mRecyclerView.addOnScrollListener(scrollListener);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    /*------------ coordinator layout computation ----------------------*/
//
//    /**
//     * RecyclerView item decoration - give equal margin around grid item
//     */
//    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
//
//        private int spanCount;
//        private int spacing;
//        private boolean includeEdge;
//
//        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
//            this.spanCount = spanCount;
//            this.spacing = spacing;
//            this.includeEdge = includeEdge;
//        }
//
//        @Override
//        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//            int position = parent.getChildAdapterPosition(view); // item position
//            int column = position % spanCount; // item column
//
//            if (includeEdge) {
//                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
//                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
//
//                if (position < spanCount) { // top edge
//                    outRect.top = spacing;
//                }
//                outRect.bottom = spacing; // item bottom
//            } else {
//                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
//                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
//                if (position >= spanCount) {
//                    outRect.top = spacing; // item top
//                }
//            }
//        }
//    }
//
//    /**
//     * Converting dp to pixel
//     */
//    private int dpToPx(int dp) {
//        Resources r = getResources();
//        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
//    }
//
//    /**
//     * Initializing collapsing toolbar
//     * Will show and hide the toolbar title on scroll
//     */
//    private void initCollapsingToolbar() {
//        final CollapsingToolbarLayout collapsingToolbar =
//                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
//        collapsingToolbar.setTitle(" ");
//        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
//        appBarLayout.setExpanded(true);
//
//        // hiding & showing the title when toolbar expanded & collapsed
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            boolean isShow = false;
//            int scrollRange = -1;
//
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
//                if (scrollRange == -1) {
//                    scrollRange = appBarLayout.getTotalScrollRange();
//                }
//                if (scrollRange + verticalOffset == 0) {
//                    collapsingToolbar.setTitle(getString(R.string.section_shop));
//                    isShow = true;
//                } else if (isShow) {
//                    collapsingToolbar.setTitle("");
//                    isShow = false;
//                }
//            }
//        });
//    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}

