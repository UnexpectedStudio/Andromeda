package andromeda.question;

public class Question {
    public String question;
    public String[] anwsers;
    
    public Question(String question, String...anwsers) {
        this.question = question;
        this.anwsers = anwsers;
    }
}
