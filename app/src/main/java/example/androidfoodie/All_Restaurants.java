package example.androidfoodie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import example.androidfoodie.Database.Database;
import example.androidfoodie.Database.ForDirectOrder;
import example.androidfoodie.Interface.ItemClickListner;
import example.androidfoodie.Model.Order;
import example.androidfoodie.Model.Request;
import example.androidfoodie.Model.Restaurant_Profile;
import example.androidfoodie.ViewHolder.FoodAdapter;
import example.androidfoodie.ViewHolder.OrderViewHolder;
import example.androidfoodie.ViewHolder.RestaurantViewHolder;

public class All_Restaurants extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference restaurants;

    FirebaseRecyclerAdapter<Restaurant_Profile, RestaurantViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_restaurants);

        database = FirebaseDatabase.getInstance();
        restaurants = database.getReference("Restaurants_id");

//        fab_F1 = (CounterFab) findViewById(R.id.fab_F1);
//
//        localDB = new Database(this);
//
//        fab_F1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                try {
//                    Intent cartIntent = new Intent(Favorite.this, Cart.class);
//                    startActivity(cartIntent);
//                } catch (NullPointerException e) {
//                    throw new IllegalStateException("There is Error", e);
//                }
//            }
//        });
        BottomNavigationView bottomNavigationView= (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_cart:
                        Intent cartIntent = new Intent(All_Restaurants.this, Cart.class);
                        startActivity(cartIntent);
                        break;
                    case R.id.favoriteFood:
                        Intent intentFavorite = new Intent(All_Restaurants.this, Favorite.class);
                        startActivity(intentFavorite);
                        break;
                    case R.id.nav_Restaurants:
                        Intent intentRestaurants = new Intent(All_Restaurants.this, All_Restaurants.class);
                        startActivity(intentRestaurants);
                        break;
                    case R.id.nav_find_Restaurants:
                        Intent intent = new Intent(All_Restaurants.this, Restaurants.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewR);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));


//        fab_F1.setCount(new Database(this).getCountCart());
        loadListFood();
    }

    private void loadListFood() {

        adapter = new FirebaseRecyclerAdapter<Restaurant_Profile, RestaurantViewHolder>(
                Restaurant_Profile.class,
                R.layout.store_list_row,
                RestaurantViewHolder.class,
                restaurants
        ) {
            @Override
            protected void populateViewHolder(RestaurantViewHolder restaurantViewHolder, final Restaurant_Profile restaurant_profile, final int i) {
                restaurantViewHolder.txtStoreID.setText(adapter.getRef(i).getKey());
                restaurantViewHolder.txtStoreName.setText(restaurant_profile.getRestaurantName());
                restaurantViewHolder.txtStoreDist.setText(restaurant_profile.getRestaurantName());
                restaurantViewHolder.txtStoreAddr.setText(restaurant_profile.getRestaurantAddress());
                restaurantViewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(final View view, int position, boolean isLongClick) {

                        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                        try {

                            Date CurrentTime = dateFormat.parse(dateFormat.format(new Date()));

                            assert CurrentTime != null;

                            if (CurrentTime.before(dateFormat.parse(restaurant_profile.getOpening())) || CurrentTime.after(dateFormat.parse(restaurant_profile.getClosing()))) {
//                        Toast.makeText(FoodDetail.this, "Restaurant is Closed Plz Find Other Restaurant or wait for opening", Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(All_Restaurants.this);
                                alertDialog.setTitle("Warning");
                                alertDialog.setMessage("Restaurant Opening Time "
                                        +restaurant_profile.getOpening()
                                        +" Closing Time "
                                        +restaurant_profile.getClosing()+
                                        " Are you sure, to place Order");
                                alertDialog.setIcon(R.drawable.ic_warning_black_24dp);

                                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Context context = view.getContext();
                                        Intent cartIntent = new Intent(context, AllFoodList.class);
                                        cartIntent.putExtra("id",adapter.getRef(i).getKey());
                                        cartIntent.putExtra("name",restaurant_profile.getRestaurantName());
                                        ((Activity) context).startActivityForResult(cartIntent, 1);
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

                            } else if (CurrentTime.after(dateFormat.parse(restaurant_profile.getOpening())) || CurrentTime.before(dateFormat.parse(restaurant_profile.getClosing()))) {
                                Context context = view.getContext();
                                Intent cartIntent = new Intent(context, AllFoodList.class);
                                cartIntent.putExtra("id",adapter.getRef(i).getKey());
                                cartIntent.putExtra("name",restaurant_profile.getRestaurantName());
                                ((Activity) context).startActivityForResult(cartIntent, 1);
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }







//                        ((Activity) context).finish();
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }
}
