package com.example.lego.models;

/**
 * Created by VietVan on 29/05/2018.
 */

public class Category {
    private String Name, Image, ShopId;

    public Category() {
    }

    public Category(String name, String image) {
        Name = name;
        Image = image;
        ShopId = "1";
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getShopId() {
        return ShopId;
    }

    public void setShopId(String shopId) {
        ShopId = shopId;
    }

    @Override
    public String toString() {
        return "Category{" +
                "Name='" + Name + '\'' +
                ", Image='" + Image + '\'' +
                ", ShopId='" + ShopId + '\'' +
                '}';
    }
}
