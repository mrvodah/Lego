package com.example.lego.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lego.R;
import com.example.lego.utils.Util;
import com.example.lego.database.Database;
import com.example.lego.models.Order;
import com.example.lego.models.Request;
import com.example.lego.ui.adapters.CartAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.widget.FButton;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Cart extends AppCompatActivity {

    private static final String TAG = "TAG";
    @BindView(R.id.listCart)
    RecyclerView listCart;
    public static TextView total;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.btnPlaceOrder)
    FButton btnPlaceOrder;

    FirebaseDatabase database;
    DatabaseReference requests;
    List<Order> cart;
    MaterialEditText address, comment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add this code before setContentView
//        CalligraphyConfig.initDefault(
//                new CalligraphyConfig.Builder()
//                        .setDefaultFontPath("fonts/restaurant_font.otf")
//                        .setFontAttrId(R.attr.fontPath)
//                        .build()
//        );

        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        total = findViewById(R.id.total);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        // Init
        cart = new ArrayList<>();
        listCart.setLayoutManager(new LinearLayoutManager(this));
        listCart.setHasFixedSize(true);

        // init swipe
        swipeLayout.setColorSchemeResources(
                R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark
        );
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadListProduct();
            }
        });

        // load for first times
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                loadListProduct();
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });
    }

    private void showAlertDialog() {

        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.order_address_comment, null);

        address = v.findViewById(R.id.edtAddress);
        comment = v.findViewById(R.id.edtComment);

        cart.clear();
        cart.addAll(new Database(this).getCarts());
        new AlertDialog.Builder(Cart.this)
                .setTitle("One more step")
                .setMessage("Enter your address")
                .setView(v)
                .setIcon(R.drawable.ic_shopping_cart_black_24dp)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // calculate total price
                        float ntotal = 0;
                        for (Order order : cart)
                            ntotal += Float.parseFloat(order.getPrice()) * Float.parseFloat(order.getQuantity());

                        // Create request
                        Request request = new Request(
                                Util.currentUser.getPhone(),
                                Util.currentUser.getName(),
                                address.getText().toString(),
                                String.valueOf(ntotal),
                                "0",
                                comment.getText().toString(),
                                Util.dateFormat.format(new Date()),
                                cart
                        );

                        // Submit to Firebase
                        // We will using System.CurrentMil to key
                        requests.child(String.valueOf(System.currentTimeMillis()))
                                .setValue(request);

                        // Clean Cart
                        new Database(Cart.this).cleanCart();
                        Toast.makeText(Cart.this, "Thank you, Order Place", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void loadListProduct() {

        cart = new Database(this).getCarts();
        listCart.setAdapter(new CartAdapter(cart, this));

        // calculate total price
        float ntotal = 0;
        for (Order order : cart)
            ntotal += Float.parseFloat(order.getPrice()) * Float.parseFloat(order.getQuantity());

        Locale locale = Locale.getDefault();
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        total.setText(format.format(ntotal));
        swipeLayout.setRefreshing(false);

    }

    @OnClick(R.id.btnPlaceOrder)
    public void onViewClicked() {
        if (cart.size() > 0)
            showAlertDialog();
        else
            Toast.makeText(this, "Your cart is empty!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Util.DELETE)) {
            deleteCart(item.getOrder());
        }

        return super.onContextItemSelected(item);
    }

    private void deleteCart(int order) {
        //We will remove item at List<Order> by position
        cart.remove(order);
        //After that, we will delete all old data from SQlite
        new Database(this).cleanCart();
        //And final, we will update new data from list<Order> to SQLite
        for (Order item : cart)
            new Database(this).addToCart(item);

        loadListProduct();
    }
}
