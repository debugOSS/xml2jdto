package io.github.debug.xml2jdto.core.exception;

/**
 * Exception thrown when an invalid method parameter is encountered. This exception extends {@link Xml2jDtoException}.
 * 
 * @author scheffer.imrich
 */
public class InvalidMethodParameterException extends Xml2jDtoException {

    /**
     * Constructs a new InvalidMethodParameterException with the specified detail message.
     *
     * @param message
     *            the detail message, which is saved for later retrieval by the {@link Throwable#getMessage()} method.
     */
    public InvalidMethodParameterException(String message) {
        super(message);
    }
}
