package com.bclers.dmukapi.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@Builder
public class APIResponse<T>
{
    private T payload;
    private HttpStatus statusCode;
    private String errorMessage;
}
