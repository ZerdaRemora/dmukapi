package com.bclers.dmukapi.controller;

import com.bclers.dmukapi.exception.UnrecognisedUrlException;
import com.bclers.dmukapi.model.APIError;
import com.bclers.dmukapi.model.APIResponse;
import com.bclers.dmukapi.repository.CommentRepository;
import com.bclers.dmukapi.service.QuizService;
import com.bclers.dmukapi.service.ScrapingService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/quiz")
public final class QuizController
{
    private QuizService quizService;
    private ScrapingService scrapingService;

    @Autowired
    public QuizController(CommentRepository commentRepository)
    {
        scrapingService = new ScrapingService(commentRepository);
        quizService = new QuizService(commentRepository);
    }

    @PostMapping("/startquiz")
    public ResponseEntity<APIResponse> startQuiz(@RequestParam String usebbc, @RequestParam String usedm, @RequestParam String useukp,
                                                 @RequestParam String cmtcount)
    {
        boolean useBbcBool = Boolean.parseBoolean(usebbc);
        boolean useDmBool = Boolean.parseBoolean(usedm);
        boolean useUkpBool = Boolean.parseBoolean(useukp);

        quizService.startQuiz(useBbcBool, useDmBool, useUkpBool, Integer.parseInt(cmtcount));

        APIResponse response = APIResponse.builder().payload(true).statusCode(HttpStatus.OK).build();
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @GetMapping("/nextcmt")
    public ResponseEntity<APIResponse> nextQuizComment()
    {
        APIResponse response = APIResponse.builder().payload(quizService.pullNextComment()).statusCode(HttpStatus.OK).build();
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @GetMapping("/getsources")
    public ResponseEntity<APIResponse> getQuizSources()
    {
        Map<String, Boolean> sources = quizService.pullSources();

        if (sources == null)
        {
            APIResponse errorResponse = APIError.errorResponseBuilder(500, "An unknown error occurred while retrieving quiz sources.");
            return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
        }

        APIResponse response = APIResponse.builder().payload(sources).statusCode(HttpStatus.OK).build();
        return new ResponseEntity<>(response, response.getStatusCode());
    }
}
