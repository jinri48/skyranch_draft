package com.example.elijah.skyranch_draft;

public class AppConfig {

    public static final String BASE_URL_API = "http://172.16.12.112:8000/api";

    //    routes
    public static final String LOGIN = BASE_URL_API + "/login";
    public static final String GET_ALL_PRODUCTS = BASE_URL_API + "/siteparts";
    public static final String GET_PRODUCT_BY_ID = BASE_URL_API + "/getProductById";
    public static final String ADD_CART_ITEMS = BASE_URL_API + "/addOrder";
    public static final String GET_PRODUCTS_BY_BRANCH = BASE_URL_API + "/sitepartsBranch";

    public static final String GET_CUSTOMERS = BASE_URL_API + "/getCustomer";
    public static final String ADD_CUSTOMER = BASE_URL_API + "/newCustomer";

}
