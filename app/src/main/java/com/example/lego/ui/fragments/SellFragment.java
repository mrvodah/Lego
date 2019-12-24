package com.example.lego.ui.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.lego.R;
import com.example.lego.interfaces.ItemClickListener;
import com.example.lego.models.Product;
import com.example.lego.ui.adapters.ProductWaitViewHolder;
import com.example.lego.utils.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SellFragment extends Fragment {

    private static final String TAG = "SellFragment";
    @BindView(R.id.rv_hire)
    RecyclerView rvOrder;
    @BindView(R.id.sw_layout)
    SwipeRefreshLayout swipeLayout;

    FirebaseDatabase database;
    DatabaseReference productWaitList;

    FirebaseRecyclerAdapter<Product, ProductWaitViewHolder> adapter;

    public SellFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        database = FirebaseDatabase.getInstance();
        productWaitList = database.getReference("ProductWait");

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
                loadProduct();
            }
        });

        // load for first times
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                loadProduct();
            }
        });
    }

    private void loadProduct() {
        adapter = new FirebaseRecyclerAdapter<Product, ProductWaitViewHolder>(
                Product.class,
                R.layout.product_wait_item,
                ProductWaitViewHolder.class,
                productWaitList.orderByChild("phone").equalTo(Util.currentUser.getPhone())
        ) {
            @Override
            protected void populateViewHolder(final ProductWaitViewHolder viewHolder, final Product model, final int position) {
                viewHolder.getProduct_name().setText(model.getName());
                Picasso.get().load(model.getImage()).into(viewHolder.getProduct_image());

                viewHolder.getProduct_delete().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                    }
                });
            }

        };

        rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrder.setAdapter(adapter);
        swipeLayout.setRefreshing(false);
    }
}
