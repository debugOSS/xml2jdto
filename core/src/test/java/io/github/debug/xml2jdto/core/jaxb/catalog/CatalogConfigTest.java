package io.github.debug.xml2jdto.core.jaxb.catalog;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
@TestMethodOrder(MethodName.class)
public class CatalogConfigTest {

    public static final String CONFIG_CATALOG_PATH = "xml2jdto.catalog.path";
    public static final String CATALOG_PATH_VALUE = "/path/to/catalog1 , /path/to/catalog2";
    public static final String EXPECTED_PATH1 = "/path/to/catalog1";
    public static final String EXPECTED_PATH2 = "/path/to/catalog2";

    @SystemStub
    private EnvironmentVariables environmentVariables;

    @Test
    public void testGetCatalogPaths_systemProperty() {
        cleanXml2jdtoEnvironmentVariables();
        System.setProperty(CONFIG_CATALOG_PATH, CATALOG_PATH_VALUE);

        CatalogConfig catalogConfig = new CatalogConfig();
        List<String> paths = catalogConfig.getCatalogPaths();

        Assertions.assertThat(paths).containsExactly(EXPECTED_PATH1, EXPECTED_PATH2);
        System.clearProperty(CatalogConfigTest.CONFIG_CATALOG_PATH);
    }

    @Test
    public void testGetCatalogPaths_envDot() {
        cleanXml2jdtoEnvironmentVariables();
        environmentVariables.set(CONFIG_CATALOG_PATH, CATALOG_PATH_VALUE);

        CatalogConfig catalogConfig = new CatalogConfig();
        List<String> paths = catalogConfig.getCatalogPaths();

        Assertions.assertThat(paths).containsExactly(EXPECTED_PATH1, EXPECTED_PATH2);
    }

    @Test
    public void testGetCatalogPaths_envUpper() {
        cleanXml2jdtoEnvironmentVariables();
        environmentVariables.set("XML2JDTO.CATALOG.PATH", CATALOG_PATH_VALUE);

        CatalogConfig catalogConfig = new CatalogConfig();
        List<String> paths = catalogConfig.getCatalogPaths();

        Assertions.assertThat(paths).containsExactly(EXPECTED_PATH1, EXPECTED_PATH2);
    }

    @Test
    public void testGetCatalogPaths_envNorm() {
        cleanXml2jdtoEnvironmentVariables();
        environmentVariables.set("XML2JDTO_CATALOG_PATH", CATALOG_PATH_VALUE);

        CatalogConfig catalogConfig = new CatalogConfig();
        List<String> paths = catalogConfig.getCatalogPaths();

        Assertions.assertThat(paths).containsExactly(EXPECTED_PATH1, EXPECTED_PATH2);
    }

    @Test
    public void testGetCatalogPathsWhenNotSet() {
        cleanXml2jdtoEnvironmentVariables();
        environmentVariables.remove(CONFIG_CATALOG_PATH);
        CatalogConfig catalogConfig = new CatalogConfig();
        List<String> paths = catalogConfig.getCatalogPaths();

        Assertions.assertThat(paths).isNull();
    }

    @Test
    public void testSetCatalogPaths() {
        cleanXml2jdtoEnvironmentVariables();
        CatalogConfig catalogConfig = new CatalogConfig();
        catalogConfig.setCatalogPaths(List.of(EXPECTED_PATH1, EXPECTED_PATH2));

        Assertions.assertThat(catalogConfig.getCatalogPaths()).containsExactly(EXPECTED_PATH1, EXPECTED_PATH2);
    }

    private void cleanXml2jdtoEnvironmentVariables() {
        // some paralel tests may set these variables, so we need to clean them
        System.clearProperty(CatalogConfigTest.CONFIG_CATALOG_PATH);

        environmentVariables.getVariables().forEach((key, value) -> {
            if (StringUtils.startsWithIgnoreCase(key, "xml2jdto"))
                environmentVariables.remove(key);
        });
    }
}
