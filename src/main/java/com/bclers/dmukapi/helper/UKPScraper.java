package com.bclers.dmukapi.helper;

import com.bclers.dmukapi.enums.CommentSortOrder;
import com.bclers.dmukapi.enums.CommentSortType;
import com.bclers.dmukapi.enums.NewsSource;
import com.bclers.dmukapi.model.Comment;
import com.bclers.dmukapi.utils.PropertyUtils;
import lombok.SneakyThrows;
import lombok.val;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.models.DistinguishedStatus;
import net.dean.jraw.models.Submission;
import net.dean.jraw.oauth.*;

import java.io.*;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UKPScraper extends WebScraper
{
    private final String APP_ID = "com.bclers.dmukapi";
    private final String APP_VERSION = "v0.3";
    private final List<String> KNOWN_BOTS = Arrays.asList("GBPolBot", "PaperboyUK", "UKPoliticsBot", "AutoModerator");

    private RedditClient reddit;

    /**
     * Initialises the Scraper using the default r/ukpolitics URL of
     * <em>https://www.reddit.com/r/ukpolitics</em>
     */
    public UKPScraper()
    {
        this("https://www.reddit.com/r/ukpolitics");
    }

    public UKPScraper(String urlToScrape)
    {
        super(urlToScrape);

        reddit = setupRedditClient();
    }

    @Override
    public Map<String, String> getArticles()
    {
        String baseUrl = "https://www.reddit.com";

        List<Submission> posts = getRedditPosts();

        if (posts == null)
            return new HashMap<>();

        Map<String, String> output = new HashMap<>();

        for (Submission post : posts)
        {
            output.put(baseUrl + post.getPermalink(), post.getTitle());
        }

        return output;
    }

    @Override
    @SneakyThrows
    public Map<String, String> getArticlesWithComments()
    {
        String baseUrl = "https://www.reddit.com";

        List<Submission> posts = getRedditPosts();

        if (posts == null)
            return new HashMap<>();

        Map<String, String> output = new HashMap<>();

        for (Submission post : posts)
        {
            if (post.getCommentCount() == 0)
                continue;

            output.put(baseUrl + post.getPermalink(), post.getTitle());
        }

        return output;
    }

    @Override
    public String getArticleTitle()
    {
        String postId = getPostId(this.getUrl());
        String articleTitle;

        if (postId == null)
            return null;

        try {
            articleTitle = reddit.submission(postId).inspect().getTitle();
        } catch (Exception e1){
            //Error thrown on bad url is a HTTPException, but trying to catch it doesnt prevent the error
            return null;
        }

        return articleTitle;
    }

    @Override
    @SneakyThrows
    public List<Comment> getComments(CommentSortType sortType, CommentSortOrder sortOrder)
    {
        // Store Article title here so we dont have to call the function for each comment
        String articleTitle = getArticleTitle();

        String postId = getPostId(this.getUrl());

        if (postId == null)
            return null;

        List<Comment> output = new ArrayList<>();
        val commentIterator = reddit.submission(postId).comments().walkTree().iterator();

        // TODO cut out of loop when comment score falls below a certain threshold to avoid low quality comments

        while (commentIterator.hasNext())
        {
            val currentComment = commentIterator.next().getSubject();

            // Skip empty (deleted) comments.
            if (currentComment.getBody() == null || currentComment.getBody().isEmpty())
                continue;

            // Skip bot comments.
            if (KNOWN_BOTS.contains(currentComment.getAuthor()))
                continue;

            // Skip admin/moderator comments.
            if (currentComment.getDistinguished() == DistinguishedStatus.ADMIN ||
                    currentComment.getDistinguished() == DistinguishedStatus.MODERATOR)
                continue;

            val convertedDate = currentComment.getCreated().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            Comment c = new Comment();
            c.setBody(currentComment.getBody());
            c.setAuthor(currentComment.getAuthor());
            c.setScore(currentComment.getScore());
            c.setCommentSource(NewsSource.R_UKPOLITICS);
            c.setLocalSiteCmtID(currentComment.getUniqueId());
            c.setDate(convertedDate);
            c.setArticleTitle(articleTitle);

            output.add(c);
        }

        return output;
    }

    @SneakyThrows
    private RedditClient setupRedditClient()
    {
        String username;
        String clientId;
        String clientSecret;
        String redirectUrl;

        if (System.getenv("GH_REDDIT_USERNAME") != null)
        {
            username = System.getenv("GH_REDDIT_USERNAME");
            clientId = System.getenv("GH_REDDIT_CLIENT_ID");
            clientSecret = System.getenv("GH_REDDIT_CLIENT_SECRET");
            redirectUrl = System.getenv("GH_REDDIT_REDIRECT");
        }
        else
        {
            Properties props = PropertyUtils.loadPropertiesFile("reddit.properties");
            username = props.getProperty("reddit.username");
            clientId = props.getProperty("reddit.clientid");
            clientSecret = props.getProperty("reddit.clientsecret");
            redirectUrl = props.getProperty("reddit.redirect");
        }

        // Set up JRAW Reddit client with relevant API access details:
        UserAgent ua = new UserAgent("windows", APP_ID, APP_VERSION, username);
        Credentials creds = Credentials.webapp(clientId, clientSecret, redirectUrl);
        NetworkAdapter adapter = new OkHttpNetworkAdapter(ua);

        TokenStore store = new JsonFileTokenStore(new File("tokenstore.json"));
        AccountHelper helper = new AccountHelper(adapter, creds, store, UUID.randomUUID());

        return helper.switchToUserless();
    }

    private List<Submission> getRedditPosts()
    {
        if (!isRedditUrl(this.getUrl()))
            return null;

        if (isPost(this.getUrl()))
            return null;

        return reddit.subreddit(getSubreddit(this.getUrl())).posts().build().accumulateMerged(1);
    }

    private boolean isRedditUrl(String url)
    {
        Pattern urlPattern = Pattern.compile("^https?://(?:www\\.)?reddit\\.com");
        Matcher postIdMatcher = urlPattern.matcher(url);

        return postIdMatcher.find();
    }

    private boolean isPost(String url)
    {
        Pattern urlPattern = Pattern.compile("reddit\\.com/r/\\w+/comments/\\w+/\\w+/?");
        Matcher postIdMatcher = urlPattern.matcher(url);

        return postIdMatcher.find();
    }

    /**
     * Given a reddit post URL, returns the ID for that post.
     *
     * @param url Reddit URL to parse
     * @return Post ID as a String
     */
    private String getPostId(String url)
    {
        Pattern urlPattern = Pattern.compile("reddit\\.com/r/\\w+/comments/(\\w+)(/\\w+)?/?");
        Matcher postIdMatcher = urlPattern.matcher(url);

        if (!postIdMatcher.find())
            return null;

        return postIdMatcher.group(1);
    }

    private String getSubreddit(String url)
    {
        Pattern urlPattern = Pattern.compile("reddit\\.com/r/(\\w+)/?.*");
        Matcher subredditMatcher = urlPattern.matcher(url);

        if (!subredditMatcher.find())
            return null;

        return subredditMatcher.group(1);
    }

    @Override
    public boolean containsComments()
    {
        return getArticleTitle() != null;
    }
}
