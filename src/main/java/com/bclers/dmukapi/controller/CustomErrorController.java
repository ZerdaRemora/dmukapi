package com.bclers.dmukapi.controller;

import com.bclers.dmukapi.model.APIError;
import com.google.gson.Gson;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@RestController
public final class CustomErrorController implements ErrorController
{
    @Override
    public String getErrorPath()
    {
        return "/error";
    }

    @GetMapping("/error")
    public String handleError(HttpServletRequest request)
    {
        Object tempStatus = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        APIError error = new APIError();

        if (tempStatus != null)
        {
            int statusCode = Integer.parseInt(tempStatus.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value())
            {
                error.setCode(404);
                error.setMessage("Request page or endpoint could not be found.");
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value())
            {
                error.setCode(500);
                error.setMessage("Unknown server error occurred.");
            }
            else
            {
                String errorMsg = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
                error.setCode(statusCode);
                error.setMessage(errorMsg);
            }
        }
        else
        {
            error.setCode(999);
            error.setMessage("Unknown error occurred.");
        }

        return new Gson().toJson(error);
    }
}
