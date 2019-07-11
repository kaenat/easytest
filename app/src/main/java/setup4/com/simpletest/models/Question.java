package setup4.com.simpletest.models;

import java.util.ArrayList;
import java.util.List;

public class Question {
    public String type;
    public String question;
    public String teacherID;
    public List<String> options;

    public Question() {
    }

    public Question(String type, String question) {
        this.type = type;
        this.question = question;
        this.teacherID = "teacher@gmail.com";
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}