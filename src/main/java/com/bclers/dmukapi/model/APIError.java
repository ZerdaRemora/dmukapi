package com.bclers.dmukapi.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class APIError
{
    private int code;
    private String message;
    private String stackTrace;

    /**
     * Small convenience function to build an {@link APIResponse} object, containing an {@link APIError} in the payload.
     * @param code HTTP Response code of the error
     * @param message Error message to display
     * @return An {@link APIResponse} containing the error details.
     */
    public static APIResponse errorResponseBuilder(int code, String message)
    {
        APIError error = new APIError();
        error.setCode(code);
        error.setMessage(message);

        return APIResponse.builder().statusCode(HttpStatus.resolve(code))
                                .payload(error).errorMessage(message).build();
    }

    /**
     * Small convenience function to build an {@link APIResponse} object, containing an {@link APIError} in the payload.
     * @param code HTTP Response code of the error
     * @param message Error message to display
     * @param stackTrace Stack trace of the exception
     * @return An {@link APIResponse} containing the error details.
     */
    public static APIResponse errorResponseBuilder(int code, String message, String stackTrace)
    {
        APIError error = new APIError();
        error.setCode(code);
        error.setMessage(message);
        error.setStackTrace(stackTrace);

        return APIResponse.builder().statusCode(HttpStatus.resolve(code))
                .payload(error).errorMessage(message).build();
    }
}
