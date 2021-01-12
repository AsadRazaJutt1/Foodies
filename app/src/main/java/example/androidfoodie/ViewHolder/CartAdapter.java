package example.androidfoodie.ViewHolder;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import example.androidfoodie.Cart;
import example.androidfoodie.Database.Database;
import example.androidfoodie.Interface.ItemClickListner;
import example.androidfoodie.Model.Order;
import example.androidfoodie.R;

import static java.security.AccessController.getContext;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView txt_cart_name, txt_price, cart_Total_price;
    public ElegantNumberButton btn_quantity;
    public Button btn_Remove;
    public ImageView cart_image;


    private ItemClickListner itemClickListner;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(View itemView) {
        super(itemView);
        txt_cart_name = (TextView) itemView.findViewById(R.id.cart_item_name);
        txt_price = (TextView) itemView.findViewById(R.id.cart_item_price);
        cart_Total_price = (TextView) itemView.findViewById(R.id.cart_Total_price);
        btn_quantity = (ElegantNumberButton) itemView.findViewById(R.id.btn_quantity);
        cart_image = (ImageView) itemView.findViewById(R.id.cart_image);
        btn_Remove = (Button) itemView.findViewById(R.id.btn_Remove);
    }

    @Override
    public void onClick(View view) {

    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {
CartAdapter adapter;
    Locale locale = new Locale("en", "US");
    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

    private List<Order> listData = new ArrayList<>();

    private Cart cart;

    public CartAdapter(List<Order> listData, Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartViewHolder holder, final int position) {

        Picasso.get().load(listData.get(position).getImage()).resize(70, 70)
                .centerCrop().into(holder.cart_image);

       /* TextDrawable drawable = TextDrawable.builder()
                .buildRound(""+listData.get(position).getQuantity(), Color.RED);
        holder.img_cart_count.setImageDrawable(drawable);*/

        holder.btn_quantity.setNumber(listData.get(position).getQuantity());
        holder.cart_Total_price.setText("Total Price: " + fmt.format(Integer.parseInt(listData.get(position).getPrice())));
        holder.btn_Remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Order order = listData.get(position);
//                order.getProductId();
//                order.getProductName();
//                order.getQuantity();
//                order.getDiscount();
//                order.getPrice();
//                order.getImage();
//
                new Database(cart).remove(order);
//                removeItem(position);
                int total = 0;
                List<Order> orders = new Database(cart).getCarts();
                for (Order item : orders)
                    total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));
//                String a= String.valueOf(order.getID());
//                Toast.makeText(cart, ""+position, Toast.LENGTH_SHORT).show();
//
                cart.txtTotalPrice.setText(fmt.format(total));
//                Intent i =new Intent(Applic,Cart.class);
//                ((Cart)context).resetGraph(context);
//                v = ((Activity)getContext()).findViewById(R.id.btn_Remove);
//                adapter.notifyDataSetChanged();
                Context context=v.getContext();
                Intent intent=new Intent(context,Cart.class);
                ((Activity) context).startActivityForResult(intent,1);
                ((Activity)context).finish();
            }
        });
        holder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {

                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));

                new Database(cart).updateCart(order);

                int total_1 = 0;
                List<Order> orders_1 = new Database(cart).getCarts();
                for (Order item : orders_1)
                    total_1 += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

//               Locale locale  = new Locale("en","US");
//               NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                cart.txtTotalPrice.setText(fmt.format(total_1));
                int price_1 = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));

                holder.cart_Total_price.setText("Total Price: " + fmt.format(price_1));
                //for calculate total price
//               int total = 0;
//               for(Order :cart)
//                   total += (Integer.parseInt(order.getPrice()))*(Integer.parseInt(order.getQuantity()));

//               Locale locale  = new Locale("en","US");
//               NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

//               cart.txtTotalPrice.setText(fmt.format(total));
                //Update total price
                //for calculate total price
                int total = 0;
                List<Order> orders = new Database(cart).getCarts();
                for (Order item : orders)
                    total += (Integer.parseInt(item.getPrice())) * (Integer.parseInt(item.getQuantity()));

//               Locale locale  = new Locale("en","US");
//               NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                cart.txtTotalPrice.setText(fmt.format(total));
                int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));

                holder.cart_Total_price.setText("Total Price: " + fmt.format(price));
            }
        });

//        Locale locale  = new Locale("en","US");
//        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        holder.txt_price.setText(fmt.format(Integer.parseInt(listData.get(position).getPrice())));
//        holder.cart_Total_price.setText("Total Price: $"+fmt.format(price));
        holder.txt_cart_name.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Order getItem(int Position) {
        return listData.get(Position);
    }

    public void removeItem(int Position) {
        listData.remove(Position);
        notifyItemChanged(Position);
    }

    public void reStoreItem(Order item, int Position) {
        listData.add(Position, item);
        notifyItemInserted(Position);
    }
//    public  void  onRestart(){
//        Intent i = new Intent(, lala.class);  //your class
//        startActivity(i);
//        finish();
//    }

}
