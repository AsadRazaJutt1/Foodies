package example.androidfoodie.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import example.androidfoodie.Model.Order;

public class ForDirectOrder extends SQLiteAssetHelper {

    private static final String DB_Name_ = "AndroidDB.db";
    private static final int DB_VER_ = 1;

    public ForDirectOrder(Context context1) {
        super(context1, DB_Name_, null, DB_VER_);
    }

    public List<Order> getCart() {
        SQLiteDatabase dbD = getReadableDatabase();
        SQLiteQueryBuilder qbD = new SQLiteQueryBuilder();

        String[] sqlDSelect = {"ID", "ProductName", "ProductId", "Quantity", "Price", "Discount", "Image", "restaurantId"};
        String sqlDTable = "OrderDetail";


        qbD.setTables(sqlDTable);
        Cursor cD = qbD.query(dbD, sqlDSelect, null, null, null, null, null);

        final List<Order> result = new ArrayList<>();

        if (cD.moveToFirst()) {
            do {
                result.add(new Order(
                        cD.getInt(cD.getColumnIndex("ID")),
                        cD.getString(cD.getColumnIndex("ProductId")),
                        cD.getString(cD.getColumnIndex("ProductName")),
                        cD.getString(cD.getColumnIndex("Quantity")),
                        cD.getString(cD.getColumnIndex("Price")),
                        cD.getString(cD.getColumnIndex("Discount")),
                        cD.getString(cD.getColumnIndex("Image")),
                        cD.getString(cD.getColumnIndex("restaurantId"))
                ));

            } while (cD.moveToNext());
        }

        return result;
    }

    public void addToC(Order order) {
        SQLiteDatabase dbD = getReadableDatabase();
        String queryD = String.format("INSERT INTO OrderDetail(ProductId, ProductName, Quantity, Price, Discount, Image, restaurantId)VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage(),
                order.getRestaurantId());
        dbD.execSQL(queryD);
    }

//    public void addToCart(Order order) {
//        SQLiteDatabase db = getReadableDatabase();
//        String query = String.format("INSERT INTO OrderDetail(ProductId, ProductName, Quantity, Price, Discount, Image, restaurant_name)VALUES('%s', '%s', '%s', '%s', '%s', '%s', '%s');",
//                order.getProductId(),
//                order.getProductName(),
//                order.getQuantity(),
//                order.getPrice(),
//                order.getDiscount(),
//                order.getRestaurant_name()
//
//
//                db.execSQL(query);
//    }

    public void cleanC() {
        SQLiteDatabase dbD = getReadableDatabase();
        String queryD = String.format("DELETE FROM OrderDetail");

        dbD.execSQL(queryD);
    }
//    public int getCountCart() {
//        int count = 0;
//        SQLiteDatabase db = getReadableDatabase();
//        String query = String.format("SELECT COUNT(*) FROM OrderDetail");
//        Cursor cursor = db.rawQuery(query, null);
//
//        if(cursor.moveToFirst())
//        {
//            do{
//                count = cursor.getInt(0);
//            }while (cursor.moveToNext());
//        }
//        return count;
//    }
//
//    public void updateCart(Order order) {
//        SQLiteDatabase db = getReadableDatabase();
//        String query = String.format("UPDATE OrderDetail SET Quantity= %s WHERE ID = %d", order.getQuantity(),order.getID());
//
//        db.execSQL(query);
//    }

}
