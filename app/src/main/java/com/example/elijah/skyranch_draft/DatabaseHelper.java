package com.example.elijah.skyranch_draft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = DatabaseHelper.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Login access table name
    private static final String TABLE_USERTOKENS = "user_token";
    private static final String KEY_ID = "_id";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_NAME = "cce_name";
    private static final String KEY_BRANCH = "branch_id";

    //shopping cart table
    private static final String TABLE_CART = "cart";
    private static final String KEY_CART_ID = "_cart_id";
    private static final String KEY_CART_HEADER_ID = "_header_id";
    private static final String KEY_CART_PRO_ID = "pro_id";
    private static final String KEY_CART_QTY = "qty";
    private static final String KEY_CART_PRICE = "sub_total";

    private static final String KEY_CART_PRO_NAME = "pro_name";
    private static final String KEY_CART_PRO_IMG_URL = "pro_img";
    private static final String KEY_CART_PRO_PART_NO = "pro_part_no";
    private static final String KEY_CART_PRO_GROUP_NO = "pro_group_no";
    private static final String KEY_CART_PRO_RETAIL_PRICE = "pro_retail_price";
    private static final String KEY_CART_PRO_STATUS = "pro_status";


    //connection table
    private static final String TABLE_API = "api_connection";
    private static final String KEY_URL_ID = "_id";
    private static final String KEY_URL_CONNECT = "url";

    // instance of the database(singleton)
    private static DatabaseHelper mDBInstance = null;
    private Context mContext;


    public static DatabaseHelper newInstance(Context context) {
        if (mDBInstance == null) {
            mDBInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return mDBInstance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        /*create statements*/
        String CREATE_TABLE_USERTOKEN = "CREATE TABLE " + TABLE_USERTOKENS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TOKEN + " TEXT, "
                + KEY_NAME + " TEXT, "
                + KEY_BRANCH + " INTEGER"
                + ")";

        String CREATE_TABLE_CART = "CREATE TABLE " + TABLE_CART + "("
                + KEY_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_CART_HEADER_ID + " INTEGER, "
                + KEY_CART_PRO_ID + " INTEGER, "
                + KEY_CART_PRO_NAME + " TEXT , "
                + KEY_CART_PRO_IMG_URL + " TEXT , "
                + KEY_CART_PRO_RETAIL_PRICE + " REAL , "
                + KEY_CART_PRO_PART_NO + " TEXT , "
                + KEY_CART_PRO_GROUP_NO + " TEXT , "
                + KEY_CART_PRO_STATUS + " TEXT , "
                + KEY_CART_QTY + " INTEGER, "
                + KEY_CART_PRICE + " REAL"
                + ")";

        String CREATE_TABLE_API_CONN = "CREATE TABLE " + TABLE_API + "("
                + KEY_URL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_URL_CONNECT+ " TEXT "
                + ")";
        try {
            db.execSQL(CREATE_TABLE_USERTOKEN);
            db.execSQL(CREATE_TABLE_CART);
            db.execSQL(CREATE_TABLE_API_CONN);

            Log.d(TAG, "onCreate: successfully ");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.d(TAG, "onCreate: Error in create Database  " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if exist
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERTOKENS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);

        // Create tables again
        onCreate(db);
    }

//    ---------USER TOKENS----------

    public List<LoginToken> getAllUserTokens() {
        List<LoginToken> userList = new ArrayList<>();
        SQLiteDatabase db = mDBInstance.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USERTOKENS;

        if (db != null) {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    LoginToken userToken = new LoginToken(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2),
                            cursor.getInt(cursor.getColumnIndex(KEY_BRANCH))
                    );

                    userList.add(userToken);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        db.close();
//        Toast.makeText(mContext, userList.toString() /*Arrays.toString(userList.toArray())*/, Toast.LENGTH_LONG).show();

        return userList;
    }

    /*
     * adds a token in the database
     * */

    public void addUserToken(LoginToken userToken) {
        long result = -1; // id of the row that was inserted
        SQLiteDatabase db = mDBInstance.getWritableDatabase();

        if (db != null) {
            ContentValues values = new ContentValues();

            if (userToken.getId() != null) {
                Log.d(TAG, "addUserToken: " + userToken.getId());
                values.put(KEY_ID, Long.parseLong(userToken.getId()));
            }
            values.put(KEY_TOKEN, userToken.getToken());
            values.put(KEY_NAME, userToken.getName());
            values.put(KEY_BRANCH, userToken.getBranch());
            try {
                result = db.insertOrThrow(TABLE_USERTOKENS, null, values);
                Log.d(TAG, "addUserToken: successfully added record id " + result);
            } catch (SQLException e) {
                Log.d(TAG, "addUserToken: error in adding " + e.getMessage());

                e.printStackTrace();
            } finally {
                db.close();
            }
        }

    }


    /*
     * gets a single record user by token
     * */
    public LoginToken getUserToken(String token) {
        SQLiteDatabase db = mDBInstance.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERTOKENS
                        + " WHERE " + KEY_TOKEN + " = '" + token + "'"
                , null);

        LoginToken userToken = null;
        if (cursor.moveToFirst()) {
            userToken = new LoginToken(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(cursor.getColumnIndex(KEY_BRANCH))
            );
        }
        cursor.close();
        Toast.makeText(mContext, userToken.toString(), Toast.LENGTH_LONG).show();
        return userToken;
    }

    public LoginToken getUserToken() {
        SQLiteDatabase db = mDBInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERTOKENS
                , null);

        LoginToken userToken = null;
        if (cursor.moveToFirst()) {
            userToken = new LoginToken(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getInt(cursor.getColumnIndex(KEY_BRANCH))
            );
        }
        cursor.close();
//        Toast.makeText(mContext, userToken.toString(), Toast.LENGTH_SHORT).show();
        return userToken;
    }


    public void deleteUsers() {
        SQLiteDatabase db = mDBInstance.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USERTOKENS, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }


//    -------CART-------------

    public long addToCart(OrderItem orderItem) {
        long product_id = orderItem.getProduct().getId();
        long result = -1;

        if (itemExistsQty(product_id) <= 0) {
            SQLiteDatabase db = mDBInstance.getWritableDatabase();
            if (db != null) {
                ContentValues values = new ContentValues();
                values.put(KEY_CART_PRO_ID, orderItem.getProduct().getId());
                values.put(KEY_CART_PRO_NAME, orderItem.getProduct().getName());
                values.put(KEY_CART_PRO_GROUP_NO, orderItem.getProduct().getGroup_no());
                values.put(KEY_CART_PRO_PART_NO, orderItem.getProduct().getPart_no());
                values.put(KEY_CART_PRO_RETAIL_PRICE, orderItem.getProduct().getO_price());
                values.put(KEY_CART_PRO_STATUS, String.valueOf(orderItem.getProduct().getStatus()));
                values.put(KEY_CART_PRO_IMG_URL, orderItem.getProduct().getImgUrl());
                values.put(KEY_CART_QTY, orderItem.getQty());
                values.put(KEY_CART_PRICE, orderItem.getAmount());
                try {
                    result = db.insertOrThrow(TABLE_CART, null, values);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.d(TAG, "addToCart: " + e.getMessage());
                } finally {
                    db.close();
                }
            }
        } else {
            // exists
            long qty = itemExistsQty(product_id);
            result = updateCartItemByPro(orderItem, qty);
        }

        Toast.makeText(mContext, "Successfully added an item to cart", Toast.LENGTH_SHORT).show();
        return result;
    }

    public long addToCrt(OrderItem orderItem) {
        long product_id = orderItem.getProduct().getId();
        long result = -1;

        long cart_qty = itemExistsQty(product_id);
        long or_qty = orderItem.getQty() + cart_qty;



        if (or_qty > 100 && or_qty < 0){ // not existing but exceeding

            SQLiteDatabase db = mDBInstance.getWritableDatabase();
            if (db != null) {
                ContentValues values = new ContentValues();
                values.put(KEY_CART_PRO_ID, orderItem.getProduct().getId());
                values.put(KEY_CART_PRO_NAME, orderItem.getProduct().getName());
                values.put(KEY_CART_PRO_GROUP_NO, orderItem.getProduct().getGroup_no());
                values.put(KEY_CART_PRO_PART_NO, orderItem.getProduct().getPart_no());
                values.put(KEY_CART_PRO_RETAIL_PRICE, orderItem.getProduct().getO_price());
                values.put(KEY_CART_PRO_STATUS, String.valueOf(orderItem.getProduct().getStatus()));
                values.put(KEY_CART_PRO_IMG_URL, orderItem.getProduct().getImgUrl());
                values.put(KEY_CART_QTY, orderItem.getQty());
                values.put(KEY_CART_PRICE, orderItem.getAmount());
                try {
                    result = db.insertOrThrow(TABLE_CART, null, values);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    db.close();
                }
            }
        }


        if (or_qty < 99 && or_qty > 0){
            result = updateCartItemByPro(orderItem, cart_qty);
        } else{
            SQLiteDatabase db = mDBInstance.getWritableDatabase();
            if (db != null) {
                ContentValues values = new ContentValues();
                values.put(KEY_CART_PRO_ID, orderItem.getProduct().getId());
                values.put(KEY_CART_PRO_NAME, orderItem.getProduct().getName());
                values.put(KEY_CART_PRO_GROUP_NO, orderItem.getProduct().getGroup_no());
                values.put(KEY_CART_PRO_PART_NO, orderItem.getProduct().getPart_no());
                values.put(KEY_CART_PRO_RETAIL_PRICE, orderItem.getProduct().getO_price());
                values.put(KEY_CART_PRO_STATUS, String.valueOf(orderItem.getProduct().getStatus()));
                values.put(KEY_CART_PRO_IMG_URL, orderItem.getProduct().getImgUrl());
                values.put(KEY_CART_QTY, orderItem.getQty());
                values.put(KEY_CART_PRICE, orderItem.getAmount());
                try {
                    result = db.insertOrThrow(TABLE_CART, null, values);
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    db.close();
                }
            }
        }
        if (result > 0){
            Toast.makeText(mContext, "Successfully added an item to cart", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

//    public boolean isCartItemExists(long pro_id) {
//        boolean product_exists = false;
//        SQLiteDatabase db = mDBInstance.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CART
//                        + " WHERE " + KEY_CART_PRO_ID + " = ?"
//                , new String[]{String.valueOf(pro_id)});
//
//        if (cursor.moveToFirst()) {
//            product_exists = true;
//        }
//        Log.d(TAG, "isCartItemExists: " + product_exists);
//        db.close();
//        return product_exists;
//    }

    public long itemExistsQty(long pro_id) {
        long qty = 0;
        SQLiteDatabase db = mDBInstance.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CART
                        + " WHERE " + KEY_CART_PRO_ID + " = ?"
                , new String[]{String.valueOf(pro_id)});

        if (cursor.moveToFirst()) {
           qty = cursor.getLong(cursor.getColumnIndex(KEY_CART_QTY));
        }

        db.close();
        return qty;
    }

    /*
     * Delete an item in the cart
     * */
    public int deleteItem(OrderItem orderItem) {
        SQLiteDatabase db = mDBInstance.getWritableDatabase();
        String whereClause = KEY_CART_ID + "=?";
        String whereArgs[] = {String.valueOf(orderItem.getId())};
        int numberOFEntriesDeleted = db.delete(TABLE_CART, whereClause, whereArgs);
        return numberOFEntriesDeleted;
    }

    /*
     *  Delete All Rows in the cart
     * */


    public int deleteAllItems() {
        SQLiteDatabase db = mDBInstance.getWritableDatabase();

        int result = db.delete(TABLE_CART, null, null);
        db.close();
        Log.d(TAG, "deleteAllItems result " + result);
        return result;
    }

    /*
     *
     *
     * */
    public int updateCartItem(OrderItem orderItem) {
        int result = -1;
        SQLiteDatabase db = mDBInstance.getWritableDatabase();

        if (db != null) {
            ContentValues values = new ContentValues();
            Log.d(TAG, "UpdateCart: orderitemVal: " + orderItem);
            Log.d(TAG, "updateCartItem: id " + orderItem.getId());

            values.put(KEY_CART_PRO_ID, orderItem.getProduct().getId());
            values.put(KEY_CART_PRO_NAME, orderItem.getProduct().getName());
            values.put(KEY_CART_PRO_GROUP_NO, orderItem.getProduct().getGroup_no());
            values.put(KEY_CART_PRO_PART_NO, orderItem.getProduct().getPart_no());
            values.put(KEY_CART_PRO_RETAIL_PRICE, orderItem.getProduct().getO_price());
            values.put(KEY_CART_PRO_STATUS, String.valueOf(orderItem.getProduct().getStatus()));
            values.put(KEY_CART_PRO_IMG_URL, orderItem.getProduct().getImgUrl());
            values.put(KEY_CART_QTY, orderItem.getQty());
            values.put(KEY_CART_PRICE, orderItem.getAmount());

            try {
                result = db.update(TABLE_CART, values, KEY_CART_ID + " = ?",
                        new String[]{String.valueOf(orderItem.getId())});
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                db.close();
            }
        }

        return result;
    }

    public int updateCartItemQty(OrderItem orderItem) {
        int result = -1;
        SQLiteDatabase db = mDBInstance.getWritableDatabase();

        if (db != null) {
            ContentValues values = new ContentValues();
            Log.d(TAG, "UpdateCart: orderitemVal: " + orderItem);
            Log.d(TAG, "updateCartItem: id " + orderItem.getId());
            values.put(KEY_CART_QTY, orderItem.getQty());
            values.put(KEY_CART_PRICE, orderItem.getAmount());
            try {
                result = db.update(TABLE_CART, values, KEY_CART_ID + " = ?",
                        new String[]{String.valueOf(orderItem.getId())});
            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                db.close();
            }
        }

        return result;
    }

    public int updateCartItemByPro(OrderItem orderItem, long last_qty) {
        int result = -1;
        SQLiteDatabase db = mDBInstance.getWritableDatabase();

        if (db != null) {
            ContentValues values = new ContentValues();
            long qty = orderItem.getQty() + last_qty;
            double price = orderItem.getProduct().getO_price() * qty;
            values.put(KEY_CART_QTY, qty);
            values.put(KEY_CART_PRICE, price);
            try {
                result = db.update(TABLE_CART, values, KEY_CART_PRO_ID + " = ?",
                        new String[]{String.valueOf(orderItem.getProduct().getId())});
            } catch (SQLException e) {
                e.printStackTrace();
                Log.d(TAG, "addToCart: " + e.getMessage());
            } finally {
                db.close();
            }
        }

        return result;
    }


    public ArrayList<OrderItem> getCart() {
        ArrayList<OrderItem> itemsInCart = new ArrayList<>();
        SQLiteDatabase db = mDBInstance.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_CART;

        if (db != null) {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    OrderItem order = new OrderItem();
                    // set order item properties
                    order.setId(cursor.getInt(cursor.getColumnIndex(KEY_CART_ID)));
                    order.setQty(cursor.getInt(cursor.getColumnIndex(KEY_CART_QTY)));
                    order.setAmount(cursor.getDouble(cursor.getColumnIndex(KEY_CART_PRICE)));

                    // set product properties
                    Product product = new Product();
                    product.setId(cursor.getLong(cursor.getColumnIndex(KEY_CART_PRO_ID)));
                    product.setName(cursor.getString(cursor.getColumnIndex(KEY_CART_PRO_NAME)));
                    product.setImgUrl(cursor.getString(cursor.getColumnIndex(KEY_CART_PRO_IMG_URL)));
                    product.setGroup_no(cursor.getString(cursor.getColumnIndex(KEY_CART_PRO_GROUP_NO)));
                    product.setPart_no(cursor.getString(cursor.getColumnIndex(KEY_CART_PRO_PART_NO)));
                    product.setO_price(cursor.getDouble(cursor.getColumnIndex(KEY_CART_PRO_RETAIL_PRICE)));
                    product.setStatus(cursor.getString(cursor.getColumnIndex(KEY_CART_PRO_STATUS)).charAt(0));
                    product.setImgUrl(cursor.getString(cursor.getColumnIndex(KEY_CART_PRO_IMG_URL)));
                    order.setProduct(product);
                    itemsInCart.add(order);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        db.close();
        return itemsInCart;
    }

    /*
       Returns the number of items inside the cart
     */
    public long getCartCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long resultCount = DatabaseUtils.queryNumEntries(db, TABLE_CART);
        db.close();
        return resultCount;
    }


    /*
    static List<Product> productItems = new ArrayList<>();
    static List<OrderItem> itemsInCart = new ArrayList<>();
    public static Product productItem = null;
    public List<OrderItem> getCart_samp(){
        List<OrderItem> itemsInCart = new ArrayList<>();
        SQLiteDatabase db = mDBInstance.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_CART;

        if (db !=null){
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    AppApi api = new AppApi(mContext);
                    api.getProductItem(new AppApi.VolleyCallback() {
                        @Override
                        public void onSuccess(Product product) {
                            productItem = product;
                            Log.d(TAG, "onSuccess callback " +productItem);
                        }
                    }, cursor.getLong(1));
                    OrderItem order = new OrderItem();
                    order.setId(cursor.getInt(0));
                    order.setQty(cursor.getInt(2));
                    order.setAmount(cursor.getDouble(3));
                    order.setProduct(productItem);
                    itemsInCart.add(order);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        db.close();
        return itemsInCart;

    }
    public List<OrderItem> cart_samp(){
        List<OrderItem> orders = getCart();
        final CountDownLatch signal = new CountDownLatch(orders.size());
        signal.countDown();

        AppApi api = new AppApi(mContext);
        for (int i = 0; i < orders.size(); i++) {
            api.getProductItem(new AppApi.VolleyCallback() {
                @Override
                public void onSuccess(Product product) {
                    productItems.add(product);
                }
            }, orders.get(i).getProduct().getId());
        }


        try {
            signal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "cart_samp: after signal: " +productItems);
        for (int i = 0; i < orders.size(); i++) {
            orders.get(i).setProduct(productItems.get(i));
            Log.d(TAG, "cart_samp: 2nd loop " + orders);
        }

        Log.d(TAG, "cart_samp: before return " +orders);
        return orders;
    }
    */

    /*
    * API CONNECTION SETTING
    * */

    public long createConnection(String api_url){
        long result = -1; // id of the row that was inserted
        SQLiteDatabase db = mDBInstance.getWritableDatabase();

        if (db != null) {
            ContentValues values = new ContentValues();

            if (api_url.trim() != null || !api_url.isEmpty()) {
                values.put(KEY_URL_CONNECT, api_url);
                try {
                    result = db.insertOrThrow(TABLE_API, null, values);
                    Log.d(TAG, "createConnection: " +result);

                } catch (SQLException e) {
                    Log.d(TAG, "createConnection: error in adding " + e.getMessage());
                    Toast.makeText(mContext, "adding api url connection failed: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } finally {
                    db.close();
                }
            }
        }

        return result;
    }

    public int deleteConnection() {
        SQLiteDatabase db = mDBInstance.getWritableDatabase();
        int result = db.delete(TABLE_API, null, null);
        db.close();
        Log.d(TAG, "delete api Connection " + result);
        return result;
    }

    /*public int updateConnection(String api_url){
        int result = -1;
        SQLiteDatabase db = mDBInstance.getWritableDatabase();

        if (db != null) {
            ContentValues values = new ContentValues();
            values.put(KEY_URL_CONNECT, api_url);

            if (getApiConnection()[0] !=null){
                try {
                    result = db.update(TABLE_CART, values, KEY_CART_ID + " = ?",
                            new String[]{getApiConnection()[0]});
                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "updating api url connection failed: " +e.getMessage(), Toast.LENGTH_SHORT).show();
                } finally {
                    db.close();
                }
            }

        }

        return result;
    }
    */

    public String[] getApiConnection(){
        SQLiteDatabase db = mDBInstance.getReadableDatabase();
        String apiProp[] = new String[2];
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_API
                , null);
        if (cursor.moveToFirst()) {
            /*apiProp[0] = String.valueOf(cursor.getInt(cursor.getColumnIndex(KEY_URL_ID)));*/
            apiProp[1] = cursor.getString(cursor.getColumnIndex(KEY_URL_CONNECT));

        }
        cursor.close();
        return apiProp;
    }
}
