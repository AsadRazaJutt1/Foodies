package example.androidfoodie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.StackView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.UUID;

import example.androidfoodie.Common.Common;
import example.androidfoodie.Database.Database;
import example.androidfoodie.Interface.ItemClickListner;
import example.androidfoodie.Model.Order;
import example.androidfoodie.Model.Request;
import example.androidfoodie.ViewHolder.OrderViewHolder;


//import static example.androidfoodie.MainActivity.GUEST;

public class OrderStatus extends AppCompatActivity {
    static CountDownTimer timer = null;
    //    final CountDownTimer finalTimer = timer;
    static char a;
    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;
    String counter = "";
    int minute;
    long min;
    TextView tv_timer;
    static TextView tv_time;
    Order order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);
        min = 30 * 60 * 1000;
        //For Counter
        tv_time = findViewById(R.id.timeS);


        //firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        counter = getIntent().getStringExtra("Counter");
        if (counter != null && !counter.isEmpty()) {
            if (timer != null) {
                timer.cancel();
                counterForOrder(min);
            } else {
                counterForOrder(min);
            }
            tv_time.setVisibility(View.VISIBLE);
//            timer.cancel();
            Toast.makeText(this, "Start", Toast.LENGTH_SHORT).show();
        }


        recyclerView = (RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);

//        layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        if (Common.IMEI != null && Common.currentUser == null) {
            loadOrdersGuest(Common.IMEI);

        } else {
            loadOrders(Common.currentUser.getPhone());
        }

    }

    private void loadOrdersGuest(String IMEI) {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("imei_R").equalTo(Common.IMEI)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, int position) {

                viewHolder.order_id.setText("Order Number : " + adapter.getRef(position).getKey());
                viewHolder.order_status.setText("Order Status : " + convertCodeToStatus(model.getStatus()));
//                viewHolder.txtOrderStatus.setTextColor();

                viewHolder.order_address.setText("Order Phone : " + model.getAddress());
                viewHolder.order_phone.setText("Order Address : " + model.getPhone());
                viewHolder.requestTotal.setText("Total : " + model.getTotal());
//                String.valueOf(model.getTimeStart());
//                viewHolder.timeStart.setText( String.valueOf(model.getTimeStart()));

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
//                        Intent intent = new Intent(OrderStatus.this, OrderTracker.class);

//                        Common.currentRequest = model;
                        //because category id is key, so we get key of this item
//                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
//                        startActivity(intent);
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    private void loadOrders(String phone) {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(final OrderViewHolder viewHolder, final Request model, int position) {

                viewHolder.order_id.setText("Order Number : " + adapter.getRef(position).getKey());
                viewHolder.order_status.setText("Order Status : " + convertCodeToStatus(model.getStatus()));
//                viewHolder.txtOrderStatus.setTextColor();

                viewHolder.order_address.setText("Order Address : " + model.getAddress());
                viewHolder.order_phone.setText("Order Phone : " + model.getPhone());
                viewHolder.requestTotal.setText("Total : " + model.getTotal());
//                String.valueOf(model.getTimeStart());
//                viewHolder.timeStart.setText("" String.valueOf(model.getTimeStart()));

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
//                        if (model.getStatus()=="5") {
//                            requests.child(adapter.getRef(position).getKey()).child("foods")
//                                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                                        food = dataSnapshot.getValue(Food.class);
//                                            TextView requestName;
//                                            final TextView requestQuantity;
//                                            order = dataSnapshot.getValue(Order.class);
////                                            requestName.setText("Name : " + order.getProductName());
////                                            requestQuantity.setText("Quantity : " + order.getQuantity());
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    });
//                        }
                        if (!model.getStatus().equals("5")) {
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
                            alertDialog.setTitle("Order Details");
                            LayoutInflater layoutInflater = getLayoutInflater();
                            View view1 = layoutInflater.inflate(R.layout.food_request, null);

                            final TextView requestName;
                            final TextView requestQuantity;

                            requestName = (TextView) view1.findViewById(R.id.food_name);
                            requestQuantity = (TextView) view1.findViewById(R.id.food_quantity);

                            requests.child(adapter.getRef(position).getKey()).child("foods")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        food = dataSnapshot.getValue(Food.class);
                                            order = dataSnapshot.getValue(Order.class);
                                            requestName.setText("Name : " + order.getProductName());
                                            requestQuantity.setText("Quantity : " + order.getQuantity());
                                            Toast.makeText(OrderStatus.this, "" + model.getStatus(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });


//                        alertDialog.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                requests.child(adapter.getRef(position).getKey()).child("foods")
//                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
////                                                request = new Request("3");
////                                                requests.child(adapter.getRef(position).getKey()).child("foods").child("status").setValue(request);
//                                            }
//
//                                            @Override
//                                            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                            }
//                                        });
//                            }
//                        }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                requests.child(adapter.getRef(position).getKey()).removeValue();
//                            }
//                        });

                            alertDialog.setView(view1);

                            alertDialog.show();

                        } else {
                            requests.child(adapter.getRef(position).getKey()).child("foods")
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                        food = dataSnapshot.getValue(Food.class);
                                            order = dataSnapshot.getValue(Order.class);
                                            Intent intent = new Intent(OrderStatus.this, FoodDetail.class);
                                            intent.putExtra("id", order.getProductId());
                                            startActivity(intent);
                                            Toast.makeText(OrderStatus.this, "" + order.getProductId(), Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    public static String convertCodeToStatus(String status) {

        if (status.equals("0"))
            return "confirmed";

        else if (status.equals("1"))
            return "Deny";

        else if (status.equals("2"))
            return "Placed";

        else if (status.equals("3"))
            return "On the Way";

        else if (status.equals("4"))
            return "Canceled";

        else
            return "Shipped";
    }

    private static void counterForOrder(long min) {


        timer = new CountDownTimer(min, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                tv_time.setText(String.format("Last Order" + "%d:%d:%d", hours, minutes, seconds));
            }

            public void onFinish() {

//                Toast.makeText(, "Your time has been completed",
//                        Toast.LENGTH_LONG).show();
//                finalTimer.cancel();
            }

        };

        timer.start();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)) {
//            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
            updateDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
//            updateFood(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
            // we are reuse AddNewMenuItem Class for update menu item
//
//            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
////            intent.putExtra("update", Common.UPDATE);
//            intent.putExtra("key", adapter.getRef(item.getOrder()).getKey());
//            intent.putExtra("OrderName", (Parcelable) adapter.getItem(item.getOrder()));
        } else {
//            delete(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void updateDialog(String key, final Request item) {
        final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(OrderStatus.this);
        alertDialog1.setTitle("Add New User");
        alertDialog1.setMessage("fill the information");
        LayoutInflater layoutInflater = getLayoutInflater();
        View viewOrderUpdate = layoutInflater.inflate(R.layout.order_update, null);
        final MaterialSpinner spinnerForOrder = (MaterialSpinner) viewOrderUpdate.findViewById(R.id.spinnerForOrder);
        spinnerForOrder.setItems("Cancel");
        alertDialog1.setView(viewOrderUpdate);
        final String currentKey = key;
        alertDialog1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus("4");
                requests.child(currentKey).setValue(item);
            }
        });
        alertDialog1.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog1.show();
    }

    private void delete(String key) {
        requests.child(key).removeValue();
    }
}
