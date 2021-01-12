package example.androidfoodie.Model;

public class Admin {
    private String Admin_Name;
    private String Admin_Email;
    private String Admin_Phone;
    private String Admin_Password;
    private String isStaff = "true";

    public Admin() {
    }

    public Admin(String admin_Name, String admin_Password) {
        Admin_Name = admin_Name;
        Admin_Password = admin_Password;
    }


    public Admin(String admin_Name, String admin_email, String admin_Password) {
        Admin_Name = admin_Name;
        Admin_Email = admin_email;
        Admin_Password = admin_Password;
    }

    public String getAdmin_Email() {
        return Admin_Email;
    }

    public void setAdmin_Email(String admin_Email) {
        Admin_Email = admin_Email;
    }

    public String getAdmin_Name() {
        return Admin_Name;
    }

    public void setAdmin_Name(String admin_Name) {
        Admin_Name = admin_Name;
    }

    public String getAdmin_Phone() {
        return Admin_Phone;
    }

    public void setAdmin_Phone(String admin_Phone) {
        Admin_Phone = admin_Phone;
    }

    public String getAdmin_Password() {
        return Admin_Password;
    }

    public void setAdmin_Password(String admin_Password) {
        Admin_Password = admin_Password;
    }

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }

}
