package io.github.debug.xml2jdto.core.jaxb.catalog;

import javax.xml.catalog.Catalog;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import io.github.debug.xml2jdto.core.exception.Xml2jDtoException;

public class CatalogProducerTest {

    @Test
    public void testGetCatalog() {
        System.setProperty(CatalogConfigTest.CONFIG_CATALOG_PATH, "xsd/storage/catalog.cat");

        Catalog catalog = new CatalogProducer().getCatalog();

        Assertions.assertThat(catalog).isNotNull();
        String uriCommon = catalog.matchPublic("http://schemas.nav.gov.hu/NTCA/1.0/common");
        Assertions.assertThat(uriCommon).endsWith("xsd/common.xsd");
        String uriFile = catalog.matchPublic("http://schemas.debug.github.io/STORAGE/1.0/file");
        Assertions.assertThat(uriFile).endsWith("xsd/storage/file.xsd");
        String uriStorage = catalog.matchPublic("http://schemas.debug.github.io/STORAGE/1.0/storage");
        Assertions.assertThat(uriStorage).endsWith("xsd/storage/storage.xsd");
    }

    @Test
    public void testFindResourceException() {
        System.setProperty(CatalogConfigTest.CONFIG_CATALOG_PATH, "fake-catalog.cat");

        try {
            new CatalogProducer().getCatalog();
        } catch (Exception e) {
            Assertions.assertThat(e).isInstanceOf(Xml2jDtoException.class);
            Assertions.assertThat(e.getMessage()).startsWith("The catalog file [fake-catalog.cat] could not be found");
        }
    }
}
