package com.bclers.dmukapi.enums;

public enum NewsSource
{
    BBC
            {
                @Override
                public String getSourceURL()
                {
                    return "https://www.bbc.co.uk/news";
                }

                @Override
                public String getSourceName()
                {
                    return "BBC News";
                }
            },
    DAILYMAIL
            {
                @Override
                public String getSourceURL()
                {
                    return "https://www.dailymail.co.uk";
                }

                @Override
                public String getSourceName()
                {
                    return "Daily Mail";
                }
            },
    R_UKPOLITICS
            {
                @Override
                public String getSourceURL()
                {
                    return "https://www.reddit.com/r/ukpolitics";
                }

                @Override
                public String getSourceName()
                {
                    return "r/UKPolitics";
                }
            };

    public abstract String getSourceURL();

    public abstract String getSourceName();

    public static NewsSource convertFromString(String toConvert)
    {
        NewsSource toReturn = null;
        switch (toConvert)
        {
            case "bbc":
                toReturn = NewsSource.BBC;
                break;
            case "dailymail":
                toReturn = NewsSource.DAILYMAIL;
                break;
            case "ukpolitics":
                toReturn = NewsSource.R_UKPOLITICS;
                break;
        }
        return toReturn;
    }

}
