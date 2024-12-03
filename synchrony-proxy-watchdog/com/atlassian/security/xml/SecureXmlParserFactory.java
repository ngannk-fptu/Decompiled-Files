/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.xml;

import com.atlassian.security.xml.RestrictedSAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLResolver;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public final class SecureXmlParserFactory {
    private static InputStream EMPTY_INPUT_STREAM = new ByteArrayInputStream(new byte[0]);
    public static final String ATTRIBUTE_LOAD_EXTERNAL = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    public static final String FEATURE_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    public static final String FEATURE_EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    private static final List<String> PROTECTED_FEATURES = Arrays.asList("http://javax.xml.XMLConstants/feature/secure-processing", "http://xml.org/sax/features/external-general-entities", "http://xml.org/sax/features/external-parameter-entities");
    private static final List<String> PROTECTED_ATTRIBUTES = Arrays.asList("http://apache.org/xml/features/nonvalidating/load-external-dtd");
    private static EntityResolver emptyEntityResolver = new EntityResolver(){

        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return new InputSource(EMPTY_INPUT_STREAM);
        }
    };
    private static final XMLResolver emptyXmlResolver = new XMLResolver(){

        @Override
        public Object resolveEntity(String publicID, String systemID, String baseURI, String namespace) {
            return EMPTY_INPUT_STREAM;
        }
    };

    private SecureXmlParserFactory() {
    }

    private static DocumentBuilderFactory createDocumentBuilderFactory() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        dbf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        dbf.setFeature(FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
        dbf.setFeature(FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
        dbf.setAttribute(ATTRIBUTE_LOAD_EXTERNAL, false);
        return dbf;
    }

    public static DocumentBuilderFactory newDocumentBuilderFactory() {
        try {
            final DocumentBuilderFactory dbf = SecureXmlParserFactory.createDocumentBuilderFactory();
            return new DocumentBuilderFactory(){

                @Override
                public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
                    return dbf.newDocumentBuilder();
                }

                @Override
                public void setAttribute(String name, Object value) throws IllegalArgumentException {
                    if (PROTECTED_ATTRIBUTES.contains(name)) {
                        return;
                    }
                    dbf.setAttribute(name, value);
                }

                @Override
                public Object getAttribute(String name) throws IllegalArgumentException {
                    return dbf.getAttribute(name);
                }

                @Override
                public void setFeature(String name, boolean value) throws ParserConfigurationException {
                    if (PROTECTED_FEATURES.contains(name)) {
                        return;
                    }
                    dbf.setAttribute(name, value);
                }

                @Override
                public boolean getFeature(String name) throws ParserConfigurationException {
                    return dbf.getFeature(name);
                }

                @Override
                public void setExpandEntityReferences(boolean expandEntityRef) {
                }

                @Override
                public boolean isNamespaceAware() {
                    return dbf.isNamespaceAware();
                }

                @Override
                public void setNamespaceAware(boolean isNamespaceAware) {
                    dbf.setNamespaceAware(isNamespaceAware);
                }
            };
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static DocumentBuilder newDocumentBuilder() {
        try {
            DocumentBuilderFactory dbf = SecureXmlParserFactory.createDocumentBuilderFactory();
            dbf.setNamespaceAware(false);
            return dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static SAXParserFactory createSAXParserFactory() throws SAXException, ParserConfigurationException {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
        spf.setFeature(ATTRIBUTE_LOAD_EXTERNAL, false);
        spf.setFeature(FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
        spf.setFeature(FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
        return new RestrictedSAXParserFactory(spf);
    }

    public static XMLReader newXmlReader() {
        try {
            SAXParserFactory spf = SecureXmlParserFactory.createSAXParserFactory();
            spf.setNamespaceAware(false);
            return spf.newSAXParser().getXMLReader();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public static XMLReader newNamespaceAwareXmlReader() {
        try {
            SAXParserFactory spf = SecureXmlParserFactory.createSAXParserFactory();
            spf.setNamespaceAware(true);
            return spf.newSAXParser().getXMLReader();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public static DocumentBuilder newNamespaceAwareDocumentBuilder() {
        try {
            DocumentBuilderFactory dbf = SecureXmlParserFactory.createDocumentBuilderFactory();
            dbf.setNamespaceAware(true);
            return dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static XMLInputFactory newXmlInputFactory() {
        XMLInputFactory fac = XMLInputFactory.newFactory();
        fac.setProperty("javax.xml.stream.supportDTD", Boolean.FALSE);
        fac.setXMLResolver(emptyXmlResolver);
        return fac;
    }

    public static EntityResolver emptyEntityResolver() {
        return emptyEntityResolver;
    }
}

