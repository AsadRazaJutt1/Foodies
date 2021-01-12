package example.androidfoodie.Interface;

import example.androidfoodie.Helper.PlacesPOJO;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("place/nearbysearch/json?")
    Call<PlacesPOJO.Root> doPlaces(@Query(value = "type", encoded = true) String type,
                                   @Query(value = "location", encoded = true) String location,
                                   @Query(value = "name", encoded = true) String name,
                                   @Query(value = "opennow", encoded = true) boolean opennow,
                                   @Query(value = "place_id", encoded = true) String place_id ,
                                   @Query(value = "rankby", encoded = true) String rankby,
                                   @Query(value = "key", encoded = true) String key);
}