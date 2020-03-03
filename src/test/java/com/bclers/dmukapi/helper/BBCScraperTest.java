package com.bclers.dmukapi.helper;

import com.bclers.dmukapi.enums.CommentSortOrder;
import com.bclers.dmukapi.enums.CommentSortType;
import com.bclers.dmukapi.model.Comment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BBCScraperTest
{

    @Test
    void testCanGetHeadlines()
    {
        BBCScraper scraper = new BBCScraper();

        Map<String, String> headlines = scraper.getArticlesWithComments();

        assertNotNull(headlines, "Returned headlines map was null.");
        // BBCScraper.getHeadlines can return empty if none of the frontpage articles have comments enabled.
        //assertFalse(headlines.isEmpty(), "Returned headlines map was empty.");
    }

    @Test
    void testGetHeadlinesIsEmptyWithArticleURL()
    {
        BBCScraper scraper = new BBCScraper("https://www.bbc.co.uk/news/technology-50301665");

        Map<String, String> headlines = scraper.getArticlesWithComments();

        // TODO: Instead of returning an empty map, should we return null or throw an exception instead?
        assertNotNull(headlines, "Headlines map was null.");
        assertTrue(headlines.isEmpty(), "Headlines map was not empty.");
    }

    @Test
    void testGetHeadlinesIsEmptyWithIncorrectURL()
    {
        BBCScraper scraper = new BBCScraper("https://www.reddit.com/r/ukpolitics/");

        Map<String, String> headlines = scraper.getArticlesWithComments();

        // As above, should this return null or throw an exception?
        assertNotNull(headlines, "Headlines map was null.");
        assertTrue(headlines.isEmpty(), "Headlines map was not empty.");
    }

    @Test
    void testCanGetArticleTitle()
    {
        BBCScraper scraper = new BBCScraper("https://www.bbc.co.uk/news/technology-50301665");

        String title = scraper.getArticleTitle();

        // Title correct as of 06/11/2019.
        assertEquals("Xiaomi smartphone has 108 megapixel camera", title, "Article title was incorrect.");
    }

    @Test
    void testGetArticleTitleReturnsNullWithIncorrectURL()
    {
        BBCScraper scraper = new BBCScraper("https://www.reddit.com/r/ukpolitics/comments/drr541/the_papers_05112019/");

        String title = scraper.getArticleTitle();

        assertNull(title, "Title String was not null.");
    }

    @Test
    void testCanGetComments()
    {
        // Article had comments as of 06/11/2019.
        BBCScraper scraper = new BBCScraper("https://www.bbc.co.uk/news/technology-50293106");

        List<Comment> comments = scraper.getComments(CommentSortType.HIGHEST_RATED, CommentSortOrder.DESCENDING);

        assertNotNull(comments, "Comments List was null.");
        assertFalse(comments.isEmpty(), "Comments List was empty.");
        assertNotNull(comments.get(0), "Comment object in list was null.");
    }

    @Test
    void testGetCommentsReturnsNullWhenArticleHasNoComments()
    {
        // Article had no comments as of 06/11/2019.
        BBCScraper scraper = new BBCScraper("https://www.bbc.co.uk/news/technology-50289982");

        List<Comment> comments = scraper.getComments(CommentSortType.HIGHEST_RATED, CommentSortOrder.DESCENDING);

        assertNull(comments, "Comments List was not null.");
    }

    @Test
    void testGetCommentsReturnsNullWithIncorrectURL()
    {
        BBCScraper scraper = new BBCScraper("https://www.reddit.com/r/ukpolitics/comments/drr541/the_papers_05112019/");

        List<Comment> comments = scraper.getComments(CommentSortType.HIGHEST_RATED, CommentSortOrder.DESCENDING);

        assertNull(comments, "Comments List was not null.");
    }
}