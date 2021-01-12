package example.androidfoodie;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import example.androidfoodie.Common.Common;
import example.androidfoodie.Common.Config;
import example.androidfoodie.Database.Database;
import example.androidfoodie.Database.ForDirectOrder;
import example.androidfoodie.Model.Order;
import example.androidfoodie.Model.Request;
import example.androidfoodie.ViewHolder.CartAdapter;

import org.json.JSONException;
import org.json.JSONObject;
//import static example.androidfoodie.MainActivity.GUEST;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotalPrice;
    Button btnPlace;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    //    static TextView tv_time;
    static EditText phoneNo;
    static EditText edtAddress;
    static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);
    String address, comment, totalPrice;
    String configuration;
    RadioGroup group_radio;
    RadioButton radioBtnCash, radioBtnPayPal;
    private static final int PAYPAL_REQUEST_CODE_0 = 0, PAYPAL_REQUEST_CODE_1 = 1;
    // for google pay
// private PaymentsClient paymentsClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);


        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        /*//google pay
        Wallet.WalletOptions walletOptions = new Wallet.WalletOptions.Builder()
                .setEnvironment(WalletConstants.ENVIRONMENT_TEST).build();

        paymentsClient = Wallet.getPaymentsClient(this, walletOptions);*/

        //init
        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = (TextView) findViewById(R.id.total);
        btnPlace = (Button) findViewById(R.id.btnPlaceOrder);

//        tv_time = findViewById(R.id.timeS);


        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Common.IMEI != null && Common.currentUser == null) {
                    if (cart.size() == 0) {
                        Toast.makeText(Cart.this, "Cart is Empty", Toast.LENGTH_SHORT).show();
                    } else {
//                    Toast.makeText(Cart.this, "Guest"+Common.currentUser.getName(), Toast.LENGTH_SHORT).show();
                        showAlertDialogGuest();
                    }
                } else {
                    if (cart.size() == 0) {
                        Toast.makeText(Cart.this, "Cart is Empty", Toast.LENGTH_SHORT).show();
                    } else {
//                    Toast.makeText(Cart.this, "User"+Common.currentUser.getName(), Toast.LENGTH_SHORT).show();
                        showAlertDialog();
                    }
                }
            }
        });

        loadListFood();
        adapter.notifyDataSetChanged();
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
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
//
                group_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {


                    }
                });
                if (!radioBtnCash.isChecked() && !radioBtnPayPal.isChecked()) {
                    Toast.makeText(Cart.this, "Select Payment Method", Toast.LENGTH_SHORT).show();
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
                        new Database(getBaseContext()).cleanCart();
                        Toast.makeText(Cart.this, "Successfully Placed Order", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Cart.this, OrderStatus.class);
//                //because category id is key, so we get key of this item
                        intent.putExtra("Counter", "a");
                        startActivity(intent);
                        finish();

                    } else {
                        final String phone = phoneNo.getText().toString();
//               final String address = edtAddress.getText().toString();

                        String formatAmount = txtTotalPrice.getText().toString()
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
                /*

//

                */
//                for (int j = 0; j <= cart.size() - 1; j++) {
//                    //create new request
//                    Request request = new Request(phoneNo.getText().toString(),
//                            edtAddress.getText().toString(),
//                            txtTotalPrice.getText().toString(),
//                            cart.get(j), Common.IMEI,cart.get(j).getRestaurantId());
//
////                submit to firebase
////                 we will using System.Current.Milli  to key
//                    Random rd = new Random();
//                    requests.child(String.valueOf(rd.nextLong()+System.currentTimeMillis()))
//                            .setValue(request);
//                }
                //delete cart
//                new Database(getBaseContext()).cleanCart();
//                Toast.makeText(Cart.this, "Thank You, Order Placed", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Cart.this, OrderStatus.class);
//                //because category id is key, so we get key of this item
//                intent.putExtra("Counter", "a");
//                startActivity(intent);
////                dialogInterface.dismiss();
////
//                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Database(getBaseContext()).cleanCart();
                dialog.dismiss();
            }
        });
        alertDialog.setView(view);    // add edit text to alert dialog

        alertDialog.show();
//        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                new Database(getBaseContext()).cleanCart();
//            dialogInterface.dismiss();
//            }
//        });
//        alertDialog.setView(view);
//        alertDialog.show();
    }

    private void showAlertDialogGuest() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
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
//
                group_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {


                    }
                });
                if (!radioBtnCash.isChecked() && !radioBtnPayPal.isChecked()) {
                    Toast.makeText(Cart.this, "Select Payment Method", Toast.LENGTH_SHORT).show();
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
                        new Database(getBaseContext()).cleanCart();
                        Toast.makeText(Cart.this, "Successfully Placed Order", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Cart.this, OrderStatus.class);
//                //because category id is key, so we get key of this item
                        intent.putExtra("Counter", "a");
                        startActivity(intent);
                        finish();

                    } else {
                        final String phone = phoneNo.getText().toString();
//               final String address = edtAddress.getText().toString();

                        String formatAmount = txtTotalPrice.getText().toString()
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
                /*

//

                */
//                for (int j = 0; j <= cart.size() - 1; j++) {
//                    //create new request
//                    Request request = new Request(phoneNo.getText().toString(),
//                            edtAddress.getText().toString(),
//                            txtTotalPrice.getText().toString(),
//                            cart.get(j), Common.IMEI,cart.get(j).getRestaurantId());
//
////                submit to firebase
////                 we will using System.Current.Milli  to key
//                    Random rd = new Random();
//                    requests.child(String.valueOf(rd.nextLong()+System.currentTimeMillis()))
//                            .setValue(request);
//                }
                //delete cart
//                new Database(getBaseContext()).cleanCart();
//                Toast.makeText(Cart.this, "Thank You, Order Placed", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(Cart.this, OrderStatus.class);
//                //because category id is key, so we get key of this item
//                intent.putExtra("Counter", "a");
//                startActivity(intent);
////                dialogInterface.dismiss();
////
//                finish();
            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Database(getBaseContext()).cleanCart();
                dialog.dismiss();
            }
        });
        alertDialog.setView(view);    // add edit text to alert dialog

        alertDialog.show();
//        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                new Database(getBaseContext()).cleanCart();
//            dialogInterface.dismiss();
//            }
//        });
//        alertDialog.setView(view);
//        alertDialog.show();
    }

    private void loadListFood() {

        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        //for calculate total price
        int total = 0;
        for (Order order : cart)
            total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(order.getQuantity()));

        Locale locale = new Locale("en", "US");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));

    }

//    @Override
//    public void onContentChanged() {
//        super.onContentChanged();
//        Intent i = new Intent(Cart.this, Cart.class);  //your class
//        startActivity(i);
//        finish();
//    }

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

                            new Database(getBaseContext()).cleanCart();
                            Toast.makeText(this, "Successfully Placed Order", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Cart.this, OrderStatus.class);
//                //because category id is key, so we get key of this item
                            intent.putExtra("Counter", "a");
                            startActivity(intent);
                            finish();
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    new Database(getBaseContext()).cleanCart();
                    Toast.makeText(this, "Payment Cancel", Toast.LENGTH_SHORT).show();
                } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
                    new Database(getBaseContext()).cleanCart();
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
                            new Database(getBaseContext()).cleanCart();
                            Toast.makeText(this, "Successfully Placed Order", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Cart.this, OrderStatus.class);
//                //because category id is key, so we get key of this item
                            intent.putExtra("Counter", "a");
                            startActivity(intent);
                            finish();
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    new Database(getBaseContext()).cleanCart();
                    Toast.makeText(this, "Payment Cancel", Toast.LENGTH_SHORT).show();
                } else if (requestCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    Toast.makeText(this, "Invalid Payment", Toast.LENGTH_SHORT).show();
                    new Database(getBaseContext()).cleanCart();
                }
                break;
        }
    }
}
