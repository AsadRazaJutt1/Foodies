package example.androidfoodie.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchData {
    // static variable single_instance of type Singleton
    private static SearchData single_instance = null;

    private List<StoreModel> storeModels;
    public List<PlacesPOJO.CustomA> results;

    // private constructor restricted to this class itself
    private SearchData() {
        this.storeModels = new ArrayList<>();
        this.results = new ArrayList<>();

    }

    // static method to create instance of Singleton class
    public static SearchData getInstance() {
        if (single_instance == null)
            single_instance = new SearchData();

        return single_instance;
    }

    public void setStores(List<StoreModel> list) {
        Collections.sort(list, new Comparator<StoreModel>() {
            @Override
            public int compare(final StoreModel lhs, StoreModel rhs) {
                double ratingOne = Double.parseDouble(lhs.rating);
                double ratingTwo = Double.parseDouble(rhs.rating);

                if (ratingOne == ratingTwo)
                    return 0;
                if (ratingOne > ratingTwo)
                    return -1;

                return 1;
            }
        });
        storeModels = list;
    }

    public List<StoreModel> getStoreModels() {
        return storeModels;
    }
}
