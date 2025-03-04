package io.github.debug.xml2jdto.core.jaxb;

import jakarta.xml.bind.UnmarshalException;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.debug.xml2jdto.core.exception.InvalidMethodParameterException;
import io.github.debug.xml2jdto.core.exception.Xml2jDtoException;

public class JaxbUtilUnmarshalTest {

    @Test
    public void testUnmarshalWithNullXml() {
        String result = JaxbUtil.unmarshal(null, String.class);
        Assertions.assertThat(result).isNull();
    }

    @Test
    public void testUnmarshalWithNullClass() {
        Assertions.assertThatThrownBy(() -> {
            JaxbUtil.unmarshal("<example>test</example>", null);
        }).isInstanceOf(InvalidMethodParameterException.class);
    }

    @Test
    public void testUnmarshalWithInvalidXml() {
        try {
            JaxbUtil.unmarshal("<example>test", String.class);
            Assertions.fail("Expected Xml2jDtoException");
        } catch (Xml2jDtoException e) {
            Assertions.assertThat(e.getMessage()).startsWith("Unmarshalling error for class");
            Assertions.assertThat(e.getCause().getClass()).isEqualTo(UnmarshalException.class);
            Assertions.assertThat(e.getCause().getLocalizedMessage())
                    .isEqualTo("unexpected element (uri:\"\", local:\"example\"). Expected elements are (none)");
        }
    }

    @Test
    public void testUnmarshalWithValidXml() {
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><ExampleRoot><exampleValue>test</exampleValue></ExampleRoot>";
        SimpleExample result = JaxbUtil.unmarshal(xml, SimpleExample.class);
        Assertions.assertThat(result.getExampleValue()).isEqualTo("test");
    }

    @XmlRootElement(name = "ExampleRoot")
    public static class SimpleExample {

        private String exampleValue;

        public String getExampleValue() {
            return exampleValue;
        }

        public void setExampleValue(String exampleValue) {
            this.exampleValue = exampleValue;
        }
    }

}
