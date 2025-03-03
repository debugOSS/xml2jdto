package io.github.debug.xml2jdto.core.jaxb;

import java.io.InputStream;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.ValidationEvent;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.ls.LSResourceResolver;

import io.github.debug.xml2jdto.core.exception.ExBuilder;
import io.github.debug.xml2jdto.core.exception.InvalidMethodParameterException;
import io.github.debug.xml2jdto.core.exception.InvalidXmlSchemaException;
import io.github.debug.xml2jdto.core.exception.MalformedXmlException;
import io.github.debug.xml2jdto.core.exception.Xml2jDtoException;
import io.github.debug.xml2jdto.core.jaxb.catalog.CatalogResourceResolver;
import io.github.debug.xml2jdto.core.jaxb.event.XsdValidationEventCollector;

/**
 * Utility class for working with JAXB (Java Architecture for XML Binding).
 * <p>
 * This class provides methods to retrieve {@link JAXBContext} instances and to unmarshal XML strings into Java objects. It uses a cache to store
 * {@link JAXBContext} instances for different classes to improve performance.
 * </p>
 * <p>
 * Example usage:
 * 
 * <pre>
 * {@code
 * MyObject obj = JaxbUtil.unmarshal(xmlString, MyObject.class);
 * }
 * </pre>
 * </p>
 * <p>
 * Thread-safety: This class is thread-safe.
 * </p>
 * 
 * @author scheffer.imrich
 */
public final class JaxbUtil {

    private static final Map<String, JAXBContext> jaxbContextCache = new ConcurrentHashMap<>();

    private static final Logger log = Logger.getLogger(JaxbUtil.class.getName());

    public static final String CLAZZ_NULL_MSG = "clazz cannot be null!";

    private JaxbUtil() {
        super();
    }

    /**
     * Retrieves the {@link JAXBContext} for the given class. If the context is already cached, it returns the cached instance. Otherwise, it creates
     * a new {@link JAXBContext}, caches it, and then returns it.
     * <p>
     * The JAXBContext must be created only once for each class, as creating it multiple times can cause performance issues.
     * 
     * @param clazz
     *            the class for which the {@link JAXBContext} is to be retrieved
     * @return the {@link JAXBContext} for the given class
     * @throws JAXBException
     *             if an error occurs while creating the {@link JAXBContext}
     * @throws InvalidMethodParameterException
     *             if the provided class is null
     */
    public static JAXBContext getJAXBContext(Class<?> clazz) throws JAXBException {
        if (clazz == null) {
            throw new InvalidMethodParameterException(CLAZZ_NULL_MSG);
        }
        String className = clazz.getName();
        if (jaxbContextCache.containsKey(className)) {
            return jaxbContextCache.get(className);
        } else {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            jaxbContextCache.put(className, jaxbContext);
            return jaxbContext;
        }
    }

    /**
     * Unmarshals the given XML string into an object of the specified class type.
     *
     * @param <T>
     *            the type of the object to be returned
     * @param xml
     *            the XML string to be unmarshalled
     * @param clazz
     *            the class of the object to be returned
     * @return the unmarshalled object of type T, or null if the XML string is blank
     * @throws InvalidMethodParameterException
     *             if the clazz parameter is null
     * @throws Xml2jDtoException
     *             if an error occurs during unmarshalling
     */
    @SuppressWarnings("unchecked")
    public static <T> T unmarshal(String xml, Class<T> clazz) {
        if (StringUtils.isBlank(xml)) {
            return null;
        }
        if (clazz == null) {
            throw new InvalidMethodParameterException(CLAZZ_NULL_MSG);
        }
        try {
            JAXBContext jaxbContext = getJAXBContext(clazz);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return (T) unmarshaller.unmarshal(new StringReader(xml));
        } catch (JAXBException e) {
            // we should not log the whole message, because it can be very long
            throw ExBuilder.newXml2jDtoException()
                    .withMessage(
                            "Unmarshalling error for class [{0}], XML [{1}]: [{2}]",
                            clazz.getName(),
                            StringUtils.abbreviate(xml, 500),
                            e.getLocalizedMessage())
                    .withCause(e)
                    .build();
        }
    }

    /**
     * Retrieves a {@link Schema} object based on the provided XSD path and resource resolver.
     *
     * @param xsdPath
     *            the path to the XSD file. Must not be blank.
     * @param lsResourceResolver
     *            the resource resolver to use. Must not be null.
     * @return the {@link Schema} object created from the XSD file.
     * @throws InvalidMethodParameterException
     *             if the xsdPath is blank or the lsResourceResolver is null.
     * @throws Xml2jDtoException
     *             if the schema cannot be found or an unexpected error occurs during schema creation.
     */
    public static Schema getSchema(String xsdPath, LSResourceResolver lsResourceResolver) {
        if (StringUtils.isBlank(xsdPath) || Objects.isNull(lsResourceResolver)) {
            throw new InvalidMethodParameterException(
                    MessageFormat.format("xsdPath cannot be null: [{0}] or lsResourceResolver cannot be null: [{1}]!", xsdPath, lsResourceResolver));
        }

        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(xsdPath);
            if (stream == null) {
                throw ExBuilder.newXml2jDtoException().withMessage("Schema on path [{0}] cannot be found!", xsdPath).build();
            }

            StreamSource src = new StreamSource(stream);
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            sf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            sf.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            sf.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            sf.setResourceResolver(lsResourceResolver);
            Schema schema = sf.newSchema(src);
            log.fine(MessageFormat.format("Schema creation finished for XSD: [{0}]", xsdPath));
            return schema;
        } catch (Xml2jDtoException e) {
            throw e;
        } catch (Exception e) {
            throw ExBuilder.newXml2jDtoException()
                    .withMessage("Unexpected error during schema creation for XSD: [{0}]", xsdPath)
                    .withCause(e)
                    .build();
        }
    }

    /**
     * Unmarshals the given XML string into an object of the specified class type. If the provided XSD path is not null, the unmarshalling process is
     * schema-validated.
     * 
     * @param <T>
     *            the type of the object to be returned
     * @param xml
     *            the XML string to be unmarshalled
     * @param clazz
     *            the class of the object to be returned
     * @param xsdPath
     *            the path to the XSD file. If null, no schema validation is performed.
     * @return the unmarshalled object of type T, or null if the XML string is blank
     * @throws InvalidMethodParameterException
     *             if the clazz parameter is null
     * @throws Xml2jDtoException
     *             if an error occurs during unmarshalling
     */
    @SuppressWarnings("unchecked")
    public static <T> T unmarshal(String xml, Class<T> clazz, String xsdPath) {
        if (Objects.isNull(xml)) {
            return null;
        }
        if (clazz == null) {
            throw new InvalidMethodParameterException(CLAZZ_NULL_MSG);
        }
        XsdValidationEventCollector eventCollector = new XsdValidationEventCollector();
        List<ValidationEvent> events = new ArrayList<>();
        try {
            JAXBContext jaxbContext = getJAXBContext(clazz);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setEventHandler(eventCollector);
            if (xsdPath != null) {
                CatalogResourceResolver resourceResolver = new CatalogResourceResolver();
                Schema schema = getSchema(xsdPath, resourceResolver);
                if (schema != null) {
                    unmarshaller.setSchema(schema);
                }
            }
            T result = (T) unmarshaller.unmarshal(new StringReader(xml));

            events = eventCollector.getEvents();
            if (!events.isEmpty()) {
                throw new InvalidXmlSchemaException(events);
            }

            return result;
        } catch (UnmarshalException e) {
            throw new MalformedXmlException(events, e);
        } catch (JAXBException e) {
            // we should not log the whole message, because it can be very long
            throw ExBuilder.newXml2jDtoException()
                    .withMessage(
                            "Unmarshalling error for class [{0}], XML [{1}]: [{2}]",
                            clazz.getName(),
                            StringUtils.abbreviate(xml, 500),
                            e.getLocalizedMessage())
                    .withCause(e)
                    .build();
        }
    }

}
