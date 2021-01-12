package example.androidfoodie.ViewHolder;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import example.androidfoodie.Interface.ItemClickListner;
import example.androidfoodie.R;


public class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txtMenuName,timing;
    public ImageView imageView;

    private ItemClickListner itemClickListner;

    public MenuViewHolder(@NonNull View itemView) {
        super(itemView);

        txtMenuName = (TextView)itemView.findViewById(R.id.menu_name);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image);
//        timing=(TextView)itemView.findViewById(R.id.timing);

        itemView.setOnClickListener(this);
    }

    public ItemClickListner getItemClickListner() {
        return itemClickListner;
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }


    @Override
    public void onClick(View view) {

        itemClickListner.onClick(view, getAdapterPosition(),false);
    }
}
