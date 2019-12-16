package com.example.lego.ui.fragments;


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
import com.example.lego.models.Request;
import com.example.lego.ui.activities.OrderDetail;
import com.example.lego.ui.activities.OrderStatus;
import com.example.lego.ui.adapters.OrderViewHolder;
import com.example.lego.utils.Util;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class HireFragment extends Fragment {

    private static final String TAG = "HireFragment";
    @BindView(R.id.rv_hire)
    RecyclerView rvOrder;

    FirebaseDatabase database;
    DatabaseReference requests;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    public HireFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hire, container, false);
        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        rvOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        rvOrder.setHasFixedSize(true);

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(Util.currentUser.getPhone())
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.getTvOrderId().setText("#" + adapter.getRef(position).getKey());
                viewHolder.getTvOrderStatus().setText(convertCodeToStatus(model.getStatus()));
                viewHolder.getTvOrderPhone().setText(model.getPhone());
                viewHolder.getTvOrderAddress().setText(model.getAddress());
                viewHolder.getTvOrderDate().setText(model.getStartDate());

                viewHolder.getLnContainer().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.currentRequest = model;

                        Intent orderDetail = new Intent(getContext(), OrderDetail.class);
                        orderDetail.putExtra("OrderId", adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        rvOrder.setAdapter(adapter);
    }

    private String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }

}
