package com.example.lego.models;

/**
 * Created by VietVan on 29/05/2018.
 */

public class User {
    private String Name, Password, Phone, IsStaff, SecureCode;

    public User() {
    }

    public User(String name, String password) {
        Name = name;
        Password = password;
        IsStaff = "false";
    }

    public User(String name, String password, String isStaff, String secureCode) {
        Name = name;
        Password = password;
        IsStaff = isStaff;
        SecureCode = secureCode;
    }

    public User(String name, String password, String secureCode) {
        Name = name;
        Password = password;
        SecureCode = secureCode;
        IsStaff = "false";
    }

    public String getSecureCode() {
        return SecureCode;
    }

    public void setSecureCode(String secureCode) {
        SecureCode = secureCode;
    }

    public String getIsStaff() {
        return IsStaff;
    }

    public void setIsStaff(String isStaff) {
        IsStaff = isStaff;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "Name='" + Name + '\'' +
                ", Password='" + Password + '\'' +
                ", Phone='" + Phone + '\'' +
                '}';
    }
}
