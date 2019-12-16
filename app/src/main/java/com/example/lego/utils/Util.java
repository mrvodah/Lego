package com.example.lego.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.lego.models.Request;
import com.example.lego.models.User;

import java.text.SimpleDateFormat;

/**
 * Created by VietVan on 29/05/2018.
 */

public class Util {
    public static User currentUser;
    public static Request currentRequest;

    public static String DELETE = "DELETE";
    public static String USER_KEY = "USER";
    public static String PW_KEY = "PASSWORD";

    public static String UPDATE = "UPDATE";

    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public static String convertCodeToStatus(String code){
        if(code.equals("0"))
            return "Placed";
        else if(code.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            if(infos != null){
                for(int i=0;i<infos.length;i++){
                    if(infos[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }

        return false;
    }
}
