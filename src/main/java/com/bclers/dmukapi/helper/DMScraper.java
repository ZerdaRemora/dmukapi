package com.bclers.dmukapi.helper;

import com.bclers.dmukapi.enums.CommentSortOrder;
import com.bclers.dmukapi.enums.CommentSortType;
import com.bclers.dmukapi.enums.NewsSource;
import com.bclers.dmukapi.model.Comment;
import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DMScraper extends WebScraper
{
    /**
     * Initialises the Scraper using the default Daily Mail URL of
     * <em>https://www.dailymail.co.uk/home/index.html</em>
     */
    public DMScraper()
    {
        super("https://www.dailymail.co.uk/home/index.html");
    }

    public DMScraper(String urlToScrape)
    {
        super(urlToScrape);
    }

    @Override
    public Map<String, String> getArticles()
    {
        return null;
    }

    @Override
    @SneakyThrows
    public Map<String, String> getArticlesWithComments()
    {
        Map<String, String> toReturn = new HashMap<>();
        String baseurl = "https://www.dailymail.co.uk";
        Document doc = Jsoup.connect(this.getUrl()).get();

        Elements headlines = doc.select(".article");
        for (Element c : headlines)
        {
            if (c.selectFirst(".comments-link") != null)
            {
                if (!c.selectFirst(".comments-link").text().equals("comments"))
                {
                    String url = baseurl + c.select("a").first().attr("href");
                    String title = c.selectFirst("h2").text();
                    toReturn.put(url, title);
                }
            }
        }
        return toReturn;
    }

    @Override
    public String getArticleTitle()
    {
        try
        {
            Document doc = Jsoup.connect(this.getUrl()).get();

            if (doc.selectFirst("#js-article-text h2") == null)
                return null;

            return doc.selectFirst("#js-article-text h2").text();
        }
        catch (IOException e)
        {
            return null;
        }
    }

    @Override
    @SneakyThrows
    public List<Comment> getComments(CommentSortType sortType, CommentSortOrder sortOrder)
    {
        // Store Article title here so we dont have to call the function for each comment
        String articleTitle = getArticleTitle();

        int numberOfComments = 20;
        Pattern articleNumberPattern = Pattern.compile("article-(\\d+)");
        Matcher articleNumberMatcher = articleNumberPattern.matcher(this.getUrl());

        // Ensure that the URL has an article number where we expect it. If not, end early.
        if (!articleNumberMatcher.find())
            return null;

        if (articleNumberMatcher.groupCount() < 1)
            return null;

        String articleNumber = articleNumberMatcher.group(1); // 0th group is whole string, 1st should be 1st match.

        // No 'sort' param sorts by date added.
        String commentUrl = "https://www.dailymail.co.uk/reader-comments/p/asset/readcomments/" + articleNumber +
                "?max=" + numberOfComments + "&order=" + sortOrder.toString(NewsSource.DAILYMAIL);

        // Only add the sort param on if sorting by score.
        if (sortType == CommentSortType.HIGHEST_RATED || sortType == CommentSortType.LOWEST_RATED)
            commentUrl += "&sort=voteRating";

        RestTemplate r = new RestTemplate();

        // User-Agent header needed to bypass a 403 Forbidden error.
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        HttpEntity request = new HttpEntity(headers);

        ResponseEntity<String> response = r.exchange(commentUrl, HttpMethod.GET, request, String.class);

        // Parse the JSON response for comment data.
        JSONObject responseObj = new JSONObject(response.getBody());

        if (responseObj.getString("status").equals("error"))
            return null;

        JSONArray parsedComments = responseObj.getJSONObject("payload").getJSONArray("page");
        List<Comment> output = new ArrayList<>();

        for (int i = 0; i < parsedComments.length(); i++)
        {
            JSONObject currentComment = parsedComments.getJSONObject(i);
            Comment c = new Comment();
            c.setBody(currentComment.getString("message"));

            if (!currentComment.isNull("userAlias"))
                c.setAuthor(currentComment.getString("userAlias"));

            c.setScore(currentComment.getInt("voteRating"));
            c.setCommentSource(NewsSource.DAILYMAIL);
            c.setLocalSiteCmtID(Long.toString(currentComment.getLong("id")));
            c.setDate(LocalDateTime.parse(currentComment.getString("dateCreated"), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")));
            c.setArticleTitle(articleTitle);

            output.add(c);
        }

        return output;
    }

    @Override
    public boolean containsComments()
    {
        try
        {
            Document doc = Jsoup.connect(this.getUrl()).get();

            if (doc.selectFirst("#js-article-text h2") != null)
            {
                if (doc.selectFirst(".comments-count") != null)
                {
                    return true;
                }
            }
            return false;

        } catch (IOException e)
        {
            return false;
        }
    }

}
