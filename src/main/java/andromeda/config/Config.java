package andromeda.config;

import andromeda.question.Question;

public class Config extends JsonConfiguration {
    public Question[] questions = {new Question("test", "test")};
    public String[] notFound = {"Je ne trouve pas quoi vous répondre..."};
    public String[] firstMessages = {};
    public float levenshtein = 1.5f;
}
