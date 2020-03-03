package com.bclers.dmukapi.dataprovider;

import com.bclers.dmukapi.model.Comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CommentQuizProvider
{
    private List<Comment> quizList;
    private Boolean useBBC;
    private Boolean useDM;
    private Boolean useUKP;


    public CommentQuizProvider(List<Comment> cmtList, boolean useBBC, boolean useDM, boolean useUKP)
    {
        quizList = cmtList;
        this.useBBC = useBBC;
        this.useDM = useDM;
        this.useUKP = useUKP;
    }

    public Map<String, Boolean> pullSources()
    {
        Map<String, Boolean> toReturn = new HashMap<>();
        toReturn.put("bbc", useBBC);
        toReturn.put("dailymail", useDM);
        toReturn.put("ukpolitics", useUKP);

        return toReturn;
    }

    public Comment pullNext()
    {
        if (quizList.size() != 0)
        {
            Random r = new Random();
            int selectedIndex = r.nextInt(quizList.size());
            Comment toReturn = quizList.get(selectedIndex);
            quizList.remove(selectedIndex);
            return toReturn;
        }
        else
        {
            return null;
        }
    }
}
