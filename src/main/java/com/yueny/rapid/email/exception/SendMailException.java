package com.yueny.rapid.email.exception;

/**
 * Send Email Exception
 */
public class SendMailException extends Exception {

    public SendMailException() {
        //.
    }

    public SendMailException(String message) {
        super(message);
    }

    public SendMailException(String message, Throwable cause) {
        super(message, cause);
    }

    public SendMailException(Throwable cause) {
        super(cause);
    }
}
