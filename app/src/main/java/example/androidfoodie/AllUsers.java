package example.androidfoodie;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AllUsers extends AppCompatActivity {
    Button btnCustomer, btnRestaurant, btnAdmin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        btnCustomer = (Button) findViewById(R.id.btnCustomer);
        btnRestaurant = (Button) findViewById(R.id.btnRestaurant);
        btnAdmin = (Button) findViewById(R.id.btnAdmin);

        btnCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent customerIntent = new Intent(AllUsers.this, MainActivity.class);
                startActivity(customerIntent);
            }
        });
        btnAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent adminIntent = new Intent(AllUsers.this, AdminPanel.class);
                startActivity(adminIntent);
            }
        });
        btnRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent restaurantIntent = new Intent(AllUsers.this, RestaurantPenal.class);
                startActivity(restaurantIntent);
            }
        });
    }
}