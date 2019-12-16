package com.example.lego.ui.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.lego.R;
import com.example.lego.models.Product;
import com.example.lego.utils.Util;
import com.example.lego.database.Database;
import com.example.lego.models.Order;
import com.example.lego.models.Rating;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    private static final String TAG = "TAG";
    @BindView(R.id.img_product)
    ImageView imgFood;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing)
    CollapsingToolbarLayout collapsing;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.btnCart)
    CounterFab btnCart;
    @BindView(R.id.product_name)
    TextView productName;
    @BindView(R.id.product_price)
    TextView productPrice;
    @BindView(R.id.layout_price)
    LinearLayout layoutPrice;
    @BindView(R.id.number_button)
    ElegantNumberButton numberButton;
    @BindView(R.id.product_description)
    TextView productDescription;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.tv_pick_date)
    TextView tvPickDate;
    @BindView(R.id.nestedScrollView)
    NestedScrollView nestedScrollView;

    FirebaseDatabase database;
    DatabaseReference products;
    DatabaseReference ratingTbl;

    String productId;
    Product currentFood;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    Calendar endDate = Calendar.getInstance();

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //add this code before setContentView
//        CalligraphyConfig.initDefault(
//                new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/restaurant_font.otf")
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        );

        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);

        endDate.add(Calendar.WEEK_OF_YEAR, 1);
        tvPickDate.setText("Chọn ngày trả: " + dateFormat.format(endDate.getTime()));

        // Init Firebase
        database = FirebaseDatabase.getInstance();
        products = database.getReference("Product");
        ratingTbl = database.getReference("Rating");

        // Init View
        collapsing.setExpandedTitleTextAppearance(R.style.ExpandedAppar);
        collapsing.setCollapsedTitleTextAppearance(R.style.CollapsedAppar);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Integer.valueOf(numberButton.getNumber()) > currentFood.getRemain()){
                    Toast.makeText(FoodDetail.this, "Số sản phẩm còn lại không đủ!", Toast.LENGTH_SHORT).show();
                }
                else{

                    new Database(getBaseContext()).addToCart(new Order(
                            productId,
                            currentFood.getName(),
                            numberButton.getNumber(),
                            currentFood.getPrice(),
                            currentFood.getDiscount(),
                            dateFormat.format(new Date()),
                            dateFormat.format(endDate.getTime())
                    ));
                    Toast.makeText(FoodDetail.this, "Added to Cart!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCart.setCount(new Database(this).getCarts().size());

        if (getIntent() != null) {
            productId = getIntent().getStringExtra("ProductId");
            getDetailFood(productId);
            getRatingFood(productId);
        }

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void getRatingFood(final String productId) {
        ratingTbl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(productId).exists()){
                    String rate = (String) dataSnapshot.child(productId).child("rateValue").getValue();
                    ratingBar.setRating(Float.parseFloat(rate));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDetailFood(String productId) {

        products.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                currentFood = dataSnapshot.getValue(Product.class);

                Picasso.get().load(currentFood.getImage()).into(imgFood);

                collapsing.setTitle(currentFood.getName());

                productName.setText(currentFood.getName());
                productPrice.setText(currentFood.getPrice());
                tvNumber.setText("Số sản phẩm: " + currentFood.getRemain() + "/" + currentFood.getTotal());
                productDescription.setText(currentFood.getDescription());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @OnClick({R.id.btnRate, R.id.btn_pick_date})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btnRate:
                showRatingDialog();
                break;
            case R.id.btn_pick_date:
                final Calendar newCalendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        endDate = Calendar.getInstance();
                        endDate.set(year, monthOfYear, dayOfMonth);
                        tvPickDate.setText("Chọn ngày trả: " + dateFormat.format(endDate.getTime()));
                    }

                }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
                break;
        }

    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Quite Ok", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rating this product")
                .setDescription("Please select soem starts and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();
    }

    @Override
    public void onPositiveButtonClicked(final int value, String comments) {
        //Get Rating and upload to firebase
        final Rating rating = new Rating(Util.currentUser.getPhone(),
                productId,
                String.valueOf(value),
                comments);

        ratingTbl.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(productId).exists()){
                    String rate = (String) dataSnapshot.child(productId).child("rateValue").getValue();
                    double ave = (value + Double.parseDouble(rate)) / 2;
                    ratingTbl.child(productId).child("rateValue").setValue((String.valueOf(ave)));
                }
                else
                    ratingTbl.child(productId).setValue(rating);

                Toast.makeText(FoodDetail.this, "Thank yu for submit rating!", Toast.LENGTH_SHORT).show();
                ratingTbl.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
