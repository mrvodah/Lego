package com.example.lego.ui.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.lego.interfaces.ItemClickListener;
import com.example.lego.R;

/**
 * Created by VietVan on 06/06/2018.
 */

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public ImageView food_image, fav_image, cart_image;
    public TextView food_name, food_price;

    ItemClickListener itemClickListener;

    public FoodViewHolder(View itemView) {
        super(itemView);

        food_image = itemView.findViewById(R.id.food_image);
        food_name = itemView.findViewById(R.id.food_name);
        food_price = itemView.findViewById(R.id.food_price);
        fav_image = itemView.findViewById(R.id.fav);
        cart_image = itemView.findViewById(R.id.iv_cart);

        itemView.setOnClickListener(this);

    }

    public ImageView getFood_image() {
        return food_image;
    }

    public void setFood_image(ImageView food_image) {
        this.food_image = food_image;
    }

    public ImageView getFav_image() {
        return fav_image;
    }

    public void setFav_image(ImageView fav_image) {
        this.fav_image = fav_image;
    }

    public ImageView getCart_image() {
        return cart_image;
    }

    public void setCart_image(ImageView cart_image) {
        this.cart_image = cart_image;
    }

    public TextView getFood_name() {
        return food_name;
    }

    public void setFood_name(TextView food_name) {
        this.food_name = food_name;
    }

    public TextView getFood_price() {
        return food_price;
    }

    public void setFood_price(TextView food_price) {
        this.food_price = food_price;
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false);
    }
}
