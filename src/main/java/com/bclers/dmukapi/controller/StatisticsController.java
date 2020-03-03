package com.bclers.dmukapi.controller;

import com.bclers.dmukapi.exception.CommentNotFoundException;
import com.bclers.dmukapi.model.APIError;
import com.bclers.dmukapi.model.APIResponse;
import com.bclers.dmukapi.model.Comment;
import com.bclers.dmukapi.repository.CommentRepository;
import com.bclers.dmukapi.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/stats")
public final class StatisticsController
{
    private StatisticsService statisticsService;

    @Autowired
    public StatisticsController(CommentRepository commentRepository)
    {
        this.statisticsService = new StatisticsService(commentRepository);
    }

    @GetMapping("/commentsviaHR")
    public ResponseEntity<APIResponse> getHRComment()
    {
        Comment highestRatedComment;

        try
        {
            highestRatedComment = statisticsService.getHighestRatedComment();
        }
        catch (CommentNotFoundException ex)
        {
            APIResponse errorResponse = APIError.errorResponseBuilder(500, ex.getMessage());
            return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
        }

        APIResponse response = APIResponse.builder().payload(highestRatedComment).statusCode(HttpStatus.OK).build();
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @GetMapping("/commentsviaLR")
    public ResponseEntity<APIResponse> getLRComment()
    {
        Comment lowestRatedComment;

        try
        {
            lowestRatedComment = statisticsService.getLowestRatedComment();
        }
        catch (CommentNotFoundException ex)
        {
            APIResponse errorResponse = APIError.errorResponseBuilder(500, ex.getMessage());
            return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
        }

        APIResponse response = APIResponse.builder().payload(lowestRatedComment).statusCode(HttpStatus.OK).build();
        return new ResponseEntity<>(response, response.getStatusCode());
    }
}
