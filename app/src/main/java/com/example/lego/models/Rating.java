package com.example.lego.models;

/**
 * Created by VietVan on 25/06/2018.
 */

public class Rating {
    private String userPhone, productId, rateValue, comment;

    public Rating() {
    }

    public Rating(String userPhone, String productId, String rateValue, String comment) {
        this.userPhone = userPhone;
        this.productId = productId;
        this.rateValue = rateValue;
        this.comment = comment;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getRateValue() {
        return rateValue;
    }

    public void setRateValue(String rateValue) {
        this.rateValue = rateValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
