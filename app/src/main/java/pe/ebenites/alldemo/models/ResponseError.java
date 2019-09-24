package pe.ebenites.alldemo.models;

/**
 * Created by ebenites on 09/01/2017.
 */

public class ResponseError {

    private String exception;

    private String message;

    public ResponseError(String message) {
        this.message = message;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ResponseError{" +
                "exception='" + exception + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
