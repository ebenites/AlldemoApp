package pe.ebenites.alldemo.models;

public class ResponseSuccess {

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseSuccess{" +
                "message='" + message + '\'' +
                '}';
    }
}
