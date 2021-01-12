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

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView order_id, order_status, order_phone, order_address, requestTotal;
    public ImageView dotsForMenu;
    private ItemClickListner itemClickListner;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        order_id = (TextView) itemView.findViewById(R.id.order_id);
        order_status = (TextView) itemView.findViewById(R.id.order_status);
        order_phone = (TextView) itemView.findViewById(R.id.order_phone);
        order_address = (TextView) itemView.findViewById(R.id.order_address);
        requestTotal = (TextView) itemView.findViewById(R.id.requestTotal);
        dotsForMenu = (ImageView) itemView.findViewById(R.id.dotsForMenu);
        itemView.setOnCreateContextMenuListener(this);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListner(ItemClickListner itemClickListner) {
        this.itemClickListner = itemClickListner;
    }

    @Override
    public void onClick(View view) {

        itemClickListner.onClick(view, getAdapterPosition(), false);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        dotsForMenu.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.add(0, 0, getAdapterPosition(), Common.UPDATE);
//                menu.add(0, 1, getAdapterPosition(), Common.DELETE);
            }
        });
    }
}
