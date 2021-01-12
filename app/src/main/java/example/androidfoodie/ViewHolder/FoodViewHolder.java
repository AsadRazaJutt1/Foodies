package example.androidfoodie.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import example.androidfoodie.Interface.ItemClickListner;
import example.androidfoodie.R;

public class FoodViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView food_name;
    public ImageView food_image, favorite;
    public TextView restaurant_timing;
    private ItemClickListner itemClickListner;

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    public FoodViewHolder(@NonNull View itemView) {
        super(itemView);

        food_name = (TextView)itemView.findViewById(R.id.food_name);
        food_image = (ImageView)itemView.findViewById(R.id.food_image);
        restaurant_timing = (TextView)itemView.findViewById(R.id.restaurant_timing);
        favorite = (ImageView) itemView.findViewById(R.id.favorite);
        itemView.setOnClickListener(this);
//        quick_cart = (ImageView)itemView.findViewById(R.id.btn_quick_cart);

    }

    @Override
    public void onClick(View view) {

        itemClickListner.onClick(view, getAdapterPosition(),false);
    }
}
