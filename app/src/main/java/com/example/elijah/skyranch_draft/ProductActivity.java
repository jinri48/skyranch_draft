package com.example.elijah.skyranch_draft;


import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = ProductActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private ProductAdapter mAdapter;
    private ArrayList<Product> mListItems; // storage var for our JSON
    private RequestQueue mRequestQ; // network calls suing volley
    private DatabaseHelper mDBHelper;
    private SessionManager session;
    private String branch_id;

    private EndlessRecyclerViewScrollListener scrollListener;
    private ProgressBar progressBar;
    GridLayoutManager gridLayoutManager;

    private String mQuery =""; // query for search view
    private SearchView mSearchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_activity);

        initObj();

        Intent intent = getIntent();
        branch_id =  intent.getStringExtra("branch_id");
        if (branch_id == null){
            branch_id = "";
        }

        // Get the intent, verify the action and get the query
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "onNewIntent: " +mQuery);
        }


        getAllProducts(1,branch_id, mQuery);
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                showProgressView();
                getAllProducts(page +1 , branch_id, mQuery);
                Log.d(TAG, "onLoadMore: query " +mQuery);
                Log.d(TAG, "onLoadMore: list size " +mListItems.size());
                Log.d(TAG, "onLoadMore: page - " +page);
                Log.d(TAG, "onLoadMore: totalItems: " +totalItemsCount);
                Log.d(TAG, "onLoadMore: items " +mListItems.get(mListItems.size()-1));

            }
        };

        mRecyclerView.addOnScrollListener(scrollListener);

    }


    private void initObj(){
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Enchanted Kingdom");
        setSupportActionBar(myToolbar);

        mRequestQ = Volley.newRequestQueue(this);
        mDBHelper = DatabaseHelper.newInstance(this);
        session = new SessionManager(this);

        progressBar = findViewById(R.id.progressBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_products);
        mListItems = new ArrayList<>();


        // make your container displayed in a linear way
        // LinearLayoutManager llm = new LinearLayoutManager(this);
        // llm.setOrientation(LinearLayoutManager.VERTICAL);
        // mRecyclerView.setLayoutManager(llm);
        gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mAdapter = new ProductAdapter(ProductActivity.this, mListItems);
        mRecyclerView.setAdapter(mAdapter);
//


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shop_menu, menu);

        MenuItem search = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) search.getActionView();
        setupSearchView(search);
        if (mQuery != null || mQuery == "") {
            Log.d(TAG, "onCreateOptionsMenu: mquery is not null,  value is" +mQuery);
            mSearchView.setQuery(mQuery, false);
            mSearchView.setIconifiedByDefault(false);
        }

//        search.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
//
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                // Do whatever you need
//                Log.d(TAG, "onMenuItemActionExpand: ");
//                return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                Log.d(TAG, "onMenuItemActionCollapse: ");
//                if (mQuery == null){
//                    Log.d(TAG, "onMenuItemActionCollapse: null" );
////                    Intent intent = new Intent(ProductActivity.this, ProductActivity.class);
////                    intent.setAction(Intent.ACTION_SEARCH);
////                    intent.putExtra(SearchManager.QUERY, "");
////                    Log.d(TAG, "onQueryTextSubmit: gonna start activity with the query none" );
////                    startActivity(intent);
//                }
//
//                // Do whatever you need
//                return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
//            }
//        });




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
                mDBHelper.deleteUsers(); // remove the current logged in user
                mDBHelper.deleteAllItems(); // delete the temp cart items
                session.setLogin(false);
                finish();
                intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }

    }


    void showProgressView() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressView() {
        progressBar.setVisibility(View.INVISIBLE);
    }

    /*-----------------------------*/
    private void setupSearchView(MenuItem searchItem){
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setFocusable(false);
        mSearchView.setFocusableInTouchMode(false);

    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        Intent intent = new Intent(ProductActivity.this, ProductActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY, s);
        Log.d(TAG, "onQueryTextSubmit: gonna start activity with the query " +s);
        startActivity(intent);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        Log.d(TAG, "onQueryTextChange: " +s);
        return false;
    }

    /*--------- HANDLE REQUESTS API TO TEXT VIEW ------------*/
//    private void parseJSON() {

    ////        String url = "https://pixabay.com/api/?key=5303976-fd6581ad4ac165d1b75cc15b3&q=yellow+flowers&image_type=photo";
//        String url = AppConfig.GET_PRODUCTS_BY_BRANCH + "?arnoc=" +branch_id;
//        JsonObjectRequest jObjreq = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONObject data = response.getJSONObject("data");
//                            JSONArray products = data.getJSONArray("data");
//
//                            for (int i = 0; i < products.length(); i++) {
//                                JSONObject item = products.getJSONObject(i);
//
//                                long id = item.getLong("product_id");
//                                String pro_name = item.getString("product_name");
//                                String pro_imgUrl = item.getString("img_url");
//                                Double price = item.getDouble("retail_price");
//
//                                long arnoc = item.getLong("arnoc");
//                                String part_no = item.getString("part_no");
//                                String group_no = item.getString("group");
//                                String category = item.getString("category");
//                                char status = item.getString("status").charAt(0);
//                                Product product = new Product(id, pro_name, pro_imgUrl, price
//                                            ,arnoc, part_no, group_no, category, status
//                              );
//                                mListItems.add(product);
//                            }
//
//                            mAdapter.notifyDataSetChanged();
////                            Log.d(TAG, "onResponse: " +mListItems.toString());
//
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Log.d("JSON", "onResponse: " + e.getMessage());
//                        }
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//                NetworkResponse response = error.networkResponse;
//                String errorMsg = error.getMessage();
//                if(response != null && response.data != null){
//                    String errorString = new String(response.data);
//                    Log.i(TAG, "GET PRODUCTS" +errorString);
//                    errorMsg = errorString;
//                }
//                Log.d(TAG, "onErrorResponse: " + error.getMessage());
//
//            }
//        });
//        mRequestQ.add(jObjreq);
//    }
    private void getAllProducts(int page_num, String branch_id, String search_item) {
        showProgressView();
        String url = AppConfig.GET_PRODUCTS_BY_BRANCH;
        String params = "?page=" + page_num + "&arnoc=" + branch_id + "&search_value=" +search_item;
        url = url+params;

        Log.d(TAG, "getAllProducts: url" +url);

        JsonObjectRequest jObjreq = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if(response.getInt("status") == 200) {
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
                            hideProgressView();
                            mAdapter.notifyDataSetChanged();
//                            Log.d(TAG, "onResponse: " +mListItems.toString());

//                            Log.d(TAG, "onResponse: listpro : " +mListItems);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("JSON", "onResponse: " + e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                NetworkResponse response = error.networkResponse;
                String errorMsg = error.getMessage();
                if(response != null && response.data != null){
                    String errorString = new String(response.data);
                    Log.i(TAG, "GET PRODUCTS" +errorString);
                    errorMsg = errorString;
                }
                Log.d(TAG, "onErrorResponse: " + error.getMessage());

            }
        });
        mRequestQ.add(jObjreq);
    }

    /*------------ coordinator layout computation ----------------------*/

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    /**
     * Initializing collapsing toolbar
     * Will show and hide the toolbar title on scroll
     */
    private void initCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(getString(R.string.app_name));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + mListItems);
        if (mQuery != null){
            mListItems.clear();
        }
    }
}
