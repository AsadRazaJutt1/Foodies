package example.androidfoodie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import example.androidfoodie.Database.Database;
import example.androidfoodie.Interface.ItemClickListner;
import example.androidfoodie.Model.Category;
import example.androidfoodie.Model.Food;
import example.androidfoodie.Model.Order;
import example.androidfoodie.ViewHolder.FoodViewHolder;

public class AllFoodList extends AppCompatActivity {
    private TextView timing;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    Category currentCategory;
    FirebaseDatabase database;
    DatabaseReference foodList, table_catagory;
    //    String categoryIdForTable = "";
    String categoryId = "";
    String F = "";
    String id = "";
    String name = "";
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //search Functionality
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;
    CounterFab fab_F2;
    //Favorites
    Database localDB;
    List<Order> card = new ArrayList<>();
    Button lahore, islamabad, faisalabad, karachi;

    //Facebook share
    // CallbackManager callbackManager;
    //ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_food_list);

        database = FirebaseDatabase.getInstance();
//        timing = (TextView) findViewById(R.id.timing);

//                table_catagory = database.getReference("Category");
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

        // filter
        lahore = (Button) findViewById(R.id.lahore);
        islamabad = (Button) findViewById(R.id.islamabad);
        faisalabad = (Button) findViewById(R.id.faisalabad);
        karachi = (Button) findViewById(R.id.karachi);


//        else {
            lahore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadListFood("Lahore");
                    lahore.setBackgroundColor(Color.parseColor("#0000FF"));
                    islamabad.setBackgroundColor(Color.parseColor("#00000000"));
                    faisalabad.setBackgroundColor(Color.parseColor("#00000000"));
                    karachi.setBackgroundColor(Color.parseColor("#00000000"));
                }
            });
            islamabad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadListFood("Lahore");
                    lahore.setBackgroundColor(Color.parseColor("#00000000"));
                    islamabad.setBackgroundColor(Color.parseColor("#0000FF"));
                    faisalabad.setBackgroundColor(Color.parseColor("#00000000"));
                    karachi.setBackgroundColor(Color.parseColor("#00000000"));
                }
            });
            faisalabad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadListFood("Karachi");
                    lahore.setBackgroundColor(Color.parseColor("#00000000"));
                    islamabad.setBackgroundColor(Color.parseColor("#00000000"));
                    faisalabad.setBackgroundColor(Color.parseColor("#0000FF"));
                    karachi.setBackgroundColor(Color.parseColor("#00000000"));
                }
            });
            karachi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadListFood("Karachi");
                    lahore.setBackgroundColor(Color.parseColor("#00000000"));
                    islamabad.setBackgroundColor(Color.parseColor("#00000000"));
                    faisalabad.setBackgroundColor(Color.parseColor("#00000000"));
                    karachi.setBackgroundColor(Color.parseColor("#0000FF"));
                }
            });
//        }


//        localDb

        localDB = new Database(this);

        fab_F2 = (CounterFab) findViewById(R.id.fab_F2);
        fab_F2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent cartIntent1 = new Intent(AllFoodList.this, Cart.class);
                    startActivity(cartIntent1);
                } catch (NullPointerException e) {
                    throw new IllegalStateException("There is Error", e);
                }
            }
        });

        fab_F2.setCount(new Database(this).getCountCart());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        //get intent here
        if (getIntent().getStringExtra("id") != null && getIntent().getStringExtra("name") != null) {
            id = getIntent().getStringExtra("id");
            name = getIntent().getStringExtra("name");
            if (!id.isEmpty() && id != null) {
                lahore.setText(name);
                islamabad.setVisibility(View.INVISIBLE);
                faisalabad.setVisibility(View.INVISIBLE);
                karachi.setVisibility(View.INVISIBLE);
                Toast.makeText(this, ""+id, Toast.LENGTH_SHORT).show();
                loadListFoodById(id);
            }
        }
//                if (getIntent() != null)
//                    categoryId = getIntent().getStringExtra("CategoryId");
//
//                if (!categoryId.isEmpty() && categoryId != null) {


//                }

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
                viewHolder.restaurant_timing.setText("Restaurant Timing: "+model.getTiming());
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
                            Toast.makeText(AllFoodList.this, "Favorite", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFavorite(adapter.getRef(position).getKey());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_24dp);
                            Toast.makeText(AllFoodList.this, "Remove", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                final Food local = model;


                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        //start new activity
                        Intent foodDetail = new Intent(AllFoodList.this, FoodDetail.class);
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

    private void loadListFood() {

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList //like: select * from foods where menu id =
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {

                viewHolder.food_name.setText(model.getName());
                viewHolder.restaurant_timing.setText("Restaurant Timing: "+model.getTiming());
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
                            Toast.makeText(AllFoodList.this, "Favorite", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFavorite(adapter.getRef(position).getKey());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_24dp);
                            Toast.makeText(AllFoodList.this, "Remove", Toast.LENGTH_SHORT).show();
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
                        Intent foodDetail = new Intent(AllFoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());  //send food id to new activity
                        startActivity(foodDetail);

                    }
                });
            }
        };

        //set adapter
        recyclerView.setAdapter(adapter);
    }
    private void loadListFoodById(String id) {

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("restaurantId").equalTo(id) //like: select * from foods where menu id =
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {

                viewHolder.food_name.setText(model.getName());
                viewHolder.restaurant_timing.setText("Restaurant Timing: "+model.getOpening()+"-"+model.getClosing());
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
                            Toast.makeText(AllFoodList.this, "Favorite", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFavorite(adapter.getRef(position).getKey());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_24dp);
                            Toast.makeText(AllFoodList.this, "Remove", Toast.LENGTH_SHORT).show();
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
                        Intent foodDetail = new Intent(AllFoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());  //send food id to new activity
                        startActivity(foodDetail);

                    }
                });
            }
        };

        //set adapter
        recyclerView.setAdapter(adapter);
    }

    private void loadListFood(String city) {

        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("city").equalTo(city) //like: select * from foods where menu id =
        ) {
            @Override
            protected void populateViewHolder(final FoodViewHolder viewHolder, final Food model, final int position) {

                viewHolder.food_name.setText(model.getName());
                viewHolder.restaurant_timing.setText("Restaurant Timing: "+model.getOpening()+"-"+model.getClosing());
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
                            Toast.makeText(AllFoodList.this, "Favorite", Toast.LENGTH_SHORT).show();
                        } else {
                            localDB.removeFavorite(adapter.getRef(position).getKey());
                            viewHolder.favorite.setImageResource(R.drawable.ic_favorite_border_24dp);
                            Toast.makeText(AllFoodList.this, "Remove", Toast.LENGTH_SHORT).show();
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
                        Intent foodDetail = new Intent(AllFoodList.this, FoodDetail.class);
                        foodDetail.putExtra("FoodId", adapter.getRef(position).getKey());  //send food id to new activity
                        startActivity(foodDetail);

                    }
                });
            }
        };

        //set adapter
        recyclerView.setAdapter(adapter);
    }

}



