package io.github.debug.xml2jdto.core.exception;

import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * A builder class for creating instances of exceptions that extend {@link Xml2jDtoException}.
 *
 * @param <E>
 *            the type of exception to be built, which must extend {@link Xml2jDtoException}
 * 
 * @author scheffer.imrich
 */
public class ExBuilder<E extends Xml2jDtoException> {

    private Class<E> exClass;

    private String message;

    private Throwable cause;

    private ExBuilder() {
        super();
    }

    /**
     * Creates a new instance of ExBuilder for Xml2jDtoException.
     *
     * @return a new instance of ExBuilder for Xml2jDtoException
     */
    public static ExBuilder<Xml2jDtoException> newXml2jDtoException() {
        ExBuilder<Xml2jDtoException> builder = new ExBuilder<>();
        builder.exClass = Xml2jDtoException.class;
        return builder;
    }

    /**
     * Sets the message for the exception being built using a formatted string.
     *
     * @param format
     *            the format string
     * @param args
     *            the arguments referenced by the format specifiers in the format string
     * @return the current instance of {@code ExBuilder} with the updated message
     */
    public ExBuilder<E> withMessage(String format, Object... args) {
        message = MessageFormat.format(format, args);
        return this;
    }

    /**
     * Sets the cause for the exception being built.
     *
     * @param cause
     *            the cause of the exception
     * @return the current instance of {@code ExBuilder} with the updated cause
     */
    public ExBuilder<E> withCause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    /**
     * Builds an instance of the exception class specified by {@code exClass}.
     * <p>
     * The method attempts to create an instance of the exception class using different constructors based on the presence of {@code message} and
     * {@code cause}. If the instantiation fails, it falls back to creating an instance of {@link Xml2jDtoException}.
     * </p>
     *
     * @return an instance of the exception class specified by {@code exClass}, or an instance of {@link Xml2jDtoException} if instantiation fails.
     * @throws Xml2jDtoException
     *             if the exception class cannot be instantiated and the fallback also fails.
     */
    public E build() {
        try {
            if (message == null && cause == null) {
                return exClass.getDeclaredConstructor().newInstance();
            } else if (message != null && cause == null) {
                return exClass.getDeclaredConstructor(String.class).newInstance(message);
            } else if (message == null) {
                return exClass.getDeclaredConstructor(null, Throwable.class).newInstance(cause);
            } else {
                return exClass.getDeclaredConstructor(String.class, Throwable.class).newInstance(message, cause);
            }
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Logger.getLogger(ExBuilder.class.getName())
                    .warning("Failed to create instance of [" + exClass.getName() + "]. Using default Xml2jDtoException constructor.");
            if (message == null && cause == null) {
                return exClass.cast(new Xml2jDtoException());
            } else if (message != null && cause == null) {
                return exClass.cast(new Xml2jDtoException(message));
            } else if (message == null) {
                return exClass.cast(new Xml2jDtoException(cause));
            } else {
                return exClass.cast(new Xml2jDtoException(message, cause));
            }
        }
    }
}
