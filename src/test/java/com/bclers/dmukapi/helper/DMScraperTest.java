package com.bclers.dmukapi.helper;

import com.bclers.dmukapi.enums.CommentSortOrder;
import com.bclers.dmukapi.enums.CommentSortType;
import com.bclers.dmukapi.model.Comment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DMScraperTest
{

    @Test
    void testCanGetHeadlines()
    {
        DMScraper scraper = new DMScraper();

        Map<String, String> headlines = scraper.getArticlesWithComments();

        assertNotNull(headlines, "Returned headlines map was null.");
        assertFalse(headlines.isEmpty(), "Returned headlines map was empty.");
    }

    @Test
    void testGetHeadlinesIsEmptyWithArticleURL()
    {
        DMScraper scraper = new DMScraper("https://www.dailymail.co.uk/news/article-7653833/Boris-Johnson-says-Jeremy-Corbyn-hates-wealth-creators-compares-STALIN.html");

        Map<String, String> headlines = scraper.getArticlesWithComments();

        // TODO: Instead of returning an empty map, should we return null or throw an exception instead?
        assertNotNull(headlines, "Headlines map was null.");
        assertTrue(headlines.isEmpty(), "Headlines map was not empty.");
    }

    @Test
    void testGetHeadlinesIsEmptyWithIncorrectURL()
    {
        DMScraper scraper = new DMScraper("https://www.reddit.com/r/ukpolitics/");

        Map<String, String> headlines = scraper.getArticlesWithComments();

        // As above, should this return null or throw an exception?
        assertNotNull(headlines, "Headlines map was null.");
        assertTrue(headlines.isEmpty(), "Headlines map was not empty.");
    }

    @Test
    void testCanGetArticleTitle()
    {
        DMScraper scraper = new DMScraper("https://www.dailymail.co.uk/news/article-7651003/Couple-wake-huge-bull-named-Arnold-staring-garden.html");

        String title = scraper.getArticleTitle();

        // Title correct as of 06/11/2019.
        assertEquals("Bull dozer! Couple wakes up to find a bull named Arnold and weighing nearly a tonne staring up at them from the back garden",
                title, "Article title was incorrect.");
    }

    @Test
    void testGetArticleTitleReturnsNullWithIncorrectURL()
    {
        DMScraper scraper = new DMScraper("https://www.reddit.com/r/ukpolitics/comments/drr541/the_papers_05112019/");

        String title = scraper.getArticleTitle();

        assertNull(title, "Title String was not null.");
    }

    @Test
    void testCanGetComments()
    {
        // Article had comments as of 06/11/2019.
        DMScraper scraper = new DMScraper("https://www.dailymail.co.uk/news/article-7651003/Couple-wake-huge-bull-named-Arnold-staring-garden.html");

        List<Comment> comments = scraper.getComments(CommentSortType.HIGHEST_RATED, CommentSortOrder.DESCENDING);

        assertNotNull(comments, "Comments List was null.");
        assertFalse(comments.isEmpty(), "Comments List was empty.");
        assertNotNull(comments.get(0), "Comment object in list was null.");
    }

    @Test
    void testGetCommentsReturnsNullWithIncorrectURL()
    {
        DMScraper scraper = new DMScraper("https://www.reddit.com/r/ukpolitics/comments/drr541/the_papers_05112019/");

        List<Comment> comments = scraper.getComments(CommentSortType.HIGHEST_RATED, CommentSortOrder.DESCENDING);

        assertNull(comments, "Comments List was not null.");
    }
}