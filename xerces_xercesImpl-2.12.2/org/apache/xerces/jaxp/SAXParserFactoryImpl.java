/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp;

import java.util.Hashtable;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.validation.Schema;
import org.apache.xerces.jaxp.SAXParserImpl;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class SAXParserFactoryImpl
extends SAXParserFactory {
    private static final String NAMESPACES_FEATURE = "http://xml.org/sax/features/namespaces";
    private static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
    private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
    private Hashtable features;
    private Schema grammar;
    private boolean isXIncludeAware;
    private boolean fSecureProcess = false;

    @Override
    public SAXParser newSAXParser() throws ParserConfigurationException {
        SAXParserImpl sAXParserImpl;
        try {
            sAXParserImpl = new SAXParserImpl(this, this.features, this.fSecureProcess);
        }
        catch (SAXException sAXException) {
            throw new ParserConfigurationException(sAXException.getMessage());
        }
        return sAXParserImpl;
    }

    private SAXParserImpl newSAXParserImpl() throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        SAXParserImpl sAXParserImpl;
        try {
            sAXParserImpl = new SAXParserImpl(this, this.features);
        }
        catch (SAXNotSupportedException sAXNotSupportedException) {
            throw sAXNotSupportedException;
        }
        catch (SAXNotRecognizedException sAXNotRecognizedException) {
            throw sAXNotRecognizedException;
        }
        catch (SAXException sAXException) {
            throw new ParserConfigurationException(sAXException.getMessage());
        }
        return sAXParserImpl;
    }

    @Override
    public void setFeature(String string, boolean bl) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException();
        }
        if (string.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            this.fSecureProcess = bl;
            return;
        }
        if (string.equals(NAMESPACES_FEATURE)) {
            this.setNamespaceAware(bl);
            return;
        }
        if (string.equals(VALIDATION_FEATURE)) {
            this.setValidating(bl);
            return;
        }
        if (string.equals(XINCLUDE_FEATURE)) {
            this.setXIncludeAware(bl);
            return;
        }
        if (this.features == null) {
            this.features = new Hashtable();
        }
        this.features.put(string, bl ? Boolean.TRUE : Boolean.FALSE);
        try {
            this.newSAXParserImpl();
        }
        catch (SAXNotSupportedException sAXNotSupportedException) {
            this.features.remove(string);
            throw sAXNotSupportedException;
        }
        catch (SAXNotRecognizedException sAXNotRecognizedException) {
            this.features.remove(string);
            throw sAXNotRecognizedException;
        }
    }

    @Override
    public boolean getFeature(String string) throws ParserConfigurationException, SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException();
        }
        if (string.equals("http://javax.xml.XMLConstants/feature/secure-processing")) {
            return this.fSecureProcess;
        }
        if (string.equals(NAMESPACES_FEATURE)) {
            return this.isNamespaceAware();
        }
        if (string.equals(VALIDATION_FEATURE)) {
            return this.isValidating();
        }
        if (string.equals(XINCLUDE_FEATURE)) {
            return this.isXIncludeAware();
        }
        return this.newSAXParserImpl().getXMLReader().getFeature(string);
    }

    @Override
    public Schema getSchema() {
        return this.grammar;
    }

    @Override
    public void setSchema(Schema schema) {
        this.grammar = schema;
    }

    @Override
    public boolean isXIncludeAware() {
        return this.isXIncludeAware;
    }

    @Override
    public void setXIncludeAware(boolean bl) {
        this.isXIncludeAware = bl;
    }
}

