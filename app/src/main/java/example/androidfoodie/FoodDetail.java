package example.androidfoodie;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import example.androidfoodie.Common.Common;
import example.androidfoodie.Common.Config;
import example.androidfoodie.Database.Database;
import example.androidfoodie.Database.ForDirectOrder;
import example.androidfoodie.Model.Food;
import example.androidfoodie.Model.Order;
import example.androidfoodie.Model.Rating;
import example.androidfoodie.Model.Request;


//import static example.androidfoodie.MainActivity.GUEST;

public class FoodDetail extends AppCompatActivity implements RatingDialogListener {

    TextView food_name, food_price, food_description, food_restaurant;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CounterFab btnCart;
    FloatingActionButton btnRating;
    ElegantNumberButton numberButton;
    Button placeOrder;
    String foodId = "";
    FirebaseDatabase database;
    DatabaseReference foods;
    DatabaseReference requests;
    DatabaseReference ratingTbl;
    Food currentFood;
    String txtTotalPrice;
    Food food;

    RatingBar ratingBar;

    String millitime;

    List<Order> cart = new ArrayList<>();
    //    List<Order> cart_Direct = new ArrayList<>();
    static EditText phoneNo;
    static EditText edtAddress;
    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    String address, comment, totalPrice;
    String configuration;
    RadioGroup group_radio;
    RadioButton radioBtnCash, radioBtnPayPal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);


        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        //Firebase
        database = FirebaseDatabase.getInstance();
        foods = database.getReference("Foods");
        //database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        ratingTbl = database.getReference("Rating");

        //init view
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart = (CounterFab) findViewById(R.id.btnCart);
        btnRating = (FloatingActionButton) findViewById(R.id.btnRating);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRaingDialog();
            }
        });

        placeOrder = (Button) findViewById(R.id.placeOrder);
        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                try {

                    Date CurrentTime = dateFormat.parse(dateFormat.format(new Date()));

                    assert CurrentTime != null;

                    if (CurrentTime.before(dateFormat.parse(currentFood.getOpening())) || CurrentTime.after(dateFormat.parse(currentFood.getClosing()))) {
//                        Toast.makeText(FoodDetail.this, "Restaurant is Closed Plz Find Other Restaurant or wait for opening", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodDetail.this);
                        alertDialog.setTitle("Warning");
                        alertDialog.setMessage("Restaurant Opening Time "+currentFood.getOpening()+" Closing Time "+currentFood.getClosing()+" Are you sure, to place Order");

                        alertDialog.setIcon(R.drawable.ic_warning_black_24dp);

                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Database(getBaseContext()).addToCart(new Order(
                                        foodId,
                                        currentFood.getName(),
                                        numberButton.getNumber(),
                                        currentFood.getPrice(),
                                        currentFood.getDiscount(),
                                        currentFood.getImage(),
                                        currentFood.getRestaurantId()

                                ));
                                cart = new Database(getBaseContext()).getCarts();
                                btnCart.setCount(new Database(getBaseContext()).getCountCart());
                                Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                            }
                        });

                        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new ForDirectOrder(getBaseContext()).cleanC();
                                dialogInterface.dismiss();
                            }
                        });

                        alertDialog.show();

                    } else if (CurrentTime.after(dateFormat.parse(currentFood.getOpening())) || CurrentTime.before(dateFormat.parse(currentFood.getClosing()))) {
                        new Database(getBaseContext()).addToCart(new Order(
                                foodId,
                                currentFood.getName(),
                                numberButton.getNumber(),
                                currentFood.getPrice(),
                                currentFood.getDiscount(),
                                currentFood.getImage(),
                                currentFood.getRestaurantId()

                        ));
                        cart = new Database(getBaseContext()).getCarts();
                        btnCart.setCount(new Database(getBaseContext()).getCountCart());
                        Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }



            }
        });

        btnCart.setCount(new Database(this).getCountCart());
        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                try {

                    Date CurrentTime = dateFormat.parse(dateFormat.format(new Date()));

                    assert CurrentTime != null;

                    if (CurrentTime.before(dateFormat.parse(currentFood.getOpening())) || CurrentTime.after(dateFormat.parse(currentFood.getClosing()))) {
//                        Toast.makeText(FoodDetail.this, "Restaurant is Closed Plz Find Other Restaurant or wait for opening", Toast.LENGTH_SHORT).show();
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodDetail.this);
                        alertDialog.setTitle("Warning");
                        alertDialog.setMessage("Restaurant Opening Time "+currentFood.getOpening()+" Closing Time "+currentFood.getClosing()+" Are you sure, to place Order");

                        alertDialog.setIcon(R.drawable.ic_warning_black_24dp);

                      alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              new ForDirectOrder(getBaseContext()).addToC(new Order(
                                      foodId,
                                      currentFood.getName(),
                                      numberButton.getNumber(),
                                      currentFood.getPrice(),
                                      currentFood.getDiscount(),
                                      currentFood.getImage(),
                                      currentFood.getRestaurantId()
//
                              ));
                              cart = new ForDirectOrder(getBaseContext()).getCart();
                              if (Common.IMEI != null && Common.currentUser == null) {
                                  showAlertDialogGuest();
                              } else {
                                  showAlertDialog();
                              }
                          }
                      });

                        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new ForDirectOrder(getBaseContext()).cleanC();
                                dialogInterface.dismiss();
                            }
                        });

                        alertDialog.show();

                    } else if (CurrentTime.after(dateFormat.parse(currentFood.getOpening())) || CurrentTime.before(dateFormat.parse(currentFood.getClosing()))) {
                        new ForDirectOrder(getBaseContext()).addToC(new Order(
                                foodId,
                                currentFood.getName(),
                                numberButton.getNumber(),
                                currentFood.getPrice(),
                                currentFood.getDiscount(),
                                currentFood.getImage(),
                                currentFood.getRestaurantId()
//
                        ));
                        cart = new ForDirectOrder(getBaseContext()).getCart();
                        if (Common.IMEI != null && Common.currentUser == null) {
                            showAlertDialogGuest();
                        } else {
                            showAlertDialog();
                        }
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });

        food_description = (TextView) findViewById(R.id.food_description);
        food_name = (TextView) findViewById(R.id.food_name);
        food_price = (TextView) findViewById(R.id.food_price);
        food_restaurant = (TextView) findViewById(R.id.food_restaurant);
        food_image = (ImageView) findViewById(R.id.img_food);


        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //get food id from Intent
        if (getIntent() != null)
            foodId = getIntent().getStringExtra("FoodId");
        if (!foodId.isEmpty()) {
            getDetailFood(foodId);
            getRatingFood(foodId);
        }
//        placeOrder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new ForDirectOrder(getBaseContext())
//                        .addToC(new Order(
//                        foodId,
//                        currentFood.getName(),
//                        numberButton.getNumber(),
//                        currentFood.getPrice(),
//                        currentFood.getDiscount()
//                ));
//                Toast.makeText(FoodDetail.this, "Add", Toast.LENGTH_SHORT).show();
////                showAlertDialog();
//            }
//        });
    }

    private void getRatingFood(String foodId) {

        Query foodRating = ratingTbl.orderByChild("foodId").equalTo(foodId);

        foodRating.addValueEventListener(new ValueEventListener() {
            int count = 0, sum = 0;

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Rating item = postSnapShot.getValue(Rating.class);
                    sum += Integer.parseInt(item.getRateValue());
                    count++;
                }
                if (count != 0) {
                    float average = sum / count;
                    ratingBar.setRating(average);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRaingDialog() {

        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad", "Not Good", "Quite Ok", "Very Good", "Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here..")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetail.this)
                .show();

    }

    private void getDetailFood(final String foodId) {
        foods.child(foodId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentFood = dataSnapshot.getValue(Food.class);

                //set Image
                Picasso.get().load(currentFood.getImage()).into(food_image);

                collapsingToolbarLayout.setTitle(currentFood.getName());

                food_price.setText(currentFood.getPrice());

                food_name.setText(currentFood.getName());

                food_description.setText(currentFood.getDescription());

                food_restaurant.setText(currentFood.getRestaurant());

                currentFood.getRestaurantId();

                currentFood.getOpening();

                currentFood.getClosing();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showAlertDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodDetail.this);
        alertDialog.setTitle("Phone No And Address");
        alertDialog.setMessage("fill the information");
        LayoutInflater layoutInflater = getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.for_order, null);// add edit text to alert dialog
        phoneNo = (EditText) view.findViewById(R.id.phoneNo);
        edtAddress = (EditText) view.findViewById(R.id.edtAddress);
        phoneNo.setVisibility(View.INVISIBLE);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                group_radio = (RadioGroup) view.findViewById(R.id.group_radio);
                radioBtnCash = (RadioButton) view.findViewById(R.id.radioBtnCash);
                radioBtnPayPal = (RadioButton) view.findViewById(R.id.radioBtnPayPal);
                group_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {


                    }
                });
                if (!radioBtnCash.isChecked() && !radioBtnPayPal.isChecked()) {
                    Toast.makeText(FoodDetail.this, "Select Payment Method", Toast.LENGTH_SHORT).show();
                } else {
                    if (radioBtnCash.isChecked()) {
                        for (int j = 0; j <= cart.size() - 1; j++) {
                            int total = 0;
                            total = (Integer.parseInt(cart.get(j).getQuantity())) * (Integer.parseInt(cart.get(j).getPrice()));

                            Locale locale = new Locale("en", "US");
                            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                            totalPrice = fmt.format(total);


                            Request request = new Request(

                                    Common.currentUser.getPhone(),
                                    Common.currentUser.getName(),
                                    edtAddress.getText().toString(),
                                    totalPrice,
                                    "0",//status
                                    "UnPaid",
                                    cart.get(j),
                                    cart.get(j).getRestaurantId()
                            );

//                submit to firebase
//                 we will using System.Current.Milli  to key
                            Random rd = new Random();
                            requests.child(String.valueOf(rd.nextLong()))
                                    .setValue(request);
                        }
                        new ForDirectOrder(getBaseContext()).cleanC();
                        Toast.makeText(FoodDetail.this, "Successfully Placed Order", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FoodDetail.this, OrderStatus.class);
//                //because category id is key, so we get key of this item
                        intent.putExtra("Counter", "a");
                        startActivity(intent);
                        finish();
                    } else {
                        int total = 0;
                        total = (Integer.parseInt(numberButton.getNumber())) * (Integer.parseInt(currentFood.getPrice()));

                        Locale locale = new Locale("en", "US");
                        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                        txtTotalPrice = fmt.format(total);
                        String formatAmount = txtTotalPrice
                                .replace("$", "")
                                .replace(",", "");
//                float amount = Float.parseFloat(formatAmount);
                        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                                "USD",
                                "Foodies App Order",
                                PayPalPayment.PAYMENT_INTENT_SALE);
                        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                        startActivityForResult(intent, 0);
                    }
                }

//

                //create new request
//                Request request = new Request(phoneNo.getText().toString(),
//                        edtAddress.getText().toString(),
//                        txtTotalPrice, cart.get(0), Common.IMEI, cart.get(0).getRestaurantId());
//
//                //submit to firebase
//                Random rd = new Random();
//                requests.child(String.valueOf(rd.nextLong() + System.currentTimeMillis()))
//                        .setValue(request);
//
//                //delete cart
//                Toast.makeText(FoodDetail.this, "Thank You, Order Placed", Toast.LENGTH_SHORT).show();
//                new ForDirectOrder(getBaseContext()).cleanC();
//                Intent intent = new Intent(FoodDetail.this, OrderStatus.class);
//                //because category id is key, so we get key of this item
//                intent.putExtra("Counter", "a");
//                startActivity(intent);
//                finish();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new ForDirectOrder(getBaseContext()).cleanC();
                dialog.dismiss();
            }
        });
        alertDialog.setView(view);    // add edit text to alert dialog

        alertDialog.show();
    }

    private void showAlertDialogGuest() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(FoodDetail.this);
        alertDialog.setTitle("Phone No And Address");
        alertDialog.setMessage("fill the information");
        LayoutInflater layoutInflater = getLayoutInflater();
        final View view = layoutInflater.inflate(R.layout.for_order, null);// add edit text to alert dialog
        phoneNo = (EditText) view.findViewById(R.id.phoneNo);
        edtAddress = (EditText) view.findViewById(R.id.edtAddress);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                group_radio = (RadioGroup) view.findViewById(R.id.group_radio);
                radioBtnCash = (RadioButton) view.findViewById(R.id.radioBtnCash);
                radioBtnPayPal = (RadioButton) view.findViewById(R.id.radioBtnPayPal);
                group_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {


                    }
                });
                if (!radioBtnCash.isChecked() && !radioBtnPayPal.isChecked()) {
                    Toast.makeText(FoodDetail.this, "Select Payment Method", Toast.LENGTH_SHORT).show();
                } else {
                    if (radioBtnCash.isChecked()) {
                        for (int j = 0; j <= cart.size() - 1; j++) {
                            int total = 0;
                            total = (Integer.parseInt(cart.get(j).getQuantity())) * (Integer.parseInt(cart.get(j).getPrice()));

                            Locale locale = new Locale("en", "US");
                            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                            totalPrice = fmt.format(total);

                            Request request = new Request(

                                    phoneNo.getText().toString(),
                                    "Guest",
                                    edtAddress.getText().toString(),
                                    totalPrice,
                                    "0",//status
                                    Common.IMEI,
                                    cart.get(j),
                                    cart.get(j).getRestaurantId(),
                                    "UnPaid"
                            );

//                submit to firebase
//                 we will using System.Current.Milli  to key
                            Random rd = new Random();
                            requests.child(String.valueOf(rd.nextLong()))
                                    .setValue(request);
                        }
                        new ForDirectOrder(getBaseContext()).cleanC();
                        Toast.makeText(FoodDetail.this, "Successfully Placed Order", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FoodDetail.this, OrderStatus.class);
//                //because category id is key, so we get key of this item
                        intent.putExtra("Counter", "a");
                        startActivity(intent);
                        finish();
                    } else {
                        int total = 0;
                        total = (Integer.parseInt(numberButton.getNumber())) * (Integer.parseInt(currentFood.getPrice()));

                        Locale locale = new Locale("en", "US");
                        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                        txtTotalPrice = fmt.format(total);
                        String formatAmount = txtTotalPrice
                                .replace("$", "")
                                .replace(",", "");
//                float amount = Float.parseFloat(formatAmount);
                        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(formatAmount),
                                "USD",
                                "Foodies App Order",
                                PayPalPayment.PAYMENT_INTENT_SALE);
                        Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
                        startActivityForResult(intent, 1);
                    }
                }

//

                //create new request
//                Request request = new Request(phoneNo.getText().toString(),
//                        edtAddress.getText().toString(),
//                        txtTotalPrice, cart.get(0), Common.IMEI, cart.get(0).getRestaurantId());
//
//                //submit to firebase
//                Random rd = new Random();
//                requests.child(String.valueOf(rd.nextLong() + System.currentTimeMillis()))
//                        .setValue(request);
//
//                //delete cart
//                Toast.makeText(FoodDetail.this, "Thank You, Order Placed", Toast.LENGTH_SHORT).show();
//                new ForDirectOrder(getBaseContext()).cleanC();
//                Intent intent = new Intent(FoodDetail.this, OrderStatus.class);
//                //because category id is key, so we get key of this item
//                intent.putExtra("Counter", "a");
//                startActivity(intent);
//                finish();
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new ForDirectOrder(getBaseContext()).cleanC();
                dialog.dismiss();
            }
        });
        alertDialog.setView(view);    // add edit text to alert dialog

        alertDialog.show();
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(int value, @NotNull String commnets) {

        final Rating rating = new Rating(Common.currentUser.getPhone(),
                foodId,
                String.valueOf(value),
                commnets);

        ratingTbl.child(Common.currentUser.getPhone()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(Common.currentUser.getPhone()).exists()) {
                    //remove old value
                    ratingTbl.child(Common.currentUser.getPhone()).removeValue();

                    //update new value
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);

                } else {
                    //update new value
                    ratingTbl.child(Common.currentUser.getPhone()).setValue(rating);

                }
                Toast.makeText(FoodDetail.this, "Thank you for submit rating..", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                    if (confirmation != null) {
                        try {

                            for (int j = 0; j <= cart.size() - 1; j++) {
                                int total = 0;
                                total = (Integer.parseInt(cart.get(j).getQuantity())) * (Integer.parseInt(cart.get(j).getPrice()));

                                Locale locale = new Locale("en", "US");
                                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                                totalPrice = fmt.format(total);
                                String paymentDetails = confirmation.toJSONObject().toString(4);
                                JSONObject jsonObject = new JSONObject(paymentDetails);

                                Request request = new Request(

                                        Common.currentUser.getPhone(),
                                        Common.currentUser.getName(),
                                        edtAddress.getText().toString(),
                                        totalPrice,
                                        "0",//status
                                        jsonObject.getJSONObject("response").getString("state"),
                                        cart.get(j),
                                        cart.get(j).getRestaurantId()
                                );

//                submit to firebase
//                 we will using System.Current.Milli  to key
                                Random rd = new Random();
                                requests.child(String.valueOf(rd.nextLong()))
                                        .setValue(request);
                            }
                            new ForDirectOrder(getBaseContext()).cleanC();
                            Toast.makeText(this, "Successfully Placed Order", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(FoodDetail.this, OrderStatus.class);
//                //because category id is key, so we get key of this item
                            intent.putExtra("Counter", "a");
                            startActivity(intent);
                            finish();
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, "Payment Cancel", Toast.LENGTH_SHORT).show();
                    new ForDirectOrder(getBaseContext()).cleanC();
                } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
                    new ForDirectOrder(getBaseContext()).cleanC();
                }
                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                    if (confirmation != null) {
                        try {

                            for (int j = 0; j <= cart.size() - 1; j++) {
                                int total = 0;
                                total = (Integer.parseInt(cart.get(j).getQuantity())) * (Integer.parseInt(cart.get(j).getPrice()));

                                Locale locale = new Locale("en", "US");
                                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                                totalPrice = fmt.format(total);
                                String paymentDetails = confirmation.toJSONObject().toString(4);
                                JSONObject jsonObject = new JSONObject(paymentDetails);

                                Request request = new Request(

                                        phoneNo.getText().toString(),
                                        "Guest",
                                        edtAddress.getText().toString(),
                                        totalPrice,
                                        "0",//status
                                        Common.IMEI,
                                        cart.get(j),
                                        cart.get(j).getRestaurantId(),
                                        jsonObject.getJSONObject("response").getString("state")
                                );

//                submit to firebase
//                 we will using System.Current.Milli  to key
                                Random rd = new Random();
                                requests.child(String.valueOf(rd.nextLong()))
                                        .setValue(request);
                            }
                            new ForDirectOrder(getBaseContext()).cleanC();
                            Toast.makeText(this, "Successfully Placed Order", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(FoodDetail.this, OrderStatus.class);
//                //because category id is key, so we get key of this item
                            intent.putExtra("Counter", "a");
                            startActivity(intent);
                            finish();
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(this, "Payment Cancel", Toast.LENGTH_SHORT).show();
                    new ForDirectOrder(getBaseContext()).cleanC();
                } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
                    new ForDirectOrder(getBaseContext()).cleanC();
                }
                break;
        }
    }
}
