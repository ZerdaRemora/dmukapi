package com.bclers.dmukapi.service;

import com.bclers.dmukapi.enums.NewsSource;
import com.bclers.dmukapi.exception.CommentNotFoundException;
import com.bclers.dmukapi.model.Comment;
import com.bclers.dmukapi.repository.CommentRepository;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService
{
    private CommentRepository commentRepository;

    public StatisticsService(CommentRepository commentRepository)
    {
        this.commentRepository = commentRepository;
    }

    public Comment getHighestRatedComment() throws CommentNotFoundException
    {
        Comment bbc = commentRepository.findFirstByCommentSourceOrderByScoreDesc(NewsSource.BBC).orElse(null);
        Comment dm = commentRepository.findFirstByCommentSourceOrderByScoreDesc(NewsSource.DAILYMAIL).orElse(null);
        Comment ukp = commentRepository.findFirstByCommentSourceOrderByScoreDesc(NewsSource.R_UKPOLITICS).orElse(null);

        Comment output = null;

        if (bbc != null && dm != null && ukp != null)
        {
            output = bbc.getScore() > dm.getScore() ? bbc : dm;
            output = output.getScore() > ukp.getScore() ? output : ukp;
        }

        if (output == null)
        {
            throw new CommentNotFoundException("Could not find a highest rated comment.");
        }

        return output;
    }

    public Comment getLowestRatedComment() throws CommentNotFoundException
    {
        Comment bbc = commentRepository.findFirstByCommentSourceOrderByScoreAsc(NewsSource.BBC).orElse(null);
        Comment dm = commentRepository.findFirstByCommentSourceOrderByScoreAsc(NewsSource.DAILYMAIL).orElse(null);
        Comment ukp = commentRepository.findFirstByCommentSourceOrderByScoreAsc(NewsSource.R_UKPOLITICS).orElse(null);

        Comment output = null;

        if (bbc != null && dm != null && ukp != null)
        {
            output = bbc.getScore() < dm.getScore() ? bbc : dm;
            output = output.getScore() < ukp.getScore() ? output : ukp;
        }

        if (output == null)
        {
            throw new CommentNotFoundException("Could not find a lowest rated comment.");
        }

        return output;
    }
}
