package com.bclers.dmukapi.exception;

public class UnrecognisedNewsSourceException extends Exception
{
    public UnrecognisedNewsSourceException()
    {
        super();
    }

    public UnrecognisedNewsSourceException(String message)
    {
        super(message);
    }

    public UnrecognisedNewsSourceException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public UnrecognisedNewsSourceException(Throwable cause)
    {
        super(cause);
    }

    public UnrecognisedNewsSourceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
