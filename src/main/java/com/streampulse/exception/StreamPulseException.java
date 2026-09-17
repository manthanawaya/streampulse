package com.streampulse.exception;

public class StreamPulseException extends Exception {
    public StreamPulseException(String message) {
        super(message);
    }

    public StreamPulseException(String message, Throwable cause) {
        super(message, cause);
    }
}
