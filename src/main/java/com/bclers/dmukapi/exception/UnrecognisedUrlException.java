package com.bclers.dmukapi.exception;

public class UnrecognisedUrlException extends Exception
{
    public UnrecognisedUrlException()
    {
        super();
    }

    public UnrecognisedUrlException(String message)
    {
        super(message);
    }

    public UnrecognisedUrlException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public UnrecognisedUrlException(Throwable cause)
    {
        super(cause);
    }

    public UnrecognisedUrlException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
