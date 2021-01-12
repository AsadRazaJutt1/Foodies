package example.androidfoodie;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.andremion.counterfab.CounterFab;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import example.androidfoodie.Common.Common;
import example.androidfoodie.Database.Database;
import example.androidfoodie.Database.ForDirectOrder;
import example.androidfoodie.Interface.ItemClickListner;
import example.androidfoodie.Model.Category;
import example.androidfoodie.Model.Complaint;
import example.androidfoodie.Model.Food;
import example.androidfoodie.Model.Order;
import example.androidfoodie.Model.Request;
import example.androidfoodie.Model.Restaurant_Profile;
import example.androidfoodie.Model.Suggestion;
import example.androidfoodie.Model.User;
import example.androidfoodie.Services.OrderListener;
import example.androidfoodie.ViewHolder.FoodViewHolder;
import example.androidfoodie.ViewHolder.MenuViewHolder;


//import info.hoang8f.widget.FButton;


public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        , AdapterView.OnItemSelectedListener,BottomNavigationView.OnNavigationItemSelectedListener {

    //    private AppBarConfiguration mAppBarConfiguration;
//    final DatabaseReference table_admin = database.getReference("");
    //ClipData.Item nav_log_out;
    MenuItem nav_log_out, nav_sign_in;
    DrawerLayout drawer_layout;
    ActionBarDrawerToggle mToggle;

    private Menu menu;

    FirebaseDatabase database;
    DatabaseReference category, foodList, restaurant;
    DatabaseReference complaints, suggestions;
    TextView txtFullName;
    RecyclerView recycler_menu, recycler_main_Food;
    private static final String[] paths = {"item 1", "item 2", "item 3"};
//    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category, MenuViewHolder> adapter;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapterF;

    CounterFab fab;

    TextView titleForComplain, orderNumber, complainMsg, complainPhone, titleForSuggestion, suggestionMsg, suggestionPhone;
    Button allFood, findRestaurants,restaurantAll;
    Database localDB;
    List<Order> card = new ArrayList<>();
    String F = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle("Menu");

        setSupportActionBar(toolbar);

        //init Firebase

        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");
        complaints = database.getReference("Complaints");
        suggestions = database.getReference("Suggestions");
        foodList = database.getReference("Foods");
        restaurant = database.getReference("Requests");

        new ForDirectOrder(getBaseContext()).cleanC();




        BottomNavigationView bottomNavigationView= (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                switch (menuItem.getItemId()) {
                    case R.id.nav_cart:
                        Intent cartIntent = new Intent(Home.this, Cart.class);
                        startActivity(cartIntent);
                        break;
                    case R.id.favoriteFood:
                        Intent intentFavorite = new Intent(Home.this, Favorite.class);
                        startActivity(intentFavorite);
                        break;
                    case R.id.nav_Restaurants:
                        Intent intentRestaurants = new Intent(Home.this, All_Restaurants.class);
                        startActivity(intentRestaurants);
                        break;
                    case R.id.nav_find_Restaurants:
                        Intent intent = new Intent(Home.this, Restaurants.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }

        });



        localDB = new Database(this);

        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent cartIntent = new Intent(Home.this, Cart.class);
                    startActivity(cartIntent);
                } catch (NullPointerException e) {
                    throw new IllegalStateException("There is Error", e);
                }
            }
        });

        fab.setCount(new Database(this).getCountCart());

        //fix click back btn from food and don't see category
      /*  if(adapter != null)
            adapter.startListening();*/

        drawer_layout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.open, R.string.close);
        drawer_layout.addDrawerListener(mToggle);
        mToggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        menu = navigationView.getMenu();
        nav_log_out = menu.findItem(R.id.nav_log_out);
        nav_sign_in = menu.findItem(R.id.nav_sign_in);
        if (Common.currentUser == null && Common.IMEI != null) {
            //set Name for user
//            Toast.makeText(this, "You are Guest", Toast.LENGTH_SHORT).show();
            nav_log_out.setVisible(false);
            nav_sign_in.setVisible(true);
            View headerView = navigationView.getHeaderView(0);
//            txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
//            txtFullName.setText(Common.currentUser.getName());
        } else {

//            Toast.makeText(this, "You are User", Toast.LENGTH_SHORT).show();
            nav_log_out.setVisible(true);
            nav_sign_in.setVisible(false);
            View headerView = navigationView.getHeaderView(0);
            txtFullName = (TextView) headerView.findViewById(R.id.txtFullName);
            txtFullName.setText(Common.currentUser.getName());
        }
        //load menu
        recycler_menu = (RecyclerView) findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        //layoutManager = new LinearLayoutManager(this);
        //        //recycler_menu.setLayoutManager(layoutManager);
//        recycler_menu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recycler_menu.setLayoutManager(new GridLayoutManager(this, 3, RecyclerView.VERTICAL, false));
        recycler_menu.setNestedScrollingEnabled(false);
        loadMenu();

        if (Common.currentUser == null && Common.IMEI != null) {
            Toast.makeText(this, "Guest", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(Home.this, OrderListener.class);
            startService(intent);

        }
    }

    private void loadMainFood() {
        adapterF = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList //like: select * from foods where menu id =
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {

                viewHolder.food_name.setText(model.getName());
                viewHolder.restaurant_timing.setText(model.getTiming());
                Picasso.get().load(model.getImage()).into(viewHolder.food_image);
                if (localDB.isFavorite(adapter.getRef(position).getKey()))
                    viewHolder.favorite.setImageResource(R.drawable.ic_favorite_24dp);

                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!localDB.isFavorite(adapter.getRef(position).getKey())) {
                            new Database(getBaseContext()).addToFavorite(new Order(
                                    F,
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    model.getImage()
                            ));
                            card = new Database(getBaseContext()).getFavorite();
//                            localDB.addToFavorite(adapter.getRef(position).getKey());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_24dp);
                            Toast.makeText(Home.this, "Favorite" + adapter.getRef(position).getKey(), Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFavorite(adapter.getRef(position).getKey());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_24dp);
                            Toast.makeText(Home.this, "Remove", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                final Food local = model;

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //start new activity
                        Intent foodDetail = new Intent(Home.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());  //send food id to new activity
                        startActivity(foodDetail);

                    }
                });
            }
        };

        //set adapter
        recycler_main_Food.setAdapter(adapter);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        fab.setCount(new Database(this).getCountCart());
    }

    @Override
    public void onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START);
        } else {
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.exit, null);
            Button button = view.findViewById(R.id.btnexit);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finishAffinity();
                }
            });
            AlertDialog.Builder alertDialogBox = new AlertDialog.Builder(this);
            alertDialogBox.setMessage("Are you sure you want to exit from application?");
            alertDialogBox.setView(view);

            alertDialogBox.show();
        }
    }

    private void loadMenu() {
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item
                , MenuViewHolder.class, category) {

            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {

                viewHolder.txtMenuName.setText(model.getName());
                //viewHolder.timing.setText(model.getTimeing());
                //Picasso.get().load(model.getTimeing());
                Picasso.get().load(model.getImage()).into(viewHolder.imageView);
                final Category clickItem = model;

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //get category id and send to new Activity
                        Intent foodList = new Intent(Home.this, FoodList.class);


                        //because category id is key, so we get key of this item
                        foodList.putExtra("CategoryId", adapter.getRef(position).getKey());
                        startActivity(foodList);

                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    //@override
    public boolean onOptionsItemSelected(MenuItem item) {

        // if(item.getItemId()==R.id.menu_search)
        //startActivity(new Intent(Home.this, SearchActivity.class));

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("Statement with Empty body")

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference table_user = database.getReference("User");
        int id = menuItem.getItemId();

        if (id == R.id.nav_menu) {
            Intent cartIntent = new Intent(Home.this, AllFoodList.class);
            startActivity(cartIntent);
        } else if (id == R.id.nav_cart) {

            Intent cartIntent = new Intent(Home.this, Cart.class);
            startActivity(cartIntent);

        } else if (id == R.id.nav_order) {

            Intent orderIntent = new Intent(Home.this, OrderStatus.class);
            startActivity(orderIntent);

        } else if (id == R.id.nav_log_out) {


            Intent signOut = new Intent(Home.this, MainActivity.class);
            signOut.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(signOut);
            finish();
            //logout
//                Intent signIn = new Intent(Home.this, SignIn.class);
//                signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                startActivity(signIn);

        } else if (id == R.id.nav_sign_in) {
            Intent signIn = new Intent(getApplicationContext(), SignIn.class);
            startActivity(signIn);
            finish();
        } else if (id == R.id.complain) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
            alertDialog.setTitle("Complain");
            alertDialog.setMessage("fill the information");
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.complain, null);

            titleForComplain = (TextView) view.findViewById(R.id.titleForComplain);
            orderNumber = (TextView) view.findViewById(R.id.orderNumber);
            complainMsg = (TextView) view.findViewById(R.id.complainMsg);
            complainPhone = (TextView) view.findViewById(R.id.complainPhone);

//            cancelComplaint = (Button) view.findViewById(R.id.cancelComplaint);
//            submitComplaint = (Button) view.findViewById(R.id.submitComplaint);
            if (Common.IMEI != null && Common.currentUser == null) {
                complainPhone.setVisibility(View.VISIBLE);
            }


//            submitComplaint.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    Toast.makeText(Home.this, "Click", Toast.LENGTH_SHORT).show();
//
//
//                }
//            });
            alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Common.currentUser == null) {
                        restaurant.child(String.valueOf(orderNumber)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(orderNumber.getText().toString()).exists()) {
                                    Request profile = dataSnapshot.child(orderNumber.getText().toString()).getValue(Request.class);

                                    Complaint complaint = new Complaint(titleForComplain.getText().toString(), complainMsg.getText().toString(), complainPhone.getText().toString(),profile.getRestaurant());
                                    complaints.child(orderNumber.getText().toString()).setValue(complaint);
                                }else {
                                    Toast.makeText(Home.this, "Wrong Order Number", Toast.LENGTH_SHORT).show();
                                }
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        dialog.dismiss();
                    } else {
                        restaurant.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(orderNumber.getText().toString()).exists()) {
                                    Request profile = dataSnapshot.child(orderNumber.getText().toString()).getValue(Request.class);
                                    Toast.makeText(Home.this, ""+profile.getRestaurant(), Toast.LENGTH_SHORT).show();
                                    Complaint complaint = new Complaint(titleForComplain.getText().toString(), complainMsg.getText().toString(), Common.currentUser.getPhone(), profile.getRestaurant());
                                    complaints.child(orderNumber.getText().toString()).setValue(complaint);

                                }else {
                                    Toast.makeText(Home.this, "Wrong Order Number", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        dialog.dismiss();
                    }
                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.setView(view);    // add edit text to alert dialog

            alertDialog.show();
        } else if (id == R.id.suggestion) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
            alertDialog.setTitle("Suggestion");
            alertDialog.setMessage("fill the information");
            LayoutInflater layoutInflater = getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.suggestion, null);

            titleForSuggestion = (TextView) view.findViewById(R.id.titleForSuggestion);
            suggestionPhone = (TextView) view.findViewById(R.id.suggestionPhone);
            suggestionMsg = (TextView) view.findViewById(R.id.suggestionMsg);
            if (Common.IMEI != null) {
                suggestionPhone.setVisibility(View.VISIBLE);
            }
            alertDialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Common.IMEI != null) {
                        Suggestion suggestion = new Suggestion(titleForSuggestion.getText().toString(), suggestionMsg.getText().toString(), suggestionPhone.getText().toString());
                        suggestions.child(String.valueOf(System.currentTimeMillis())).setValue(suggestion);
                        dialog.dismiss();
                    } else {
                        Suggestion suggestion = new Suggestion(titleForSuggestion.getText().toString(), suggestionMsg.getText().toString(), Common.currentUser.getPhone());
                        suggestions.child(String.valueOf(System.currentTimeMillis())).setValue(suggestion);
                        dialog.dismiss();
                    }
                }
            });
            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

//            Spinner spinner1;
//         static final String[] paths = {"item 1", "item 2", "item 3"};


////            spinner1 = (Spinner) view.findViewById(R.id.spiner1);
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Home.this,
//                    android.R.layout.simple_spinner_item, paths);
//
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            spinner1.setAdapter(adapter);
//            spinner1.setOnItemSelectedListener(this);

//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position,
//            long id) {
//                // TODO Auto-generated method stub
//                Toast.makeText(this, "YOUR SELECTION IS : " + parent.getItemAtPosition(position).toString(), Toast.LENGTH_SHORT).show();
//
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // TODO Auto-generated method stub
//
//            }


            alertDialog.setView(view);    // add edit text to alert dialog
            alertDialog.show();
        } else if (id == R.id.favoriteFood) {
            Intent orderIntentF = new Intent(Home.this, Favorite.class);
            startActivity(orderIntentF);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

//    public void allFoods(View view) {
//        Intent intent=new Intent(Home.this,AllFoodList.class);
//        startActivity(intent);
//    }
}
