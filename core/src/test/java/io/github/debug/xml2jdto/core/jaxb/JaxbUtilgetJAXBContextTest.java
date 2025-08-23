package io.github.debug.xml2jdto.core.jaxb;

import java.security.InvalidParameterException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.github.debug.xml2jdto.core.exception.InvalidMethodParameterException;
import io.github.debug.xml2jdto.core.exception.Xml2jDtoException;

@DisplayName("JaxbUtil JAXBContext Tests")
public class JaxbUtilgetJAXBContextTest {

    @DisplayName("getJAXBContext(Class<?> clazz) Tests")
    @Nested
    class ForClass {

        @Test
        public void nullClass() {
            Assertions.assertThatThrownBy(() -> JaxbUtil.getJAXBContext((Class<?>) null)).isInstanceOf(InvalidMethodParameterException.class);
        }

        @Test
        public void validClass() throws JAXBException {
            JAXBContext context = JaxbUtil.getJAXBContext(String.class);
            Assertions.assertThat(context).isNotNull();
        }

        @Test
        public void contextCache() throws JAXBException {
            JAXBContext context1 = JaxbUtil.getJAXBContext(String.class);
            JAXBContext context2 = JaxbUtil.getJAXBContext(String.class);
            Assertions.assertThat(context1).isSameAs(context2);
        }

        @Test
        public void differentClasses() throws JAXBException {
            JAXBContext context1 = JaxbUtil.getJAXBContext(String.class);
            JAXBContext context2 = JaxbUtil.getJAXBContext(Integer.class);
            Assertions.assertThat(context1).isNotSameAs(context2);
        }

        @Test
        void withInvalidClass() {
            class NotJaxbAnnotated {
            }

            Assertions.assertThatThrownBy(() -> JaxbUtil.getJAXBContext(NotJaxbAnnotated.class))
                    .isInstanceOf(Xml2jDtoException.class)
                    .hasMessageContaining("Error creating JAXBContext");
        }
    }

    @DisplayName("getJAXBContext(Class<?>... forClasses) Tests")
    @Nested
    class ForClassArray {

        @XmlRootElement
        public static class Dummy1 {
            public String value;
        }

        @XmlRootElement
        public static class Dummy2 {
            public int number;
        }

        @Test
        public void withNullClassArray() {
            Assertions.assertThatThrownBy(() -> JaxbUtil.getJAXBContext((Class<?>[]) null))
                    .isInstanceOf(InvalidParameterException.class)
                    .hasMessageContaining("forClasses is null");
        }

        @Test
        void withMultipleClasses() {
            JAXBContext context = JaxbUtil.getJAXBContext(Dummy1.class, Dummy2.class);
            Assertions.assertThat(context).isNotNull();
            Assertions.assertThat(context.toString()).containsSubsequence(Dummy1.class.getName(), Dummy2.class.getName());
        }

        @Test
        void withNullElementInArray() {
            Assertions.assertThatThrownBy(() -> JaxbUtil.getJAXBContext(Dummy1.class, null, Dummy2.class))
                    .isInstanceOf(Xml2jDtoException.class)
                    .hasMessageContainingAll(Dummy1.class.getName(), Dummy2.class.getName(), "null");
        }

        @Test
        void withMixedClassOrder() {
            JAXBContext context1 = JaxbUtil.getJAXBContext(Dummy1.class, Dummy2.class);
            JAXBContext context2 = JaxbUtil.getJAXBContext(Dummy2.class, Dummy1.class);
            Assertions.assertThat(context1).isSameAs(context2);
        }
    }

}
