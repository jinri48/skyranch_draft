package com.example.elijah.skyranch_draft;

public class AppConfig {



    public static String BASE_URL_API = "http://172.16.12.112:8000/api";

    //    public static String BASE_URL_API = "http://192.168.88.254:8000/api";
    //     public static String BASE_URL_API = "http://192.168.223.1:8000/api";


    public static String LOGIN;
    public static String GET_ALL_PRODUCTS;
    public static String GET_PRODUCT_BY_ID;
    public static String ADD_CART_ITEMS;
    public static String GET_PRODUCTS_BY_BRANCH;

    public static String GET_CUSTOMERS;
    public static String ADD_CUSTOMER;
    public static String PHONE_EXISTS;
    public static String GET_PRODUCT_GROUPS;

    public static String GET_SALES_HISTORY;
    public static String GET_SALES_TOTAL;

    public static String GET_ORDER_DETAILS;
    public static String APPEND_DETAIL;
    public static String GET_IS_ONDUTY;

    public static void init(){
        LOGIN = BASE_URL_API + "/login";
        GET_ALL_PRODUCTS = BASE_URL_API + "/siteparts";
        GET_PRODUCT_BY_ID = BASE_URL_API + "/getProductById";
        ADD_CART_ITEMS = BASE_URL_API + "/addOrder";
        GET_PRODUCTS_BY_BRANCH = BASE_URL_API + "/sitepartsBranch";
        GET_CUSTOMERS = BASE_URL_API + "/getCustomer";
        ADD_CUSTOMER = BASE_URL_API + "/newCustomer";
        PHONE_EXISTS = BASE_URL_API + "/phoneExist";
        GET_PRODUCT_GROUPS = BASE_URL_API + "/getProductGroups";
        GET_SALES_HISTORY = BASE_URL_API + "/getSalesHistory";
        GET_SALES_TOTAL = BASE_URL_API + "/getSalesTotal";
        GET_ORDER_DETAILS = BASE_URL_API + "/order-slip/header/";
        APPEND_DETAIL ="/details";
        GET_IS_ONDUTY = BASE_URL_API + "/onduty";
    }




}
