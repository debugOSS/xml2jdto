package io.github.debug.xml2jdto.core.jaxb;

import java.io.StringReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;

import io.github.debug.xml2jdto.core.exception.ExBuilder;
import io.github.debug.xml2jdto.core.exception.InvalidMethodParameterException;
import io.github.debug.xml2jdto.core.exception.Xml2jDtoException;

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
}
