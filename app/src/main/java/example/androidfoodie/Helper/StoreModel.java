package example.androidfoodie.Helper;

public class StoreModel {

    public String name, address;
    public String lat;
    public String lng;
    public String rating;
    public String txtStoreID;

    public StoreModel(String name, String address, String lat, String lng, String rating, String txtStoreID) {
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.txtStoreID = txtStoreID;
    }

    @Override
    public String toString() {
        return "StoreModel{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", rating='" + rating + '\'' +
                ", txtStoreID='" + txtStoreID + '\'' +
                '}';
    }
}