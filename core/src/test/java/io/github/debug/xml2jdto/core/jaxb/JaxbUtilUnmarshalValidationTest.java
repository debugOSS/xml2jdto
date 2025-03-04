package io.github.debug.xml2jdto.core.jaxb;

import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.ValidationEvent;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

import io.github.debug.xml2jdto.core.dto.Person;
import io.github.debug.xml2jdto.core.exception.InvalidMethodParameterException;
import io.github.debug.xml2jdto.core.exception.InvalidXmlSchemaException;
import io.github.debug.xml2jdto.core.exception.MalformedXmlException;

public class JaxbUtilUnmarshalValidationTest {

    private static final String VALID_XML = "<MyObject><name>Test</name></MyObject>";
    private static final String INVALID_XML = "<MyObject><name>Test</name>";
    private static final String XSD_PATH = "xsd/valid-schema.xsd";

    @XmlRootElement(name = "MyObject")
    private static class MyObject {
        private String name;

        public String getName() {
            return name;
        }

        // used in jaxb marhaller inner logic
        @SuppressWarnings("unused")
        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void testUnmarshal_withValidXmlAndNoSchema() {
        MyObject result = JaxbUtil.unmarshal(VALID_XML, MyObject.class, null);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo("Test");
    }

    @Test
    public void testUnmarshal_withNullXml() {
        MyObject result = JaxbUtil.unmarshal(null, MyObject.class, null);
        Assertions.assertThat(result).isNull();
    }

    @Test
    public void testUnmarshal_withNullClass() {
        Assertions.assertThatThrownBy(() -> JaxbUtil.unmarshal(VALID_XML, null, null))
                .isInstanceOf(InvalidMethodParameterException.class)
                .hasMessage(JaxbUtil.CLAZZ_NULL_MSG);
    }

    @Test
    public void testUnmarshal_withInvalidXml() {
        try {
            JaxbUtil.unmarshal(INVALID_XML, MyObject.class, XSD_PATH);
            Assertions.fail("Expected MalformedXmlException");
        } catch (MalformedXmlException e) {
            Assertions.assertThat(e.getMessage()).startsWith(MalformedXmlException.MALFORMED_XML_MSG);
            Assertions.assertThat(e.getCause().getClass()).isEqualTo(UnmarshalException.class);
            if (e.getCause() instanceof UnmarshalException ue) {
                Assertions.assertThat(ue.getLinkedException().toString())
                        .isEqualTo(
                                "org.xml.sax.SAXParseException; lineNumber: 1; columnNumber: 28; XML document structures must start and end within the same entity.");
            }
        }
    }

    @Test
    public void testUnmarshal_withValidXml() {
        String xml = """
                <person>
                    <name>Test</name>
                    <age>11</age>
                    <address>123 Any Street</address>
                </person>
                """;
        Person result = JaxbUtil.unmarshal(xml, Person.class, XSD_PATH);
        Assertions.assertThat(result.getName()).isEqualTo("Test");
        Assertions.assertThat(result.getAge()).isEqualTo(11);
        Element address = (Element) result.getAddress();
        Assertions.assertThat(address.getTextContent()).isEqualTo("123 Any Street");
    }

    @Test
    public void testUnmarshal_withOtherXsd() {
        String xsdPath = "xsd/valid-schema.xsd";
        try {
            JaxbUtil.unmarshal(VALID_XML, MyObject.class, xsdPath);
            Assertions.fail("Expected InvalidXmlSchemaException");
        } catch (InvalidXmlSchemaException e) {
            Assertions.assertThat(e.getMessage()).startsWith(InvalidXmlSchemaException.SCHEMA_VALIDATION_FAILED_MSG);
            Assertions.assertThat(e.getEvents()).hasSize(1);
            ValidationEvent event = e.getEvents().get(0);
            Assertions.assertThat(event.getSeverity()).isEqualTo(ValidationEvent.FATAL_ERROR);
            Assertions.assertThat(event.getMessage()).isEqualTo("cvc-elt.1.a: Cannot find the declaration of element 'MyObject'.");
            Assertions.assertThat(event.getLocator().toString()).isEqualTo("[node=null,object=null,url=null,line=1,col=11,offset=-1]");
        }
    }
}
