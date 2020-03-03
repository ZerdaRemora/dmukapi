package com.bclers.dmukapi.repository;

import com.bclers.dmukapi.enums.NewsSource;
import com.bclers.dmukapi.model.Comment;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentRepositoryTest
{
    @Autowired
    private CommentRepository repository;

    @Test
    void testFindById()
    {
        Comment c = new Comment();
        c.setBody("Test Body");
        c.setAuthor("JUnit");
        c.setDate(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        c.setScore(123);
        c.setCommentSource(NewsSource.BBC);
        c.setLocalSiteCmtID("LocalID");

        c = repository.save(c);
        int id = c.getId();

        Comment response = repository.findById(id).orElse(null);

        assertNotNull(response);
        assertEquals(c, response);

        // Cleanup
        repository.delete(response);
    }

    @Test
    void testFindAll()
    {
        Comment firstComment = new Comment("Comment1", "Author1", LocalDateTime.now());
        Comment secondComment = new Comment("Comment2", "Author2", LocalDateTime.now());
        Comment thirdComment = new Comment("Comment3", "Author3", LocalDateTime.now());

        firstComment = repository.save(firstComment);
        secondComment = repository.save(secondComment);
        thirdComment = repository.save(thirdComment);

        List<Comment> comments = (List<Comment>) repository.findAll();

        assertTrue(comments.size() >= 3);

        // Cleanup
        repository.delete(firstComment);
        repository.delete(secondComment);
        repository.delete(thirdComment);
    }

}