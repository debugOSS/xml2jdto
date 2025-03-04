package io.github.debug.xml2jdto.core.jaxb;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.debug.xml2jdto.core.exception.InvalidMethodParameterException;

public class JaxbUtilgetJAXBContextTest {

    @Test
    public void testGetJAXBContextWithNullClass() {
        Assertions.assertThatThrownBy(() -> JaxbUtil.getJAXBContext(null)).isInstanceOf(InvalidMethodParameterException.class);
    }

    @Test
    public void testGetJAXBContextWithValidClass() throws JAXBException {
        JAXBContext context = JaxbUtil.getJAXBContext(String.class);
        Assertions.assertThat(context).isNotNull();
    }

    @Test
    public void testGetJAXBContextCache() throws JAXBException {
        JAXBContext context1 = JaxbUtil.getJAXBContext(String.class);
        JAXBContext context2 = JaxbUtil.getJAXBContext(String.class);
        Assertions.assertThat(context1).isSameAs(context2);
    }

    @Test
    public void testGetJAXBContextWithDifferentClasses() throws JAXBException {
        JAXBContext context1 = JaxbUtil.getJAXBContext(String.class);
        JAXBContext context2 = JaxbUtil.getJAXBContext(Integer.class);
        Assertions.assertThat(context1).isNotSameAs(context2);
    }
}
