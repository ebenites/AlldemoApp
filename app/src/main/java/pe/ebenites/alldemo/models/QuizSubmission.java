package pe.ebenites.alldemo.models;

import java.util.Date;

public class QuizSubmission {

    private Integer id;

    private Integer users_id;

    private Integer lessons_id;

    private Double score;

    private String finished;

    private Date finished_at;

    private Integer phase;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUsers_id() {
        return users_id;
    }

    public void setUsers_id(Integer users_id) {
        this.users_id = users_id;
    }

    public Integer getLessons_id() {
        return lessons_id;
    }

    public void setLessons_id(Integer lessons_id) {
        this.lessons_id = lessons_id;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getFinished() {
        return finished;
    }

    public void setFinished(String finished) {
        this.finished = finished;
    }

    public Date getFinished_at() {
        return finished_at;
    }

    public void setFinished_at(Date finished_at) {
        this.finished_at = finished_at;
    }

    public Integer getPhase() {
        return phase;
    }

    public void setPhase(Integer phase) {
        this.phase = phase;
    }

    @Override
    public String toString() {
        return "QuizSubmission{" +
                "id=" + id +
                ", users_id=" + users_id +
                ", lessons_id=" + lessons_id +
                ", score=" + score +
                ", finished='" + finished + '\'' +
                ", finished_at=" + finished_at +
                ", phase=" + phase +
                '}';
    }
}
