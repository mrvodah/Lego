package com.example.lego.network;

import static easy.language.app.network.RetrofitClient.GOOGLE_TRANS;

/**
 * Created by VietVan on 11/28/2018.
 */

public class NetworkModule {
    public NetworkModule() {
    }

    public static ApiInterface getService() {
        return RetrofitClient.getClient().create(ApiInterface.class);
    }

    public static ApiInterfaceV3 getServiceV3() {
        return RetrofitClient.getClient().create(ApiInterfaceV3.class);
    }

    public static ApiInterface getYoutubeService(){
        return RetrofitClient.getYoutube().create(ApiInterface.class);
    }

    public static ApiInterface getGoogleService(){
        return RetrofitClient.getGoogle().create(ApiInterface.class);
    }


    public static ApiTrans getServiceTransGoogle() {
        return RetrofitClient.getTrans(GOOGLE_TRANS).create(ApiTrans.class);
    }

    public static ApiTrans getServiceTransTraTu() {
        return RetrofitClient.getTrans(2).create(ApiTrans.class);
    }
}
