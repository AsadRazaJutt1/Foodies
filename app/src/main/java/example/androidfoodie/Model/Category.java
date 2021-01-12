package example.androidfoodie.Model;

public class Category {

    private String Name;
    private String Image;
//    private String Timing;

    public Category() {
    }

    public Category(String name, String image) {
        Name = name;
        Image = image;
    }

//    public Category(String name, String image, String timing) {
//        Name = name;
//        Image = image;
//        Timing = timing;
//    }

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

//    public String getTiming() {
//        return Timing;
//    }
//
//    public void setTiming(String timing) {
//        Timing = timing;
//    }
}
