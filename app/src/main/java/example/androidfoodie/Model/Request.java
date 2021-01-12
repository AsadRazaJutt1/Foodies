package example.androidfoodie.Model;

import java.util.List;

public class Request {
    private int timeStart;
    private String phone;
    private String name;
    private String address;
    private String total;
    private String status;
    private String IMEI_R;
    private Order foods;
    private String restaurant;
    private String paymentState;//list of food order

//    public String getFoodName() {
//        return foodName;
//    }
//
//    public void setFoodName(String foodName) {
//        this.foodName = foodName;
//    }
//
//    private String foodName, Image, Description, Price, MenuId, Discount;

    public Request() {
    }

    public Request(String phone, String name, String address, String total,String status, String paymentState, Order foods,String restaurant) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.paymentState = paymentState;
        this.foods = foods;
        this.restaurant = restaurant;
        this.timeStart = 30;

    }

    public Request(String phone, String name, String address, String total, Order foods) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.foods = foods;
//        this.restaurant = restaurant;
        this.status = "0";
        this.timeStart = 30;
        // default is 0 , 0: placed, 1: shipping, 2: shipped
    }


    public Request(String phone, String name, String address, String total,String status, String IMEI_R, Order foods, String restaurant, String paymentState) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.total = total;
        this.status = status;
        this.IMEI_R = IMEI_R;
        this.foods = foods;
        this.restaurant = restaurant;
        this.timeStart = 30;
        this.paymentState = paymentState;
        // default is 0 , 0: placed, 1: shipping, 2: shipped
    }

//    public Request(String phone, String name, String address, String total) {
//        this.phone = phone;
//        this.name = name;
//        this.address = address;
//        this.total = total;
//        this.status = "0";
//    }
//    public Request(String phone, String name, String address, String total, Order order) {
//        this.phone = phone;
//        this.name = name;
//        this.address = address;
//        this.total = total;
//        this.order=order;
//        this.status = "0";
//        this.timeStart="30";
//    }


    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public Order getFoods() {
        return foods;
    }

    public void setFoods(Order foods) {
        this.foods = foods;
    }

    public String getIMEI_R() {
        return IMEI_R;
    }

    public void setIMEI_R(String IMEI_R) {
        this.IMEI_R = IMEI_R;
    }

    public int getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
    }

//    public String getDescription() {
//        return Description;
//    }
//
//    public void setDescription(String description) {
//        Description = description;
//    }
//
//    public String getPrice() {
//        return Price;
//    }
//
//    public void setPrice(String price) {
//        Price = price;
//    }
//
//    public String getMenuId() {
//        return MenuId;
//    }
//
//    public void setMenuId(String menuId) {
//        this.MenuId = menuId;
//    }
//
//    public String getDiscount() {
//        return Discount;
//    }
//
//    public void setDiscount(String discount) {
//        Discount = discount;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

//    public List<Order> getFoods() {
//        return foods;
//    }
//
//    public void setFoods(List<Order> foods) {
//        this.foods = foods;
//    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }
}
