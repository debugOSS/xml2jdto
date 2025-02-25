package io.github.debug.xml2jdto.core.exception;

/**
 * Exception thrown to indicate that an error occurred in the XML to DTO conversion process. This is a runtime exception, so it does not need to be
 * declared in a method's throws clause.
 * 
 * @see RuntimeException
 * 
 * @author scheffer.imrich
 */
public class Xml2jDtoException extends RuntimeException {

    /**
     * Constructs a new Xml2jDtoException with {@code null} as its detail message. The cause is not initialized, and may subsequently be initialized
     * by a call to {@link #initCause}.
     */
    public Xml2jDtoException() {
        super();
    }

    /**
     * Constructs a new Xml2jDtoException with the specified detail message.
     *
     * @param message
     *            the detail message
     */
    public Xml2jDtoException(String message) {
        super(message);
    }

    /**
     * Constructs a new Xml2jDtoException with the specified detail message and cause.
     *
     * @param message
     *            the detail message (which is saved for later retrieval by the {@link Throwable#getMessage()} method).
     * @param cause
     *            the cause (which is saved for later retrieval by the {@link Throwable#getCause()} method). (A null value is permitted, and indicates
     *            that the cause is nonexistent or unknown.)
     */
    public Xml2jDtoException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new Xml2jDtoException with the specified cause.
     *
     * @param cause
     *            the cause of the exception. A null value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public Xml2jDtoException(Throwable cause) {
        super(cause);
    }
}
