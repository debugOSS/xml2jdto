package io.github.debug.xml2jdto.core.jaxb.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.ValidationEventHandler;

/**
 * The {@code XsdValidationEventCollector} class implements the {@code ValidationEventHandler} interface to collect validation events during XML
 * un/marshalling.
 * <p>
 * This class provides methods to clear the collected validation events and retrieve them as an unmodifiable list.
 * </p>
 * 
 * @author scheffer.imrich
 */
public class XsdValidationEventCollector implements ValidationEventHandler {

    private List<ValidationEvent> events;

    /**
     * Default constructor, constructs a new object.
     */
    public XsdValidationEventCollector() {
        super();
    }

    /**
     * Clears all events from the events list. This method removes all elements from the list returned by {@link #getEventsList()}.
     */
    public void clearEvents() {
        getEventsList().clear();
    }

    /**
     * Retrieves an unmodifiable list of validation events.
     *
     * @return an unmodifiable list of {@link ValidationEvent} objects.
     */
    public List<ValidationEvent> getEvents() {
        return Collections.unmodifiableList(getEventsList());
    }

    private List<ValidationEvent> getEventsList() {
        if (events == null) {
            events = new ArrayList<>();
        }
        return events;
    }

    @Override
    public boolean handleEvent(ValidationEvent event) {
        getEventsList().add(event);
        // don't break the un/marshalling process
        return true;
    }
}
