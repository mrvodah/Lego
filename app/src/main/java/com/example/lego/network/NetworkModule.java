package com.example.lego.network;

/**
 * Created by VietVan on 11/28/2018.
 */

public class NetworkModule {
    public NetworkModule() {
    }

    public static ApiInterface getService() {
        return RetrofitClient.getClient().create(ApiInterface.class);
    }
}
