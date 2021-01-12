package example.androidfoodie.Model;

public class Complaint {
    String title, orderNumber, complaintMsg, phone, restaurant;

    public Complaint() {
    }

    public Complaint(String title, String complaintMsg, String phone) {
        this.title = title;
        this.complaintMsg = complaintMsg;
        this.phone = phone;
    }

    public Complaint(String title, String complaintMsg, String phone, String restaurant) {
        this.title = title;
        this.complaintMsg = complaintMsg;
        this.phone = phone;
        this.restaurant = restaurant;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(String restaurant) {
        this.restaurant = restaurant;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getComplaintMsg() {
        return complaintMsg;
    }

    public void setComplaintMsg(String complaintMsg) {
        this.complaintMsg = complaintMsg;
    }
}
