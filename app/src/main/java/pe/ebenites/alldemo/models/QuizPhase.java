package pe.ebenites.alldemo.models;

import java.util.List;

public class QuizPhase {

    private Integer id;

    private Integer phase;

    private Integer quizsubmissions_id;

    private QuizSubmission quizsubmission;

    private List<Question> questions;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPhase() {
        return phase;
    }

    public void setPhase(Integer phase) {
        this.phase = phase;
    }

    public Integer getQuizsubmissions_id() {
        return quizsubmissions_id;
    }

    public void setQuizsubmissions_id(Integer quizsubmissions_id) {
        this.quizsubmissions_id = quizsubmissions_id;
    }

    public QuizSubmission getQuizsubmission() {
        return quizsubmission;
    }

    public void setQuizsubmission(QuizSubmission quizsubmission) {
        this.quizsubmission = quizsubmission;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return "QuizPhase{" +
                "id=" + id +
                ", phase=" + phase +
                ", quizsubmissions_id=" + quizsubmissions_id +
                ", quizsubmission=" + quizsubmission +
                ", questions=" + questions +
                '}';
    }
}
