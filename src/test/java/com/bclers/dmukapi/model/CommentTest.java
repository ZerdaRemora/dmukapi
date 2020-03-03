package com.bclers.dmukapi.model;

import com.bclers.dmukapi.enums.NewsSource;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class CommentTest
{
    private int id = 66;
    private String localSiteCmtId = "LocalID66";
    private int score = 500;
    private String body = "This is a test comment.";
    private String author = "Bob";
    private LocalDateTime date = LocalDateTime.now();
    private String articleTitle = "Test aritcle passes test";
    private Date now = Calendar.getInstance().getTime();

    @Test
    void testCommentConstructors()
    {
        Comment c = new Comment(body);
        Comment d = new Comment(body, author, date);
        Comment e = new Comment(id, localSiteCmtId, body, author, score, date, NewsSource.BBC, articleTitle, now);

        assertEquals(body, c.getBody(), "Could not get body set in Comment constructor.");
        assertEquals(author, d.getAuthor(), "Could not get author set in Comment constructor.");
        assertEquals(date, d.getDate(), "Could not get date set in Comment constructor.");
        assertEquals(id, e.getId(), "Could not get ID set in Comment constructor");
        assertEquals(localSiteCmtId, e.getLocalSiteCmtID(), "Could not get local site ID set in Comment constructor");
        assertEquals(score, e.getScore(), "Could not get score set in comment constructor.");
        assertEquals(NewsSource.BBC, e.getCommentSource(), "Could not get source set in Comment constructor");
        assertEquals(articleTitle, e.getArticleTitle(), "Could not get article title set in Comment constructor");
    }

    @Test
    void testToString()
    {
        Comment c = new Comment(id, localSiteCmtId, body, author, score, date, NewsSource.BBC, articleTitle, now);
        String expected = String.format("%s %s %s %d %s %s %s", id, body, author, score, date, NewsSource.BBC, articleTitle);

        assertEquals(expected, c.toString());
    }
}