package com.example.lego.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.lego.ui.activities.Cart;
import com.example.lego.utils.Util;
import com.example.lego.database.Database;
import com.example.lego.models.Order;
import com.example.lego.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by VietVan on 06/06/2018.
 */



public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    List<Order> list;
    Context context;

    public CartAdapter(List<Order> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, null);

        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder holder, final int position) {
        holder.setData(list.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                new Database(context).updateCart(String.valueOf(newValue), list.get(position).getID());

                // calculate total price
                List<Order> cart = new Database(context).getCarts();
                int ntotal = 0;
                for (Order order : cart)
                    ntotal += Integer.parseInt(order.getPrice()) * Integer.parseInt(order.getQuantity());

                Locale locale = new Locale("en", "US");
                NumberFormat format = NumberFormat.getCurrencyInstance(locale);
                Cart.total.setText(format.format(ntotal));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{

        TextView name, price, time;
        ImageView cart_count;
        ElegantNumberButton btn_quantity;

        public CartViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.cart_item_name);
            price = itemView.findViewById(R.id.cart_item_price);
            time = itemView.findViewById(R.id.cart_item_time);
            btn_quantity = itemView.findViewById(R.id.btn_quantity);
            cart_count = itemView.findViewById(R.id.cart_item_count);

            itemView.setOnCreateContextMenuListener(this);

        }

        public void setData(Order order){
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound("" + order.getQuantity(), Color.RED);
            cart_count.setImageDrawable(drawable);

            Locale locale = new Locale("en", "US");
            NumberFormat format = NumberFormat.getCurrencyInstance(locale);;
            float nprice = Float.parseFloat(order.getPrice()) * Float.parseFloat(order.getQuantity());
            price.setText(format.format(nprice));

            time.setText("Thời gian thuê: " + order.getStartDate() + " - " + order.getEndDate());

            name.setText(order.getProductName());

            btn_quantity.setNumber(order.getQuantity());
        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action!");

            menu.add(0,0,getAdapterPosition(), Util.DELETE);
        }
    }

}
