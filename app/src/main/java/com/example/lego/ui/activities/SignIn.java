package com.example.lego.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lego.R;
import com.example.lego.utils.Util;
import com.example.lego.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.rey.material.widget.CheckBox;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.widget.FButton;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignIn extends AppCompatActivity {

    private static final String TAG = "TAG";
    @BindView(R.id.edtPhone)
    MaterialEditText edtPhone;
    @BindView(R.id.edtPassword)
    MaterialEditText edtPassword;
    @BindView(R.id.si_btnSignIn)
    FButton siBtnSignIn;
    @BindView(R.id.cbRemember)
    CheckBox cbRemember;

    FirebaseDatabase database;
    DatabaseReference table_user;

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

        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        //Init paper
        Paper.init(this);

        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");
    }

    @OnClick(R.id.si_btnSignIn)
    public void onViewClicked() {

        if (Util.isConnectedToInternet(getBaseContext())) {

            // save user & passw
            if (cbRemember.isChecked()) {
                Paper.book().write(Util.USER_KEY, edtPhone.getText().toString());
                Paper.book().write(Util.PW_KEY, edtPassword.getText().toString());
            }

            final ProgressDialog progressDialog = new ProgressDialog(SignIn.this);
            progressDialog.setMessage("Please waiting ...");
            progressDialog.show();

            table_user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    progressDialog.dismiss();
                    //check if user not exist in database
                    if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                        // get user information
                        User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                        user.setPhone(edtPhone.getText().toString());

                        if (user.getPassword().equals(edtPassword.getText().toString())) {
                            Util.currentUser = user;
                            if(Boolean.parseBoolean(user.getIsStaff())){
                                startActivity(new Intent(SignIn.this, HomeStaffActivity.class));
                                finish();
                            }
                            else{
                                startActivity(new Intent(SignIn.this, Home.class));
                                finish();
                            }
                            table_user.removeEventListener(this);

                        } else {
                            Toast.makeText(SignIn.this, "Sign in failed!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignIn.this, "User not exists", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Toast.makeText(this, "Please check your connection!", Toast.LENGTH_SHORT).show();
            return;
        }

    }
}
