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
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Used for scraping news data from the homepage of BBC News.
 */
public class BBCScraper extends WebScraper
{
    /**
     * Initialises the Scraper using the default BBC News URL of
     * <em>https://www.bbc.co.uk/news</em>
     */
    public BBCScraper()
    {
        super("https://www.bbc.co.uk/news");
    }

    public BBCScraper(String urlToScrape)
    {
        super(urlToScrape);
    }

    @SneakyThrows
    public String getTopHeadline()
    {
        Document doc = Jsoup.connect(this.getUrl()).get();

        return doc.select(".nw-c-top-stories__primary-item h3").first().text();
    }

    @Override
    public Map<String, String> getArticles()
    {
        return null;
    }

    /**
     * Looks at primary, secondary and tertiary headlines on the BBC News homepage for articles with comments.
     *
     * @return A list of URLs to articles with comments.
     */
    @Override
    @SneakyThrows
    public Map<String, String> getArticlesWithComments()
    {
        Document doc = Jsoup.connect(this.getUrl()).get();

        String baseUrl = "https://www.bbc.co.uk";
        Map<String, String> articles = new HashMap<>();

        // Check top headline:
        if (doc.selectFirst(".nw-c-top-stories__primary-item .gs-c-comment-count") != null)
            articles.put(baseUrl + doc.selectFirst(".nw-c-top-stories__primary-item a").attr("href"), doc.selectFirst(".nw-c-top-stories__primary-item h3").text());


        // Check secondary & tertiary headlines:
        Elements additionalHeadlines = new Elements();
        Elements secondaryHeadlines = secondaryHeadlineElements(doc);
        Elements tertiaryHeadlines = tertiaryHeadlineElements(doc);

        if (secondaryHeadlines != null)
            additionalHeadlines.addAll(secondaryHeadlines);

        if (tertiaryHeadlines != null)
            additionalHeadlines.addAll(tertiaryHeadlines);

        for (Element headline : additionalHeadlines)
            if (headline.selectFirst(".gs-c-comment-count") != null)
                articles.put(baseUrl + headline.selectFirst("a").attr("href"), headline.selectFirst("h3").text());

        return articles;
    }

    @Override
    public String getArticleTitle()
    {
        try
        {
            Document doc = Jsoup.connect(this.getUrl()).get();

            if (doc.selectFirst(".story-body__h1") == null)
                return null;

            return doc.selectFirst(".story-body__h1").text();

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

        // Set be set by input if we wish to
        int numberOfComments = 20;

        Pattern articleNumberPattern = Pattern.compile("(\\d+)(?!.*\\d)");
        Matcher articleNumberMatcher = articleNumberPattern.matcher(this.getUrl());

        // Ensure that the URL has an article number where we expect it. If not, end early.
        if (!articleNumberMatcher.find() || articleNumberMatcher.groupCount() < 1)
        {
            return null;
        }

        int forumNumber = Integer.parseInt(articleNumberMatcher.group(1));

        // Replace special chars in url
        String convertedURL = URLEncoder.encode(this.getUrl(), "UTF-8");
        Document doc = Jsoup.connect(this.getUrl()).get();

        Element commentsButton = doc.selectFirst("#comp-comments-button");
        if (commentsButton == null)
            return null;

        // TODO: Needs a cleanup
        String commentUrl = "https://ssl.bbc.co.uk/modules/comments/ajax/comments/?siteId=newscommentsmodule&forumId=__CPS__" + forumNumber
                + "&filter=none&sortOrder=" + sortOrder.toString() + "&sortBy=" + sortType.toString(NewsSource.BBC)
                + "&mock=0&mockUser=&parentUri= " + convertedURL + "&loc=en-GB&preset=responsive&initial_page_size=" + numberOfComments + "&transTags=0";

        // Make an HTTP GET request in order to get the Comments JSON data.
        HttpHeaders headers = new HttpHeaders();
        headers.set("Referer", this.getUrl());
        HttpEntity request = new HttpEntity(headers);

        RestTemplate r = new RestTemplate();
        ResponseEntity<String> response = r.exchange(commentUrl, HttpMethod.GET, request, String.class);

        // Parse the JSON and retrieve the HTML for the comments.
        JsonParser parser = JsonParserFactory.getJsonParser();
        Map<String, Object> map = parser.parseMap(response.getBody());

        // Parse the comments HTML and get each comment's text value.
        Document commentsDoc = Jsoup.parse(map.get("comments").toString());
        Elements comments = commentsDoc.select(".cmt-normal");

        List<Comment> output = new ArrayList<>();

        // Finally, print out the comments :)
        for (Element c : comments)
        {
            long localID = Long.parseLong(c.attr("id").substring(8));
            String text = c.selectFirst(".cmt-text").text();
            if (!text.equals("This comment was removed because it broke the house rules. Explain"))
            {
                String author = c.selectFirst("a[class^=userId]").text();
                String date = c.selectFirst(".cmt-time").text();
                int upvotes = Integer.parseInt(c.selectFirst(".cmt-rating-positive-value").text());
                int downvotes = Integer.parseInt(c.selectFirst(".cmt-rating-negative-value").text());

                Comment comment = new Comment(text);
                comment.setAuthor(author);

                if (date.contains("ago") || date.contains("Just now"))
                {
                    LocalDateTime commentParse = LocalDateTime.now();
                    if (date.contains("ago"))
                    {
                        int minOffset = Integer.parseInt(date.replaceAll("[^\\d]", ""));
                        if (date.contains("minutes"))
                        {
                            commentParse = commentParse.minusMinutes(minOffset);
                        } else if (date.contains("hours")) // else must contain hours
                        {
                            commentParse = commentParse.minusHours(minOffset);
                        } // else would be a "just now" case therefore no time subtraction required
                    }

                    comment.setDate(commentParse);
                }
                else
                {
                    comment.setDate(LocalDateTime.parse(date, DateTimeFormatter.ofPattern("d MMM uuuu HH:mm")));
                }

                comment.setLocalSiteCmtID(Long.toString(localID));
                comment.setCommentSource(NewsSource.BBC);
                comment.setScore(upvotes - downvotes);
                comment.setArticleTitle(articleTitle);

                output.add(comment);
            }
        }

        return output;
    }

    @Override
    @SneakyThrows
    public boolean containsComments()
    {
        try
        {
            Document doc = Jsoup.connect(this.getUrl()).get();

            if (doc.selectFirst(".story-body__h1") != null)
            {
                if (doc.selectFirst(".story-body").selectFirst(".comment-count") != null)
                    return true;
            }

            return false;

        } catch (IOException e)
        {
            return false;
        }
    }

    private Elements secondaryHeadlineElements(Document doc)
    {
        Element container = doc.selectFirst("#nw-c-topstories-domestic");

        if (container == null)
            return null;

        return container.select(".nw-c-top-stories__secondary-item");
    }

    private Elements tertiaryHeadlineElements(Document doc)
    {
        Element container = doc.selectFirst("#nw-c-topstories-domestic");

        if (container == null)
            return null;

        Element tertiaryContainer = container.selectFirst(".nw-c-top-stories__tertiary-items>div");

        if (tertiaryContainer == null)
            return null;

        return tertiaryContainer.children();
    }
}
