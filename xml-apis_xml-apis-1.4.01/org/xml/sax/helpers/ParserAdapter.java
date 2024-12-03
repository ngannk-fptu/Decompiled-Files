/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax.helpers;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.NamespaceSupport;
import org.xml.sax.helpers.ParserFactory;

public class ParserAdapter
implements XMLReader,
DocumentHandler {
    private static final String FEATURES = "http://xml.org/sax/features/";
    private static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    private static final String XMLNS_URIs = "http://xml.org/sax/features/xmlns-uris";
    private NamespaceSupport nsSupport;
    private AttributeListAdapter attAdapter;
    private boolean parsing = false;
    private String[] nameParts = new String[3];
    private Parser parser = null;
    private AttributesImpl atts = null;
    private boolean namespaces = true;
    private boolean prefixes = false;
    private boolean uris = false;
    Locator locator;
    EntityResolver entityResolver = null;
    DTDHandler dtdHandler = null;
    ContentHandler contentHandler = null;
    ErrorHandler errorHandler = null;

    public ParserAdapter() throws SAXException {
        String string = System.getProperty("org.xml.sax.parser");
        try {
            this.setup(ParserFactory.makeParser());
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new SAXException("Cannot find SAX1 driver class " + string, classNotFoundException);
        }
        catch (IllegalAccessException illegalAccessException) {
            throw new SAXException("SAX1 driver class " + string + " found but cannot be loaded", illegalAccessException);
        }
        catch (InstantiationException instantiationException) {
            throw new SAXException("SAX1 driver class " + string + " loaded but cannot be instantiated", instantiationException);
        }
        catch (ClassCastException classCastException) {
            throw new SAXException("SAX1 driver class " + string + " does not implement org.xml.sax.Parser");
        }
        catch (NullPointerException nullPointerException) {
            throw new SAXException("System property org.xml.sax.parser not specified");
        }
    }

    public ParserAdapter(Parser parser) {
        this.setup(parser);
    }

    private void setup(Parser parser) {
        if (parser == null) {
            throw new NullPointerException("Parser argument must not be null");
        }
        this.parser = parser;
        this.atts = new AttributesImpl();
        this.nsSupport = new NamespaceSupport();
        this.attAdapter = new AttributeListAdapter();
    }

    public void setFeature(String string, boolean bl) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string.equals(NAMESPACES)) {
            this.checkNotParsing("feature", string);
            this.namespaces = bl;
            if (!this.namespaces && !this.prefixes) {
                this.prefixes = true;
            }
        } else if (string.equals(NAMESPACE_PREFIXES)) {
            this.checkNotParsing("feature", string);
            this.prefixes = bl;
            if (!this.prefixes && !this.namespaces) {
                this.namespaces = true;
            }
        } else if (string.equals(XMLNS_URIs)) {
            this.checkNotParsing("feature", string);
            this.uris = bl;
        } else {
            throw new SAXNotRecognizedException("Feature: " + string);
        }
    }

    public boolean getFeature(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string.equals(NAMESPACES)) {
            return this.namespaces;
        }
        if (string.equals(NAMESPACE_PREFIXES)) {
            return this.prefixes;
        }
        if (string.equals(XMLNS_URIs)) {
            return this.uris;
        }
        throw new SAXNotRecognizedException("Feature: " + string);
    }

    public void setProperty(String string, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException("Property: " + string);
    }

    public Object getProperty(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        throw new SAXNotRecognizedException("Property: " + string);
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    public void setDTDHandler(DTDHandler dTDHandler) {
        this.dtdHandler = dTDHandler;
    }

    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void parse(String string) throws IOException, SAXException {
        this.parse(new InputSource(string));
    }

    public void parse(InputSource inputSource) throws IOException, SAXException {
        if (this.parsing) {
            throw new SAXException("Parser is already in use");
        }
        this.setupParser();
        this.parsing = true;
        try {
            this.parser.parse(inputSource);
            Object var3_2 = null;
            this.parsing = false;
        }
        catch (Throwable throwable) {
            Object var3_3 = null;
            this.parsing = false;
            throw throwable;
        }
        this.parsing = false;
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
        if (this.contentHandler != null) {
            this.contentHandler.setDocumentLocator(locator);
        }
    }

    public void startDocument() throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.startDocument();
        }
    }

    public void endDocument() throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.endDocument();
        }
    }

    public void startElement(String string, AttributeList attributeList) throws SAXException {
        String string2;
        String string3;
        Vector<SAXException> vector = null;
        if (!this.namespaces) {
            if (this.contentHandler != null) {
                this.attAdapter.setAttributeList(attributeList);
                this.contentHandler.startElement("", "", string.intern(), this.attAdapter);
            }
            return;
        }
        this.nsSupport.pushContext();
        int n = attributeList.getLength();
        int n2 = 0;
        while (n2 < n) {
            block23: {
                block25: {
                    int n3;
                    String string4;
                    block24: {
                        string4 = attributeList.getName(n2);
                        if (!string4.startsWith("xmlns")) break block23;
                        n3 = string4.indexOf(58);
                        if (n3 != -1 || string4.length() != 5) break block24;
                        string3 = "";
                        break block25;
                    }
                    if (n3 != 5) break block23;
                    string3 = string4.substring(n3 + 1);
                }
                string2 = attributeList.getValue(n2);
                if (!this.nsSupport.declarePrefix(string3, string2)) {
                    this.reportError("Illegal Namespace prefix: " + string3);
                } else if (this.contentHandler != null) {
                    this.contentHandler.startPrefixMapping(string3, string2);
                }
            }
            ++n2;
        }
        this.atts.clear();
        int n4 = 0;
        while (n4 < n) {
            int n5;
            Object object;
            string3 = attributeList.getName(n4);
            String string5 = attributeList.getType(n4);
            string2 = attributeList.getValue(n4);
            if (string3.startsWith("xmlns") && (object = (n5 = string3.indexOf(58)) == -1 && string3.length() == 5 ? "" : (n5 != 5 ? null : string3.substring(6))) != null) {
                if (this.prefixes) {
                    if (this.uris) {
                        this.atts.addAttribute("http://www.w3.org/XML/1998/namespace", (String)object, string3.intern(), string5, string2);
                    } else {
                        this.atts.addAttribute("", "", string3.intern(), string5, string2);
                    }
                }
            } else {
                try {
                    object = this.processName(string3, true, true);
                    this.atts.addAttribute(object[0], object[1], object[2], string5, string2);
                }
                catch (SAXException sAXException) {
                    if (vector == null) {
                        vector = new Vector<SAXException>();
                    }
                    vector.addElement(sAXException);
                    this.atts.addAttribute("", string3, string3, string5, string2);
                }
            }
            ++n4;
        }
        if (vector != null && this.errorHandler != null) {
            int n6 = 0;
            while (n6 < vector.size()) {
                this.errorHandler.error((SAXParseException)vector.elementAt(n6));
                ++n6;
            }
        }
        if (this.contentHandler != null) {
            String[] stringArray = this.processName(string, false, false);
            this.contentHandler.startElement(stringArray[0], stringArray[1], stringArray[2], this.atts);
        }
    }

    public void endElement(String string) throws SAXException {
        if (!this.namespaces) {
            if (this.contentHandler != null) {
                this.contentHandler.endElement("", "", string.intern());
            }
            return;
        }
        String[] stringArray = this.processName(string, false, false);
        if (this.contentHandler != null) {
            this.contentHandler.endElement(stringArray[0], stringArray[1], stringArray[2]);
            Enumeration enumeration = this.nsSupport.getDeclaredPrefixes();
            while (enumeration.hasMoreElements()) {
                String string2 = (String)enumeration.nextElement();
                this.contentHandler.endPrefixMapping(string2);
            }
        }
        this.nsSupport.popContext();
    }

    public void characters(char[] cArray, int n, int n2) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.characters(cArray, n, n2);
        }
    }

    public void ignorableWhitespace(char[] cArray, int n, int n2) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.ignorableWhitespace(cArray, n, n2);
        }
    }

    public void processingInstruction(String string, String string2) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.processingInstruction(string, string2);
        }
    }

    private void setupParser() {
        if (!this.prefixes && !this.namespaces) {
            throw new IllegalStateException();
        }
        this.nsSupport.reset();
        if (this.uris) {
            this.nsSupport.setNamespaceDeclUris(true);
        }
        if (this.entityResolver != null) {
            this.parser.setEntityResolver(this.entityResolver);
        }
        if (this.dtdHandler != null) {
            this.parser.setDTDHandler(this.dtdHandler);
        }
        if (this.errorHandler != null) {
            this.parser.setErrorHandler(this.errorHandler);
        }
        this.parser.setDocumentHandler(this);
        this.locator = null;
    }

    private String[] processName(String string, boolean bl, boolean bl2) throws SAXException {
        String[] stringArray = this.nsSupport.processName(string, this.nameParts, bl);
        if (stringArray == null) {
            if (bl2) {
                throw this.makeException("Undeclared prefix: " + string);
            }
            this.reportError("Undeclared prefix: " + string);
            stringArray = new String[3];
            stringArray[1] = "";
            stringArray[0] = "";
            stringArray[2] = string.intern();
        }
        return stringArray;
    }

    void reportError(String string) throws SAXException {
        if (this.errorHandler != null) {
            this.errorHandler.error(this.makeException(string));
        }
    }

    private SAXParseException makeException(String string) {
        if (this.locator != null) {
            return new SAXParseException(string, this.locator);
        }
        return new SAXParseException(string, null, null, -1, -1);
    }

    private void checkNotParsing(String string, String string2) throws SAXNotSupportedException {
        if (this.parsing) {
            throw new SAXNotSupportedException("Cannot change " + string + ' ' + string2 + " while parsing");
        }
    }

    final class AttributeListAdapter
    implements Attributes {
        private AttributeList qAtts;

        AttributeListAdapter() {
        }

        void setAttributeList(AttributeList attributeList) {
            this.qAtts = attributeList;
        }

        public int getLength() {
            return this.qAtts.getLength();
        }

        public String getURI(int n) {
            return "";
        }

        public String getLocalName(int n) {
            return "";
        }

        public String getQName(int n) {
            return this.qAtts.getName(n).intern();
        }

        public String getType(int n) {
            return this.qAtts.getType(n).intern();
        }

        public String getValue(int n) {
            return this.qAtts.getValue(n);
        }

        public int getIndex(String string, String string2) {
            return -1;
        }

        public int getIndex(String string) {
            int n = ParserAdapter.this.atts.getLength();
            int n2 = 0;
            while (n2 < n) {
                if (this.qAtts.getName(n2).equals(string)) {
                    return n2;
                }
                ++n2;
            }
            return -1;
        }

        public String getType(String string, String string2) {
            return null;
        }

        public String getType(String string) {
            return this.qAtts.getType(string).intern();
        }

        public String getValue(String string, String string2) {
            return null;
        }

        public String getValue(String string) {
            return this.qAtts.getValue(string);
        }
    }
}

