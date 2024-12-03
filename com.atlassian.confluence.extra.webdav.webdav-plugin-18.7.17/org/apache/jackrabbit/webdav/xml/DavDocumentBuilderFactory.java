/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.xml;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

public class DavDocumentBuilderFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DavDocumentBuilderFactory.class);
    private final DocumentBuilderFactory DEFAULT_FACTORY;
    private DocumentBuilderFactory BUILDER_FACTORY;
    private static final EntityResolver DEFAULT_ENTITY_RESOLVER = new EntityResolver(){

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws IOException {
            LOG.debug("Resolution of external entities in XML payload not supported - publicId: " + publicId + ", systemId: " + systemId);
            throw new IOException("This parser does not support resolution of external entities (publicId: " + publicId + ", systemId: " + systemId + ")");
        }
    };

    public DavDocumentBuilderFactory() {
        this.BUILDER_FACTORY = this.DEFAULT_FACTORY = this.createFactory();
    }

    private DocumentBuilderFactory createFactory() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setIgnoringComments(true);
        factory.setIgnoringElementContentWhitespace(true);
        factory.setCoalescing(true);
        try {
            factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        }
        catch (ParserConfigurationException e) {
            LOG.warn("Secure XML processing is not supported", (Throwable)e);
        }
        catch (AbstractMethodError e) {
            LOG.warn("Secure XML processing is not supported", (Throwable)e);
        }
        return factory;
    }

    public void setFactory(DocumentBuilderFactory documentBuilderFactory) {
        LOG.debug("DocumentBuilderFactory changed to: " + documentBuilderFactory);
        this.BUILDER_FACTORY = documentBuilderFactory != null ? documentBuilderFactory : this.DEFAULT_FACTORY;
    }

    public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilder db = this.BUILDER_FACTORY.newDocumentBuilder();
        if (this.BUILDER_FACTORY == this.DEFAULT_FACTORY) {
            db.setEntityResolver(DEFAULT_ENTITY_RESOLVER);
        }
        db.setErrorHandler(new DefaultHandler());
        return db;
    }
}

