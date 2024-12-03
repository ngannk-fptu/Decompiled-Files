/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serialize;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xml.serialize.BaseMarkupSerializer;
import org.apache.xml.serialize.ElementState;
import org.apache.xml.serialize.OutputFormat;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.AttributeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMLSerializer
extends BaseMarkupSerializer {
    protected static final boolean DEBUG = false;
    protected NamespaceSupport fNSBinder;
    protected NamespaceSupport fLocalNSBinder;
    protected SymbolTable fSymbolTable;
    protected static final String PREFIX = "NS";
    protected boolean fNamespaces = false;
    protected boolean fNamespacePrefixes = true;
    private boolean fPreserveSpace;

    public XMLSerializer() {
        super(new OutputFormat("xml", null, false));
    }

    public XMLSerializer(OutputFormat outputFormat) {
        super(outputFormat != null ? outputFormat : new OutputFormat("xml", null, false));
        this._format.setMethod("xml");
    }

    public XMLSerializer(Writer writer, OutputFormat outputFormat) {
        super(outputFormat != null ? outputFormat : new OutputFormat("xml", null, false));
        this._format.setMethod("xml");
        this.setOutputCharStream(writer);
    }

    public XMLSerializer(OutputStream outputStream, OutputFormat outputFormat) {
        super(outputFormat != null ? outputFormat : new OutputFormat("xml", null, false));
        this._format.setMethod("xml");
        this.setOutputByteStream(outputStream);
    }

    @Override
    public void setOutputFormat(OutputFormat outputFormat) {
        super.setOutputFormat(outputFormat != null ? outputFormat : new OutputFormat("xml", null, false));
    }

    public void setNamespaces(boolean bl) {
        this.fNamespaces = bl;
        if (this.fNSBinder == null) {
            this.fNSBinder = new NamespaceSupport();
            this.fLocalNSBinder = new NamespaceSupport();
            this.fSymbolTable = new SymbolTable();
        }
    }

    @Override
    public void startElement(String string, String string2, String string3, Attributes attributes) throws SAXException {
        try {
            String string4;
            Object object;
            String string5;
            Object object2;
            if (this._printer == null) {
                String string6 = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", null);
                throw new IllegalStateException(string6);
            }
            ElementState elementState = this.getElementState();
            if (this.isDocumentState()) {
                if (!this._started) {
                    this.startDocument(string2 == null || string2.length() == 0 ? string3 : string2);
                }
            } else {
                if (elementState.empty) {
                    this._printer.printText('>');
                }
                if (elementState.inCData) {
                    this._printer.printText("]]>");
                    elementState.inCData = false;
                }
                if (this._indenting && !elementState.preserveSpace && (elementState.empty || elementState.afterElement || elementState.afterComment)) {
                    this._printer.breakLine();
                }
            }
            boolean bl = elementState.preserveSpace;
            attributes = this.extractNamespaces(attributes);
            if (string3 == null || string3.length() == 0) {
                if (string2 == null) {
                    String string7 = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoName", null);
                    throw new SAXException(string7);
                }
                string3 = string != null && !string.equals("") ? ((object2 = this.getPrefix(string)) != null && ((String)object2).length() > 0 ? (String)object2 + ":" + string2 : string2) : string2;
            }
            this._printer.printText('<');
            this._printer.printText(string3);
            this._printer.indent();
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); ++i) {
                    this._printer.printSpace();
                    string5 = attributes.getQName(i);
                    if (string5 != null && string5.length() == 0) {
                        string5 = attributes.getLocalName(i);
                        object = attributes.getURI(i);
                        if (!(object == null || ((String)object).length() == 0 || string != null && string.length() != 0 && ((String)object).equals(string) || (object2 = this.getPrefix((String)object)) == null || ((String)object2).length() <= 0)) {
                            string5 = (String)object2 + ":" + string5;
                        }
                    }
                    if ((string4 = attributes.getValue(i)) == null) {
                        string4 = "";
                    }
                    this._printer.printText(string5);
                    this._printer.printText("=\"");
                    this.printEscaped(string4);
                    this._printer.printText('\"');
                    if (!string5.equals("xml:space")) continue;
                    bl = string4.equals("preserve") ? true : this._format.getPreserveSpace();
                }
            }
            if (this._prefixes != null) {
                object2 = this._prefixes.entrySet().iterator();
                while (object2.hasNext()) {
                    this._printer.printSpace();
                    object = (Map.Entry)object2.next();
                    string4 = (String)object.getKey();
                    string5 = (String)object.getValue();
                    if (string5.length() == 0) {
                        this._printer.printText("xmlns=\"");
                        this.printEscaped(string4);
                        this._printer.printText('\"');
                        continue;
                    }
                    this._printer.printText("xmlns:");
                    this._printer.printText(string5);
                    this._printer.printText("=\"");
                    this.printEscaped(string4);
                    this._printer.printText('\"');
                }
            }
            elementState = this.enterElementState(string, string2, string3, bl);
            string5 = string2 == null || string2.length() == 0 ? string3 : string + "^" + string2;
            elementState.doCData = this._format.isCDataElement(string5);
            elementState.unescaped = this._format.isNonEscapingElement(string5);
        }
        catch (IOException iOException) {
            throw new SAXException(iOException);
        }
    }

    @Override
    public void endElement(String string, String string2, String string3) throws SAXException {
        try {
            this.endElementIO(string, string2, string3);
        }
        catch (IOException iOException) {
            throw new SAXException(iOException);
        }
    }

    public void endElementIO(String string, String string2, String string3) throws IOException {
        this._printer.unindent();
        ElementState elementState = this.getElementState();
        if (elementState.empty) {
            this._printer.printText("/>");
        } else {
            if (elementState.inCData) {
                this._printer.printText("]]>");
            }
            if (this._indenting && !elementState.preserveSpace && (elementState.afterElement || elementState.afterComment)) {
                this._printer.breakLine();
            }
            this._printer.printText("</");
            this._printer.printText(elementState.rawName);
            this._printer.printText('>');
        }
        elementState = this.leaveElementState();
        elementState.afterElement = true;
        elementState.afterComment = false;
        elementState.empty = false;
        if (this.isDocumentState()) {
            this._printer.flush();
        }
    }

    @Override
    public void startElement(String string, AttributeList attributeList) throws SAXException {
        try {
            if (this._printer == null) {
                String string2 = DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "NoWriterSupplied", null);
                throw new IllegalStateException(string2);
            }
            ElementState elementState = this.getElementState();
            if (this.isDocumentState()) {
                if (!this._started) {
                    this.startDocument(string);
                }
            } else {
                if (elementState.empty) {
                    this._printer.printText('>');
                }
                if (elementState.inCData) {
                    this._printer.printText("]]>");
                    elementState.inCData = false;
                }
                if (this._indenting && !elementState.preserveSpace && (elementState.empty || elementState.afterElement || elementState.afterComment)) {
                    this._printer.breakLine();
                }
            }
            boolean bl = elementState.preserveSpace;
            this._printer.printText('<');
            this._printer.printText(string);
            this._printer.indent();
            if (attributeList != null) {
                for (int i = 0; i < attributeList.getLength(); ++i) {
                    this._printer.printSpace();
                    String string3 = attributeList.getName(i);
                    String string4 = attributeList.getValue(i);
                    if (string4 != null) {
                        this._printer.printText(string3);
                        this._printer.printText("=\"");
                        this.printEscaped(string4);
                        this._printer.printText('\"');
                    }
                    if (!string3.equals("xml:space")) continue;
                    bl = string4.equals("preserve") ? true : this._format.getPreserveSpace();
                }
            }
            elementState = this.enterElementState(null, null, string, bl);
            elementState.doCData = this._format.isCDataElement(string);
            elementState.unescaped = this._format.isNonEscapingElement(string);
        }
        catch (IOException iOException) {
            throw new SAXException(iOException);
        }
    }

    @Override
    public void endElement(String string) throws SAXException {
        this.endElement(null, null, string);
    }

    protected void startDocument(String string) throws IOException {
        String string2 = this._printer.leaveDTD();
        if (!this._started) {
            if (!this._format.getOmitXMLDeclaration()) {
                StringBuffer stringBuffer = new StringBuffer("<?xml version=\"");
                if (this._format.getVersion() != null) {
                    stringBuffer.append(this._format.getVersion());
                } else {
                    stringBuffer.append("1.0");
                }
                stringBuffer.append('\"');
                String string3 = this._format.getEncoding();
                if (string3 != null) {
                    stringBuffer.append(" encoding=\"");
                    stringBuffer.append(string3);
                    stringBuffer.append('\"');
                }
                if (this._format.getStandalone() && this._docTypeSystemId == null && this._docTypePublicId == null) {
                    stringBuffer.append(" standalone=\"yes\"");
                }
                stringBuffer.append("?>");
                this._printer.printText(stringBuffer);
                this._printer.breakLine();
            }
            if (!this._format.getOmitDocumentType()) {
                if (this._docTypeSystemId != null) {
                    this._printer.printText("<!DOCTYPE ");
                    this._printer.printText(string);
                    if (this._docTypePublicId != null) {
                        this._printer.printText(" PUBLIC ");
                        this.printDoctypeURL(this._docTypePublicId);
                        if (this._indenting) {
                            this._printer.breakLine();
                            for (int i = 0; i < 18 + string.length(); ++i) {
                                this._printer.printText(" ");
                            }
                        } else {
                            this._printer.printText(" ");
                        }
                        this.printDoctypeURL(this._docTypeSystemId);
                    } else {
                        this._printer.printText(" SYSTEM ");
                        this.printDoctypeURL(this._docTypeSystemId);
                    }
                    if (string2 != null && string2.length() > 0) {
                        this._printer.printText(" [");
                        this.printText(string2, true, true);
                        this._printer.printText(']');
                    }
                    this._printer.printText(">");
                    this._printer.breakLine();
                } else if (string2 != null && string2.length() > 0) {
                    this._printer.printText("<!DOCTYPE ");
                    this._printer.printText(string);
                    this._printer.printText(" [");
                    this.printText(string2, true, true);
                    this._printer.printText("]>");
                    this._printer.breakLine();
                }
            }
        }
        this._started = true;
        this.serializePreRoot();
    }

    @Override
    protected void serializeElement(Element element) throws IOException {
        String string;
        String string2;
        Attr attr;
        int n;
        if (this.fNamespaces) {
            this.fLocalNSBinder.reset();
            this.fNSBinder.pushContext();
        }
        String string3 = element.getTagName();
        ElementState elementState = this.getElementState();
        if (this.isDocumentState()) {
            if (!this._started) {
                this.startDocument(string3);
            }
        } else {
            if (elementState.empty) {
                this._printer.printText('>');
            }
            if (elementState.inCData) {
                this._printer.printText("]]>");
                elementState.inCData = false;
            }
            if (this._indenting && !elementState.preserveSpace && (elementState.empty || elementState.afterElement || elementState.afterComment)) {
                this._printer.breakLine();
            }
        }
        this.fPreserveSpace = elementState.preserveSpace;
        int n2 = 0;
        NamedNodeMap namedNodeMap = null;
        if (element.hasAttributes()) {
            namedNodeMap = element.getAttributes();
            n2 = namedNodeMap.getLength();
        }
        if (!this.fNamespaces) {
            this._printer.printText('<');
            this._printer.printText(string3);
            this._printer.indent();
            for (n = 0; n < n2; ++n) {
                attr = (Attr)namedNodeMap.item(n);
                string2 = attr.getName();
                string = attr.getValue();
                if (string == null) {
                    string = "";
                }
                this.printAttribute(string2, string, attr.getSpecified(), attr);
            }
        } else {
            String string4;
            boolean bl;
            String string5;
            String string6;
            for (n = 0; n < n2; ++n) {
                attr = (Attr)namedNodeMap.item(n);
                string6 = attr.getNamespaceURI();
                if (string6 == null || !string6.equals(NamespaceContext.XMLNS_URI)) continue;
                string = attr.getNodeValue();
                if (string == null) {
                    string = XMLSymbols.EMPTY_STRING;
                }
                if (string.equals(NamespaceContext.XMLNS_URI)) {
                    if (this.fDOMErrorHandler == null) continue;
                    string5 = DOMMessageFormatter.formatMessage("http://www.w3.org/TR/1998/REC-xml-19980210", "CantBindXMLNS", null);
                    this.modifyDOMError(string5, (short)2, null, attr);
                    bl = this.fDOMErrorHandler.handleError(this.fDOMError);
                    if (bl) continue;
                    throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SerializationStopped", null));
                }
                string4 = attr.getPrefix();
                string4 = string4 == null || string4.length() == 0 ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(string4);
                string5 = this.fSymbolTable.addSymbol(attr.getLocalName());
                if (string4 == XMLSymbols.PREFIX_XMLNS) {
                    if ((string = this.fSymbolTable.addSymbol(string)).length() == 0) continue;
                    this.fNSBinder.declarePrefix(string5, string);
                    continue;
                }
                string = this.fSymbolTable.addSymbol(string);
                this.fNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, string);
            }
            string6 = element.getNamespaceURI();
            string4 = element.getPrefix();
            if (string6 != null && string4 != null && string6.length() == 0 && string4.length() != 0) {
                string4 = null;
                this._printer.printText('<');
                this._printer.printText(element.getLocalName());
                this._printer.indent();
            } else {
                this._printer.printText('<');
                this._printer.printText(string3);
                this._printer.indent();
            }
            if (string6 != null) {
                string6 = this.fSymbolTable.addSymbol(string6);
                String string7 = string4 = string4 == null || string4.length() == 0 ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(string4);
                if (this.fNSBinder.getURI(string4) != string6) {
                    if (this.fNamespacePrefixes) {
                        this.printNamespaceAttr(string4, string6);
                    }
                    this.fLocalNSBinder.declarePrefix(string4, string6);
                    this.fNSBinder.declarePrefix(string4, string6);
                }
            } else if (element.getLocalName() == null) {
                if (this.fDOMErrorHandler != null) {
                    string5 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalElementName", new Object[]{element.getNodeName()});
                    this.modifyDOMError(string5, (short)2, null, element);
                    bl = this.fDOMErrorHandler.handleError(this.fDOMError);
                    if (!bl) {
                        throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SerializationStopped", null));
                    }
                }
            } else {
                string6 = this.fNSBinder.getURI(XMLSymbols.EMPTY_STRING);
                if (string6 != null && string6.length() > 0) {
                    if (this.fNamespacePrefixes) {
                        this.printNamespaceAttr(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
                    }
                    this.fLocalNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
                    this.fNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
                }
            }
            for (n = 0; n < n2; ++n) {
                attr = (Attr)namedNodeMap.item(n);
                string = attr.getValue();
                string2 = attr.getNodeName();
                string6 = attr.getNamespaceURI();
                if (string6 != null && string6.length() == 0) {
                    string6 = null;
                    string2 = attr.getLocalName();
                }
                if (string == null) {
                    string = XMLSymbols.EMPTY_STRING;
                }
                if (string6 != null) {
                    string4 = attr.getPrefix();
                    string4 = string4 == null ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(string4);
                    string5 = this.fSymbolTable.addSymbol(attr.getLocalName());
                    if (string6 != null && string6.equals(NamespaceContext.XMLNS_URI)) {
                        String string8;
                        string4 = attr.getPrefix();
                        string4 = string4 == null || string4.length() == 0 ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(string4);
                        string5 = this.fSymbolTable.addSymbol(attr.getLocalName());
                        if (string4 == XMLSymbols.PREFIX_XMLNS) {
                            string8 = this.fLocalNSBinder.getURI(string5);
                            if ((string = this.fSymbolTable.addSymbol(string)).length() == 0 || string8 != null) continue;
                            if (this.fNamespacePrefixes) {
                                this.printNamespaceAttr(string5, string);
                            }
                            this.fLocalNSBinder.declarePrefix(string5, string);
                            continue;
                        }
                        string6 = this.fNSBinder.getURI(XMLSymbols.EMPTY_STRING);
                        string8 = this.fLocalNSBinder.getURI(XMLSymbols.EMPTY_STRING);
                        string = this.fSymbolTable.addSymbol(string);
                        if (string8 != null || !this.fNamespacePrefixes) continue;
                        this.printNamespaceAttr(XMLSymbols.EMPTY_STRING, string);
                        continue;
                    }
                    string6 = this.fSymbolTable.addSymbol(string6);
                    String string9 = this.fNSBinder.getURI(string4);
                    if (string4 == XMLSymbols.EMPTY_STRING || string9 != string6) {
                        string2 = attr.getNodeName();
                        String string10 = this.fNSBinder.getPrefix(string6);
                        if (string10 != null && string10 != XMLSymbols.EMPTY_STRING) {
                            string4 = string10;
                            string2 = string4 + ":" + string5;
                        } else {
                            if (string4 == XMLSymbols.EMPTY_STRING || this.fLocalNSBinder.getURI(string4) != null) {
                                int n3 = 1;
                                string4 = this.fSymbolTable.addSymbol(PREFIX + n3++);
                                while (this.fLocalNSBinder.getURI(string4) != null) {
                                    string4 = this.fSymbolTable.addSymbol(PREFIX + n3++);
                                }
                                string2 = string4 + ":" + string5;
                            }
                            if (this.fNamespacePrefixes) {
                                this.printNamespaceAttr(string4, string6);
                            }
                            string = this.fSymbolTable.addSymbol(string);
                            this.fLocalNSBinder.declarePrefix(string4, string);
                            this.fNSBinder.declarePrefix(string4, string6);
                        }
                    }
                    this.printAttribute(string2, string == null ? XMLSymbols.EMPTY_STRING : string, attr.getSpecified(), attr);
                    continue;
                }
                if (attr.getLocalName() == null) {
                    if (this.fDOMErrorHandler != null) {
                        string5 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NullLocalAttrName", new Object[]{attr.getNodeName()});
                        this.modifyDOMError(string5, (short)2, null, attr);
                        boolean bl2 = this.fDOMErrorHandler.handleError(this.fDOMError);
                        if (!bl2) {
                            throw new RuntimeException(DOMMessageFormatter.formatMessage("http://apache.org/xml/serializer", "SerializationStopped", null));
                        }
                    }
                    this.printAttribute(string2, string, attr.getSpecified(), attr);
                    continue;
                }
                this.printAttribute(string2, string, attr.getSpecified(), attr);
            }
        }
        if (element.hasChildNodes()) {
            elementState = this.enterElementState(null, null, string3, this.fPreserveSpace);
            elementState.doCData = this._format.isCDataElement(string3);
            elementState.unescaped = this._format.isNonEscapingElement(string3);
            for (Node node = element.getFirstChild(); node != null; node = node.getNextSibling()) {
                this.serializeNode(node);
            }
            if (this.fNamespaces) {
                this.fNSBinder.popContext();
            }
            this.endElementIO(null, null, string3);
        } else {
            if (this.fNamespaces) {
                this.fNSBinder.popContext();
            }
            this._printer.unindent();
            this._printer.printText("/>");
            elementState.afterElement = true;
            elementState.afterComment = false;
            elementState.empty = false;
            if (this.isDocumentState()) {
                this._printer.flush();
            }
        }
    }

    private void printNamespaceAttr(String string, String string2) throws IOException {
        this._printer.printSpace();
        if (string == XMLSymbols.EMPTY_STRING) {
            this._printer.printText(XMLSymbols.PREFIX_XMLNS);
        } else {
            this._printer.printText("xmlns:" + string);
        }
        this._printer.printText("=\"");
        this.printEscaped(string2);
        this._printer.printText('\"');
    }

    private void printAttribute(String string, String string2, boolean bl, Attr attr) throws IOException {
        if (bl || (this.features & 0x40) == 0) {
            if (this.fDOMFilter != null && (this.fDOMFilter.getWhatToShow() & 2) != 0) {
                short s = this.fDOMFilter.acceptNode(attr);
                switch (s) {
                    case 2: 
                    case 3: {
                        return;
                    }
                }
            }
            this._printer.printSpace();
            this._printer.printText(string);
            this._printer.printText("=\"");
            this.printEscaped(string2);
            this._printer.printText('\"');
        }
        if (string.equals("xml:space")) {
            this.fPreserveSpace = string2.equals("preserve") ? true : this._format.getPreserveSpace();
        }
    }

    @Override
    protected String getEntityRef(int n) {
        switch (n) {
            case 60: {
                return "lt";
            }
            case 62: {
                return "gt";
            }
            case 34: {
                return "quot";
            }
            case 39: {
                return "apos";
            }
            case 38: {
                return "amp";
            }
        }
        return null;
    }

    private Attributes extractNamespaces(Attributes attributes) throws SAXException {
        if (attributes == null) {
            return null;
        }
        int n = attributes.getLength();
        AttributesImpl attributesImpl = new AttributesImpl(attributes);
        for (int i = n - 1; i >= 0; --i) {
            String string = attributesImpl.getQName(i);
            if (!string.startsWith("xmlns")) continue;
            if (string.length() == 5) {
                this.startPrefixMapping("", attributes.getValue(i));
                attributesImpl.removeAttribute(i);
                continue;
            }
            if (string.charAt(5) != ':') continue;
            this.startPrefixMapping(string.substring(6), attributes.getValue(i));
            attributesImpl.removeAttribute(i);
        }
        return attributesImpl;
    }

    @Override
    protected void printEscaped(String string) throws IOException {
        int n = string.length();
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (!XMLChar.isValid(c)) {
                if (++i < n) {
                    this.surrogates(c, string.charAt(i), false);
                    continue;
                }
                this.fatalError("The character '" + (char)c + "' is an invalid XML character");
                continue;
            }
            if (c == '\n' || c == '\r' || c == '\t') {
                this.printHex(c);
                continue;
            }
            if (c == '<') {
                this._printer.printText("&lt;");
                continue;
            }
            if (c == '&') {
                this._printer.printText("&amp;");
                continue;
            }
            if (c == '\"') {
                this._printer.printText("&quot;");
                continue;
            }
            if (c >= ' ' && this._encodingInfo.isPrintable(c)) {
                this._printer.printText(c);
                continue;
            }
            this.printHex(c);
        }
    }

    protected void printXMLChar(int n) throws IOException {
        if (n == 13) {
            this.printHex(n);
        } else if (n == 60) {
            this._printer.printText("&lt;");
        } else if (n == 38) {
            this._printer.printText("&amp;");
        } else if (n == 62) {
            this._printer.printText("&gt;");
        } else if (n == 10 || n == 9 || n >= 32 && this._encodingInfo.isPrintable((char)n)) {
            this._printer.printText((char)n);
        } else {
            this.printHex(n);
        }
    }

    @Override
    protected void printText(String string, boolean bl, boolean bl2) throws IOException {
        int n = string.length();
        if (bl) {
            for (int i = 0; i < n; ++i) {
                char c = string.charAt(i);
                if (!XMLChar.isValid(c)) {
                    if (++i < n) {
                        this.surrogates(c, string.charAt(i), true);
                        continue;
                    }
                    this.fatalError("The character '" + c + "' is an invalid XML character");
                    continue;
                }
                if (bl2) {
                    this._printer.printText(c);
                    continue;
                }
                this.printXMLChar(c);
            }
        } else {
            for (int i = 0; i < n; ++i) {
                char c = string.charAt(i);
                if (!XMLChar.isValid(c)) {
                    if (++i < n) {
                        this.surrogates(c, string.charAt(i), true);
                        continue;
                    }
                    this.fatalError("The character '" + c + "' is an invalid XML character");
                    continue;
                }
                if (bl2) {
                    this._printer.printText(c);
                    continue;
                }
                this.printXMLChar(c);
            }
        }
    }

    @Override
    protected void printText(char[] cArray, int n, int n2, boolean bl, boolean bl2) throws IOException {
        if (bl) {
            while (n2-- > 0) {
                char c;
                if (!XMLChar.isValid(c = cArray[n++])) {
                    if (n2-- > 0) {
                        this.surrogates(c, cArray[n++], true);
                        continue;
                    }
                    this.fatalError("The character '" + c + "' is an invalid XML character");
                    continue;
                }
                if (bl2) {
                    this._printer.printText(c);
                    continue;
                }
                this.printXMLChar(c);
            }
        } else {
            while (n2-- > 0) {
                char c;
                if (!XMLChar.isValid(c = cArray[n++])) {
                    if (n2-- > 0) {
                        this.surrogates(c, cArray[n++], true);
                        continue;
                    }
                    this.fatalError("The character '" + c + "' is an invalid XML character");
                    continue;
                }
                if (bl2) {
                    this._printer.printText(c);
                    continue;
                }
                this.printXMLChar(c);
            }
        }
    }

    @Override
    protected void checkUnboundNamespacePrefixedNode(Node node) throws IOException {
        if (this.fNamespaces) {
            Node node2 = node.getFirstChild();
            while (node2 != null) {
                Node node3 = node2.getNextSibling();
                String string = node2.getPrefix();
                String string2 = string = string == null || string.length() == 0 ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(string);
                if (this.fNSBinder.getURI(string) == null && string != null) {
                    this.fatalError("The replacement text of the entity node '" + node.getNodeName() + "' contains an element node '" + node2.getNodeName() + "' with an undeclared prefix '" + string + "'.");
                }
                if (node2.getNodeType() == 1) {
                    NamedNodeMap namedNodeMap = node2.getAttributes();
                    for (int i = 0; i < namedNodeMap.getLength(); ++i) {
                        String string3 = namedNodeMap.item(i).getPrefix();
                        String string4 = string3 = string3 == null || string3.length() == 0 ? XMLSymbols.EMPTY_STRING : this.fSymbolTable.addSymbol(string3);
                        if (this.fNSBinder.getURI(string3) != null || string3 == null) continue;
                        this.fatalError("The replacement text of the entity node '" + node.getNodeName() + "' contains an element node '" + node2.getNodeName() + "' with an attribute '" + namedNodeMap.item(i).getNodeName() + "' an undeclared prefix '" + string3 + "'.");
                    }
                }
                if (node2.hasChildNodes()) {
                    this.checkUnboundNamespacePrefixedNode(node2);
                }
                node2 = node3;
            }
        }
    }

    @Override
    public boolean reset() {
        super.reset();
        if (this.fNSBinder != null) {
            this.fNSBinder.reset();
            this.fNSBinder.declarePrefix(XMLSymbols.EMPTY_STRING, XMLSymbols.EMPTY_STRING);
        }
        return true;
    }
}

