/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.apache.xerces.impl.xs.opti.SchemaDOMParser;
import org.apache.xerces.util.JAXPNamespaceContextWrapper;
import org.apache.xerces.util.StAXLocationWrapper;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLStringBuffer;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.w3c.dom.Document;

final class StAXSchemaParser {
    private static final int CHUNK_SIZE = 1024;
    private static final int CHUNK_MASK = 1023;
    private final char[] fCharBuffer = new char[1024];
    private SymbolTable fSymbolTable = new SymbolTable();
    private SchemaDOMParser fSchemaDOMParser;
    private final StAXLocationWrapper fLocationWrapper = new StAXLocationWrapper();
    private final JAXPNamespaceContextWrapper fNamespaceContext = new JAXPNamespaceContextWrapper(this.fSymbolTable);
    private final org.apache.xerces.xni.QName fElementQName = new org.apache.xerces.xni.QName();
    private final org.apache.xerces.xni.QName fAttributeQName = new org.apache.xerces.xni.QName();
    private final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
    private final XMLString fTempString = new XMLString();
    private final ArrayList fDeclaredPrefixes = new ArrayList();
    private final XMLStringBuffer fStringBuffer = new XMLStringBuffer();
    private int fDepth;

    public StAXSchemaParser() {
        this.fNamespaceContext.setDeclaredPrefixes(this.fDeclaredPrefixes);
    }

    public void reset(SchemaDOMParser schemaDOMParser, SymbolTable symbolTable) {
        this.fSchemaDOMParser = schemaDOMParser;
        this.fSymbolTable = symbolTable;
        this.fNamespaceContext.setSymbolTable(this.fSymbolTable);
        this.fNamespaceContext.reset();
    }

    public Document getDocument() {
        return this.fSchemaDOMParser.getDocument();
    }

    /*
     * Enabled aggressive block sorting
     */
    public void parse(XMLEventReader xMLEventReader) throws XMLStreamException, XNIException {
        XMLEvent xMLEvent = xMLEventReader.peek();
        if (xMLEvent != null) {
            int n = xMLEvent.getEventType();
            if (n != 7 && n != 1) {
                throw new XMLStreamException();
            }
            this.fLocationWrapper.setLocation(xMLEvent.getLocation());
            this.fSchemaDOMParser.startDocument(this.fLocationWrapper, null, this.fNamespaceContext, null);
            block12: while (xMLEventReader.hasNext()) {
                xMLEvent = xMLEventReader.nextEvent();
                n = xMLEvent.getEventType();
                switch (n) {
                    case 1: {
                        ++this.fDepth;
                        StartElement startElement = xMLEvent.asStartElement();
                        this.fillQName(this.fElementQName, startElement.getName());
                        this.fLocationWrapper.setLocation(startElement.getLocation());
                        this.fNamespaceContext.setNamespaceContext(startElement.getNamespaceContext());
                        this.fillXMLAttributes(startElement);
                        this.fillDeclaredPrefixes(startElement);
                        this.addNamespaceDeclarations();
                        this.fNamespaceContext.pushContext();
                        this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, null);
                        break;
                    }
                    case 2: {
                        EndElement endElement = xMLEvent.asEndElement();
                        this.fillQName(this.fElementQName, endElement.getName());
                        this.fillDeclaredPrefixes(endElement);
                        this.fLocationWrapper.setLocation(endElement.getLocation());
                        this.fSchemaDOMParser.endElement(this.fElementQName, null);
                        this.fNamespaceContext.popContext();
                        --this.fDepth;
                        if (this.fDepth > 0) break;
                        break block12;
                    }
                    case 4: {
                        this.sendCharactersToSchemaParser(xMLEvent.asCharacters().getData(), false);
                        break;
                    }
                    case 6: {
                        this.sendCharactersToSchemaParser(xMLEvent.asCharacters().getData(), true);
                        break;
                    }
                    case 12: {
                        this.fSchemaDOMParser.startCDATA(null);
                        this.sendCharactersToSchemaParser(xMLEvent.asCharacters().getData(), false);
                        this.fSchemaDOMParser.endCDATA(null);
                        break;
                    }
                    case 3: {
                        ProcessingInstruction processingInstruction = (ProcessingInstruction)xMLEvent;
                        this.fillProcessingInstruction(processingInstruction.getData());
                        this.fSchemaDOMParser.processingInstruction(processingInstruction.getTarget(), this.fTempString, null);
                        break;
                    }
                    case 11: {
                        break;
                    }
                    case 9: {
                        break;
                    }
                    case 5: {
                        break;
                    }
                    case 7: {
                        ++this.fDepth;
                    }
                }
            }
            this.fLocationWrapper.setLocation(null);
            this.fNamespaceContext.setNamespaceContext(null);
            this.fSchemaDOMParser.endDocument(null);
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    public void parse(XMLStreamReader xMLStreamReader) throws XMLStreamException, XNIException {
        if (xMLStreamReader.hasNext()) {
            int n = xMLStreamReader.getEventType();
            if (n != 7 && n != 1) {
                throw new XMLStreamException();
            }
            this.fLocationWrapper.setLocation(xMLStreamReader.getLocation());
            this.fSchemaDOMParser.startDocument(this.fLocationWrapper, null, this.fNamespaceContext, null);
            boolean bl = true;
            block12: while (xMLStreamReader.hasNext()) {
                if (!bl) {
                    n = xMLStreamReader.next();
                } else {
                    bl = false;
                }
                switch (n) {
                    case 1: {
                        ++this.fDepth;
                        this.fLocationWrapper.setLocation(xMLStreamReader.getLocation());
                        this.fNamespaceContext.setNamespaceContext(xMLStreamReader.getNamespaceContext());
                        this.fillQName(this.fElementQName, xMLStreamReader.getNamespaceURI(), xMLStreamReader.getLocalName(), xMLStreamReader.getPrefix());
                        this.fillXMLAttributes(xMLStreamReader);
                        this.fillDeclaredPrefixes(xMLStreamReader);
                        this.addNamespaceDeclarations();
                        this.fNamespaceContext.pushContext();
                        this.fSchemaDOMParser.startElement(this.fElementQName, this.fAttributes, null);
                        break;
                    }
                    case 2: {
                        this.fLocationWrapper.setLocation(xMLStreamReader.getLocation());
                        this.fNamespaceContext.setNamespaceContext(xMLStreamReader.getNamespaceContext());
                        this.fillQName(this.fElementQName, xMLStreamReader.getNamespaceURI(), xMLStreamReader.getLocalName(), xMLStreamReader.getPrefix());
                        this.fillDeclaredPrefixes(xMLStreamReader);
                        this.fSchemaDOMParser.endElement(this.fElementQName, null);
                        this.fNamespaceContext.popContext();
                        --this.fDepth;
                        if (this.fDepth > 0) break;
                        break block12;
                    }
                    case 4: {
                        this.fTempString.setValues(xMLStreamReader.getTextCharacters(), xMLStreamReader.getTextStart(), xMLStreamReader.getTextLength());
                        this.fSchemaDOMParser.characters(this.fTempString, null);
                        break;
                    }
                    case 6: {
                        this.fTempString.setValues(xMLStreamReader.getTextCharacters(), xMLStreamReader.getTextStart(), xMLStreamReader.getTextLength());
                        this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
                        break;
                    }
                    case 12: {
                        this.fSchemaDOMParser.startCDATA(null);
                        this.fTempString.setValues(xMLStreamReader.getTextCharacters(), xMLStreamReader.getTextStart(), xMLStreamReader.getTextLength());
                        this.fSchemaDOMParser.characters(this.fTempString, null);
                        this.fSchemaDOMParser.endCDATA(null);
                        break;
                    }
                    case 3: {
                        this.fillProcessingInstruction(xMLStreamReader.getPIData());
                        this.fSchemaDOMParser.processingInstruction(xMLStreamReader.getPITarget(), this.fTempString, null);
                        break;
                    }
                    case 11: {
                        break;
                    }
                    case 9: {
                        break;
                    }
                    case 5: {
                        break;
                    }
                    case 7: {
                        ++this.fDepth;
                    }
                }
            }
            this.fLocationWrapper.setLocation(null);
            this.fNamespaceContext.setNamespaceContext(null);
            this.fSchemaDOMParser.endDocument(null);
        }
    }

    private void sendCharactersToSchemaParser(String string, boolean bl) {
        if (string != null) {
            int n = string.length();
            int n2 = n & 0x3FF;
            if (n2 > 0) {
                string.getChars(0, n2, this.fCharBuffer, 0);
                this.fTempString.setValues(this.fCharBuffer, 0, n2);
                if (bl) {
                    this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
                } else {
                    this.fSchemaDOMParser.characters(this.fTempString, null);
                }
            }
            int n3 = n2;
            while (n3 < n) {
                string.getChars(n3, n3 += 1024, this.fCharBuffer, 0);
                this.fTempString.setValues(this.fCharBuffer, 0, 1024);
                if (bl) {
                    this.fSchemaDOMParser.ignorableWhitespace(this.fTempString, null);
                    continue;
                }
                this.fSchemaDOMParser.characters(this.fTempString, null);
            }
        }
    }

    private void fillProcessingInstruction(String string) {
        char[] cArray = this.fCharBuffer;
        int n = string.length();
        if (cArray.length < n) {
            cArray = string.toCharArray();
        } else {
            string.getChars(0, n, cArray, 0);
        }
        this.fTempString.setValues(cArray, 0, n);
    }

    private void fillXMLAttributes(StartElement startElement) {
        this.fAttributes.removeAllAttributes();
        Iterator<Attribute> iterator = startElement.getAttributes();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            this.fillQName(this.fAttributeQName, attribute.getName());
            String string = attribute.getDTDType();
            int n = this.fAttributes.getLength();
            this.fAttributes.addAttributeNS(this.fAttributeQName, string != null ? string : XMLSymbols.fCDATASymbol, attribute.getValue());
            this.fAttributes.setSpecified(n, attribute.isSpecified());
        }
    }

    private void fillXMLAttributes(XMLStreamReader xMLStreamReader) {
        this.fAttributes.removeAllAttributes();
        int n = xMLStreamReader.getAttributeCount();
        for (int i = 0; i < n; ++i) {
            this.fillQName(this.fAttributeQName, xMLStreamReader.getAttributeNamespace(i), xMLStreamReader.getAttributeLocalName(i), xMLStreamReader.getAttributePrefix(i));
            String string = xMLStreamReader.getAttributeType(i);
            this.fAttributes.addAttributeNS(this.fAttributeQName, string != null ? string : XMLSymbols.fCDATASymbol, xMLStreamReader.getAttributeValue(i));
            this.fAttributes.setSpecified(i, xMLStreamReader.isAttributeSpecified(i));
        }
    }

    private void addNamespaceDeclarations() {
        String string = null;
        String string2 = null;
        String string3 = null;
        String string42 = null;
        String string5 = null;
        for (String string42 : this.fDeclaredPrefixes) {
            string5 = this.fNamespaceContext.getURI(string42);
            if (string42.length() > 0) {
                string = XMLSymbols.PREFIX_XMLNS;
                string2 = string42;
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

    private void fillDeclaredPrefixes(StartElement startElement) {
        this.fillDeclaredPrefixes(startElement.getNamespaces());
    }

    private void fillDeclaredPrefixes(EndElement endElement) {
        this.fillDeclaredPrefixes(endElement.getNamespaces());
    }

    private void fillDeclaredPrefixes(Iterator iterator) {
        this.fDeclaredPrefixes.clear();
        while (iterator.hasNext()) {
            Namespace namespace = (Namespace)iterator.next();
            String string = namespace.getPrefix();
            this.fDeclaredPrefixes.add(string != null ? string : "");
        }
    }

    private void fillDeclaredPrefixes(XMLStreamReader xMLStreamReader) {
        this.fDeclaredPrefixes.clear();
        int n = xMLStreamReader.getNamespaceCount();
        for (int i = 0; i < n; ++i) {
            String string = xMLStreamReader.getNamespacePrefix(i);
            this.fDeclaredPrefixes.add(string != null ? string : "");
        }
    }

    private void fillQName(org.apache.xerces.xni.QName qName, QName qName2) {
        this.fillQName(qName, qName2.getNamespaceURI(), qName2.getLocalPart(), qName2.getPrefix());
    }

    final void fillQName(org.apache.xerces.xni.QName qName, String string, String string2, String string3) {
        string = string != null && string.length() > 0 ? this.fSymbolTable.addSymbol(string) : null;
        string2 = string2 != null ? this.fSymbolTable.addSymbol(string2) : XMLSymbols.EMPTY_STRING;
        string3 = string3 != null && string3.length() > 0 ? this.fSymbolTable.addSymbol(string3) : XMLSymbols.EMPTY_STRING;
        String string4 = string2;
        if (string3 != XMLSymbols.EMPTY_STRING) {
            this.fStringBuffer.clear();
            this.fStringBuffer.append(string3);
            this.fStringBuffer.append(':');
            this.fStringBuffer.append(string2);
            string4 = this.fSymbolTable.addSymbol(this.fStringBuffer.ch, this.fStringBuffer.offset, this.fStringBuffer.length);
        }
        qName.setValues(string3, string2, string4, string);
    }
}

