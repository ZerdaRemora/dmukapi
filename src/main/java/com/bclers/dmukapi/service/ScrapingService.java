package com.bclers.dmukapi.service;

import com.bclers.dmukapi.enums.CommentSortOrder;
import com.bclers.dmukapi.enums.CommentSortType;
import com.bclers.dmukapi.enums.NewsSource;
import com.bclers.dmukapi.exception.InternalServerException;
import com.bclers.dmukapi.exception.UnrecognisedNewsSourceException;
import com.bclers.dmukapi.exception.UnrecognisedUrlException;
import com.bclers.dmukapi.helper.*;
import com.bclers.dmukapi.model.Comment;
import com.bclers.dmukapi.model.FilterParameter;
import com.bclers.dmukapi.model.SortParameter;
import com.bclers.dmukapi.repository.CommentRepository;
import com.bclers.dmukapi.utils.LocalDateTimeConverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class ScrapingService
{
    private CommentRepository commentRepository;
    private Gson gson = new GsonBuilder().serializeNulls().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter()).create();

    public ScrapingService(CommentRepository commentRepository)
    {
        this.commentRepository = commentRepository;
    }

    public String articleTitle(String url) throws UnrecognisedUrlException
    {
        WebScraper scraper = WebScraper.getInstance(url);

        if (scraper == null)
        {
            throw new UnrecognisedUrlException("Could not create scraper object as URL was not recognised.");
        }

        return scraper.getArticleTitle();
    }

    public Map<String, String> headlines(NewsSource source) throws UnrecognisedNewsSourceException, InternalServerException
    {
        WebScraper scraper;

        if (source == null)
        {
            throw new UnrecognisedNewsSourceException("News Source not recognised. Please use 'bbc', 'dailymail' or 'ukpolitics'.");
        }

        scraper = WebScraper.getInstance(source.getSourceURL());

        if (scraper == null)
        {
            throw new InternalServerException("Server was unable to create Scraper object.");
        }

        return scraper.getArticlesWithComments();
    }

    public boolean containsComment(String url) throws UnrecognisedUrlException
    {
        WebScraper scraper = WebScraper.getInstance(url);

        if (scraper == null)
        {
            throw new UnrecognisedUrlException("Could not create scraper object as URL was not recognised.");
        }

        return scraper.containsComments();
    }

    public Comment findCommentById(int id)
    {
        return commentRepository.findById(id).orElse(null);
    }

    public List<Comment> findAllComments()
    {
        List<Comment> output = new ArrayList<>();
        commentRepository.findAll().forEach(output::add);
        return output;
    }

    public List<Comment> findFilteredComments(FilterParameter params)
    {
        int page = params.getStart() / params.getLength();
        Sort sort = Sort.unsorted();

        for (SortParameter param : params.getOrder())
        {
            sort = sort.and(Sort.by(
                    Sort.Order.by(param.getColumnName())
                            .with(Sort.Direction.fromString(param.getDirection()))
                            .ignoreCase()));
        }

        return new ArrayList<>(commentRepository.findAll(PageRequest.of(page, params.getLength(), sort)));
    }

    public long countComments()
    {
        return commentRepository.count();
    }

    public List<Comment> comments(String url, CommentSortType sorttype, CommentSortOrder sortorder) throws UnrecognisedUrlException
    {
        WebScraper scraper = WebScraper.getInstance(url);

        if (scraper == null)
        {
            throw new UnrecognisedUrlException("Could not create scraper object as URL was not recognised.");
        }

        List<Comment> cmts = scraper.getComments(sorttype, sortorder);

        for (Comment cmt : cmts)
        {
            if (!commentRepository.findByCommentSourceAndLocalSiteCmtID(cmt.getCommentSource(), cmt.getLocalSiteCmtID()).isPresent())
            {
                commentRepository.save(cmt);
            }
        }

        return cmts;
    }

    /**
     * Scrapes the selected websites for headlines with comments, then scrapes the comments from 5 of those articles
     * and inserts them into the database.
     *
     * TODO: In need of a refactor. Just using {@link #comments} to insert into the database is unclear.
     * @param useBBC Should BBC News be scraped?
     * @param useDM Should Daily Mail be scraped?
     * @param useUKP Should r/UKPolitics be scraped?
     * @throws UnrecognisedUrlException If this is thrown, something has gone wrong in the auto-scraping process (headline
     *                                  url not grabbed correctly). Maybe this shouldn't be thrown to the REST API.
     */
    public void autoFetchComments(boolean useBBC, boolean useDM, boolean useUKP) throws UnrecognisedUrlException
    {
        if (useBBC)
        {
            // Create BBC Scraper
            BBCScraper bbcScraper = new BBCScraper();
            log.info("Created BBC Scraper");
            // Acquire all articles
            Map<String, String> bbcArticleList = bbcScraper.getArticlesWithComments();
            for (Map.Entry<String, String> entry : bbcArticleList.entrySet())
            {
                // Fetch most upvoted & downvoted comments
                comments(entry.getKey(), CommentSortType.HIGHEST_RATED, CommentSortOrder.DESCENDING);
                comments(entry.getKey(), CommentSortType.LOWEST_RATED, CommentSortOrder.DESCENDING);
                log.info("BBC Article Added");
            }
        }

        if (useDM)
        {
            // Create DM Scraper
            DMScraper dmScraper = new DMScraper();
            log.info("Created DM Scraper");
            Map<String, String> dmArticleList = dmScraper.getArticlesWithComments();
            // Acquire 5 random articles
            List<String> dmArticleURLs = new ArrayList<>(dmArticleList.keySet());
            Random r = new Random();
            for (int i = 0; i < 5; i++)
            {
                // Fetch most upvoted & downvoted comments
                String randomURL = dmArticleURLs.get(r.nextInt(dmArticleURLs.size()));
                comments(randomURL, CommentSortType.HIGHEST_RATED, CommentSortOrder.DESCENDING);
                comments(randomURL, CommentSortType.LOWEST_RATED, CommentSortOrder.DESCENDING);
                log.info("DM Article Added");
            }
        }

        if (useUKP)
        {
            // Create UKP Scraper
            UKPScraper ukpScraper = new UKPScraper();
            log.info("Created UKP Scraper");
            Map<String, String> ukpArticleList = ukpScraper.getArticlesWithComments();
            // Acquire 5 random posts
            List<String> ukpPostURLs = new ArrayList<>(ukpArticleList.keySet());
            Random r = new Random();
            for (int i = 0; i < 5; i++)
            {
                // Fetch most upvoted & downvoted comments
                String randomURL = ukpPostURLs.get(r.nextInt(ukpPostURLs.size()));
                comments(randomURL, CommentSortType.HIGHEST_RATED, CommentSortOrder.DESCENDING);
                comments(randomURL, CommentSortType.LOWEST_RATED, CommentSortOrder.DESCENDING);
                log.info("UKP Article Added");
            }
        }
    }
}
