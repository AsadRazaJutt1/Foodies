package example.androidfoodie.Model;

public class Restaurant_Profile {
    private String restaurantName;
    private String restaurantPassword;
    private String restaurantPhone;
    private String restaurantAddress;
    private String restaurantId;
    private String closing;
    private String opening;

    public Restaurant_Profile() {
    }

    public Restaurant_Profile(String restaurantName, String restaurantPassword, String restaurantAddress) {
        this.restaurantName = restaurantName;
        this.restaurantPassword = restaurantPassword;
        this.restaurantAddress = restaurantAddress;

    }

    public Restaurant_Profile(String restaurantName, String restaurantPassword, String restaurantPhone, String restaurantAddress, String restaurantId) {
        this.restaurantName = restaurantName;
        this.restaurantPassword = restaurantPassword;
        this.restaurantPhone = restaurantPhone;
        this.restaurantAddress = restaurantAddress;
        this.restaurantId = restaurantId;
    }

    public String getClosing() {
        return closing;
    }

    public void setClosing(String closing) {
        this.closing = closing;
    }

    public String getOpening() {
        return opening;
    }

    public void setOpening(String opening) {
        this.opening = opening;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantAddress() {
        return restaurantAddress;
    }

    public void setRestaurantAddress(String restaurantAddress) {
        this.restaurantAddress = restaurantAddress;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantPassword() {
        return restaurantPassword;
    }

    public void setRestaurantPassword(String restaurantPassword) {
        this.restaurantPassword = restaurantPassword;
    }

    public String getRestaurantPhone() {
        return restaurantPhone;
    }

    public void setRestaurantPhone(String restaurantPhone) {
        this.restaurantPhone = restaurantPhone;
    }


}
