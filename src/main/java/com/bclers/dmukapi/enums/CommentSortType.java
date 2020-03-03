package com.bclers.dmukapi.enums;

public enum CommentSortType
{
    HIGHEST_RATED
            {
                @Override
                public String toString(NewsSource cmtSource)
                {
                    if (cmtSource == NewsSource.BBC)
                    {
                        return "HighestRating";
                    }
                    else
                    {
                        return HIGHEST_RATED.toString();
                    }
                }
            },
    LOWEST_RATED
            {
                @Override
                public String toString(NewsSource cmtSource)
                {
                    if (cmtSource == NewsSource.BBC)
                    {
                        return "LowestRating";
                    }
                    else
                    {
                        return LOWEST_RATED.toString();
                    }
                }
            },
    CREATED
            {
                @Override
                public String toString(NewsSource cmtSource)
                {
                    if (cmtSource == NewsSource.BBC)
                    {
                        return "Created";
                    }
                    else
                    {
                        return CREATED.toString();
                    }
                }
            };

    public abstract String toString(NewsSource cmtSource);

    public static CommentSortType convertFromString(String sortType)
    {
        //Default value
        CommentSortType toReturn = CommentSortType.HIGHEST_RATED;
        switch (sortType.toLowerCase())
        {
            case "highestrated":
                toReturn = CommentSortType.HIGHEST_RATED;
                break;
            case "lowestrated":
                toReturn = CommentSortType.LOWEST_RATED;
                break;
            case "created":
                toReturn = CommentSortType.CREATED;
                break;
        }
        return toReturn;
    }

}
