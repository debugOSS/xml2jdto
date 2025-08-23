package io.github.debug.xml2jdto.core.jaxb;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.github.debug.xml2jdto.core.dto.Person;
import io.github.debug.xml2jdto.core.exception.InvalidXmlSchemaException;
import io.github.debug.xml2jdto.core.exception.Xml2jDtoException;

@DisplayName("JaxbUtil Marshal Tests")
public class JaxbUtilMarshalTest {

    private static final String XSD_PATH = "xsd/valid-schema.xsd";

    @DisplayName("Without Schema Tests")
    @Nested
    class NoSchema {

        @Test
        public void nullDtoNoProperties() {
            String result = JaxbUtil.marshal(null, (Map<String, Object>) null);
            assertThat(result).isNull();
        }

        @Test
        public void validDtoNoProperties() {
            Person dto = new Person();
            dto.setName("Test Name");
            dto.setAge(30);
            dto.setAddress("Test Address");

            String result = JaxbUtil.marshal(dto, (Map<String, Object>) null);
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result)
                    .isEqualTo(
                            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><person><name>Test Name</name><age>30</age><address xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">Test Address</address></person>");
        }

        @Test
        public void withDefaultProperties() {
            Person dto = new Person();
            dto.setName("Test Name");
            dto.setAge(30);
            dto.setAddress("Test Address íöüóőúéáűôňäýžťčšľ");

            String result = JaxbUtil.marshal(dto);
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result)
                    .isEqualTo(
                            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><person><name>Test Name</name><age>30</age><address xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">Test Address íöüóőúéáűôňäýžťčšľ</address></person>");
        }

        @Test
        public void withDefaultPropertiesFormatted() {
            Person dto = new Person();
            dto.setName("Test Name");
            dto.setAge(30);
            dto.setAddress("Test Address íöüóőúéáűôňäýžťčšľ");

            String result = JaxbUtil.marshalFormatted(dto);
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
        public void invalidProperty() {
            Person dto = new Person();
            dto.setName("Test Name");
            dto.setAge(30);
            dto.setAddress("Test Address");

            Map<String, Object> properties = Map.of("invalid.property", "value");

            Assertions.assertThatThrownBy(() -> JaxbUtil.marshal(dto, properties))
                    .isInstanceOf(Xml2jDtoException.class)
                    .hasMessageContaining("Failed to set property name[invalid.property]");
        }

        @Test
        public void testMarshal_JAXBException() {
            TestDto dto = new TestDto();

            Assertions.assertThatThrownBy(() -> JaxbUtil.marshal(dto))
                    .isInstanceOf(InvalidXmlSchemaException.class)
                    .hasMessage(
                            "Xml schema validation failed, events: [unable to marshal type \"io.github.debug.xml2jdto.core.jaxb.JaxbUtilMarshalTest$TestDto\" as an element because it is missing an @XmlRootElement annotation]");
        }
    }

    @DisplayName("With Schema Tests")
    @Nested
    class WithSchema {

        @Test
        public void withNullDto() {
            String result = JaxbUtil.marshal(null, "");
            assertThat(result).isNull();
        }

        @Test
        public void withNullSchema() {
            Person dto = new Person();
            dto.setName("Test Name");
            dto.setAge(30);
            dto.setAddress("Test Address");

            String result = JaxbUtil.marshal(dto, (String) null);
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result)
                    .isEqualTo(
                            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><person><name>Test Name</name><age>30</age><address xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">Test Address</address></person>");
        }

        @Test
        public void withDefaultProperties() {
            Person dto = new Person();
            dto.setName("Test Name");
            dto.setAge(30);
            dto.setAddress("Test Address íöüóőúéáűôňäýžťčšľ");

            String result = JaxbUtil.marshal(dto, XSD_PATH);
            Assertions.assertThat(result).isNotNull();
            Assertions.assertThat(result)
                    .isEqualTo(
                            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><person><name>Test Name</name><age>30</age><address xsi:type=\"xs:string\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">Test Address íöüóőúéáűôňäýžťčšľ</address></person>");
        }

        @Test
        public void withInvalidDto() {
            Person dto = new Person();
            dto.setName("Test Name");
            // Invalid age
            dto.setAge(-30);
            dto.setAddress("Test Address íöüóőúéáűôňäýžťčšľ");

            Assertions.assertThatThrownBy(() -> JaxbUtil.marshal(dto, XSD_PATH))
                    .isInstanceOf(InvalidXmlSchemaException.class)
                    .hasMessageContaining(
                            "Xml schema validation failed, events: [cvc-minInclusive-valid: Value '-30' is not facet-valid with respect to minInclusive '1' for type 'positiveInteger'., cvc-type.3.1.3: The value '-30' of element 'age' is not valid.]");
        }

        @Test
        public void invalidProperty() {
            Person dto = new Person();
            dto.setName("Test Name");
            dto.setAge(30);
            dto.setAddress("Test Address");

            Map<String, Object> properties = Map.of("invalid.property", "value");

            Assertions.assertThatThrownBy(() -> JaxbUtil.marshal(dto, XSD_PATH, properties))
                    .isInstanceOf(Xml2jDtoException.class)
                    .hasMessageContaining("Failed to set property name[invalid.property]");
        }

        @Test
        public void withoutXmlRootElement() {
            TestDto dto = new TestDto();

            Assertions.assertThatThrownBy(() -> JaxbUtil.marshal(dto, XSD_PATH))
                    .isInstanceOf(InvalidXmlSchemaException.class)
                    .hasMessage(
                            "Xml schema validation failed, events: [unable to marshal type \"io.github.debug.xml2jdto.core.jaxb.JaxbUtilMarshalTest$TestDto\" as an element because it is missing an @XmlRootElement annotation]");
        }

        @Test
        void whenSchemaPathIsInvalid() {
            TestDto dto = new TestDto();
            Assertions.assertThatThrownBy(() -> JaxbUtil.marshal(dto, "nonexistent.xsd"))
                    .isInstanceOf(Xml2jDtoException.class)
                    .hasMessageContaining("Schema on path [nonexistent.xsd] cannot be found!");
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
