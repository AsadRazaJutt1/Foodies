package example.androidfoodie.Model;

public class Food {
    private String Name, Image, Description, Price, MenuId, Discount, number;
    private String timing, restaurant,restaurantId, closing, opening;

    public Food() {
    }

    public Food(String name, String image, String description, String price, String menuId, String discount) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        MenuId = menuId;
        Discount = discount;
    }

    public Food(String name, String description, String price, String menuId, String number) {
        Name = name;
        Description = description;
        Price = price;
        MenuId = menuId;
        number = number;
    }

    public Food(String name, String image, String description, String price, String menuId, String discount, String number, String timing, String restaurant) {
        Name = name;
        Image = image;
        Description = description;
        Price = price;
        MenuId = menuId;
        Discount = discount;
        this.number = number;
        this.timing = timing;
        this.restaurant = restaurant;
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

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant_name) {
        this.restaurant = restaurant_name;
    }

    public String getTiming() {
        return timing;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        this.MenuId = menuId;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }
}
