package com.bclers.dmukapi.controller;

import com.bclers.dmukapi.enums.CommentSortOrder;
import com.bclers.dmukapi.enums.CommentSortType;
import com.bclers.dmukapi.enums.NewsSource;
import com.bclers.dmukapi.exception.InternalServerException;
import com.bclers.dmukapi.exception.UnrecognisedNewsSourceException;
import com.bclers.dmukapi.exception.UnrecognisedUrlException;
import com.bclers.dmukapi.model.*;
import com.bclers.dmukapi.model.dto.AutoScraperDTO;
import com.bclers.dmukapi.repository.CommentRepository;
import com.bclers.dmukapi.service.ScrapingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api", produces = "application/json")
@Slf4j
public final class ScraperController
{
    private ScrapingService scrapingService;

    @Autowired
    public ScraperController(CommentRepository commentRepository)
    {
        this.scrapingService = new ScrapingService(commentRepository);
    }

    @RequestMapping(path = "/articletitle", method = RequestMethod.GET)
    public ResponseEntity<APIResponse> articleTitle(@RequestParam String url)
    {
        if (StringUtils.isEmpty(url))
        {
            APIResponse errorResponse = APIError.errorResponseBuilder(400, "No URL provided.");
            return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
        }

        String articleTitle;
        try
        {
            articleTitle = scrapingService.articleTitle(url);
        }
        catch (UnrecognisedUrlException ex)
        {
            APIResponse errorResponse = APIError.errorResponseBuilder(400,
                    "Could not create scraper object as URL was not recognised.");
            return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
        }

        APIResponse response = APIResponse.builder().payload(articleTitle).statusCode(HttpStatus.OK).build();
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @GetMapping("/headlines")
    public ResponseEntity<APIResponse> headlines(@RequestParam String src)
    {
        if (StringUtils.isEmpty(src))
        {
            APIResponse errorResponse = APIError.errorResponseBuilder(400, "No News Source provided.");
            return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
        }

        Map<String, String> headlines;

        try
        {
            headlines = scrapingService.headlines(NewsSource.convertFromString(src));
        }
        catch (UnrecognisedNewsSourceException ex)
        {
            APIResponse errorResponse = APIError.errorResponseBuilder(400,
                    "Could not create scraper object as the News Source was not recognised.");
            return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
        }
        catch (InternalServerException ex)
        {
            APIResponse errorResponse = APIError.errorResponseBuilder(500,
                    "Server was unable to create scraper object.", ExceptionUtils.getStackTrace(ex));
            return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
        }

        APIResponse response = APIResponse.builder().payload(headlines).statusCode(HttpStatus.OK).build();
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @GetMapping("/commentbyid")
    public ResponseEntity<APIResponse> findCommentById(@RequestParam int id)
    {
        Comment cmt = scrapingService.findCommentById(id);
        APIResponse response = APIResponse.builder().payload(cmt).statusCode(HttpStatus.OK).build();
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @GetMapping("/allcomments")
    public ResponseEntity<APIResponse> findAllComments()
    {
        // Getting all comments can be very slow. It's better to use a method that allows filtering.
        List<Comment> cmts = scrapingService.findAllComments();
        APIResponse response = APIResponse.builder().payload(cmts).statusCode(HttpStatus.OK).build();
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @PostMapping("/filtercomments")
    public ResponseEntity<DataTableResponse> findFilteredComments(@RequestBody FilterParameter params)
    {
        List<Comment> cmts = scrapingService.findFilteredComments(params);
        long commentCount = scrapingService.countComments();

        // TODO: recordsFiltered is total number of comments with current filters applied.
        //       recordsTotal is total number of comments with no filters applied.
        //       As we're not filtering yet, these values will be the same.
        DataTableResponse response = DataTableResponse.builder().data(cmts)
                .recordsFiltered(commentCount).recordsTotal(commentCount).statusCode(HttpStatus.OK).build();

        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @GetMapping("/comments")
    public ResponseEntity<APIResponse> comments(@RequestParam(required = false) String url, @RequestParam String sorttype, @RequestParam String sortorder)
    {
        if (StringUtils.isEmpty(url))
        {
            APIResponse errorResponse = APIError.errorResponseBuilder(400, "No URL provided.");
            return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
        }

        List<Comment> result;

        try
        {
            result = scrapingService.comments(url, CommentSortType.convertFromString(sorttype), CommentSortOrder.convertFromString(sortorder));
        }
        catch (UnrecognisedUrlException ex)
        {
            APIResponse errorResponse = APIError.errorResponseBuilder(400,
                        "Could not create scraper object as URL was not recognised.");
            return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
        }

        APIResponse response = APIResponse.builder().payload(result).statusCode(HttpStatus.OK).build();
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @GetMapping("/articlecontainscomments")
    public ResponseEntity<APIResponse> containsComment(@RequestParam String url)
    {
        if (StringUtils.isEmpty(url))
        {
            APIResponse errorResponse = APIError.errorResponseBuilder(400, "No URL provided.");
            return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
        }

        boolean containsComment;

        try
        {
            containsComment = scrapingService.containsComment(url);
        }
        catch (UnrecognisedUrlException ex)
        {
            APIResponse errorResponse = APIError.errorResponseBuilder(400,
                    "Could not create scraper object as URL was not recognised.");
            return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
        }

        APIResponse response = APIResponse.builder().payload(containsComment).statusCode(HttpStatus.OK).build();
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @PostMapping("/triggerautofetch")
    @ResponseBody
    public ResponseEntity<APIResponse> triggerAutoFetch(@RequestBody AutoScraperDTO autoScraperDTO)
    {
        try
        {
            log.info("Starting Auto Fetch Method with params useBBC: " + autoScraperDTO.isUseBBC() + " useDM: " + autoScraperDTO.isUseDM() + "useUKP " + autoScraperDTO.isUseUKP() + ".");
            scrapingService.autoFetchComments(autoScraperDTO.isUseBBC(), autoScraperDTO.isUseDM(), autoScraperDTO.isUseUKP());
        }
        catch (UnrecognisedUrlException ex)
        {
            APIResponse errorResponse = APIError.errorResponseBuilder(500,
                    "An error occurred when the autofetcher tried to query a url.", ExceptionUtils.getStackTrace(ex));
            return new ResponseEntity<>(errorResponse, errorResponse.getStatusCode());
        }

        APIResponse response = APIResponse.builder().payload(true).statusCode(HttpStatus.OK).build();
        return new ResponseEntity<>(response, response.getStatusCode());
    }
}