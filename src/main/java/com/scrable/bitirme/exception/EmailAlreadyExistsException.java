package com.scrable.bitirme.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message)
    {
        super(message);
    }
}
