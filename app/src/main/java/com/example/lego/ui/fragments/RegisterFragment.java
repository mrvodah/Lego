package com.example.lego.ui.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.lego.R;
import com.example.lego.models.User;
import com.example.lego.utils.Util;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private static final String TAG = "RegisterFragment";
    @BindView(R.id.edtPhone)
    MaterialEditText edtPhone;
    @BindView(R.id.edtNane)
    MaterialEditText edtNane;
    @BindView(R.id.edtPassword)
    MaterialEditText edtPassword;
    @BindView(R.id.edtSecureCode)
    MaterialEditText edtSecureCode;

    FirebaseDatabase database;
    DatabaseReference table_user;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");


    }

    @OnClick(R.id.su_btnSignUp)
    public void onViewClicked() {
        if (Util.isConnectedToInternet(getContext())) {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Please waiting ...");
            progressDialog.show();

            table_user.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    progressDialog.dismiss();

                    if (dataSnapshot.child(edtPhone.getText().toString()).exists())
                        Toast.makeText(getContext(), "Phone Number already registed!", Toast.LENGTH_SHORT).show();
                    else {
                        User user = new User(
                                edtNane.getText().toString(),
                                edtPassword.getText().toString(),
                                edtSecureCode.getText().toString()
                        );

                        table_user.child(edtPhone.getText().toString()).setValue(user);
                        Toast.makeText(getContext(), "Sign Up Successful~", Toast.LENGTH_SHORT).show();
//                        finish();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(getContext(), "Please check your connection!", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
