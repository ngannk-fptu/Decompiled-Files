/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.sax;

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public abstract class AbstractXMLReader
implements XMLReader {
    private static final String URI_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    protected boolean namespaces = true;
    protected boolean namespacePrefixes = false;
    protected ContentHandler contentHandler;
    protected LexicalHandler lexicalHandler;
    protected DTDHandler dtdHandler;
    protected EntityResolver entityResolver;
    protected ErrorHandler errorHandler;

    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }

    public void setDTDHandler(DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }

    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException(name);
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespaces".equals(name)) {
            this.namespaces = value;
        } else if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            this.namespacePrefixes = value;
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespaces".equals(name)) {
            return this.namespaces;
        }
        if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            return this.namespacePrefixes;
        }
        if (URI_LEXICAL_HANDLER.equals(name)) {
            return this.lexicalHandler;
        }
        throw new SAXNotRecognizedException(name);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (!URI_LEXICAL_HANDLER.equals(name)) {
            throw new SAXNotRecognizedException(name);
        }
        this.lexicalHandler = (LexicalHandler)value;
    }
}

