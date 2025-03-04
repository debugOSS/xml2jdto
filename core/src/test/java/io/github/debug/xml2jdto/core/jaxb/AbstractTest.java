package io.github.debug.xml2jdto.core.jaxb;

import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;

/**
 * AbstractTest is an abstract base class for test classes. It sets up logging configuration before any test classes are executed.
 * 
 * @see LogLevelRule
 * @see JaxbUtilgetSchemaTest
 * 
 * @author scheffer.imrich
 */
public abstract class AbstractTest {

    @BeforeAll
    public static void beforeAll() {
        // need for visual control of logging
        try (InputStream is = JaxbUtilgetSchemaTest.class.getResourceAsStream("/logging.properties")) {
            LogManager.getLogManager().readConfiguration(is);
        } catch (Exception e) {
            Logger.getLogger(AbstractTest.class.getName()).severe("Error while reading logging.properties file");
        }
    }
}
