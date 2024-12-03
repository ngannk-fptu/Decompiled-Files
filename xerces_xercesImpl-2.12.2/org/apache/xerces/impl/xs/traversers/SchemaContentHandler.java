/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import org.apache.xerces.impl.xs.opti.SchemaDOMParser;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.SAXLocatorWrapper;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParseException;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

final class SchemaContentHandler
implements ContentHandler {
    private SymbolTable fSymbolTable;
    private SchemaDOMParser fSchemaDOMParser;
    private final SAXLocatorWrapper fSAXLocatorWrapper = new SAXLocatorWrapper();
    private NamespaceSupport fNamespaceContext = new NamespaceSupport();
    private boolean fNeedPushNSContext;
    private boolean fNamespacePrefixes = false;
    private boolean fStringsInternalized = false;
    private final QName fElementQName = new QName();
    private final QName fAttributeQName = new QName();
    private final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
    private final XMLString fTempString = new XMLString();
    private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();

    public Document getDocument() {
        return this.fSchemaDOMParser.getDocument();
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.fSAXLocatorWrapper.setLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        this.fNeedPushNSContext = true;
        this.fNamespaceContext.reset();
        try {
            this.fSchemaDOMParser.startDocument(this.fSAXLocatorWrapper, null, this.fNamespaceContext, null);
        }
        catch (XMLParseException xMLParseException) {
            SchemaContentHandler.convertToSAXParseException(xMLParseException);
        }
        catch (XNIException xNIException) {
            SchemaContentHandler.convertToSAXException(xNIException);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        this.fSAXLocatorWrapper.setLocator(null);
        try {
            this.fSchemaDOMParser.endDocument(null);
        }
        catch (XMLParseException xMLParseException) {
            SchemaContentHandler.convertToSAXParseException(xMLParseException);
        }
        catch (XNIException xNIException) {
            SchemaContentHandler.convertToSAXException(xNIException);
        }
    }

    @Override
    public void startPrefixMapping(String string, String string2) throws SAXException {
        if (this.fNeedPushNSContext) {
            this.fNeedPushNSContext = false;
            this.fNamespaceContext.pushContext();
        }
        if (!this.fStringsInternalized) {
            string = string != null ? this.fSymbolTable.addSymbol(string) : XMLSymbols.EMPTY_STRING;
            string2 = string2 != null && string2.length() > 0 ? this.fSymbolTable.addSymbol(string2) : null;
        } else {
            if (string == null) {
                string = XMLSymbols.EMPTY_STRING;
            }
            if (string2 != null && string2.length() == 0) {
                string2 = null;
            }
        }
        this.fNamespaceContext.declarePrefix(string, string2);
    }

    @Override
    public void endPrefixMapping(String string) throws SAXException {
    }

    @Override
    public void startElement(String string, String string2, String string3, Attributes attributes) throws SAXException {
        int n;
        if (this.fNeedPushNSContext) {
            this.fNamespaceContext.pushContext();
        }
        this.fNeedPushNSContext = true;
        this.fillQName(this.fElementQName, string, string2, string3);
        this.fillXMLAttributes(attributes);
        if (!this.fNamespacePrefixes && (n = this.fNamespaceContext.getDeclaredPrefixCount()) > 0) {
            this.addNamespaceDeclarations(n);
        }
        try {
            this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, null);
        }
        catch (XMLParseException xMLParseException) {
            SchemaContentHandler.convertToSAXParseException(xMLParseException);
        }
        catch (XNIException xNIException) {
            SchemaContentHandler.convertToSAXException(xNIException);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void endElement(String string, String string2, String string3) throws SAXException {
        this.fillQName(this.fElementQName, string, string2, string3);
        try {
            this.fSchemaDOMParser.endElement(this.fElementQName, null);
        }
        catch (XMLParseException xMLParseException) {
            SchemaContentHandler.convertToSAXParseException(xMLParseException);
        }
        catch (XNIException xNIException) {
            SchemaContentHandler.convertToSAXException(xNIException);
        }
        finally {
            this.fNamespaceContext.popContext();
        }
    }

    @Override
    public void characters(char[] cArray, int n, int n2) throws SAXException {
        try {
            this.fTempString.setValues(cArray, n, n2);
            this.fSchemaDOMParser.characters(this.fTempString, null);
        }
        catch (XMLParseException xMLParseException) {
            SchemaContentHandler.convertToSAXParseException(xMLParseException);
        }
        catch (XNIException xNIException) {
            SchemaContentHandler.convertToSAXException(xNIException);
        }
    }

    @Override
    public void ignorableWhitespace(char[] cArray, int n, int n2) throws SAXException {
        try {
            this.fTempString.setValues(cArray, n, n2);
            this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
        }
        catch (XMLParseException xMLParseException) {
            SchemaContentHandler.convertToSAXParseException(xMLParseException);
        }
        catch (XNIException xNIException) {
            SchemaContentHandler.convertToSAXException(xNIException);
        }
    }

    @Override
    public void processingInstruction(String string, String string2) throws SAXException {
        try {
            this.fTempString.setValues(string2.toCharArray(), 0, string2.length());
            this.fSchemaDOMParser.processingInstruction(string, this.fTempString, null);
        }
        catch (XMLParseException xMLParseException) {
            SchemaContentHandler.convertToSAXParseException(xMLParseException);
        }
        catch (XNIException xNIException) {
            SchemaContentHandler.convertToSAXException(xNIException);
        }
    }

    @Override
    public void skippedEntity(String string) throws SAXException {
    }

    private void fillQName(QName qName, String string, String string2, String string3) {
        if (!this.fStringsInternalized) {
            string = string != null && string.length() > 0 ? this.fSymbolTable.addSymbol(string) : null;
            string2 = string2 != null ? this.fSymbolTable.addSymbol(string2) : XMLSymbols.EMPTY_STRING;
            string3 = string3 != null ? this.fSymbolTable.addSymbol(string3) : XMLSymbols.EMPTY_STRING;
        } else {
            if (string != null && string.length() == 0) {
                string = null;
            }
            if (string2 == null) {
                string2 = XMLSymbols.EMPTY_STRING;
            }
            if (string3 == null) {
                string3 = XMLSymbols.EMPTY_STRING;
            }
        }
        String string4 = XMLSymbols.EMPTY_STRING;
        int n = string3.indexOf(58);
        if (n != -1) {
            string4 = this.fSymbolTable.addSymbol(string3.substring(0, n));
            if (string2 == XMLSymbols.EMPTY_STRING) {
                string2 = this.fSymbolTable.addSymbol(string3.substring(n + 1));
            }
        } else if (string2 == XMLSymbols.EMPTY_STRING) {
            string2 = string3;
        }
        qName.setValues(string4, string2, string3, string);
    }

    private void fillXMLAttributes(Attributes attributes) {
        this.fAttributes.removeAllAttributes();
        int n = attributes.getLength();
        for (int i = 0; i < n; ++i) {
            this.fillQName(this.fAttributeQName, attributes.getURI(i), attributes.getLocalName(i), attributes.getQName(i));
            String string = attributes.getType(i);
            this.fAttributes.addAttributeNS(this.fAttributeQName, string != null ? string : XMLSymbols.fCDATASymbol, attributes.getValue(i));
            this.fAttributes.setSpecified(i, true);
        }
    }

    private void addNamespaceDeclarations(int n) {
        String string = null;
        String string2 = null;
        String string3 = null;
        String string4 = null;
        String string5 = null;
        for (int i = 0; i < n; ++i) {
            string4 = this.fNamespaceContext.getDeclaredPrefixAt(i);
            string5 = this.fNamespaceContext.getURI(string4);
            if (string4.length() > 0) {
                string = XMLSymbols.PREFIX_XMLNS;
                string2 = string4;
                this.fStringBuffer.clear();
                this.fStringBuffer.append(string);
                this.fStringBuffer.append(':');
                this.fStringBuffer.append(string2);
                string3 = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
            } else {
                string = XMLSymbols.EMPTY_STRING;
                string2 = XMLSymbols.PREFIX_XMLNS;
                string3 = XMLSymbols.PREFIX_XMLNS;
            }
            this.fAttributeQName.setValues(string, string2, string3, NamespaceContext.XMLNS_URI);
            this.fAttributes.addAttribute(this.fAttributeQName, XMLSymbols.fCDATASymbol, string5 != null ? string5 : XMLSymbols.EMPTY_STRING);
        }
    }

    public void reset(SchemaDOMParser schemaDOMParser, SymbolTable symbolTable, boolean bl, boolean bl2) {
        this.fSchemaDOMParser = schemaDOMParser;
        this.fSymbolTable = symbolTable;
        this.fNamespacePrefixes = bl;
        this.fStringsInternalized = bl2;
    }

    static void convertToSAXParseException(XMLParseException xMLParseException) throws SAXException {
        Exception exception = xMLParseException.getException();
        if (exception == null) {
            LocatorImpl locatorImpl = new LocatorImpl();
            locatorImpl.setPublicId(xMLParseException.getPublicId());
            locatorImpl.setSystemId(xMLParseException.getExpandedSystemId());
            locatorImpl.setLineNumber(xMLParseException.getLineNumber());
            locatorImpl.setColumnNumber(xMLParseException.getColumnNumber());
            throw new SAXParseException(xMLParseException.getMessage(), locatorImpl);
        }
        if (exception instanceof SAXException) {
            throw (SAXException)exception;
        }
        throw new SAXException(exception);
    }

    static void convertToSAXException(XNIException xNIException) throws SAXException {
        Exception exception = xNIException.getException();
        if (exception == null) {
            throw new SAXException(xNIException.getMessage());
        }
        if (exception instanceof SAXException) {
            throw (SAXException)exception;
        }
        throw new SAXException(exception);
    }
}

