package example.androidfoodie.Model;

public class Suggestion {
    String title,suggestionMsg,phone;

    public Suggestion() {
    }

    public Suggestion(String title, String suggestionMsg, String phone) {
        this.title = title;
        this.suggestionMsg = suggestionMsg;
        this.phone=phone;
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

    public String getSuggestionMsg() {
        return suggestionMsg;
    }

    public void setSuggestionMsg(String suggestionMsg) {
        this.suggestionMsg = suggestionMsg;
    }
}
