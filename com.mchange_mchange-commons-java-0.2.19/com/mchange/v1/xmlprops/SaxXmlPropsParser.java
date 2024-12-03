/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.xmlprops;

import com.mchange.v1.xml.StdErrErrorHandler;
import com.mchange.v1.xmlprops.XmlPropsException;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SaxXmlPropsParser {
    static final String DEFAULT_XML_READER = "org.apache.xerces.parsers.SAXParser";
    static final String XMLPROPS_NAMESPACE_URI = "http://www.mchange.com/namespaces/xmlprops";

    public static Properties parseXmlProps(InputStream inputStream) throws XmlPropsException {
        try {
            String string = DEFAULT_XML_READER;
            XMLReader xMLReader = (XMLReader)Class.forName(string).newInstance();
            InputSource inputSource = new InputSource(inputStream);
            return SaxXmlPropsParser.parseXmlProps(inputSource, xMLReader, null, null);
        }
        catch (XmlPropsException xmlPropsException) {
            throw xmlPropsException;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            throw new XmlPropsException("Exception while instantiating XMLReader.", exception);
        }
    }

    private static Properties parseXmlProps(InputSource inputSource, XMLReader xMLReader, EntityResolver entityResolver, ErrorHandler errorHandler) throws XmlPropsException {
        try {
            if (entityResolver != null) {
                xMLReader.setEntityResolver(entityResolver);
            }
            if (errorHandler == null) {
                errorHandler = new StdErrErrorHandler();
            }
            xMLReader.setErrorHandler(errorHandler);
            XmlPropsContentHandler xmlPropsContentHandler = new XmlPropsContentHandler();
            xMLReader.setContentHandler(xmlPropsContentHandler);
            xMLReader.parse(inputSource);
            return xmlPropsContentHandler.getLastProperties();
        }
        catch (Exception exception) {
            if (exception instanceof SAXException) {
                ((SAXException)exception).getException().printStackTrace();
            }
            exception.printStackTrace();
            throw new XmlPropsException(exception);
        }
    }

    public static void main(String[] stringArray) {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(stringArray[0]));
            SaxXmlPropsParser saxXmlPropsParser = new SaxXmlPropsParser();
            Properties properties = SaxXmlPropsParser.parseXmlProps(bufferedInputStream);
            for (String string : properties.keySet()) {
                String string2 = properties.getProperty(string);
                System.err.println(string + '=' + string2);
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    static class XmlPropsContentHandler
    implements ContentHandler {
        Locator locator;
        Properties props;
        String name;
        StringBuffer valueBuf;

        XmlPropsContentHandler() {
        }

        @Override
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        @Override
        public void startDocument() throws SAXException {
            this.props = new Properties();
        }

        @Override
        public void startElement(String string, String string2, String string3, Attributes attributes) {
            System.err.println("--> startElement( " + string + ", " + string2 + ", " + attributes + ")");
            if (!string.equals("") && !string.equals(SaxXmlPropsParser.XMLPROPS_NAMESPACE_URI)) {
                return;
            }
            if (string2.equals("property")) {
                this.name = attributes.getValue(string, "name");
                this.valueBuf = new StringBuffer();
            }
        }

        @Override
        public void characters(char[] cArray, int n, int n2) throws SAXException {
            if (this.valueBuf != null) {
                this.valueBuf.append(cArray, n, n2);
            }
        }

        @Override
        public void ignorableWhitespace(char[] cArray, int n, int n2) throws SAXException {
            if (this.valueBuf != null) {
                this.valueBuf.append(cArray, n, n2);
            }
        }

        @Override
        public void endElement(String string, String string2, String string3) throws SAXException {
            if (!string.equals("") && !string.equals(SaxXmlPropsParser.XMLPROPS_NAMESPACE_URI)) {
                return;
            }
            if (string2.equals("property")) {
                System.err.println("NAME: " + this.name);
                this.props.put(this.name, this.valueBuf.toString());
                this.valueBuf = null;
            }
        }

        @Override
        public void endDocument() throws SAXException {
        }

        @Override
        public void startPrefixMapping(String string, String string2) throws SAXException {
        }

        @Override
        public void endPrefixMapping(String string) throws SAXException {
        }

        @Override
        public void processingInstruction(String string, String string2) throws SAXException {
        }

        @Override
        public void skippedEntity(String string) throws SAXException {
        }

        public Properties getLastProperties() {
            return this.props;
        }
    }
}

