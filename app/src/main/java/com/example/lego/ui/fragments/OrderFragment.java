package com.example.lego.ui.fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lego.R;
import com.example.lego.models.Product;
import com.example.lego.models.Request;
import com.example.lego.ui.activities.OrderDetail;
import com.example.lego.ui.activities.OrderStatus;
import com.example.lego.ui.adapters.OrderViewHolder;
import com.example.lego.utils.DialogUtil;
import com.example.lego.utils.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrderFragment extends Fragment {

    private static final String TAG = "OrderFragment";
    @BindView(R.id.rv_hire)
    RecyclerView rvOrder;

    FirebaseDatabase database;
    DatabaseReference requests;

    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    public OrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        layoutManager = new LinearLayoutManager(getContext());
        rvOrder.setLayoutManager(layoutManager);
        rvOrder.setHasFixedSize(true);

        loadOrders();
    }

    private void loadOrders() {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout_staff,
                OrderViewHolder.class,
                requests.orderByChild("status").equalTo("0")
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.getTvOrderId().setText("#" + adapter.getRef(position).getKey());
                viewHolder.getTvOrderStatus().setText(convertCodeToStatus(model.getStatus()));
                viewHolder.getTvOrderPhone().setText(model.getPhone());
                viewHolder.getTvOrderAddress().setText(model.getAddress());

                viewHolder.getFbAccept().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Request item = adapter.getItem(position);
                        item.setStatus("1");
                        requests.child(adapter.getRef(position).getKey()).setValue(item);
                    }
                });

                viewHolder.getFbRemove().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requests.child(adapter.getRef(position).getKey()).removeValue();
                    }
                });

                viewHolder.getLnContent().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.currentRequest = model;
                        Intent intent = new Intent(getContext(), OrderDetail.class);
                        startActivity(intent);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rvOrder.setAdapter(adapter);
    }

    private String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Waiting";

        return "Accept";
    }

}
