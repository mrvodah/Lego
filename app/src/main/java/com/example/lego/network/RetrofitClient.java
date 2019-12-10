package com.example.lego.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by VietVan on 11/28/2018.
 */

public class RetrofitClient {

    private static final String BASE_URL = "https://api.easylanguage.vn/v3/";

    private static Retrofit retrofit = null;

    public static int GOOGLE_TRANS = 1;

    public static Retrofit getClient() {
        if (retrofit == null) {

            OkHttpClient client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new StethoInterceptor())
//                    .addInterceptor(new ChuckInterceptor(context))
                    .build();

            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
//                    .baseUrl("http://mobile-uat.mn.com.vn/")
                    .baseUrl(BASE_URL)
                    .client(client)
                    .build();
        }

        return retrofit;
    }

}
