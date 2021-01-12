package example.androidfoodie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
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
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import example.androidfoodie.Database.Database;
import example.androidfoodie.Database.ForDirectOrder;
import example.androidfoodie.Interface.ItemClickListner;
import example.androidfoodie.Model.Category;
import example.androidfoodie.Model.Food;
import example.androidfoodie.Model.Order;
import example.androidfoodie.ViewHolder.FoodViewHolder;

public class FoodList extends AppCompatActivity {

    private TextView timing;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Category currentCategory;
    FirebaseDatabase database;
    DatabaseReference foodList, table_catagory;
//    String categoryIdForTable = "";
    String categoryId = "";
String F="";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //search Functionality
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    CounterFab fab_F;
    //Favorites
    Database localDB;
    List<Order> card = new ArrayList<>();
    //Facebook share
    // CallbackManager callbackManager;
    //ShareDialog shareDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);
        database = FirebaseDatabase.getInstance();
//        timing = (TextView) findViewById(R.id.timing);

        table_catagory = database.getReference("Category");
        //       if (getIntent() != null)
//        categoryIdForTable = getIntent().getStringExtra("CategoryId");

//        if (!categoryIdForTable.isEmpty() && categoryIdForTable != null) {
//
//            loadTiming(categoryIdForTable);
//        }else {
//            Toast.makeText(this, "Error on restaurant time", Toast.LENGTH_SHORT).show();
//        }
        //Firebase
        database = FirebaseDatabase.getInstance();
        foodList = database.getReference("Foods");

//        localDb

        localDB =new Database(this);

        fab_F = (CounterFab) findViewById(R.id.fab_F);
        fab_F.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent cartIntent = new Intent(FoodList.this, Cart.class);
                    startActivity(cartIntent);
                } catch (NullPointerException e) {
                    throw new IllegalStateException("There is Error", e);
                }
            }
        });

        fab_F.setCount(new Database(this).getCountCart());

        BottomNavigationView bottomNavigationView= (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {


                switch (menuItem.getItemId()) {
                    case R.id.nav_cart:
                        Intent cartIntent = new Intent(FoodList.this, Cart.class);
                        startActivity(cartIntent);
                        break;
                    case R.id.favoriteFood:
                        Intent intentFavorite = new Intent(FoodList.this, Favorite.class);
                        startActivity(intentFavorite);
                        break;
                    case R.id.nav_Restaurants:
                        Intent intentRestaurants = new Intent(FoodList.this, All_Restaurants.class);
                        startActivity(intentRestaurants);
                        break;
                    case R.id.nav_find_Restaurants:
                        Intent intent = new Intent(FoodList.this, Restaurants.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }

        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //get intent here
        if (getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");

        if (!categoryId.isEmpty() && categoryId != null) {

            loadListFood(categoryId);
        }

        //search
        materialSearchBar = (MaterialSearchBar) findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your Food");

        //materialSearchBar.setSpeechMode(false);       no need bcz we already define in XML
        loadSuggest();    //write functoin to load suggest from database
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(5);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //when user type their text,  we will change suggest list

                List<String> suggest = new ArrayList<String>();
                for (String search : suggestList) {
                    if (search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }

                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

                //when search bar is close
                //restore original suggest adapter
                if (!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {

                //when search finished
                //show result of search bar
                startSearch(text);

            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

   /* @Override
    protected void onPostResume() {
        super.onPostResume();

        //fix click back on FoodDetail and get no item in Food list
        if(adapter != null)
            adapter.startListening();
    }*/

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("name").equalTo(text.toString())     //compare name
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {

                viewHolder.food_name.setText(model.getName());
                viewHolder.restaurant_timing.setText("Restaurant Timing: "+model.getOpening()+"-"+model.getClosing());
                Picasso.get().load(model.getImage()).into(viewHolder.food_image);
                if(localDB.isFavorite(adapter.getRef(position).getKey()))
                    viewHolder.favorite.setImageResource(R.drawable.ic_favorite_24dp);

                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localDB.isFavorite(adapter.getRef(position).getKey())){
                            new Database(getBaseContext()).addToFavorite(new Order(
                                    F,
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    model.getImage()
                            ));
                            card =new Database(getBaseContext()).getFavorite();
//                            localDB.addToFavorite(adapter.getRef(position).getKey());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_24dp);
                            Toast.makeText(FoodList.this, "Favorite", Toast.LENGTH_SHORT).show();
                        }else {
                            localDB.removeFavorite(adapter.getRef(position).getKey());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_24dp);
                            Toast.makeText(FoodList.this, "Remove", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                final Food local = model;


                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //start new activity
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", searchAdapter.getRef(position).getKey());  //send food id to new activity
                        startActivity(foodDetail);

                    }
                });
            }
        };

        recyclerView.setAdapter(searchAdapter);      //set adapter for recycler view is search result
    }

    private void loadSuggest() {
        foodList.orderByChild("menuId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            Food item = postSnapshot.getValue(Food.class);
                            suggestList.add(item.getName());    // add name of food to suggest list
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

//    private void loadTiming(final String categoryIdForTable) {
//        table_catagory.child(categoryIdForTable).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                currentCategory = dataSnapshot.getValue(Category.class);
//
//                //set Timing
//
//                timing.setText(currentCategory.getTiming());
//                // Toast.makeText(FoodList.this, "timing", Toast.LENGTH_SHORT).show();
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void loadListFood(String categoryId) {

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("menuId").equalTo(categoryId) //like: select * from foods where menu id =
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {

                viewHolder.food_name.setText(model.getName());
                viewHolder.restaurant_timing.setText("Restaurant Timing: "+model.getOpening()+"-"+model.getClosing());
                Picasso.get().load(model.getImage()).into(viewHolder.food_image);
                if(localDB.isFavorite(adapter.getRef(position).getKey()))
                viewHolder.favorite.setImageResource(R.drawable.ic_favorite_24dp);

                viewHolder.favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!localDB.isFavorite(adapter.getRef(position).getKey())){
                            new Database(getBaseContext()).addToFavorite(new Order(
                                    F,
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    model.getImage()
                            ));
                            card =new Database(getBaseContext()).getFavorite();
//                            localDB.addToFavorite(adapter.getRef(position).getKey());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_24dp);
                            Toast.makeText(FoodList.this, "Favorite", Toast.LENGTH_SHORT).show();
                        }else {
                            localDB.removeFavorite(adapter.getRef(position).getKey());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_24dp);
                            Toast.makeText(FoodList.this, "Remove", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                //Quick Cart
//                viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        new Database(getBaseContext()).addToCart(new Order(
//                                adapter.getRef(position).getKey(),
//                                model.getName(),
//                                "1",
//                                model.getPrice(),
//                                model.getDiscount(),
//                                model.getImage()
//
//                        ));
//                        //cart =new Database(getBaseContext()).getCarts();
//                        Toast.makeText(FoodList.this, "Added to Cart", Toast.LENGTH_SHORT).show();
//
//                    }
//                });

                final Food local = model;

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //start new activity
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());  //send food id to new activity
                        startActivity(foodDetail);

                    }
                });
            }
        };

        //set adapter
        recyclerView.setAdapter(adapter);
    }

//    String open= Objects.requireNonNull(dataSnapshot.child("Open").getValue()).toString();
//    String close= Objects.requireNonNull(dataSnapshot.child("Close").getValue()).toString();
//
//    @SuppressLint("SimpleDateFormat")
//    SimpleDateFormat dateFormat=new SimpleDateFormat("HH:mm:ss");
//                                    try {
//        Date opentime=dateFormat.parse(open);
//        Date closetime=dateFormat.parse(close);
//
//        //Toast.makeText(getApplicationContext(), b, Toast.LENGTH_SHORT).show();
//        //Toast.makeText(getApplicationContext(), b, Toast.LENGTH_SHORT).show();
//
//        Calendar calendar=Calendar.getInstance();
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");
//        final String timestamp="Current Time:" +format.format(calendar.getTime());
//        // TimePicker timePicker=format.format(calendar.getTime());
//        // Toast.makeText(getApplicationContext(), timestamp, Toast.LENGTH_SHORT).show();
//
//        if (timestamp.compareTo(String.valueOf(opentime))<=0){
//            Toast.makeText(Restaurants.this, "Restaurant is closed", Toast.LENGTH_SHORT).show();
//        }else {
//            Toast.makeText(Restaurants.this, "Restaurant is Open", Toast.LENGTH_SHORT).show();
//        }
//
//    } catch (ParseException e) {
//        e.printStackTrace();
//    }
}
