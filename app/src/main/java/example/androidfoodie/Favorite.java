package example.androidfoodie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.database.Query;
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
import example.androidfoodie.ViewHolder.CartAdapter;
import example.androidfoodie.ViewHolder.FoodAdapter;
import example.androidfoodie.ViewHolder.FoodViewHolder;

public class Favorite extends AppCompatActivity {
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txtTotalPrice;
    Button btnPlace;

    List<Order> foodLocal = new ArrayList<>();
    FoodAdapter adapter;
    //    static TextView tv_time;
    static EditText phoneNo;
    EditText edtAddress;
    CounterFab fab_F1;
    Database localDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
//        database = FirebaseDatabase.getInstance();
//        database = FirebaseDatabase.getInstance();
//        requests = database.getReference("Foods");

        fab_F1 = (CounterFab) findViewById(R.id.fab_F1);

        localDB = new Database(this);

        fab_F1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent cartIntent = new Intent(Favorite.this, Cart.class);
                    startActivity(cartIntent);
                } catch (NullPointerException e) {
                    throw new IllegalStateException("There is Error", e);
                }
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_food1);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));


        fab_F1.setCount(new Database(this).getCountCart());
        loadListFood();
//
    }

    private void loadListFood() {
        foodLocal = new Database(this).getFavorite();
        adapter = new FoodAdapter(foodLocal, this);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}