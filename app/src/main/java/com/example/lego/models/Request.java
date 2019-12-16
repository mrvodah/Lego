package com.example.lego.models;

import java.util.List;

/**
 * Created by VietVan on 06/06/2018.
 */

public class Request{
    public String phone;
    public String name;
    public String address;
    public String total;
    public String status;
    public String comment;
    public String startDate;
    public List<Order> list;

    public Request() {
    }

    public Request(String phone, String name, String address, String total, String status, String comment, String startDate, List<Order> list) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.startDate = startDate;
        this.list = list;
    }

//    public Request(String phone, String name, String address, String total, String status, String comment, List<Order> list) {
//        this.phone = phone;
//        this.name = name;
//        this.address = address;
//        this.total = total;
//        this.status = status;
//        this.comment = comment;
//        this.list = list;
//    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<Order> getList() {
        return list;
    }

    public void setList(List<Order> list) {
        this.list = list;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "Request{" +
                "phone='" + phone + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", total='" + total + '\'' +
                ", status='" + status + '\'' +
                ", comment='" + comment + '\'' +
                ", startDate='" + startDate + '\'' +
                ", list=" + list +
                '}';
    }
}
