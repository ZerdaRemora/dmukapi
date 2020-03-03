package com.bclers.dmukapi.helper;

import com.bclers.dmukapi.enums.CommentSortOrder;
import com.bclers.dmukapi.enums.CommentSortType;
import com.bclers.dmukapi.model.Comment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UKPScraperTest
{

    @Test
    void testCanGetArticles()
    {
        UKPScraper scraper = new UKPScraper();

        Map<String, String> headlines = scraper.getArticles();

        assertNotNull(headlines, "Returned headlines map was null.");
        assertFalse(headlines.isEmpty(), "Returned headlines map was empty.");
    }

    @Test
    void testCanGetArticlesWithComments()
    {
        UKPScraper scraper = new UKPScraper();

        Map<String, String> headlines = scraper.getArticlesWithComments();

        assertNotNull(headlines, "Returned headlines map was null.");
        assertFalse(headlines.isEmpty(), "Returned headlines map was empty.");
    }

    @Test
    void testGetArticlesWithCommentsIsEmptyWithArticleURL()
    {
        UKPScraper scraper = new UKPScraper("https://www.reddit.com/r/ukpolitics/comments/drr541/the_papers_05112019/");

        Map<String, String> headlines = scraper.getArticlesWithComments();

        // TODO: Instead of returning an empty map, should we return null or throw an exception instead?
        assertNotNull(headlines, "Headlines map was null.");
        assertTrue(headlines.isEmpty(), "Headlines map was not empty.");
    }

    @Test
    void testGetArticlesWithCommentsIsEmptyWithIncorrectURL()
    {
        UKPScraper scraper = new UKPScraper("https://www.dailymail.co.uk/home/index.html");

        Map<String, String> headlines = scraper.getArticlesWithComments();

        // As above, should this return null or throw an exception?
        assertNotNull(headlines, "Headlines map was null.");
        assertTrue(headlines.isEmpty(), "Headlines map was not empty.");
    }

    @Test
    void testCanGetArticleTitle()
    {
        UKPScraper scraper = new UKPScraper("https://www.reddit.com/r/ukpolitics/comments/drr541/the_papers_05112019/");

        String title = scraper.getArticleTitle();

        // Title correct as of 06/11/2019.
        assertEquals("The Papers (05/11/2019)", title, "Article title was incorrect.");
    }

    @Test
    void testGetArticleTitleReturnsNullWithIncorrectURL()
    {
        UKPScraper scraper = new UKPScraper("https://www.dailymail.co.uk/news/article-7651003/Couple-wake-huge-bull-named-Arnold-staring-garden.html");

        String title = scraper.getArticleTitle();

        assertNull(title, "Title String was not null.");
    }

    @Test
    void testCanGetComments()
    {
        // Post had comments as of 06/11/2019.
        UKPScraper scraper = new UKPScraper("https://www.reddit.com/r/ukpolitics/comments/drr541/the_papers_05112019/");

        List<Comment> comments = scraper.getComments(CommentSortType.HIGHEST_RATED, CommentSortOrder.DESCENDING);

        assertNotNull(comments, "Comments List was null.");
        assertFalse(comments.isEmpty(), "Comments List was empty.");
        assertNotNull(comments.get(0), "Comment object in list was null.");
    }

    @Test
    void testGetCommentsReturnsNullWithIncorrectURL()
    {
        UKPScraper scraper = new UKPScraper("https://www.dailymail.co.uk/news/article-7651003/Couple-wake-huge-bull-named-Arnold-staring-garden.html");

        List<Comment> comments = scraper.getComments(CommentSortType.HIGHEST_RATED, CommentSortOrder.DESCENDING);

        assertNull(comments, "Comments List was not null.");
    }
}