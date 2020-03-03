package com.bclers.dmukapi.exception;

public class InternalServerException extends Exception
{
    public InternalServerException()
    {
        super();
    }

    public InternalServerException(String message)
    {
        super(message);
    }

    public InternalServerException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InternalServerException(Throwable cause)
    {
        super(cause);
    }

    public InternalServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
