/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.parser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.transform.Transformer;
import org.apache.tika.exception.TikaException;
import org.apache.tika.utils.XMLReaderUtils;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class ParseContext
implements Serializable {
    private static final long serialVersionUID = -5921436862145826534L;
    private final Map<String, Object> context = new HashMap<String, Object>();

    public <T> void set(Class<T> key, T value) {
        if (value != null) {
            this.context.put(key.getName(), value);
        } else {
            this.context.remove(key.getName());
        }
    }

    public <T> T get(Class<T> key) {
        return (T)this.context.get(key.getName());
    }

    public <T> T get(Class<T> key, T defaultValue) {
        T value = this.get(key);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    public XMLReader getXMLReader() throws TikaException {
        XMLReader reader = this.get(XMLReader.class);
        if (reader != null) {
            return reader;
        }
        return XMLReaderUtils.getXMLReader();
    }

    public SAXParser getSAXParser() throws TikaException {
        SAXParser parser = this.get(SAXParser.class);
        if (parser != null) {
            return parser;
        }
        return XMLReaderUtils.getSAXParser();
    }

    public SAXParserFactory getSAXParserFactory() {
        SAXParserFactory factory = this.get(SAXParserFactory.class);
        if (factory == null) {
            factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            try {
                factory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
            }
            catch (ParserConfigurationException parserConfigurationException) {
            }
            catch (SAXNotSupportedException sAXNotSupportedException) {
            }
            catch (SAXNotRecognizedException sAXNotRecognizedException) {
                // empty catch block
            }
        }
        return factory;
    }

    private DocumentBuilderFactory getDocumentBuilderFactory() {
        DocumentBuilderFactory documentBuilderFactory = this.get(DocumentBuilderFactory.class);
        if (documentBuilderFactory != null) {
            return documentBuilderFactory;
        }
        return XMLReaderUtils.getDocumentBuilderFactory();
    }

    public DocumentBuilder getDocumentBuilder() throws TikaException {
        DocumentBuilder documentBuilder = this.get(DocumentBuilder.class);
        if (documentBuilder != null) {
            return documentBuilder;
        }
        return XMLReaderUtils.getDocumentBuilder();
    }

    public XMLInputFactory getXMLInputFactory() {
        XMLInputFactory factory = this.get(XMLInputFactory.class);
        if (factory != null) {
            return factory;
        }
        return XMLReaderUtils.getXMLInputFactory();
    }

    public Transformer getTransformer() throws TikaException {
        Transformer transformer = this.get(Transformer.class);
        if (transformer != null) {
            return transformer;
        }
        return XMLReaderUtils.getTransformer();
    }
}

