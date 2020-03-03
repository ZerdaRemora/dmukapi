package com.bclers.dmukapi.helper;

import com.bclers.dmukapi.enums.CommentSortOrder;
import com.bclers.dmukapi.enums.CommentSortType;
import com.bclers.dmukapi.model.Comment;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;
import java.util.Map;

public abstract class WebScraper
{
    @Getter
    @Setter
    private String url;

    WebScraper(String urlToScrape)
    {
        this.url = urlToScrape;
    }

    /**
     * Factory method for building the appropriate Scraper object depending
     * on the input url. <br>
     * Currently builds {@link BBCScraper} and {@link DMScraper}.
     *
     * @param url The URL to be scraped.
     * @return the appropriate scraper for the given url, or null if no relevant scraper exists.
     */
    public static WebScraper getInstance(String url)
    {
        if (url.matches("^https://www.bbc.co.uk/news/?.*"))
        {
            return new BBCScraper(url);
        }
        else if (url.matches("^https://www.dailymail.co.uk/?.*"))
        {
            return new DMScraper(url);
        }
        else if (url.matches("^https://www.reddit.com/r/ukpolitics/?.*"))
        {
            return new UKPScraper(url);
        }
        else
        {
            return null;
        }
    }

    @SneakyThrows
    public String getPageTitle()
    {
        Document doc = Jsoup.connect(url).get();

        return doc.title();
    }

    public abstract Map<String, String> getArticles();

    public abstract Map<String, String> getArticlesWithComments();

    // TODO find out exact error for invalid url to replace with the generic one
    public abstract String getArticleTitle();

    public abstract boolean containsComments();

    /**
     * When the scraper is on an article page with comments, this method will
     * return a list of {@link Comment} objects with the comment data.
     *
     * @return a list of {@link Comment}s from the current page.
     */
    public abstract List<Comment> getComments(CommentSortType sortType, CommentSortOrder sortOrder);
}
