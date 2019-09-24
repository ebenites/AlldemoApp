package pe.ebenites.alldemo.models;

public class Page {

    private Integer id;

    private Integer lessons_id;

    private String type;

    private String body;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Page{" +
                "id=" + id +
                ", lessons_id=" + lessons_id +
                ", type='" + type + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
