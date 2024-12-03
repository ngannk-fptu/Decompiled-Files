/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2;

import java.io.IOException;
import org.apache.commons.configuration2.Configuration;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

public abstract class ConfigurationXMLReader
implements XMLReader {
    protected static final String NS_URI = "";
    private static final String DEFAULT_ROOT_NAME = "config";
    private static final Attributes EMPTY_ATTRS = new AttributesImpl();
    private ContentHandler contentHandler;
    private SAXException exception;
    private String rootName = "config";

    protected ConfigurationXMLReader() {
    }

    @Override
    public void parse(String systemId) throws IOException, SAXException {
        this.parseConfiguration();
    }

    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        this.parseConfiguration();
    }

    @Override
    public boolean getFeature(String name) {
        return false;
    }

    @Override
    public void setFeature(String name, boolean value) {
    }

    @Override
    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        this.contentHandler = handler;
    }

    @Override
    public DTDHandler getDTDHandler() {
        return null;
    }

    @Override
    public void setDTDHandler(DTDHandler handler) {
    }

    @Override
    public EntityResolver getEntityResolver() {
        return null;
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
    }

    @Override
    public Object getProperty(String name) {
        return null;
    }

    @Override
    public void setProperty(String name, Object value) {
    }

    public String getRootName() {
        return this.rootName;
    }

    public void setRootName(String string) {
        this.rootName = string;
    }

    protected void fireElementStart(String name, Attributes attribs) {
        if (this.getException() == null) {
            try {
                Attributes at = attribs == null ? EMPTY_ATTRS : attribs;
                this.getContentHandler().startElement(NS_URI, name, name, at);
            }
            catch (SAXException ex) {
                this.exception = ex;
            }
        }
    }

    protected void fireElementEnd(String name) {
        if (this.getException() == null) {
            try {
                this.getContentHandler().endElement(NS_URI, name, name);
            }
            catch (SAXException ex) {
                this.exception = ex;
            }
        }
    }

    protected void fireCharacters(String text) {
        if (this.getException() == null) {
            try {
                char[] ch = text.toCharArray();
                this.getContentHandler().characters(ch, 0, ch.length);
            }
            catch (SAXException ex) {
                this.exception = ex;
            }
        }
    }

    public SAXException getException() {
        return this.exception;
    }

    protected void parseConfiguration() throws IOException, SAXException {
        if (this.getParsedConfiguration() == null) {
            throw new IOException("No configuration specified!");
        }
        if (this.getContentHandler() != null) {
            this.exception = null;
            this.getContentHandler().startDocument();
            this.processKeys();
            if (this.getException() != null) {
                throw this.getException();
            }
            this.getContentHandler().endDocument();
        }
    }

    public abstract Configuration getParsedConfiguration();

    protected abstract void processKeys() throws IOException, SAXException;
}

