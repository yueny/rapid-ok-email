package com.yueny.rapid.email.exception;

/**
 * Send Email Exception
 */
public class SendMailException extends Exception {
    private String msg = "操作异常";

    public SendMailException() {
        //.
    }

    public SendMailException(String message) {
        super(message);
        this.msg = message;
    }

    public SendMailException(String message, Throwable cause) {
        super(message, cause);
        this.msg = message;
    }

    public SendMailException(Throwable cause) {
        super(cause);
        this.msg = cause.getMessage();
    }
}
