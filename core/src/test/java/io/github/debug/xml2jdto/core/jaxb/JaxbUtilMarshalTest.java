package io.github.debug.xml2jdto.core.jaxb;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import jakarta.xml.bind.MarshalException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import io.github.debug.xml2jdto.core.dto.Person;
import io.github.debug.xml2jdto.core.exception.Xml2jDtoException;

public class JaxbUtilMarshalTest {

    @Test
    public void testMarshal_NullDto() {
        String result = JaxbUtil.marshal(null, null);
        assertThat(result).isNull();
    }

    @Test
    public void testMarshal_ValidDto() {
        Person dto = new Person();
        dto.setName("Test Name");
        dto.setAge(30);
        dto.setAddress("Test Address");

        String result = JaxbUtil.marshal(dto, null);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result)
                .isEqualTo(
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><person><name>Test Name</name><age>30</age><address xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">Test Address</address></person>");
    }

    @Test
    public void testMarshal_WithProperties() {
        Person dto = new Person();
        dto.setName("Test Name");
        dto.setAge(30);
        dto.setAddress("Test Address íöüóőúéáűôňäýžťčšľ");

        String result = JaxbUtil.marshal(dto);
        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result)
                .isEqualTo(
                        """
                                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                                <person>
                                    <name>Test Name</name>
                                    <age>30</age>
                                    <address xsi:type="xs:string" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">Test Address íöüóőúéáűôňäýžťčšľ</address>
                                </person>
                                """);
    }

    @Test
    public void testMarshal_InvalidProperty() {
        Person dto = new Person();
        dto.setName("Test Name");
        dto.setAge(30);
        dto.setAddress("Test Address");

        Map<String, Object> properties = Map.of("invalid.property", "value");

        try {
            JaxbUtil.marshal(dto, properties);
            Assertions.fail("Should throw an Xml2jDtoException");
        } catch (Xml2jDtoException e) {
            Assertions.assertThat(e).isInstanceOf(Xml2jDtoException.class).hasMessageContaining("Failed to set property name[invalid.property]");
        }
    }

    @Test
    public void testMarshal_JAXBException() {
        TestDto dto = new TestDto();

        try {
            JaxbUtil.marshal(dto, null);
            Assertions.fail("Should throw an Xml2jDtoException");
        } catch (Xml2jDtoException e) {
            Assertions.assertThat(e)
                    .isInstanceOf(Xml2jDtoException.class)
                    .hasMessageContaining("Failed DTO[io.github.debug.xml2jdto.core.jaxb.JaxbUtilMarshalTest$TestDto] -> String conversion: [null]");
            Assertions.assertThat(e.getCause()).isInstanceOf(MarshalException.class);
            MarshalException cause = (MarshalException) e.getCause();
            Assertions.assertThat(cause.getLinkedException())
                    .isInstanceOf(SAXException.class)
                    .hasMessage(
                            "unable to marshal type \"io.github.debug.xml2jdto.core.jaxb.JaxbUtilMarshalTest$TestDto\" as an element because it is missing an @XmlRootElement annotation");
        }
    }

    // Test DTO class for testing purposes
    public static class TestDto {
        private int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
