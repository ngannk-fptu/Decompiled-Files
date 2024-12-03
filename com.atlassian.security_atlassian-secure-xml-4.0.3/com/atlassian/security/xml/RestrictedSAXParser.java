/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.xml;

import com.atlassian.security.xml.RestrictedXMLReader;
import com.atlassian.security.xml.SecureXmlParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.validation.Schema;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

class RestrictedSAXParser
extends SAXParser {
    private final SAXParser delegate;

    public RestrictedSAXParser(SAXParser inner) {
        this.delegate = inner;
    }

    @Override
    public Parser getParser() throws SAXException {
        return this.delegate.getParser();
    }

    @Override
    public XMLReader getXMLReader() throws SAXException {
        XMLReader innerReader = this.delegate.getXMLReader();
        innerReader.setEntityResolver(SecureXmlParserFactory.emptyEntityResolver());
        return new RestrictedXMLReader(innerReader);
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
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        this.delegate.setProperty(name, value);
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.delegate.getProperty(name);
    }

    @Override
    public void reset() {
        this.delegate.reset();
    }

    @Override
    public void parse(InputStream is, HandlerBase hb) throws SAXException, IOException {
        this.delegate.parse(is, hb);
    }

    @Override
    public void parse(InputStream is, HandlerBase hb, String systemId) throws SAXException, IOException {
        this.delegate.parse(is, hb, systemId);
    }

    @Override
    public void parse(InputStream is, DefaultHandler dh) throws SAXException, IOException {
        this.delegate.parse(is, dh);
    }

    @Override
    public void parse(InputStream is, DefaultHandler dh, String systemId) throws SAXException, IOException {
        this.delegate.parse(is, dh, systemId);
    }

    @Override
    public void parse(String uri, HandlerBase hb) throws SAXException, IOException {
        this.delegate.parse(uri, hb);
    }

    @Override
    public void parse(String uri, DefaultHandler dh) throws SAXException, IOException {
        this.delegate.parse(uri, dh);
    }

    @Override
    public void parse(File f, HandlerBase hb) throws SAXException, IOException {
        this.delegate.parse(f, hb);
    }

    @Override
    public void parse(File f, DefaultHandler dh) throws SAXException, IOException {
        this.delegate.parse(f, dh);
    }

    @Override
    public void parse(InputSource is, HandlerBase hb) throws SAXException, IOException {
        this.delegate.parse(is, hb);
    }

    @Override
    public void parse(InputSource is, DefaultHandler dh) throws SAXException, IOException {
        this.delegate.parse(is, dh);
    }

    @Override
    public Schema getSchema() {
        return this.delegate.getSchema();
    }

    @Override
    public boolean isXIncludeAware() {
        return this.delegate.isXIncludeAware();
    }
}

