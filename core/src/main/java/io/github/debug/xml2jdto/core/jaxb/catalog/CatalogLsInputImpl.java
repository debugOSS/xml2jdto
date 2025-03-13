package io.github.debug.xml2jdto.core.jaxb.catalog;

import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

/**
 * Implementation of the LSInput interface for handling XML catalog inputs. This class provides methods to set and get various input sources such as
 * character streams, byte streams, and string data. It also allows setting and getting system IDs, public IDs, base URIs, encodings, and certified
 * text flags.
 * 
 * <p>
 * This implementation returns default values (mostly null or false) for all methods except for the system ID, which is set through the constructor or
 * the setSystemId method.
 * </p>
 * 
 * <br/>
 * Example usage:
 * <pre>
 * {@code
 * CatalogLsInputImpl input = new CatalogLsInputImpl("systemId");
 * String systemId = input.getSystemId();
 * }
 * </pre>
 * 
 * @see org.w3c.dom.ls.LSInput
 * 
 * @author scheffer.imrich
 */
public class CatalogLsInputImpl implements LSInput {

    private String systemId;

    /**
     * Constructs a new instance of CatalogLsInputImpl with the specified system ID.
     *
     * @param systemId
     *            the system ID to be set for this instance
     */
    public CatalogLsInputImpl(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public Reader getCharacterStream() {
        return null;
    }

    @Override
    public void setCharacterStream(Reader characterStream) {
        // default
    }

    @Override
    public InputStream getByteStream() {
        return null;
    }

    @Override
    public void setByteStream(InputStream byteStream) {
        // default
    }

    @Override
    public String getStringData() {
        return null;
    }

    @Override
    public void setStringData(String stringData) {
        // default
    }

    @Override
    public String getSystemId() {
        return systemId;
    }

    @Override
    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    @Override
    public String getPublicId() {
        return null;
    }

    @Override
    public void setPublicId(String publicId) {
        // default
    }

    @Override
    public String getBaseURI() {
        return null;
    }

    @Override
    public void setBaseURI(String baseURI) {
        // default
    }

    @Override
    public String getEncoding() {
        return null;
    }

    @Override
    public void setEncoding(String encoding) {
        // default
    }

    @Override
    public boolean getCertifiedText() {
        return false;
    }

    @Override
    public void setCertifiedText(boolean certifiedText) {
        // default
    }
}
