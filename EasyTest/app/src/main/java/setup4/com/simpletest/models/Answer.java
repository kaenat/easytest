package setup4.com.simpletest.models;

public class Answer {
    public String questionID;
    public String studentID;
    public String answer;

    public Answer() {
    }

    public Answer(String questionID, String studentID, String answer) {
        this.questionID = questionID;
        this.studentID = studentID;
        this.answer = answer;
    }
}
