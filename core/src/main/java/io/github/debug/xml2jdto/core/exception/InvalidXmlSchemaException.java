package io.github.debug.xml2jdto.core.exception;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.xml.bind.ValidationEvent;

/**
 * Exception thrown when an XML schema validation fails. It means that the XML document is correct in terms of syntax, but it does not conform to the
 * schema requirements.
 * <p>
 * This exception is a subclass of {@link Xml2jDtoException}. It contains a list of {@link ValidationEvent} that provides details about the validation
 * errors.
 * 
 * @author scheffer.imrich
 */
public class InvalidXmlSchemaException extends Xml2jDtoException {

    /**
     * Validation event list
     */
    private final List<ValidationEvent> events;

    /**
     * Error message indicating that XML schema validation has failed.
     */
    public static final String SCHEMA_VALIDATION_FAILED_MSG = "Xml schema validation failed";

    /**
     * Constructs a new InvalidXmlSchemaException with the specified list of validation events and the cause of the exception.
     *
     * @param events
     *            the list of validation events that caused the exception
     * @param e
     *            the cause of the exception
     */
    public InvalidXmlSchemaException(List<ValidationEvent> events, Throwable e) {
        super(SCHEMA_VALIDATION_FAILED_MSG, e);
        this.events = List.copyOf(events);
    }

    /**
     * Constructs a new InvalidXmlSchemaException with the specified list of validation events.
     *
     * @param events
     *            the list of validation events that caused the exception
     */
    public InvalidXmlSchemaException(List<ValidationEvent> events) {
        super(SCHEMA_VALIDATION_FAILED_MSG);
        this.events = List.copyOf(events);
    }

    /**
     * Retrieves the list of validation events associated with this exception.
     *
     * @return a list of {@link ValidationEvent} objects representing the validation events.
     */
    public List<ValidationEvent> getEvents() {
        return this.events;
    }

    @Override
    public String getMessage() {
        if (events != null && !events.isEmpty()) {
            StringBuilder sb = new StringBuilder(super.getMessage());
            sb.append(", events: [");
            sb.append(events.stream().map(ValidationEvent::getMessage).collect(Collectors.joining(", ")));
            sb.append("]");
            return sb.toString();
        }
        return super.getMessage();
    }
}
