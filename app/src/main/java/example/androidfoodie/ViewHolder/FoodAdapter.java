package example.androidfoodie.ViewHolder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import example.androidfoodie.Cart;
import example.androidfoodie.Database.Database;
import example.androidfoodie.Favorite;
import example.androidfoodie.FoodDetail;
import example.androidfoodie.FoodList;
import example.androidfoodie.Interface.ItemClickListner;
import example.androidfoodie.Model.Order;
import example.androidfoodie.R;


public class FoodAdapter extends RecyclerView.Adapter<FoodViewHolder> {
    FoodAdapter adapter;
Database database;
String F="";
    private List<Order> listData = new ArrayList<>();
    private Favorite foodFav;
    public FoodAdapter(List<Order> listData, Favorite foodFav) {
        this.listData = listData;
        this.foodFav = foodFav;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(foodFav);
        View itemView = inflater.inflate(R.layout.food_item, parent, false);
        return new FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final FoodViewHolder holder, final int position) {
//        Toast.makeText(foodFav, ""+position, Toast.LENGTH_SHORT).show();
        try {
            Picasso.get().load(listData.get(position).getImage()).into(holder.food_image);
            holder.food_name.setText(listData.get(position).getProductName());
//            if(database.isFavorite(listData.get(position).getProductId()))
                holder.favorite.setImageResource(R.drawable.ic_favorite_24dp);
            holder.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   database= new Database(foodFav);
                    if(!database.isFavorite(listData.get(position).getProductId())){
                        new Database(foodFav).addToFavorite(new Order(
                                F,
                                listData.get(position).getProductId(),
                                listData.get(position).getProductName(),
                                listData.get(position).getImage()
                        ));
                        listData =new Database(foodFav).getFavorite();
//                            localDB.addToFavorite(adapter.getRef(position).getKey());
                        holder.favorite.setImageResource(R.drawable.ic_favorite_24dp);
//                        Toast.makeText(Favorite.this, "Favorite"+adapter.getRef(position).getKey(), Toast.LENGTH_SHORT).show();
                    }else {
                        database.removeFavorite(listData.get(position).getProductId());
                        holder.favorite.setImageResource(R.drawable.ic_favorite_border_24dp);
//                        Toast.makeText(FoodList.this, "Remove", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }catch (Exception ex){
            Toast.makeText(foodFav, ""+ex, Toast.LENGTH_SHORT).show();
        }
        holder.setItemClickListner(new ItemClickListner() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Context context=view.getContext();
                Intent foodDetail = new Intent(context, FoodDetail.class);
                foodDetail.putExtra("FoodId", listData.get(position).getProductId());  //send food id to new activity
                ((Activity) context).startActivityForResult(foodDetail,1);
                ((Activity)context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}