package io.github.debug.xml2jdto.core.jaxb.catalog;

import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.catalog.Catalog;
import javax.xml.catalog.CatalogFeatures;
import javax.xml.catalog.CatalogManager;

import io.github.debug.xml2jdto.core.exception.ExBuilder;

/**
 * The CatalogProducer class is responsible for producing and managing the catalog. It retrieves the catalog configuration and initializes the catalog
 * if it is not already initialized.
 * 
 * <p>
 * This class uses a {@link CatalogConfig} object to obtain the catalog paths and attempts to find the catalog resource. If the resource is found, it
 * initializes the catalog using the {@link CatalogManager}.
 * </p>
 * 
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * {@code
 * CatalogProducer producer = new CatalogProducer();
 * Catalog catalog = producer.getCatalog();
 * }
 * </pre>
 * 
 * @see CatalogConfig
 * @see CatalogManager
 * 
 * @author scheffer.imrich
 */
public class CatalogProducer {

    private static final Logger log = Logger.getLogger(CatalogProducer.class.getName());

    private final CatalogConfig catalogConfig = new CatalogConfig();

    private static Catalog catalog;

    /**
     * Default constructor.
     */
    public CatalogProducer() {
        super();
    }

    /**
     * Retrieves the catalog. If the catalog is not already initialized, it will attempt to find the resource and initialize the catalog.
     *
     * @return the catalog object, or null if the resource could not be found
     */
    public Catalog getCatalog() {
        if (catalog == null) {
            findResource();
        }
        return catalog;
    }

    private void findResource() {
        List<String> paths = catalogConfig.getCatalogPaths();
        List<URI> catalogUris = new ArrayList<>();

        for (String catalogPath : paths) {
            try {
                URI catalogUri = Thread.currentThread().getContextClassLoader().getResource(catalogPath).toURI();
                log.info(MessageFormat.format("The catalog file [{0}] has been founded on URI [{1}].", catalogPath, catalogUri));
                catalogUris.add(catalogUri);
            } catch (Exception e) {
                throw ExBuilder.newXml2jDtoException()
                        .withMessage("The catalog file [{0}] could not be found: [{1}].", catalogPath, e.getLocalizedMessage())
                        .withCause(e)
                        .build();
            }
        }
        log.info(MessageFormat.format("Number of founded catalog file: [{0}].", catalogUris.size()));
        catalog = CatalogManager.catalog(CatalogFeatures.defaults(), catalogUris.toArray(new URI[0]));
    }
}
