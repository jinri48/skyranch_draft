package com.example.elijah.skyranch_draft;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

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

    //shopping cart table
    private static final String TABLE_CART = "cart";
    private static final String KEY_CART_ID = "_cart_id";
    private static final String KEY_CART_HEADER_ID = "_header_id" ;
    private static final String KEY_CART_PRO_ID = "pro_id";
    private static final String KEY_CART_QTY = "qty";
    private static final String KEY_CART_PRICE = "sub_total";

    private static final String KEY_CART_PRO_NAME = "pro_name";
    private static final String KEY_CART_PRO_IMG_URL = "pro_img";
    private static final String KEY_CART_PRO_PART_NO = "pro_part_no";
    private static final String KEY_CART_PRO_GROUP_NO = "pro_group_no";
    private static final String KEY_CART_PRO_RETAIL_PRICE = "pro_retail_price";
    private static final String KEY_CART_PRO_STATUS = "pro_status";


    // instance of the database(singleton)
    private static DatabaseHelper mDBInstance = null;
    private Context mContext;


    public static DatabaseHelper newInstance(Context context){
        if (mDBInstance == null){
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
                + KEY_NAME + " TEXT"
                +")";

        String CREATE_TABLE_CART = "CREATE TABLE "+TABLE_CART + "("
                +KEY_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                +KEY_CART_HEADER_ID+ " INTEGER, "
                +KEY_CART_PRO_ID + " INTEGER, "
                +KEY_CART_PRO_NAME + " TEXT , "
                +KEY_CART_PRO_IMG_URL+ " TEXT , "
                +KEY_CART_PRO_RETAIL_PRICE+ " REAL , "
                +KEY_CART_PRO_PART_NO + " TEXT , "
                +KEY_CART_PRO_GROUP_NO+ " TEXT , "
                +KEY_CART_PRO_STATUS+ " TEXT , "
                +KEY_CART_QTY + " INTEGER, "
                +KEY_CART_PRICE + " REAL"
                +")";
        try {
            db.execSQL(CREATE_TABLE_USERTOKEN);
            db.execSQL(CREATE_TABLE_CART);

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

    public List<LoginToken> getAllUserTokens(){
        List<LoginToken> userList = new ArrayList<>();
        SQLiteDatabase db = mDBInstance.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_USERTOKENS;

        if (db !=null){
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    LoginToken userToken =  new LoginToken(
                            cursor.getString(0),
                            cursor.getString(1),
                            cursor.getString(2)
                    );

                    userList.add(userToken);
                    cursor.moveToNext();
                }
            }
            cursor.close();
        }
        db.close();
//        Toast.makeText(mContext, userList.toString() /*Arrays.toString(userList.toArray())*/, Toast.LENGTH_LONG).show();

        return  userList;
    }

    /*
    * adds a token in the database
    * */

    public void addUserToken(LoginToken userToken){
        long result= -1; // id of the row that was inserted
        SQLiteDatabase db = mDBInstance.getWritableDatabase();

        if (db != null){
            ContentValues values = new ContentValues();

            if (userToken.getId() != null){
                Log.d(TAG, "addUserToken: "+userToken.getId());
                values.put(KEY_ID, Long.parseLong(userToken.getId()));
            }
            values.put(KEY_TOKEN, userToken.getToken());
            values.put(KEY_NAME, userToken.getName());
            try {
                result = db.insertOrThrow(TABLE_USERTOKENS, null, values);
                Toast.makeText(mContext, "Added Record " +result, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "addUserToken: successfully added record id " +result);
            } catch (SQLException e) {
                Log.d(TAG, "addUserToken: error in adding " +e.getMessage());
                e.printStackTrace();
            } finally {
                db.close();
            }
        }

    }


    /*
    * gets a single record of token
    * */
    public LoginToken getUserToken(String token) {
        SQLiteDatabase db = mDBInstance.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USERTOKENS
                + " WHERE " + KEY_TOKEN + " = '" + token + "'"
                , null);

        LoginToken userToken = null;
        if (cursor.moveToFirst()){
            userToken = new LoginToken(
                    cursor.getString(0),
                    cursor.getString(1),
                    cursor.getString(2)
            );
        }
        cursor.close();
        Toast.makeText(mContext, userToken.toString(), Toast.LENGTH_LONG).show();
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
    public long addToCart(OrderItem orderItem){
        long result = -1;
        SQLiteDatabase db = mDBInstance.getWritableDatabase();

        if (db != null){
            ContentValues values = new ContentValues();
            Log.d(TAG, "addToCart: orderitemVal" +orderItem);

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
                Log.d(TAG, "addToCart: "+e.getMessage());
            }finally {
                db.close();
            }
        }

        Toast.makeText(mContext, "inserted row: "+result, Toast.LENGTH_SHORT).show();
        return result;
    }

    public int deleteItem(OrderItem orderItem) {
        SQLiteDatabase db = mDBInstance.getWritableDatabase();
        String whereClause = KEY_CART_ID +"=?";
        String whereArgs[] = {String.valueOf(orderItem.getId())};
        int numberOFEntriesDeleted = db.delete(TABLE_CART, whereClause, whereArgs);
        Log.d(TAG, "Deleted an item");
        return numberOFEntriesDeleted;
    }

    static List<OrderItem> itemsInCart = new ArrayList<>();
//    public List<OrderItem> getAllCartItems(){
//        List<OrderItem> itemsInCart = new ArrayList<>();
//        SQLiteDatabase db = mDBInstance.getReadableDatabase();
//        String selectQuery = "SELECT * FROM " + TABLE_CART;
//
//        if (db !=null){
//            final Cursor cursor = db.rawQuery(selectQuery, null);
//            if (cursor.moveToFirst()) {
//                while (!cursor.isAfterLast()) {
//                    OrderItem order = new OrderItem();
//                    order.setId(cursor.getInt(0));
//                    Product product = new Product();
////                     call the api to get the product details
//                    AppApi api = new AppApi(mContext);
//                    api.getProductItem(new AppApi.VolleyCallback() {
//                        @Override
//                        public void onSuccess(Product productItem) {
//                            product = productItem;
//                            Log.d(TAG, "onSuccess: api " +productItem);
//                        }
//                    }, cursor.getLong(1));
//
//                    order.setProduct(product);
//                    order.setQty(cursor.getInt(2));
//                    order.setAmount(cursor.getDouble(3));
//                    itemsInCart.add(order);
//                    Log.d(TAG, "onSuccess: order " +order);
//                    Log.d(TAG, "getAllCartItems: outside" +order);
//                    cursor.moveToNext();
//                }
//            }
//            cursor.close();
//        }
//        db.close();
//        return itemsInCart;
//    }

    static List<Product> productItems = new ArrayList<>();

    public ArrayList<OrderItem> getCart(){
        ArrayList<OrderItem> itemsInCart = new ArrayList<>();
        SQLiteDatabase db = mDBInstance.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_CART;

        if (db !=null){
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

}
