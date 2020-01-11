package com.example.lego.ui.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lego.R;
import com.example.lego.interfaces.ItemClickListener;

/**
 * Created by VietVan on 06/06/2018.
 */

public class ProductViewHolderStaff extends RecyclerView.ViewHolder {

    public ImageView product_image;
    public TextView product_name, product_price, product_phone, product_number;
    public Button btnEdit, btnRemove;

    ItemClickListener itemClickListener;

    public ProductViewHolderStaff(View itemView) {
        super(itemView);

        product_image = itemView.findViewById(R.id.product_image);
        product_name = itemView.findViewById(R.id.product_name);
        product_phone = itemView.findViewById(R.id.product_phone);
        product_price = itemView.findViewById(R.id.product_price);
        product_number = itemView.findViewById(R.id.product_number);

        btnEdit = itemView.findViewById(R.id.btnEdit);
        btnRemove = itemView.findViewById(R.id.btnRemove);
    }

    public ImageView getProduct_image() {
        return product_image;
    }

    public void setProduct_image(ImageView product_image) {
        this.product_image = product_image;
    }

    public TextView getProduct_name() {
        return product_name;
    }

    public void setProduct_name(TextView product_name) {
        this.product_name = product_name;
    }

    public TextView getProduct_price() {
        return product_price;
    }

    public void setProduct_price(TextView product_price) {
        this.product_price = product_price;
    }

    public TextView getProduct_phone() {
        return product_phone;
    }

    public void setProduct_phone(TextView product_phone) {
        this.product_phone = product_phone;
    }

    public TextView getProduct_number() {
        return product_number;
    }

    public void setProduct_number(TextView product_number) {
        this.product_number = product_number;
    }

    public Button getBtnEdit() {
        return btnEdit;
    }

    public void setBtnEdit(Button btnEdit) {
        this.btnEdit = btnEdit;
    }

    public Button getBtnRemove() {
        return btnRemove;
    }

    public void setBtnRemove(Button btnRemove) {
        this.btnRemove = btnRemove;
    }
}
