package com.example.lego.models;

import java.io.Serializable;

/**
 * Created by VietVan on 06/06/2018.
 */

public class Order implements Serializable {
    private int ID;
    private String ProductId;
    private String ProductName;
    private String Quantity;
    private String Price;
    private String Discount;
    private String startDate;
    private String endDate;

    public Order() {
    }

//    public Order(String productId, String productName, String quantity, String price, String discount) {
//        ProductId = productId;
//        ProductName = productName;
//        Quantity = quantity;
//        Price = price;
//        Discount = discount;
//    }

    public Order(String productId, String productName, String quantity, String price, String discount, String startDate, String endDate) {
        ProductId = productId;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

//    public Order(int ID, String productId, String productName, String quantity, String price, String discount) {
//        this.ID = ID;
//        ProductId = productId;
//        ProductName = productName;
//        Quantity = quantity;
//        Price = price;
//        Discount = discount;
//    }

    public Order(int ID, String productId, String productName, String quantity, String price, String discount, String startDate, String endDate) {
        this.ID = ID;
        ProductId = productId;
        ProductName = productName;
        Quantity = quantity;
        Price = price;
        Discount = discount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getProductId() {
        return ProductId;
    }

    public void setProductId(String productId) {
        ProductId = productId;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
