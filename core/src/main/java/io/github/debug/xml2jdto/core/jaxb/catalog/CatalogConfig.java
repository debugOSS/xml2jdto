package io.github.debug.xml2jdto.core.jaxb.catalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * The {@code CatalogConfig} class is responsible for managing the configuration of catalog paths used in the application. It retrieves the catalog
 * paths from system properties or environment variables and provides methods to access and modify these paths.
 * 
 * <p>
 * The catalog paths are expected to be specified as a comma-separated string in the system property or environment variable defined by
 * {@code CATALOG_PATH}.
 * 
 * <p>
 * Example usage:
 * 
 * <pre>
 * {@code
 * CatalogConfig config = new CatalogConfig();
 * String[] paths = config.getCatalogPaths();
 * config.setCatalogPaths(new String[] { "/path/to/catalog1", "/path/to/catalog2" });
 * }
 * </pre>
 * 
 * <p>
 * System property or environment variable:
 * <ul>
 * <li>{@code xml2jdto.catalog.path} - Comma-separated list of catalog paths.</li>
 * </ul>
 * 
 * @see System#getProperty(String)
 * @see System#getenv(String)
 * 
 * @author scheffer.imrich
 */
public class CatalogConfig {

    private static final String CATALOG_PATH = "xml2jdto.catalog.path";

    private List<String> catalogPaths;

    /**
     * Default constructor.
     */
    public CatalogConfig() {
        super();
    }

    /**
     * Retrieves the catalog paths from system properties or environment variables.
     * <p>
     * This method first checks if the catalog paths have already been initialized. If not, it attempts to retrieve the catalog paths from a system
     * property identified by {@code CATALOG_PATH}. If the system property is not set, it then attempts to retrieve the catalog paths from an
     * environment variable with the same name. If either the system property or the environment variable is found, the paths are split by commas and
     * stored in the {@code catalogPaths} array.
     * </p>
     *
     * @return a list of catalog paths, or {@code null} if neither the system property nor the environment variable is set.
     */
    public List<String> getCatalogPaths() {
        if (catalogPaths == null) {
            String property = System.getProperty(CATALOG_PATH);
            if (property == null) {
                property = getNormalizedEnv(CATALOG_PATH);
            }
            if (property != null) {
                catalogPaths = new ArrayList<String>();
                for (String path : StringUtils.split(property, ",")) {
                    catalogPaths.add(path.trim());
                }
            }
        }
        return catalogPaths;
    }

    /**
     * Sets the catalog paths.
     *
     * @param catalogPaths
     *            a list of strings representing the catalog paths to be set
     */
    public void setCatalogPaths(List<String> catalogPaths) {
        this.catalogPaths = catalogPaths;
    }

    /**
     * Retrieves an environment variable by normalizing the name to ignore case, hyphens, and special characters.
     *
     * @param name the name of the environment variable
     * @return the value of the environment variable, or {@code null} if not found
     */
    private String getNormalizedEnv(String name) {
        String normalizedKey = normalizeEnvKey(name);
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            if (normalizeEnvKey(entry.getKey()).equals(normalizedKey)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Normalizes an environment variable key by converting it to lowercase and replacing special characters with underscores.
     *
     * @param key the key to normalize
     * @return the normalized key
     */
    private String normalizeEnvKey(String key) {
        return key.toLowerCase().replaceAll("[^a-z0-9]", "_");
    }
}
