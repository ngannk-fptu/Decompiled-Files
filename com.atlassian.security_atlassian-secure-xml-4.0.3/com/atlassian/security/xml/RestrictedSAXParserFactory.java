/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.xml;

import com.atlassian.security.xml.RestrictedSAXParser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

class RestrictedSAXParserFactory
extends SAXParserFactory {
    private final SAXParserFactory delegate;

    RestrictedSAXParserFactory(SAXParserFactory inner) {
        this.delegate = inner;
    }

    @Override
    public SAXParser newSAXParser() throws ParserConfigurationException, SAXException {
        SAXParser innerParser = this.delegate.newSAXParser();
        return new RestrictedSAXParser(innerParser);
    }

    @Override
    public void setNamespaceAware(boolean awareness) {
        this.delegate.setNamespaceAware(awareness);
    }

    @Override
    public void setValidating(boolean validating) {
        this.delegate.setValidating(validating);
    }

    @Override
    public boolean isNamespaceAware() {
        return this.delegate.isNamespaceAware();
    }

    @Override
    public boolean isValidating() {
        return this.delegate.isValidating();
    }

    @Override
    public void setFeature(String name, boolean value) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        if (RestrictedSAXParserFactory.checkFeatures(name, value)) {
            this.delegate.setFeature(name, value);
        }
    }

    static boolean checkFeatures(String name, boolean value) {
        if (name.equals("http://javax.xml.XMLConstants/feature/secure-processing") && !value) {
            return false;
        }
        if (name.equals("http://apache.org/xml/features/nonvalidating/load-external-dtd") && value) {
            return false;
        }
        if (name.equals("http://xml.org/sax/features/external-general-entities") && value) {
            return false;
        }
        return !name.equals("http://xml.org/sax/features/external-parameter-entities") || !value;
    }

    @Override
    public boolean getFeature(String name) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        return this.delegate.getFeature(name);
    }

    @Override
    public Schema getSchema() {
        return this.delegate.getSchema();
    }

    @Override
    public void setSchema(Schema schema) {
        this.delegate.setSchema(schema);
    }

    @Override
    public void setXIncludeAware(boolean state) {
        this.delegate.setXIncludeAware(state);
    }

    @Override
    public boolean isXIncludeAware() {
        return this.delegate.isXIncludeAware();
    }
}

