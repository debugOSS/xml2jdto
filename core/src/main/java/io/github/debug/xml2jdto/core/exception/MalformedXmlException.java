package io.github.debug.xml2jdto.core.exception;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.ValidationEvent;

/**
 * Exception thrown when an XML is malformed.
 * <br/>
 * This exception is a subclass of {@link Xml2jDtoException}. It contains a list of {@link ValidationEvent} that describe the validation errors.
 * 
 * @author scheffer.imrich
 */
public class MalformedXmlException extends Xml2jDtoException {

    /**
     * Validation event list
     */
    private final List<ValidationEvent> events = new ArrayList<>();

    /**
     * A constant error message indicating that the XML is malformed.
     */
    public static final String MALFORMED_XML_MSG = "Xml malformed";

    /**
     * Constructs a new MalformedXmlException with the specified list of validation events and cause.
     *
     * @param events
     *            the list of validation events that caused the exception
     * @param e
     *            the cause of the exception
     */
    public MalformedXmlException(List<ValidationEvent> events, Throwable e) {
        super(MALFORMED_XML_MSG, e);
        this.events.addAll(events);
    }

    /**
     * Constructs a new MalformedXmlException with the specified list of validation events.
     *
     * @param events
     *            the list of validation events that caused the exception
     */
    public MalformedXmlException(List<ValidationEvent> events) {
        super(MALFORMED_XML_MSG);
        this.events.addAll(events);
    }

    /**
     * Retrieves the list of validation events associated with this exception.
     *
     * @return a list of {@link ValidationEvent} objects representing the validation events.
     */
    public List<ValidationEvent> getEvents() {
        return this.events;
    }
}
