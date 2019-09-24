package pe.ebenites.alldemo.models;

import android.support.annotation.NonNull;

import java.util.List;

public class Question {

    private Integer id;

    private Integer lessons_id;

    private String body;

    private Integer level;

    private Double weight;

    private List<Answer> answers;


    private Double score;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getLessons_id() {
        return lessons_id;
    }

    public void setLessons_id(Integer lessons_id) {
        this.lessons_id = lessons_id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public void setChecked(@NonNull Integer answers_id){
        for (Answer answer : this.answers) {
            if (answers_id.equals(answer.getId())) {
                answer.setChecked(true);
            } else {
                answer.setChecked(false);
            }
        }
    }

    public void clearChecked(){
        for (Answer answer : this.answers) {
            answer.setChecked(false);
        }
    }

    public boolean isChecked(){
        for (Answer answer : this.answers) {
            if (answer.getChecked()) {
                return true;
            }
        }
        return false;
    }

    public Integer getCheckedAnswer(){
        for (Answer answer : this.answers) {
            if (answer.getChecked()) {
                return answer.getId();
            }
        }
        return null;
    }

    public Integer getCorrectAnswer(){
        for (Answer answer : this.answers) {
            if ("1".equals(answer.getCorrect())) {
                return answer.getId();
            }
        }
        return null;
    }

    public Boolean isCorrect(){
        for (Answer answer : this.answers) {
            if (answer.getChecked()) {
                return "1".equals(answer.getCorrect());
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Question{" +
                "id=" + id +
                ", lessons_id=" + lessons_id +
                ", body='" + body + '\'' +
                ", level=" + level +
                ", weight=" + weight +
                ", answers=" + answers +
                ", score=" + score +
                '}';
    }
}
