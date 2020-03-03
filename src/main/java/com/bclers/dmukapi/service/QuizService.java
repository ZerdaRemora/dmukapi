package com.bclers.dmukapi.service;

import com.bclers.dmukapi.dataprovider.CommentQuizProvider;
import com.bclers.dmukapi.enums.NewsSource;
import com.bclers.dmukapi.model.Comment;
import com.bclers.dmukapi.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class QuizService
{
    private CommentRepository commentRepository;
    private CommentQuizProvider commentQuizProvider;

    public QuizService(CommentRepository commentRepository)
    {
        this.commentRepository = commentRepository;
    }

    public void startQuiz(boolean usebbc, boolean usedm, boolean useukp, int cmtcount)
    {
        List<Comment> quizCmts = quizStartup(cmtcount, usebbc, usedm, useukp);

        while (quizCmts.size() > cmtcount)
        {
            Random r = new Random();
            quizCmts.remove(r.nextInt(quizCmts.size()));
        }

        // Create new CommentProviderClass and pass in the arraylist to use
        commentQuizProvider = new CommentQuizProvider(quizCmts, usebbc, usedm, useukp);
    }

    public Comment pullNextComment()
    {
        return commentQuizProvider.pullNext();
    }

    public Map<String, Boolean> pullSources()
    {
        return commentQuizProvider.pullSources();
    }

    private List<Comment> quizStartup(int numCmt, boolean useBBC, boolean useDM, boolean useUKP)
    {
        List<Comment> totalList = new ArrayList<>();
        Random r = new Random();

        if (useBBC)
        {
            List<Comment> bbcList = new ArrayList<>(commentRepository.findAllByCommentSource(NewsSource.BBC));
            if (bbcList != null)
            {
                if (bbcList.size() < numCmt)
                    numCmt = bbcList.size();

                for (int i = 0; i < numCmt; i++)
                {
                    int randIndex = r.nextInt(bbcList.size());
                    totalList.add(bbcList.get(randIndex));
                    bbcList.remove(randIndex);
                }
            }
        }

        if (useDM)
        {
            List<Comment> dmList = new ArrayList<>(commentRepository.findAllByCommentSource(NewsSource.DAILYMAIL));
            if (dmList != null)
            {
                if (dmList.size() < numCmt)
                    numCmt = dmList.size();

                for (int i = 0; i < numCmt; i++)
                {
                    int randIndex = r.nextInt(dmList.size());
                    totalList.add(dmList.get(randIndex));
                    dmList.remove(randIndex);
                }
            }
        }

        if (useUKP)
        {
            List<Comment> ukpList = new ArrayList<>(commentRepository.findAllByCommentSource(NewsSource.R_UKPOLITICS));
            if (ukpList != null)
            {
                if (ukpList.size() < numCmt)
                    numCmt = ukpList.size();

                for (int i = 0; i < numCmt; i++)
                {
                    int randIndex = r.nextInt(ukpList.size());
                    totalList.add(ukpList.get(randIndex));
                    ukpList.remove(randIndex);
                }
            }
        }

        return totalList;
    }
}
