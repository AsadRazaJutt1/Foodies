package example.androidfoodie.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import example.androidfoodie.Common.Common;
import example.androidfoodie.Interface.ItemClickListner;
import example.androidfoodie.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
   public TextView txtStoreName;
   public TextView txtStoreAddr;
   public TextView txtStoreDist, txtStoreID;
    private ItemClickListner itemClickListner;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);

        txtStoreName = (TextView) itemView.findViewById(R.id.txtStoreName);
        txtStoreAddr = (TextView) itemView.findViewById(R.id.txtStoreAddr);
        txtStoreDist = (TextView) itemView.findViewById(R.id.txtStoreDist);
        txtStoreID = (TextView) itemView.findViewById(R.id.txtStoreID);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View view) {

        itemClickListner.onClick(view, getAdapterPosition(), false);

    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        dotsForMenu.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                menu.add(0, 0, getAdapterPosition(), Common.UPDATE);
////                menu.add(0, 1, getAdapterPosition(), Common.DELETE);
//            }
//        });
//    }
}
