/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.dom;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMSource;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.EmptyIterator;
import org.codehaus.stax2.ri.EmptyNamespaceContext;
import org.codehaus.stax2.ri.SingletonIterator;
import org.codehaus.stax2.ri.Stax2Util;
import org.codehaus.stax2.ri.typed.StringBase64Decoder;
import org.codehaus.stax2.ri.typed.ValueDecoderFactory;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import org.codehaus.stax2.validation.DTDValidationSchema;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public abstract class DOMWrappingReader
implements XMLStreamReader2,
AttributeInfo,
DTDInfo,
LocationInfo,
NamespaceContext,
XMLStreamConstants {
    protected static final int INT_SPACE = 32;
    private static final int MASK_GET_TEXT = 6768;
    private static final int MASK_GET_TEXT_XXX = 4208;
    private static final int MASK_GET_ELEMENT_TEXT = 4688;
    protected static final int MASK_TYPED_ACCESS_BINARY = 4178;
    protected static final int ERR_STATE_NOT_START_ELEM = 1;
    protected static final int ERR_STATE_NOT_ELEM = 2;
    protected static final int ERR_STATE_NOT_PI = 3;
    protected static final int ERR_STATE_NOT_TEXTUAL = 4;
    protected static final int ERR_STATE_NOT_TEXTUAL_XXX = 5;
    protected static final int ERR_STATE_NOT_TEXTUAL_OR_ELEM = 6;
    protected static final int ERR_STATE_NO_LOCALNAME = 7;
    protected final String _systemId;
    protected final Node _rootNode;
    protected final boolean _cfgNsAware;
    protected final boolean _coalescing;
    protected boolean _cfgInternNames = false;
    protected boolean _cfgInternNsURIs = false;
    protected int _currEvent = 7;
    protected Node _currNode;
    protected int _depth = 0;
    protected String _coalescedText;
    protected Stax2Util.TextBuffer _textBuffer = new Stax2Util.TextBuffer();
    protected List<Node> _attrList = null;
    protected List<String> _nsDeclList = null;
    protected ValueDecoderFactory _decoderFactory;
    protected StringBase64Decoder _base64Decoder = null;

    protected DOMWrappingReader(DOMSource src, boolean nsAware, boolean coalescing) throws XMLStreamException {
        Node treeRoot = src.getNode();
        if (treeRoot == null) {
            throw new IllegalArgumentException("Can not pass null Node for constructing a DOM-based XMLStreamReader");
        }
        this._cfgNsAware = nsAware;
        this._coalescing = coalescing;
        this._systemId = src.getSystemId();
        switch (treeRoot.getNodeType()) {
            case 1: 
            case 9: 
            case 11: {
                break;
            }
            default: {
                throw new XMLStreamException("Can not create an XMLStreamReader for a DOM node of type " + treeRoot.getClass());
            }
        }
        this._rootNode = this._currNode = treeRoot;
    }

    protected void setInternNames(boolean state) {
        this._cfgInternNames = state;
    }

    protected void setInternNsURIs(boolean state) {
        this._cfgInternNsURIs = state;
    }

    protected abstract void throwStreamException(String var1, Location var2) throws XMLStreamException;

    @Override
    public String getCharacterEncodingScheme() {
        return null;
    }

    @Override
    public String getEncoding() {
        return this.getCharacterEncodingScheme();
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public boolean isStandalone() {
        return false;
    }

    @Override
    public boolean standaloneSet() {
        return false;
    }

    @Override
    public abstract Object getProperty(String var1);

    @Override
    public abstract boolean isPropertySupported(String var1);

    @Override
    public abstract boolean setProperty(String var1, Object var2);

    @Override
    public int getAttributeCount() {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        return this._attrList.size();
    }

    @Override
    public String getAttributeLocalName(int index) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (index >= this._attrList.size() || index < 0) {
            this.handleIllegalAttrIndex(index);
            return null;
        }
        Attr attr = (Attr)this._attrList.get(index);
        return this._internName(this._safeGetLocalName(attr));
    }

    @Override
    public QName getAttributeName(int index) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (index >= this._attrList.size() || index < 0) {
            this.handleIllegalAttrIndex(index);
            return null;
        }
        Attr attr = (Attr)this._attrList.get(index);
        return this._constructQName(attr.getNamespaceURI(), this._safeGetLocalName(attr), attr.getPrefix());
    }

    @Override
    public String getAttributeNamespace(int index) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (index >= this._attrList.size() || index < 0) {
            this.handleIllegalAttrIndex(index);
            return null;
        }
        Attr attr = (Attr)this._attrList.get(index);
        return this._internNsURI(attr.getNamespaceURI());
    }

    @Override
    public String getAttributePrefix(int index) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (index >= this._attrList.size() || index < 0) {
            this.handleIllegalAttrIndex(index);
            return null;
        }
        Attr attr = (Attr)this._attrList.get(index);
        return this._internName(attr.getPrefix());
    }

    @Override
    public String getAttributeType(int index) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (index >= this._attrList.size() || index < 0) {
            this.handleIllegalAttrIndex(index);
            return null;
        }
        return "CDATA";
    }

    @Override
    public String getAttributeValue(int index) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (index >= this._attrList.size() || index < 0) {
            this.handleIllegalAttrIndex(index);
            return null;
        }
        Attr attr = (Attr)this._attrList.get(index);
        return attr.getValue();
    }

    @Override
    public String getAttributeValue(String nsURI, String localName) {
        Attr attr;
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        Element elem = (Element)this._currNode;
        NamedNodeMap attrs = elem.getAttributes();
        if (nsURI != null && nsURI.length() == 0) {
            nsURI = null;
        }
        return (attr = (Attr)attrs.getNamedItemNS(nsURI, localName)) == null ? null : attr.getValue();
    }

    @Override
    public String getElementText() throws XMLStreamException {
        int type;
        if (this._currEvent != 1) {
            this.reportParseProblem(1);
        }
        if (this._coalescing) {
            int type2;
            String text = null;
            while ((type2 = this.next()) != 2) {
                if (type2 == 5 || type2 == 3) continue;
                if ((1 << type2 & 0x1250) == 0) {
                    this.reportParseProblem(4);
                }
                if (text == null) {
                    text = this.getText();
                    continue;
                }
                text = text + this.getText();
            }
            return text == null ? "" : text;
        }
        this._textBuffer.reset();
        while ((type = this.next()) != 2) {
            if (type == 5 || type == 3) continue;
            if ((1 << type & 0x1250) == 0) {
                this.reportParseProblem(4);
            }
            this._textBuffer.append(this.getText());
        }
        return this._textBuffer.get();
    }

    @Override
    public int getEventType() {
        return this._currEvent;
    }

    @Override
    public String getLocalName() {
        if (this._currEvent == 1 || this._currEvent == 2) {
            return this._internName(this._safeGetLocalName(this._currNode));
        }
        if (this._currEvent != 9) {
            this.reportWrongState(7);
        }
        return this._internName(this._currNode.getNodeName());
    }

    @Override
    public final Location getLocation() {
        return this.getStartLocation();
    }

    @Override
    public QName getName() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(1);
        }
        return this._constructQName(this._currNode.getNamespaceURI(), this._safeGetLocalName(this._currNode), this._currNode.getPrefix());
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this;
    }

    @Override
    public int getNamespaceCount() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        if (this._nsDeclList == null) {
            if (!this._cfgNsAware) {
                return 0;
            }
            this._calcNsAndAttrLists(this._currEvent == 1);
        }
        return this._nsDeclList.size() / 2;
    }

    @Override
    public String getNamespacePrefix(int index) {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        if (this._nsDeclList == null) {
            if (!this._cfgNsAware) {
                this.handleIllegalNsIndex(index);
            }
            this._calcNsAndAttrLists(this._currEvent == 1);
        }
        if (index < 0 || index + index >= this._nsDeclList.size()) {
            this.handleIllegalNsIndex(index);
        }
        return this._nsDeclList.get(index + index);
    }

    @Override
    public String getNamespaceURI() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        return this._internNsURI(this._currNode.getNamespaceURI());
    }

    @Override
    public String getNamespaceURI(int index) {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        if (this._nsDeclList == null) {
            if (!this._cfgNsAware) {
                this.handleIllegalNsIndex(index);
            }
            this._calcNsAndAttrLists(this._currEvent == 1);
        }
        if (index < 0 || index + index >= this._nsDeclList.size()) {
            this.handleIllegalNsIndex(index);
        }
        return this._nsDeclList.get(index + index + 1);
    }

    @Override
    public String getPIData() {
        if (this._currEvent != 3) {
            this.reportWrongState(3);
        }
        return this._currNode.getNodeValue();
    }

    @Override
    public String getPITarget() {
        if (this._currEvent != 3) {
            this.reportWrongState(3);
        }
        return this._internName(this._currNode.getNodeName());
    }

    @Override
    public String getPrefix() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        return this._internName(this._currNode.getPrefix());
    }

    @Override
    public String getText() {
        if (this._coalescedText != null) {
            return this._coalescedText;
        }
        if ((1 << this._currEvent & 0x1A70) == 0) {
            this.reportWrongState(4);
        }
        return this._currNode.getNodeValue();
    }

    @Override
    public char[] getTextCharacters() {
        String text = this.getText();
        return text.toCharArray();
    }

    @Override
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int len) {
        String text;
        if ((1 << this._currEvent & 0x1070) == 0) {
            this.reportWrongState(5);
        }
        if (len > (text = this.getText()).length()) {
            len = text.length();
        }
        text.getChars(sourceStart, sourceStart + len, target, targetStart);
        return len;
    }

    @Override
    public int getTextLength() {
        if ((1 << this._currEvent & 0x1070) == 0) {
            this.reportWrongState(5);
        }
        return this.getText().length();
    }

    @Override
    public int getTextStart() {
        if ((1 << this._currEvent & 0x1070) == 0) {
            this.reportWrongState(5);
        }
        return 0;
    }

    @Override
    public boolean hasName() {
        return this._currEvent == 1 || this._currEvent == 2;
    }

    @Override
    public boolean hasNext() {
        return this._currEvent != 8;
    }

    @Override
    public boolean hasText() {
        return (1 << this._currEvent & 0x1A70) != 0;
    }

    @Override
    public boolean isAttributeSpecified(int index) {
        Element elem;
        Attr attr;
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if ((attr = (Attr)(elem = (Element)this._currNode).getAttributes().item(index)) == null) {
            this.handleIllegalAttrIndex(index);
            return false;
        }
        return attr.getSpecified();
    }

    @Override
    public boolean isCharacters() {
        return this._currEvent == 4;
    }

    @Override
    public boolean isEndElement() {
        return this._currEvent == 2;
    }

    @Override
    public boolean isStartElement() {
        return this._currEvent == 1;
    }

    @Override
    public boolean isWhiteSpace() {
        if (this._currEvent == 4 || this._currEvent == 12) {
            String text = this.getText();
            int len = text.length();
            for (int i = 0; i < len; ++i) {
                if (text.charAt(i) <= ' ') continue;
                return false;
            }
            return true;
        }
        return this._currEvent == 6;
    }

    @Override
    public void require(int type, String nsUri, String localName) throws XMLStreamException {
        int curr = this._currEvent;
        if (curr != type) {
            if (curr == 12) {
                curr = 4;
            } else if (curr == 6) {
                curr = 4;
            }
        }
        if (type != curr) {
            this.throwStreamException("Required type " + Stax2Util.eventTypeDesc(type) + ", current type " + Stax2Util.eventTypeDesc(curr));
        }
        if (localName != null) {
            String n;
            if (curr != 1 && curr != 2 && curr != 9) {
                this.throwStreamException("Required a non-null local name, but current token not a START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE (was " + Stax2Util.eventTypeDesc(this._currEvent) + ")");
            }
            if ((n = this.getLocalName()) != localName && !n.equals(localName)) {
                this.throwStreamException("Required local name '" + localName + "'; current local name '" + n + "'.");
            }
        }
        if (nsUri != null) {
            if (curr != 1 && curr != 2) {
                this.throwStreamException("Required non-null NS URI, but current token not a START_ELEMENT or END_ELEMENT (was " + Stax2Util.eventTypeDesc(curr) + ")");
            }
            String uri = this.getNamespaceURI();
            if (nsUri.length() == 0) {
                if (uri != null && uri.length() > 0) {
                    this.throwStreamException("Required empty namespace, instead have '" + uri + "'.");
                }
            } else if (nsUri != uri && !nsUri.equals(uri)) {
                this.throwStreamException("Required namespace '" + nsUri + "'; have '" + uri + "'.");
            }
        }
    }

    @Override
    public int next() throws XMLStreamException {
        this._coalescedText = null;
        block0 : switch (this._currEvent) {
            case 7: {
                switch (this._currNode.getNodeType()) {
                    case 9: 
                    case 11: {
                        this._currNode = this._currNode.getFirstChild();
                        if (this._currNode != null) break block0;
                        this._currEvent = 8;
                        return 8;
                    }
                    case 1: {
                        this._currEvent = 1;
                        return 1;
                    }
                    default: {
                        throw new XMLStreamException("Internal error: unexpected DOM root node type " + this._currNode.getNodeType() + " for node '" + this._currNode + "'");
                    }
                }
            }
            case 8: {
                throw new NoSuchElementException("Can not call next() after receiving END_DOCUMENT");
            }
            case 1: {
                ++this._depth;
                this._attrList = null;
                Node firstChild = this._currNode.getFirstChild();
                if (firstChild == null) {
                    this._currEvent = 2;
                    return 2;
                }
                this._nsDeclList = null;
                this._currNode = firstChild;
                break;
            }
            case 2: {
                --this._depth;
                this._attrList = null;
                this._nsDeclList = null;
                if (this._currNode == this._rootNode) {
                    this._currEvent = 8;
                    return 8;
                }
            }
            default: {
                Node next = this._currNode.getNextSibling();
                if (next != null) {
                    this._currNode = next;
                    break;
                }
                this._currNode = this._currNode.getParentNode();
                short type = this._currNode.getNodeType();
                if (type == 1) {
                    this._currEvent = 2;
                    return 2;
                }
                if (this._currNode != this._rootNode || type != 9 && type != 11) {
                    throw new XMLStreamException("Internal error: non-element parent node (" + type + ") that is not the initial root node");
                }
                this._currEvent = 8;
                return 8;
            }
        }
        switch (this._currNode.getNodeType()) {
            case 4: {
                if (this._coalescing) {
                    this.coalesceText(12);
                    break;
                }
                this._currEvent = 12;
                break;
            }
            case 8: {
                this._currEvent = 5;
                break;
            }
            case 10: {
                this._currEvent = 11;
                break;
            }
            case 1: {
                this._currEvent = 1;
                break;
            }
            case 5: {
                this._currEvent = 9;
                break;
            }
            case 7: {
                this._currEvent = 3;
                break;
            }
            case 3: {
                if (this._coalescing) {
                    this.coalesceText(4);
                    break;
                }
                this._currEvent = 4;
                break;
            }
            case 2: 
            case 6: 
            case 12: {
                throw new XMLStreamException("Internal error: unexpected DOM node type " + this._currNode.getNodeType() + " (attr/entity/notation?), for node '" + this._currNode + "'");
            }
            default: {
                throw new XMLStreamException("Internal error: unrecognized DOM node type " + this._currNode.getNodeType() + ", for node '" + this._currNode + "'");
            }
        }
        return this._currEvent;
    }

    @Override
    public int nextTag() throws XMLStreamException {
        block5: while (true) {
            int next = this.next();
            switch (next) {
                case 3: 
                case 5: 
                case 6: {
                    continue block5;
                }
                case 4: 
                case 12: {
                    if (this.isWhiteSpace()) continue block5;
                    this.throwStreamException("Received non-all-whitespace CHARACTERS or CDATA event in nextTag().");
                    break;
                }
                case 1: 
                case 2: {
                    return next;
                }
            }
            this.throwStreamException("Received event " + Stax2Util.eventTypeDesc(next) + ", instead of START_ELEMENT or END_ELEMENT.");
        }
    }

    @Override
    public void close() throws XMLStreamException {
    }

    @Override
    public String getNamespaceURI(String prefix) {
        boolean defaultNs;
        boolean bl = defaultNs = prefix == null || prefix.length() == 0;
        for (Node n = this._currNode; n != null; n = n.getParentNode()) {
            NamedNodeMap attrs = n.getAttributes();
            if (attrs == null) continue;
            int len = attrs.getLength();
            for (int i = 0; i < len; ++i) {
                Node attr = attrs.item(i);
                String thisPrefix = attr.getPrefix();
                if (!(thisPrefix == null || thisPrefix.length() == 0 ? defaultNs && "xmlns".equals(attr.getLocalName()) : !defaultNs && "xmlns".equals(thisPrefix) && prefix.equals(attr.getLocalName()))) continue;
                return attr.getNodeValue();
            }
        }
        return null;
    }

    @Override
    public String getPrefix(String namespaceURI) {
        Node n = this._currNode;
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        while (n != null) {
            NamedNodeMap attrs = n.getAttributes();
            int len = attrs.getLength();
            for (int i = 0; i < len; ++i) {
                Node attr = attrs.item(i);
                String thisPrefix = attr.getPrefix();
                if (thisPrefix == null || thisPrefix.length() == 0) {
                    if (!"xmlns".equals(attr.getLocalName()) || !namespaceURI.equals(attr.getNodeValue())) continue;
                    return "";
                }
                if (!"xmlns".equals(thisPrefix) || !namespaceURI.equals(attr.getNodeValue())) continue;
                return attr.getLocalName();
            }
            n = n.getParentNode();
        }
        return null;
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceURI) {
        String prefix = this.getPrefix(namespaceURI);
        if (prefix == null) {
            return EmptyIterator.getInstance();
        }
        return SingletonIterator.create(prefix);
    }

    @Override
    public boolean getElementAsBoolean() throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder dec = this._decoderFactory().getBooleanDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public int getElementAsInt() throws XMLStreamException {
        ValueDecoderFactory.IntDecoder dec = this._decoderFactory().getIntDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public long getElementAsLong() throws XMLStreamException {
        ValueDecoderFactory.LongDecoder dec = this._decoderFactory().getLongDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public float getElementAsFloat() throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder dec = this._decoderFactory().getFloatDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public double getElementAsDouble() throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder dec = this._decoderFactory().getDoubleDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public BigInteger getElementAsInteger() throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder dec = this._decoderFactory().getIntegerDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder dec = this._decoderFactory().getDecimalDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public QName getElementAsQName() throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder dec = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public byte[] getElementAsBinary() throws XMLStreamException {
        return this.getElementAsBinary(Base64Variants.getDefaultVariant());
    }

    @Override
    public byte[] getElementAsBinary(Base64Variant v) throws XMLStreamException {
        Stax2Util.ByteAggregator aggr = this._base64Decoder().getByteAggregator();
        byte[] buffer = aggr.startAggregation();
        while (true) {
            int readCount;
            int offset = 0;
            int len = buffer.length;
            do {
                if ((readCount = this.readElementAsBinary(buffer, offset, len, v)) < 1) {
                    return aggr.aggregateAll(buffer, offset);
                }
                offset += readCount;
            } while ((len -= readCount) > 0);
            buffer = aggr.addFullBlock(buffer);
        }
    }

    @Override
    public void getElementAs(TypedValueDecoder tvd) throws XMLStreamException {
        String value = this.getElementText();
        value = Stax2Util.trimSpaces(value);
        try {
            if (value == null) {
                tvd.handleEmptyValue();
            } else {
                tvd.decode(value);
            }
        }
        catch (IllegalArgumentException iae) {
            throw this._constructTypeException(iae, value);
        }
    }

    @Override
    public int readElementAsIntArray(int[] value, int from, int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getIntArrayDecoder(value, from, length));
    }

    @Override
    public int readElementAsLongArray(long[] value, int from, int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getLongArrayDecoder(value, from, length));
    }

    @Override
    public int readElementAsFloatArray(float[] value, int from, int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getFloatArrayDecoder(value, from, length));
    }

    @Override
    public int readElementAsDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getDoubleArrayDecoder(value, from, length));
    }

    @Override
    public int readElementAsArray(TypedArrayDecoder tad) throws XMLStreamException {
        String string;
        int ptr;
        if (this._currEvent == 1) {
            Node fc = this._currNode.getFirstChild();
            if (fc == null) {
                this._currEvent = 2;
                return -1;
            }
            this._coalescedText = this.coalesceTypedText(fc);
            this._currEvent = 4;
            this._currNode = this._currNode.getLastChild();
        } else {
            if (this._currEvent != 4 && this._currEvent != 12) {
                if (this._currEvent == 2) {
                    return -1;
                }
                this.reportWrongState(6);
            }
            if (this._coalescedText == null) {
                throw new IllegalStateException("First call to readElementAsArray() must be for a START_ELEMENT, not directly for a textual event");
            }
        }
        String input = this._coalescedText;
        int end = input.length();
        int count = 0;
        String value = null;
        try {
            int len;
            block4: for (ptr = 0; ptr < end; ++ptr) {
                while (input.charAt(ptr) <= ' ') {
                    if (++ptr < end) continue;
                    break block4;
                }
                int start = ptr++;
                while (ptr < end && input.charAt(ptr) > ' ') {
                    ++ptr;
                }
                ++count;
                value = input.substring(start, ptr);
                if (!tad.decodeValue(value)) continue;
                break;
            }
            string = (len = end - ptr) < 1 ? "" : input.substring(ptr);
        }
        catch (IllegalArgumentException iae) {
            try {
                Location loc = this.getLocation();
                throw new TypedXMLStreamException(value, iae.getMessage(), loc, iae);
            }
            catch (Throwable throwable) {
                int len = end - ptr;
                this._coalescedText = len < 1 ? "" : input.substring(ptr);
                throw throwable;
            }
        }
        this._coalescedText = string;
        if (count < 1) {
            this._currEvent = 2;
            this._currNode = this._currNode.getParentNode();
            return -1;
        }
        return count;
    }

    private String coalesceTypedText(Node firstNode) throws XMLStreamException {
        this._textBuffer.reset();
        this._attrList = null;
        block5: for (Node n = firstNode; n != null; n = n.getNextSibling()) {
            switch (n.getNodeType()) {
                case 1: {
                    this.throwStreamException("Element content can not contain child START_ELEMENT when using Typed Access methods");
                }
                case 3: 
                case 4: {
                    this._textBuffer.append(n.getNodeValue());
                    continue block5;
                }
                case 7: 
                case 8: {
                    continue block5;
                }
                default: {
                    this.throwStreamException("Unexpected DOM node type (" + n.getNodeType() + ") when trying to decode Typed content");
                }
            }
        }
        return this._textBuffer.get();
    }

    @Override
    public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength) throws XMLStreamException {
        return this.readElementAsBinary(resultBuffer, offset, maxLength, Base64Variants.getDefaultVariant());
    }

    @Override
    public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength, Base64Variant v) throws XMLStreamException {
        if (resultBuffer == null) {
            throw new IllegalArgumentException("resultBuffer is null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Illegal offset (" + offset + "), must be [0, " + resultBuffer.length + "[");
        }
        if (maxLength < 1 || offset + maxLength > resultBuffer.length) {
            if (maxLength == 0) {
                return 0;
            }
            throw new IllegalArgumentException("Illegal maxLength (" + maxLength + "), has to be positive number, and offset+maxLength can not exceed" + resultBuffer.length);
        }
        StringBase64Decoder dec = this._base64Decoder();
        int type = this._currEvent;
        if ((1 << type & 0x1052) == 0) {
            if (type == 2) {
                if (!dec.hasData()) {
                    return -1;
                }
            } else {
                this.reportWrongState(6);
            }
        }
        if (type == 1) {
            do {
                if ((type = this.next()) != 2) continue;
                return -1;
            } while (type == 5 || type == 3);
            if ((1 << type & 0x1250) == 0) {
                this.reportParseProblem(4);
            }
            dec.init(v, true, this.getText());
        }
        int totalCount = 0;
        while (true) {
            int count;
            try {
                count = dec.decode(resultBuffer, offset, maxLength);
            }
            catch (IllegalArgumentException iae) {
                throw this._constructTypeException(iae, "");
            }
            offset += count;
            totalCount += count;
            if ((maxLength -= count) < 1 || this._currEvent == 2) break;
            while ((type = this.next()) == 5 || type == 3 || type == 6) {
            }
            if (type == 2) {
                int left = dec.endOfContent();
                if (left < 0) {
                    throw this._constructTypeException("Incomplete base64 triplet at the end of decoded content", "");
                }
                if (left <= 0) break;
                continue;
            }
            if ((1 << type & 0x1250) == 0) {
                this.reportParseProblem(4);
            }
            dec.init(v, false, this.getText());
        }
        return totalCount > 0 ? totalCount : -1;
    }

    @Override
    public int getAttributeIndex(String namespaceURI, String localName) {
        return this.findAttributeIndex(namespaceURI, localName);
    }

    @Override
    public boolean getAttributeAsBoolean(int index) throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder dec = this._decoderFactory().getBooleanDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public int getAttributeAsInt(int index) throws XMLStreamException {
        ValueDecoderFactory.IntDecoder dec = this._decoderFactory().getIntDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public long getAttributeAsLong(int index) throws XMLStreamException {
        ValueDecoderFactory.LongDecoder dec = this._decoderFactory().getLongDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public float getAttributeAsFloat(int index) throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder dec = this._decoderFactory().getFloatDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public double getAttributeAsDouble(int index) throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder dec = this._decoderFactory().getDoubleDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public BigInteger getAttributeAsInteger(int index) throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder dec = this._decoderFactory().getIntegerDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public BigDecimal getAttributeAsDecimal(int index) throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder dec = this._decoderFactory().getDecimalDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public QName getAttributeAsQName(int index) throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder dec = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public final void getAttributeAs(int index, TypedValueDecoder tvd) throws XMLStreamException {
        String value = this.getAttributeValue(index);
        value = Stax2Util.trimSpaces(value);
        try {
            if (value == null) {
                tvd.handleEmptyValue();
            } else {
                tvd.decode(value);
            }
        }
        catch (IllegalArgumentException iae) {
            throw this._constructTypeException(iae, value);
        }
    }

    @Override
    public int[] getAttributeAsIntArray(int index) throws XMLStreamException {
        ValueDecoderFactory.IntArrayDecoder dec = this._decoderFactory().getIntArrayDecoder();
        this._getAttributeAsArray(dec, this.getAttributeValue(index));
        return dec.getValues();
    }

    @Override
    public long[] getAttributeAsLongArray(int index) throws XMLStreamException {
        ValueDecoderFactory.LongArrayDecoder dec = this._decoderFactory().getLongArrayDecoder();
        this._getAttributeAsArray(dec, this.getAttributeValue(index));
        return dec.getValues();
    }

    @Override
    public float[] getAttributeAsFloatArray(int index) throws XMLStreamException {
        ValueDecoderFactory.FloatArrayDecoder dec = this._decoderFactory().getFloatArrayDecoder();
        this._getAttributeAsArray(dec, this.getAttributeValue(index));
        return dec.getValues();
    }

    @Override
    public double[] getAttributeAsDoubleArray(int index) throws XMLStreamException {
        ValueDecoderFactory.DoubleArrayDecoder dec = this._decoderFactory().getDoubleArrayDecoder();
        this._getAttributeAsArray(dec, this.getAttributeValue(index));
        return dec.getValues();
    }

    @Override
    public int getAttributeAsArray(int index, TypedArrayDecoder tad) throws XMLStreamException {
        return this._getAttributeAsArray(tad, this.getAttributeValue(index));
    }

    protected int _getAttributeAsArray(TypedArrayDecoder tad, String attrValue) throws XMLStreamException {
        int count;
        block5: {
            int ptr = 0;
            int start = 0;
            int end = attrValue.length();
            String lexical = null;
            count = 0;
            try {
                while (ptr < end) {
                    while (attrValue.charAt(ptr) <= ' ') {
                        if (++ptr < end) continue;
                        break block5;
                    }
                    start = ptr++;
                    while (ptr < end && attrValue.charAt(ptr) > ' ') {
                        ++ptr;
                    }
                    int tokenEnd = ptr++;
                    lexical = attrValue.substring(start, tokenEnd);
                    ++count;
                    if (!tad.decodeValue(lexical) || this.checkExpand(tad)) continue;
                    break;
                }
            }
            catch (IllegalArgumentException iae) {
                Location loc = this.getLocation();
                throw new TypedXMLStreamException(lexical, iae.getMessage(), loc, iae);
            }
        }
        return count;
    }

    private final boolean checkExpand(TypedArrayDecoder tad) {
        if (tad instanceof ValueDecoderFactory.BaseArrayDecoder) {
            ((ValueDecoderFactory.BaseArrayDecoder)tad).expand();
            return true;
        }
        return false;
    }

    @Override
    public byte[] getAttributeAsBinary(int index) throws XMLStreamException {
        return this.getAttributeAsBinary(index, Base64Variants.getDefaultVariant());
    }

    @Override
    public byte[] getAttributeAsBinary(int index, Base64Variant v) throws XMLStreamException {
        String lexical = this.getAttributeValue(index);
        StringBase64Decoder dec = this._base64Decoder();
        dec.init(v, true, lexical);
        try {
            return dec.decodeCompletely();
        }
        catch (IllegalArgumentException iae) {
            throw this._constructTypeException(iae, lexical);
        }
    }

    @Override
    @Deprecated
    public Object getFeature(String name) {
        throw new IllegalArgumentException("Unrecognized feature \"" + name + "\"");
    }

    @Override
    @Deprecated
    public void setFeature(String name, Object value) {
        throw new IllegalArgumentException("Unrecognized feature \"" + name + "\"");
    }

    @Override
    public void skipElement() throws XMLStreamException {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        int nesting = 1;
        while (true) {
            int type;
            if ((type = this.next()) == 1) {
                ++nesting;
                continue;
            }
            if (type == 2 && --nesting == 0) break;
        }
    }

    @Override
    public AttributeInfo getAttributeInfo() throws XMLStreamException {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        return this;
    }

    @Override
    public int findAttributeIndex(String nsURI, String localName) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        Element elem = (Element)this._currNode;
        NamedNodeMap attrs = elem.getAttributes();
        if (nsURI != null && nsURI.length() == 0) {
            nsURI = null;
        }
        int len = attrs.getLength();
        for (int i = 0; i < len; ++i) {
            boolean isEmpty;
            Node attr = attrs.item(i);
            String ln = this._safeGetLocalName(attr);
            if (!localName.equals(ln)) continue;
            String thisUri = attr.getNamespaceURI();
            boolean bl = isEmpty = thisUri == null || thisUri.length() == 0;
            if (!(nsURI == null ? isEmpty : !isEmpty && nsURI.equals(thisUri))) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int getIdAttributeIndex() {
        return -1;
    }

    @Override
    public int getNotationAttributeIndex() {
        return -1;
    }

    @Override
    public DTDInfo getDTDInfo() throws XMLStreamException {
        if (this._currEvent != 11) {
            return null;
        }
        return this;
    }

    @Override
    public final LocationInfo getLocationInfo() {
        return this;
    }

    @Override
    public int getText(Writer w, boolean preserveContents) throws IOException, XMLStreamException {
        String text = this.getText();
        w.write(text);
        return text.length();
    }

    @Override
    public int getDepth() {
        return this._depth;
    }

    @Override
    public boolean isEmptyElement() throws XMLStreamException {
        return false;
    }

    @Override
    public NamespaceContext getNonTransientNamespaceContext() {
        return EmptyNamespaceContext.getInstance();
    }

    @Override
    public String getPrefixedName() {
        switch (this._currEvent) {
            case 1: 
            case 2: {
                String prefix = this._currNode.getPrefix();
                String ln = this._safeGetLocalName(this._currNode);
                if (prefix == null) {
                    return this._internName(ln);
                }
                StringBuffer sb = new StringBuffer(ln.length() + 1 + prefix.length());
                sb.append(prefix);
                sb.append(':');
                sb.append(ln);
                return this._internName(sb.toString());
            }
            case 9: {
                return this.getLocalName();
            }
            case 3: {
                return this.getPITarget();
            }
            case 11: {
                return this.getDTDRootName();
            }
        }
        throw new IllegalStateException("Current state (" + Stax2Util.eventTypeDesc(this._currEvent) + ") not START_ELEMENT, END_ELEMENT, ENTITY_REFERENCE, PROCESSING_INSTRUCTION or DTD");
    }

    @Override
    public void closeCompletely() throws XMLStreamException {
    }

    @Override
    public Object getProcessedDTD() {
        return null;
    }

    @Override
    public String getDTDRootName() {
        if (this._currEvent == 11) {
            return this._internName(((DocumentType)this._currNode).getName());
        }
        return null;
    }

    @Override
    public String getDTDPublicId() {
        if (this._currEvent == 11) {
            return ((DocumentType)this._currNode).getPublicId();
        }
        return null;
    }

    @Override
    public String getDTDSystemId() {
        if (this._currEvent == 11) {
            return ((DocumentType)this._currNode).getSystemId();
        }
        return null;
    }

    @Override
    public String getDTDInternalSubset() {
        return null;
    }

    @Override
    public DTDValidationSchema getProcessedDTDSchema() {
        return null;
    }

    @Override
    public long getStartingByteOffset() {
        return -1L;
    }

    @Override
    public long getStartingCharOffset() {
        return 0L;
    }

    @Override
    public long getEndingByteOffset() throws XMLStreamException {
        return -1L;
    }

    @Override
    public long getEndingCharOffset() throws XMLStreamException {
        return -1L;
    }

    @Override
    public XMLStreamLocation2 getStartLocation() {
        return XMLStreamLocation2.NOT_AVAILABLE;
    }

    @Override
    public XMLStreamLocation2 getCurrentLocation() {
        return XMLStreamLocation2.NOT_AVAILABLE;
    }

    @Override
    public final XMLStreamLocation2 getEndLocation() throws XMLStreamException {
        return XMLStreamLocation2.NOT_AVAILABLE;
    }

    @Override
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return null;
    }

    @Override
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        return null;
    }

    @Override
    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        return null;
    }

    protected void coalesceText(int initialType) {
        short type;
        Node n;
        this._textBuffer.reset();
        this._textBuffer.append(this._currNode.getNodeValue());
        while ((n = this._currNode.getNextSibling()) != null && ((type = n.getNodeType()) == 3 || type == 4)) {
            this._currNode = n;
            this._textBuffer.append(this._currNode.getNodeValue());
        }
        this._coalescedText = this._textBuffer.get();
        this._currEvent = 4;
    }

    private QName _constructQName(String uri, String ln, String prefix) {
        return new QName(this._internNsURI(uri), this._internName(ln), this._internName(prefix));
    }

    private void _calcNsAndAttrLists(boolean attrsToo) {
        NamedNodeMap attrsIn = this._currNode.getAttributes();
        int len = attrsIn.getLength();
        if (len == 0) {
            this._attrList = Collections.emptyList();
            this._nsDeclList = Collections.emptyList();
            return;
        }
        if (!this._cfgNsAware) {
            this._attrList = new ArrayList<Node>(len);
            for (int i = 0; i < len; ++i) {
                this._attrList.add(attrsIn.item(i));
            }
            this._nsDeclList = Collections.emptyList();
            return;
        }
        ArrayList<Node> attrsOut = null;
        ArrayList<String> nsOut = null;
        for (int i = 0; i < len; ++i) {
            Node attr = attrsIn.item(i);
            String prefix = attr.getPrefix();
            if (prefix == null || prefix.length() == 0) {
                if (!"xmlns".equals(attr.getLocalName())) {
                    if (!attrsToo) continue;
                    if (attrsOut == null) {
                        attrsOut = new ArrayList<Node>(len - i);
                    }
                    attrsOut.add(attr);
                    continue;
                }
                prefix = null;
            } else {
                if (!"xmlns".equals(prefix)) {
                    if (!attrsToo) continue;
                    if (attrsOut == null) {
                        attrsOut = new ArrayList(len - i);
                    }
                    attrsOut.add(attr);
                    continue;
                }
                prefix = attr.getLocalName();
            }
            if (nsOut == null) {
                nsOut = new ArrayList<String>((len - i) * 2);
            }
            nsOut.add(this._internName(prefix));
            nsOut.add(this._internNsURI(attr.getNodeValue()));
        }
        this._attrList = attrsOut == null ? Collections.emptyList() : attrsOut;
        this._nsDeclList = nsOut == null ? Collections.emptyList() : nsOut;
    }

    private void handleIllegalAttrIndex(int index) {
        Element elem = (Element)this._currNode;
        NamedNodeMap attrs = elem.getAttributes();
        int len = attrs.getLength();
        String msg = "Illegal attribute index " + index + "; element <" + elem.getNodeName() + "> has " + (len == 0 ? "no" : String.valueOf(len)) + " attributes";
        throw new IllegalArgumentException(msg);
    }

    private void handleIllegalNsIndex(int index) {
        String msg = "Illegal namespace declaration index " + index + " (has " + this.getNamespaceCount() + " ns declarations)";
        throw new IllegalArgumentException(msg);
    }

    private String _safeGetLocalName(Node n) {
        String ln = n.getLocalName();
        if (ln == null) {
            ln = n.getNodeName();
        }
        return ln;
    }

    protected void reportWrongState(int errorType) {
        throw new IllegalStateException(this.findErrorDesc(errorType, this._currEvent));
    }

    protected void reportParseProblem(int errorType) throws XMLStreamException {
        this.throwStreamException(this.findErrorDesc(errorType, this._currEvent));
    }

    protected void throwStreamException(String msg) throws XMLStreamException {
        this.throwStreamException(msg, this.getErrorLocation());
    }

    protected Location getErrorLocation() {
        Location loc = this.getCurrentLocation();
        if (loc == null) {
            loc = this.getLocation();
        }
        return loc;
    }

    protected TypedXMLStreamException _constructTypeException(IllegalArgumentException iae, String lexicalValue) {
        XMLStreamLocation2 loc;
        String msg = iae.getMessage();
        if (msg == null) {
            msg = "";
        }
        if ((loc = this.getStartLocation()) == null) {
            return new TypedXMLStreamException(lexicalValue, msg, iae);
        }
        return new TypedXMLStreamException(lexicalValue, msg, loc);
    }

    protected TypedXMLStreamException _constructTypeException(String msg, String lexicalValue) {
        XMLStreamLocation2 loc = this.getStartLocation();
        if (loc == null) {
            return new TypedXMLStreamException(lexicalValue, msg);
        }
        return new TypedXMLStreamException(lexicalValue, msg, loc);
    }

    protected ValueDecoderFactory _decoderFactory() {
        if (this._decoderFactory == null) {
            this._decoderFactory = new ValueDecoderFactory();
        }
        return this._decoderFactory;
    }

    protected StringBase64Decoder _base64Decoder() {
        if (this._base64Decoder == null) {
            this._base64Decoder = new StringBase64Decoder();
        }
        return this._base64Decoder;
    }

    protected String findErrorDesc(int errorType, int currEvent) {
        String evtDesc = Stax2Util.eventTypeDesc(currEvent);
        switch (errorType) {
            case 1: {
                return "Current event " + evtDesc + ", needs to be START_ELEMENT";
            }
            case 2: {
                return "Current event " + evtDesc + ", needs to be START_ELEMENT or END_ELEMENT";
            }
            case 7: {
                return "Current event (" + evtDesc + ") has no local name";
            }
            case 3: {
                return "Current event (" + evtDesc + ") needs to be PROCESSING_INSTRUCTION";
            }
            case 4: {
                return "Current event (" + evtDesc + ") not a textual event";
            }
            case 6: {
                return "Current event (" + evtDesc + " not START_ELEMENT, END_ELEMENT, CHARACTERS or CDATA";
            }
            case 5: {
                return "Current event " + evtDesc + ", needs to be one of CHARACTERS, CDATA, SPACE or COMMENT";
            }
        }
        return "Internal error (unrecognized error type: " + errorType + ")";
    }

    protected String _internName(String name) {
        if (name == null) {
            return "";
        }
        return this._cfgInternNames ? name.intern() : name;
    }

    protected String _internNsURI(String uri) {
        if (uri == null) {
            return "";
        }
        return this._cfgInternNsURIs ? uri.intern() : uri;
    }
}

