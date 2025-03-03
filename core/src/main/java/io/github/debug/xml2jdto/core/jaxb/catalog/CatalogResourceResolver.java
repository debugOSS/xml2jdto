package io.github.debug.xml2jdto.core.jaxb.catalog;

import java.io.StringReader;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.Objects;
import java.util.logging.Logger;

import javax.xml.catalog.Catalog;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;

/**
 * The {@code CatalogResourceResolver} class implements the {@code LSResourceResolver} interface to resolve resources using a catalog. It utilizes a
 * {@code CatalogProducer} to obtain the catalog and attempts to resolve the system ID, public ID, and namespace URI using the catalog.
 * 
 * <p>
 * This class provides a mechanism to resolve XML resources by matching public IDs, system IDs, and namespace URIs against entries in a catalog. If a
 * match is found, the corresponding system ID is returned. If no match is found, it logs a warning message.
 * 
 * <p>
 * Note: This class is thread-safe as it does not modify the state of the catalog or the catalog producer.
 * 
 * @see LSResourceResolver
 * @see CatalogProducer
 * @see Catalog
 * 
 * @author scheffer.imrich
 */
public class CatalogResourceResolver implements LSResourceResolver {

    private static final Logger log = Logger.getLogger(CatalogResourceResolver.class.getName());

    private static final CatalogProducer catalogProducer = CatalogProducer.getInstance();

    /**
     * Default constructor.
     */
    public CatalogResourceResolver() {
        super();
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        String resolvedSystemId = resolve(catalogProducer.getCatalog(), publicId, systemId, namespaceURI);
        InputSource inputSource = Objects.nonNull(resolvedSystemId) ? new InputSource(resolvedSystemId) : new InputSource(new StringReader(""));
        return inputSource.isEmpty() ? null : new CatalogLsInputImpl(inputSource.getSystemId());
    }

    private String resolve(Catalog catalog, String publicId, String systemId, String namespaceURI) {
        log.fine(
                MessageFormat
                        .format("Start catalog resolving, publicId:[{0}], systemId:[{1}], namespaceUri:[{2}]", publicId, systemId, namespaceURI));
        String resolvedSystemId = null;

        if (namespaceURI != null) {
            resolvedSystemId = catalog.matchPublic(namespaceURI);
        }

        if (systemId != null) {
            resolvedSystemId = catalog.matchSystem(systemId);
        }

        if (resolvedSystemId == null && publicId != null) {
            resolvedSystemId = catalog.matchPublic(publicId);
        }

        if (resolvedSystemId == null && systemId != null) {
            resolvedSystemId = catalog.matchURI(systemId);
        }

        // alternative catalogs
        if (resolvedSystemId == null) {
            Iterator<Catalog> iter = catalog.catalogs().iterator();
            while (iter.hasNext()) {
                resolvedSystemId = resolve(iter.next(), publicId, systemId, namespaceURI);
                if (resolvedSystemId != null) {
                    break;
                }

            }
        }
        if (Objects.isNull(resolvedSystemId)) {
            log.warning(
                    MessageFormat.format(
                            "No source found with catalog, publicId:[{0}], systemId:[{1}], namespaceUri:[{2}].",
                            publicId,
                            systemId,
                            namespaceURI));
        } else {
            log.fine(MessageFormat.format("Resolved source with catalog: resolvedSystemId:[{0}]", resolvedSystemId));
        }
        return resolvedSystemId;
    }
}
