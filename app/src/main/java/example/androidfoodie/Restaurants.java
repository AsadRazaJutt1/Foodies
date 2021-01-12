package example.androidfoodie;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Restaurants extends TabActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);

        Resources resources = getResources();
        TabHost tabHost = getTabHost();

        // Android tab
        Intent intentMaps = new Intent().setClass(this, MapsActivity.class);
        TabSpec tabSpecMap = tabHost
                .newTabSpec("Map View")
                .setIndicator("", resources.getDrawable(R.drawable.baseline_near_me_black_18dp))
                .setContent(intentMaps);

        // Apple tab
        Intent intentList = new Intent().setClass(this, Cafes.class);
        TabSpec tabSpecList = tabHost
                .newTabSpec("List View")
                .setIndicator("", resources.getDrawable(R.drawable.baseline_local_dining_black_18dp))
                .setContent(intentList);

        // add all tabs
        tabHost.addTab(tabSpecMap);
        tabHost.addTab(tabSpecList);

        //set Windows tab as default (zero based)
        tabHost.setCurrentTab(2);
    }

}