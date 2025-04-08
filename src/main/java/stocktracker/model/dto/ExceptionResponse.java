package stocktracker.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

public class ExceptionResponse {
    @JsonProperty
    private HttpStatus httpStatus;
    @JsonProperty
    private String exceptionClassName;
    @JsonProperty
    private String message;

    public ExceptionResponse(HttpStatus httpStatus, String exceptionClassName, String message) {
        this.httpStatus = httpStatus;
        this.exceptionClassName = exceptionClassName;
        this.message = message;
    }
}
