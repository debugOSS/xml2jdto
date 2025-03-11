package io.github.debug.xml2jdto.core.jaxb;

import java.io.IOException;

import javax.xml.validation.Schema;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXParseException;

import io.github.debug.xml2jdto.core.exception.InvalidMethodParameterException;
import io.github.debug.xml2jdto.core.exception.Xml2jDtoException;
import io.github.debug.xml2jdto.core.jaxb.catalog.CatalogLsInputImpl;
import io.github.debug.xml2jdto.core.junit.LogLevel;
import io.github.debug.xml2jdto.core.junit.LogLevelExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(LogLevelExtension.class)
public class JaxbUtilgetSchemaTest extends AbstractTest {

    private static final String LOG_LEVEL_JAXBUTIL = "io.github.debug.xml2jdto.core.jaxb.JaxbUtil=ALL";

    @Test
    @LogLevel(packageToLevel = { LOG_LEVEL_JAXBUTIL })
    public void testGetSchema_withValidXsdPathAndResolver() {
        LSResourceResolver resolver = Mockito.mock(LSResourceResolver.class);
        String xsdPath = "xsd/common.xsd";

        Schema schema = JaxbUtil.loadSchemaFromXsdPath(xsdPath, resolver);

        Assertions.assertThat(schema).isNotNull();
    }

    @Test
    public void testGetSchema_withNullXsdPath() {
        LSResourceResolver resolver = Mockito.mock(LSResourceResolver.class);

        Assertions.assertThatThrownBy(() -> JaxbUtil.loadSchemaFromXsdPath(null, resolver))
                .isInstanceOf(InvalidMethodParameterException.class)
                .hasMessageContaining("xsdPath cannot be null");
    }

    @Test
    public void testGetSchema_withNullResolver() {
        String xsdPath = "valid-schema.xsd";

        Assertions.assertThatThrownBy(() -> JaxbUtil.loadSchemaFromXsdPath(xsdPath, null))
                .isInstanceOf(InvalidMethodParameterException.class)
                .hasMessageContaining("lsResourceResolver cannot be null");
    }

    @Test
    public void testGetSchema_withInvalidXsdPath() {
        LSResourceResolver resolver = Mockito.mock(LSResourceResolver.class);
        String xsdPath = "invalid-schema.xsd";

        Assertions.assertThatThrownBy(() -> JaxbUtil.loadSchemaFromXsdPath(xsdPath, resolver))
                .isInstanceOf(Xml2jDtoException.class)
                .hasMessageContaining("Schema on path [invalid-schema.xsd] cannot be found");
    }

    @Test
    public void testGetSchema_withExceptionDuringSchemaCreation() throws IOException {
        String xsdPath = "xsd/invalid-schema.xsd";
        LSResourceResolver lsResourceResolver = new LSResourceResolver() {
            @Override
            public CatalogLsInputImpl resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
                // no matter what is the input, we will return a valid CatalogLsInputImpl, but the schema creation will fail
                return new CatalogLsInputImpl("http://invalid/schema");
            }
        };

        try {
            JaxbUtil.loadSchemaFromXsdPath(xsdPath, lsResourceResolver);
            Assertions.fail("Expected Xml2jDtoException to be thrown");
        } catch (Xml2jDtoException e) {
            Assertions.assertThat(e.getLocalizedMessage()).startsWith("Unexpected error during schema creation for XSD");
            Assertions.assertThat(e.getCause().getClass()).isNotEqualTo(NullPointerException.class);
            Assertions.assertThat(e.getCause().getClass()).isEqualTo(SAXParseException.class);
        }
    }

}
