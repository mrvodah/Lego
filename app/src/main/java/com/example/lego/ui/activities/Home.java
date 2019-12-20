package com.example.lego.ui.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.lego.Constant;
import com.example.lego.R;
import com.example.lego.ui.fragments.HireFragment;
import com.example.lego.ui.fragments.MyListFragment;
import com.example.lego.ui.fragments.OtherListFragment;
import com.example.lego.ui.fragments.SellFragment;
import com.example.lego.utils.Util;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import io.paperdb.Paper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "TAG";
    FirebaseDatabase database;
    DatabaseReference user;

    TextView tvFullName;

    DrawerLayout drawer;

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

        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("LegoApp");
        setSupportActionBar(toolbar);

        //init firebase
        database = FirebaseDatabase.getInstance();
        user = database.getReference("User");

        //init paper
        Paper.init(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // set name for user
        View headerView = navigationView.getHeaderView(0);
        tvFullName = headerView.findViewById(R.id.tvfullname);
        tvFullName.setText(Util.currentUser.getName());

//        Intent service = new Intent(Home.this, ListenDialog.class);
//        startService(service);
//
//        Intent serviceOrder = new Intent(Home.this, ListenOrder.class);
//        startService(serviceOrder);

        loadContent(Constant.FRAGMENT_OTHER_LIST);
//        loadContent(Constant.FRAGMENT_MY_LIST);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_1:
                loadContent(Constant.FRAGMENT_OTHER_LIST);
                break;
            case R.id.nav_2:
                loadContent(Constant.FRAGMENT_MY_LIST);
                break;
            case R.id.nav_3:
                loadContent(Constant.FRAGMENT_HIRE_LIST);
                break;
            case R.id.nav_4:
                loadContent(Constant.FRAGMENT_SELL_LIST);
                break;
            case R.id.nav_5:
                View v = LayoutInflater.from(Home.this).inflate(R.layout.change_password_layout, null);

                final MaterialEditText pw, npw, rpnpw;
                pw = v.findViewById(R.id.edtPw);
                npw = v.findViewById(R.id.edtnPw);
                rpnpw = v.findViewById(R.id.edtrpnPw);

                new AlertDialog.Builder(Home.this)
                        .setTitle("CHANGE PASSWORD")
                        .setMessage("Please fill all information")
                        .setView(v)
                        .setPositiveButton("CHANGE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (pw.getText().toString().equals(Util.currentUser.getPassword()) &&
                                        npw.getText().toString().equals(rpnpw.getText().toString())) {

//                                Util.currentUser.setPassword(npw.getText().toString());
//                                user.child(Util.currentUser.getPhone()).setValue(
//                                        new User(
//                                                Util.currentUser.getName(),
//                                                Util.currentUser.getPassword(),
//                                                Util.currentUser.getIsStaff(),
//                                                Util.currentUser.getSecureCode()
//                                        )
//                                );
//
//                                Toast.makeText(Home.this, "Password changed!", Toast.LENGTH_SHORT).show();

                                    Map<String, Object> map = new HashMap<>();
                                    map.put("password", npw.getText().toString());

                                    user.child(Util.currentUser.getPhone()).updateChildren(map)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(Home.this, "Password was updated!", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Home.this, "Update Password get error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                } else {

                                    Toast.makeText(Home.this, "Wrong Password or New Password or Repeat new Password not same! Please check again~", Toast.LENGTH_SHORT).show();

                                }

                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.nav_6:
                //Delete rememeber user & pwd
                Paper.book().destroy();

                Intent intent = new Intent(Home.this, SplashScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadContent(int fragmentList) {
        Fragment fragment = new Fragment();
        Log.d(TAG, "loadContent: " + fragmentList);
        switch (fragmentList) {
            case Constant.FRAGMENT_OTHER_LIST:
                fragment = new OtherListFragment();
                break;
            case Constant.FRAGMENT_MY_LIST:
                fragment = new MyListFragment();
                break;
            case Constant.FRAGMENT_HIRE_LIST:
                fragment = new HireFragment();
                break;
            case Constant.FRAGMENT_SELL_LIST:
                fragment = new SellFragment();
                break;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.rl_container, fragment)
                .commit();

    }
}
