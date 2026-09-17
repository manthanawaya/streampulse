package com.streampulse.exception;

public class InvalidDataException extends StreamPulseException {
    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
