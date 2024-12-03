/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.xml;

import com.atlassian.security.xml.RestrictedSAXParserFactory;
import java.io.IOException;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

class RestrictedXMLReader
implements XMLReader {
    private final XMLReader delegate;

    RestrictedXMLReader(XMLReader innerReader) {
        this.delegate = innerReader;
    }

    @Override
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.delegate.getFeature(name);
    }

    @Override
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (RestrictedSAXParserFactory.checkFeatures(name, value)) {
            this.delegate.setFeature(name, value);
        }
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.delegate.getProperty(name);
    }

    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        this.delegate.setProperty(name, value);
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
    }

    @Override
    public EntityResolver getEntityResolver() {
        return this.delegate.getEntityResolver();
    }

    @Override
    public void setDTDHandler(DTDHandler handler) {
        this.delegate.setDTDHandler(handler);
    }

    @Override
    public DTDHandler getDTDHandler() {
        return this.delegate.getDTDHandler();
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        this.delegate.setContentHandler(handler);
    }

    @Override
    public ContentHandler getContentHandler() {
        return this.delegate.getContentHandler();
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
        this.delegate.setErrorHandler(handler);
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return this.delegate.getErrorHandler();
    }

    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        this.delegate.parse(input);
    }

    @Override
    public void parse(String systemId) throws IOException, SAXException {
        this.delegate.parse(systemId);
    }
}

