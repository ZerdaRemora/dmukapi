package com.bclers.dmukapi.enums;

public enum CommentSortOrder
{
    ASCENDING
            {
                @Override
                public String toString(NewsSource cmtSource)
                {
                    if (cmtSource == NewsSource.DAILYMAIL)
                    {
                        return "asc";
                    }
                    else
                    {
                        return ASCENDING.toString();
                    }
                }
            },
    DESCENDING
            {
                @Override
                public String toString(NewsSource cmtSource)
                {
                    if (cmtSource == NewsSource.DAILYMAIL)
                    {
                        return "desc";
                    }
                    else
                    {
                        return ASCENDING.toString();
                    }
                }
            };

    public abstract String toString(NewsSource cmtSource);

    public static CommentSortOrder convertFromString(String sortOrder)
    {
        if (sortOrder.toLowerCase().equals("ascending"))
        {
            return CommentSortOrder.ASCENDING;
        }
        else
        {
            return CommentSortOrder.DESCENDING;
        }
    }
}
