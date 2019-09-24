package pe.ebenites.alldemo.models;

public class Answer {

    private Integer id;

    private Integer questions_id;

    private String body;

    private String correct;


    private Boolean checked = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuestions_id() {
        return questions_id;
    }

    public void setQuestions_id(Integer questions_id) {
        this.questions_id = questions_id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCorrect() {
        return correct;
    }

    public void setCorrect(String correct) {
        this.correct = correct;
    }

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", questions_id=" + questions_id +
                ", body='" + body + '\'' +
                ", correct='" + correct + '\'' +
                ", checked=" + checked +
                '}';
    }
}
