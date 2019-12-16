package com.example.lego.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lego.models.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by VietVan on 06/06/2018.
 */

public class Database extends SQLiteAssetHelper {

    private static String DB_NAME = "EatItDB.db";
    private static int DB_VERSION = 1;

    public Database(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    public List<Order> getCarts(){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from OrderDetail", null);
        List<Order> list = new ArrayList<>();
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            list.add(new Order(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6),
                    cursor.getString(7)
            ));

            cursor.moveToNext();
        }

        return list;
    }

    public void addToCart(Order order){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ProductId", order.getProductId());
        values.put("ProductName", order.getProductName());
        values.put("Quantity", order.getQuantity());
        values.put("Price", order.getPrice());
        values.put("Discount", order.getDiscount());
        values.put("StartDate", order.getStartDate());
        values.put("EndDate", order.getEndDate());

        sqLiteDatabase.insert("OrderDetail", null, values);
    }

    public void cleanCart(){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        String query = String.format("Delete from OrderDetail");

        sqLiteDatabase.execSQL(query);
    }

    //Favorties
    public void addToFavorites(String productId){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("ProductId", productId);

        db.insert("Favorites", null, values);
    }

    public void removeFromFavorites(String productId){
        SQLiteDatabase db = getWritableDatabase();

        db.delete("Favorites", "ProductId = " + productId, null);
    }

    public boolean isFavorite(String productId){
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Favorites where ProductId = " + productId, null);

        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        else{
            cursor.close();
            return true;
        }

    }

    public void updateCart(String newValue, int id){
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("Quantity", newValue);

        sqLiteDatabase.update("OrderDetail", values, "ID = " + String.valueOf(id), null);
    }

}
