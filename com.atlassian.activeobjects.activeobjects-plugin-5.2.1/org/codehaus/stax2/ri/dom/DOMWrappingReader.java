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
    protected List _attrList = null;
    protected List _nsDeclList = null;
    protected ValueDecoderFactory _decoderFactory;
    protected StringBase64Decoder _base64Decoder = null;

    protected DOMWrappingReader(DOMSource dOMSource, boolean bl, boolean bl2) throws XMLStreamException {
        Node node = dOMSource.getNode();
        if (node == null) {
            throw new IllegalArgumentException("Can not pass null Node for constructing a DOM-based XMLStreamReader");
        }
        this._cfgNsAware = bl;
        this._coalescing = bl2;
        this._systemId = dOMSource.getSystemId();
        switch (node.getNodeType()) {
            case 1: 
            case 9: 
            case 11: {
                break;
            }
            default: {
                throw new XMLStreamException("Can not create an XMLStreamReader for a DOM node of type " + node.getClass());
            }
        }
        this._rootNode = this._currNode = node;
    }

    protected void setInternNames(boolean bl) {
        this._cfgInternNames = bl;
    }

    protected void setInternNsURIs(boolean bl) {
        this._cfgInternNsURIs = bl;
    }

    protected abstract void throwStreamException(String var1, Location var2) throws XMLStreamException;

    public String getCharacterEncodingScheme() {
        return null;
    }

    public String getEncoding() {
        return this.getCharacterEncodingScheme();
    }

    public String getVersion() {
        return null;
    }

    public boolean isStandalone() {
        return false;
    }

    public boolean standaloneSet() {
        return false;
    }

    public abstract Object getProperty(String var1);

    public abstract boolean isPropertySupported(String var1);

    public abstract boolean setProperty(String var1, Object var2);

    public int getAttributeCount() {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        return this._attrList.size();
    }

    public String getAttributeLocalName(int n) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (n >= this._attrList.size() || n < 0) {
            this.handleIllegalAttrIndex(n);
            return null;
        }
        Attr attr = (Attr)this._attrList.get(n);
        return this._internName(this._safeGetLocalName(attr));
    }

    public QName getAttributeName(int n) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (n >= this._attrList.size() || n < 0) {
            this.handleIllegalAttrIndex(n);
            return null;
        }
        Attr attr = (Attr)this._attrList.get(n);
        return this._constructQName(attr.getNamespaceURI(), this._safeGetLocalName(attr), attr.getPrefix());
    }

    public String getAttributeNamespace(int n) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (n >= this._attrList.size() || n < 0) {
            this.handleIllegalAttrIndex(n);
            return null;
        }
        Attr attr = (Attr)this._attrList.get(n);
        return this._internNsURI(attr.getNamespaceURI());
    }

    public String getAttributePrefix(int n) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (n >= this._attrList.size() || n < 0) {
            this.handleIllegalAttrIndex(n);
            return null;
        }
        Attr attr = (Attr)this._attrList.get(n);
        return this._internName(attr.getPrefix());
    }

    public String getAttributeType(int n) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (n >= this._attrList.size() || n < 0) {
            this.handleIllegalAttrIndex(n);
            return null;
        }
        return "CDATA";
    }

    public String getAttributeValue(int n) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if (this._attrList == null) {
            this._calcNsAndAttrLists(true);
        }
        if (n >= this._attrList.size() || n < 0) {
            this.handleIllegalAttrIndex(n);
            return null;
        }
        Attr attr = (Attr)this._attrList.get(n);
        return attr.getValue();
    }

    public String getAttributeValue(String string, String string2) {
        Attr attr;
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        Element element = (Element)this._currNode;
        NamedNodeMap namedNodeMap = element.getAttributes();
        if (string != null && string.length() == 0) {
            string = null;
        }
        return (attr = (Attr)namedNodeMap.getNamedItemNS(string, string2)) == null ? null : attr.getValue();
    }

    public String getElementText() throws XMLStreamException {
        int n;
        if (this._currEvent != 1) {
            this.reportParseProblem(1);
        }
        if (this._coalescing) {
            int n2;
            String string = null;
            while ((n2 = this.next()) != 2) {
                if (n2 == 5 || n2 == 3) continue;
                if ((1 << n2 & 0x1250) == 0) {
                    this.reportParseProblem(4);
                }
                if (string == null) {
                    string = this.getText();
                    continue;
                }
                string = string + this.getText();
            }
            return string == null ? "" : string;
        }
        this._textBuffer.reset();
        while ((n = this.next()) != 2) {
            if (n == 5 || n == 3) continue;
            if ((1 << n & 0x1250) == 0) {
                this.reportParseProblem(4);
            }
            this._textBuffer.append(this.getText());
        }
        return this._textBuffer.get();
    }

    public int getEventType() {
        return this._currEvent;
    }

    public String getLocalName() {
        if (this._currEvent == 1 || this._currEvent == 2) {
            return this._internName(this._safeGetLocalName(this._currNode));
        }
        if (this._currEvent != 9) {
            this.reportWrongState(7);
        }
        return this._internName(this._currNode.getNodeName());
    }

    public final Location getLocation() {
        return this.getStartLocation();
    }

    public QName getName() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(1);
        }
        return this._constructQName(this._currNode.getNamespaceURI(), this._safeGetLocalName(this._currNode), this._currNode.getPrefix());
    }

    public NamespaceContext getNamespaceContext() {
        return this;
    }

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

    public String getNamespacePrefix(int n) {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        if (this._nsDeclList == null) {
            if (!this._cfgNsAware) {
                this.handleIllegalNsIndex(n);
            }
            this._calcNsAndAttrLists(this._currEvent == 1);
        }
        if (n < 0 || n + n >= this._nsDeclList.size()) {
            this.handleIllegalNsIndex(n);
        }
        return (String)this._nsDeclList.get(n + n);
    }

    public String getNamespaceURI() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        return this._internNsURI(this._currNode.getNamespaceURI());
    }

    public String getNamespaceURI(int n) {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        if (this._nsDeclList == null) {
            if (!this._cfgNsAware) {
                this.handleIllegalNsIndex(n);
            }
            this._calcNsAndAttrLists(this._currEvent == 1);
        }
        if (n < 0 || n + n >= this._nsDeclList.size()) {
            this.handleIllegalNsIndex(n);
        }
        return (String)this._nsDeclList.get(n + n + 1);
    }

    public String getPIData() {
        if (this._currEvent != 3) {
            this.reportWrongState(3);
        }
        return this._currNode.getNodeValue();
    }

    public String getPITarget() {
        if (this._currEvent != 3) {
            this.reportWrongState(3);
        }
        return this._internName(this._currNode.getNodeName());
    }

    public String getPrefix() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            this.reportWrongState(2);
        }
        return this._internName(this._currNode.getPrefix());
    }

    public String getText() {
        if (this._coalescedText != null) {
            return this._coalescedText;
        }
        if ((1 << this._currEvent & 0x1A70) == 0) {
            this.reportWrongState(4);
        }
        return this._currNode.getNodeValue();
    }

    public char[] getTextCharacters() {
        String string = this.getText();
        return string.toCharArray();
    }

    public int getTextCharacters(int n, char[] cArray, int n2, int n3) {
        String string;
        if ((1 << this._currEvent & 0x1070) == 0) {
            this.reportWrongState(5);
        }
        if (n3 > (string = this.getText()).length()) {
            n3 = string.length();
        }
        string.getChars(n, n + n3, cArray, n2);
        return n3;
    }

    public int getTextLength() {
        if ((1 << this._currEvent & 0x1070) == 0) {
            this.reportWrongState(5);
        }
        return this.getText().length();
    }

    public int getTextStart() {
        if ((1 << this._currEvent & 0x1070) == 0) {
            this.reportWrongState(5);
        }
        return 0;
    }

    public boolean hasName() {
        return this._currEvent == 1 || this._currEvent == 2;
    }

    public boolean hasNext() {
        return this._currEvent != 8;
    }

    public boolean hasText() {
        return (1 << this._currEvent & 0x1A70) != 0;
    }

    public boolean isAttributeSpecified(int n) {
        Element element;
        Attr attr;
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        if ((attr = (Attr)(element = (Element)this._currNode).getAttributes().item(n)) == null) {
            this.handleIllegalAttrIndex(n);
            return false;
        }
        return attr.getSpecified();
    }

    public boolean isCharacters() {
        return this._currEvent == 4;
    }

    public boolean isEndElement() {
        return this._currEvent == 2;
    }

    public boolean isStartElement() {
        return this._currEvent == 1;
    }

    public boolean isWhiteSpace() {
        if (this._currEvent == 4 || this._currEvent == 12) {
            String string = this.getText();
            int n = string.length();
            for (int i = 0; i < n; ++i) {
                if (string.charAt(i) <= ' ') continue;
                return false;
            }
            return true;
        }
        return this._currEvent == 6;
    }

    public void require(int n, String string, String string2) throws XMLStreamException {
        String string3;
        int n2 = this._currEvent;
        if (n2 != n) {
            if (n2 == 12) {
                n2 = 4;
            } else if (n2 == 6) {
                n2 = 4;
            }
        }
        if (n != n2) {
            this.throwStreamException("Required type " + Stax2Util.eventTypeDesc(n) + ", current type " + Stax2Util.eventTypeDesc(n2));
        }
        if (string2 != null) {
            if (n2 != 1 && n2 != 2 && n2 != 9) {
                this.throwStreamException("Required a non-null local name, but current token not a START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE (was " + Stax2Util.eventTypeDesc(this._currEvent) + ")");
            }
            if ((string3 = this.getLocalName()) != string2 && !string3.equals(string2)) {
                this.throwStreamException("Required local name '" + string2 + "'; current local name '" + string3 + "'.");
            }
        }
        if (string != null) {
            if (n2 != 1 && n2 != 2) {
                this.throwStreamException("Required non-null NS URI, but current token not a START_ELEMENT or END_ELEMENT (was " + Stax2Util.eventTypeDesc(n2) + ")");
            }
            string3 = this.getNamespaceURI();
            if (string.length() == 0) {
                if (string3 != null && string3.length() > 0) {
                    this.throwStreamException("Required empty namespace, instead have '" + string3 + "'.");
                }
            } else if (string != string3 && !string.equals(string3)) {
                this.throwStreamException("Required namespace '" + string + "'; have '" + string3 + "'.");
            }
        }
    }

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
                Node node = this._currNode.getFirstChild();
                if (node == null) {
                    this._currEvent = 2;
                    return 2;
                }
                this._nsDeclList = null;
                this._currNode = node;
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
                Node node = this._currNode.getNextSibling();
                if (node != null) {
                    this._currNode = node;
                    break;
                }
                this._currNode = this._currNode.getParentNode();
                short s = this._currNode.getNodeType();
                if (s == 1) {
                    this._currEvent = 2;
                    return 2;
                }
                if (this._currNode != this._rootNode || s != 9 && s != 11) {
                    throw new XMLStreamException("Internal error: non-element parent node (" + s + ") that is not the initial root node");
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

    public int nextTag() throws XMLStreamException {
        block5: while (true) {
            int n = this.next();
            switch (n) {
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
                    return n;
                }
            }
            this.throwStreamException("Received event " + Stax2Util.eventTypeDesc(n) + ", instead of START_ELEMENT or END_ELEMENT.");
        }
    }

    public void close() throws XMLStreamException {
    }

    public String getNamespaceURI(String string) {
        boolean bl;
        boolean bl2 = bl = string == null || string.length() == 0;
        for (Node node = this._currNode; node != null; node = node.getParentNode()) {
            NamedNodeMap namedNodeMap = node.getAttributes();
            if (namedNodeMap == null) continue;
            int n = namedNodeMap.getLength();
            for (int i = 0; i < n; ++i) {
                Node node2 = namedNodeMap.item(i);
                String string2 = node2.getPrefix();
                if (!(string2 == null || string2.length() == 0 ? bl && "xmlns".equals(node2.getLocalName()) : !bl && "xmlns".equals(string2) && string.equals(node2.getLocalName()))) continue;
                return node2.getNodeValue();
            }
        }
        return null;
    }

    public String getPrefix(String string) {
        Node node = this._currNode;
        if (string == null) {
            string = "";
        }
        while (node != null) {
            NamedNodeMap namedNodeMap = node.getAttributes();
            int n = namedNodeMap.getLength();
            for (int i = 0; i < n; ++i) {
                Node node2 = namedNodeMap.item(i);
                String string2 = node2.getPrefix();
                if (string2 == null || string2.length() == 0) {
                    if (!"xmlns".equals(node2.getLocalName()) || !string.equals(node2.getNodeValue())) continue;
                    return "";
                }
                if (!"xmlns".equals(string2) || !string.equals(node2.getNodeValue())) continue;
                return node2.getLocalName();
            }
            node = node.getParentNode();
        }
        return null;
    }

    public Iterator getPrefixes(String string) {
        String string2 = this.getPrefix(string);
        if (string2 == null) {
            return EmptyIterator.getInstance();
        }
        return new SingletonIterator(string2);
    }

    public boolean getElementAsBoolean() throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder booleanDecoder = this._decoderFactory().getBooleanDecoder();
        this.getElementAs(booleanDecoder);
        return booleanDecoder.getValue();
    }

    public int getElementAsInt() throws XMLStreamException {
        ValueDecoderFactory.IntDecoder intDecoder = this._decoderFactory().getIntDecoder();
        this.getElementAs(intDecoder);
        return intDecoder.getValue();
    }

    public long getElementAsLong() throws XMLStreamException {
        ValueDecoderFactory.LongDecoder longDecoder = this._decoderFactory().getLongDecoder();
        this.getElementAs(longDecoder);
        return longDecoder.getValue();
    }

    public float getElementAsFloat() throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder floatDecoder = this._decoderFactory().getFloatDecoder();
        this.getElementAs(floatDecoder);
        return floatDecoder.getValue();
    }

    public double getElementAsDouble() throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder doubleDecoder = this._decoderFactory().getDoubleDecoder();
        this.getElementAs(doubleDecoder);
        return doubleDecoder.getValue();
    }

    public BigInteger getElementAsInteger() throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder integerDecoder = this._decoderFactory().getIntegerDecoder();
        this.getElementAs(integerDecoder);
        return integerDecoder.getValue();
    }

    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder decimalDecoder = this._decoderFactory().getDecimalDecoder();
        this.getElementAs(decimalDecoder);
        return decimalDecoder.getValue();
    }

    public QName getElementAsQName() throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder qNameDecoder = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getElementAs(qNameDecoder);
        return qNameDecoder.getValue();
    }

    public byte[] getElementAsBinary() throws XMLStreamException {
        return this.getElementAsBinary(Base64Variants.getDefaultVariant());
    }

    public byte[] getElementAsBinary(Base64Variant base64Variant) throws XMLStreamException {
        Stax2Util.ByteAggregator byteAggregator = this._base64Decoder().getByteAggregator();
        byte[] byArray = byteAggregator.startAggregation();
        while (true) {
            int n;
            int n2 = 0;
            int n3 = byArray.length;
            do {
                if ((n = this.readElementAsBinary(byArray, n2, n3, base64Variant)) < 1) {
                    return byteAggregator.aggregateAll(byArray, n2);
                }
                n2 += n;
            } while ((n3 -= n) > 0);
            byArray = byteAggregator.addFullBlock(byArray);
        }
    }

    public void getElementAs(TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        String string = this.getElementText();
        string = Stax2Util.trimSpaces(string);
        try {
            if (string == null) {
                typedValueDecoder.handleEmptyValue();
            } else {
                typedValueDecoder.decode(string);
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw this._constructTypeException(illegalArgumentException, string);
        }
    }

    public int readElementAsIntArray(int[] nArray, int n, int n2) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getIntArrayDecoder(nArray, n, n2));
    }

    public int readElementAsLongArray(long[] lArray, int n, int n2) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getLongArrayDecoder(lArray, n, n2));
    }

    public int readElementAsFloatArray(float[] fArray, int n, int n2) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getFloatArrayDecoder(fArray, n, n2));
    }

    public int readElementAsDoubleArray(double[] dArray, int n, int n2) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getDoubleArrayDecoder(dArray, n, n2));
    }

    public int readElementAsArray(TypedArrayDecoder typedArrayDecoder) throws XMLStreamException {
        String string;
        int n;
        Object object;
        if (this._currEvent == 1) {
            object = this._currNode.getFirstChild();
            if (object == null) {
                this._currEvent = 2;
                return -1;
            }
            this._coalescedText = this.coalesceTypedText((Node)object);
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
        object = this._coalescedText;
        int n2 = ((String)object).length();
        int n3 = 0;
        String string2 = null;
        try {
            int n4;
            block4: for (n = 0; n < n2; ++n) {
                while (((String)object).charAt(n) <= ' ') {
                    if (++n < n2) continue;
                    break block4;
                }
                n4 = n++;
                while (n < n2 && ((String)object).charAt(n) > ' ') {
                    ++n;
                }
                ++n3;
                string2 = ((String)object).substring(n4, n);
                if (!typedArrayDecoder.decodeValue(string2)) continue;
                break;
            }
            string = (n4 = n2 - n) < 1 ? "" : ((String)object).substring(n);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            try {
                Location location = this.getLocation();
                throw new TypedXMLStreamException(string2, illegalArgumentException.getMessage(), location, illegalArgumentException);
            }
            catch (Throwable throwable) {
                int n5 = n2 - n;
                this._coalescedText = n5 < 1 ? "" : ((String)object).substring(n);
                throw throwable;
            }
        }
        this._coalescedText = string;
        if (n3 < 1) {
            this._currEvent = 2;
            this._currNode = this._currNode.getParentNode();
            return -1;
        }
        return n3;
    }

    private String coalesceTypedText(Node node) throws XMLStreamException {
        this._textBuffer.reset();
        this._attrList = null;
        block5: for (Node node2 = node; node2 != null; node2 = node2.getNextSibling()) {
            switch (node2.getNodeType()) {
                case 1: {
                    this.throwStreamException("Element content can not contain child START_ELEMENT when using Typed Access methods");
                }
                case 3: 
                case 4: {
                    this._textBuffer.append(node2.getNodeValue());
                    continue block5;
                }
                case 7: 
                case 8: {
                    continue block5;
                }
                default: {
                    this.throwStreamException("Unexpected DOM node type (" + node2.getNodeType() + ") when trying to decode Typed content");
                }
            }
        }
        return this._textBuffer.get();
    }

    public int readElementAsBinary(byte[] byArray, int n, int n2) throws XMLStreamException {
        return this.readElementAsBinary(byArray, n, n2, Base64Variants.getDefaultVariant());
    }

    public int readElementAsBinary(byte[] byArray, int n, int n2, Base64Variant base64Variant) throws XMLStreamException {
        if (byArray == null) {
            throw new IllegalArgumentException("resultBuffer is null");
        }
        if (n < 0) {
            throw new IllegalArgumentException("Illegal offset (" + n + "), must be [0, " + byArray.length + "[");
        }
        if (n2 < 1 || n + n2 > byArray.length) {
            if (n2 == 0) {
                return 0;
            }
            throw new IllegalArgumentException("Illegal maxLength (" + n2 + "), has to be positive number, and offset+maxLength can not exceed" + byArray.length);
        }
        StringBase64Decoder stringBase64Decoder = this._base64Decoder();
        int n3 = this._currEvent;
        if ((1 << n3 & 0x1052) == 0) {
            if (n3 == 2) {
                if (!stringBase64Decoder.hasData()) {
                    return -1;
                }
            } else {
                this.reportWrongState(6);
            }
        }
        if (n3 == 1) {
            do {
                if ((n3 = this.next()) != 2) continue;
                return -1;
            } while (n3 == 5 || n3 == 3);
            if ((1 << n3 & 0x1250) == 0) {
                this.reportParseProblem(4);
            }
            stringBase64Decoder.init(base64Variant, true, this.getText());
        }
        int n4 = 0;
        while (true) {
            int n5;
            try {
                n5 = stringBase64Decoder.decode(byArray, n, n2);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw this._constructTypeException(illegalArgumentException, "");
            }
            n += n5;
            n4 += n5;
            if ((n2 -= n5) < 1 || this._currEvent == 2) break;
            while ((n3 = this.next()) == 5 || n3 == 3 || n3 == 6) {
            }
            if (n3 == 2) {
                int n6 = stringBase64Decoder.endOfContent();
                if (n6 < 0) {
                    throw this._constructTypeException("Incomplete base64 triplet at the end of decoded content", "");
                }
                if (n6 <= 0) break;
                continue;
            }
            if ((1 << n3 & 0x1250) == 0) {
                this.reportParseProblem(4);
            }
            stringBase64Decoder.init(base64Variant, false, this.getText());
        }
        return n4 > 0 ? n4 : -1;
    }

    public int getAttributeIndex(String string, String string2) {
        return this.findAttributeIndex(string, string2);
    }

    public boolean getAttributeAsBoolean(int n) throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder booleanDecoder = this._decoderFactory().getBooleanDecoder();
        this.getAttributeAs(n, booleanDecoder);
        return booleanDecoder.getValue();
    }

    public int getAttributeAsInt(int n) throws XMLStreamException {
        ValueDecoderFactory.IntDecoder intDecoder = this._decoderFactory().getIntDecoder();
        this.getAttributeAs(n, intDecoder);
        return intDecoder.getValue();
    }

    public long getAttributeAsLong(int n) throws XMLStreamException {
        ValueDecoderFactory.LongDecoder longDecoder = this._decoderFactory().getLongDecoder();
        this.getAttributeAs(n, longDecoder);
        return longDecoder.getValue();
    }

    public float getAttributeAsFloat(int n) throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder floatDecoder = this._decoderFactory().getFloatDecoder();
        this.getAttributeAs(n, floatDecoder);
        return floatDecoder.getValue();
    }

    public double getAttributeAsDouble(int n) throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder doubleDecoder = this._decoderFactory().getDoubleDecoder();
        this.getAttributeAs(n, doubleDecoder);
        return doubleDecoder.getValue();
    }

    public BigInteger getAttributeAsInteger(int n) throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder integerDecoder = this._decoderFactory().getIntegerDecoder();
        this.getAttributeAs(n, integerDecoder);
        return integerDecoder.getValue();
    }

    public BigDecimal getAttributeAsDecimal(int n) throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder decimalDecoder = this._decoderFactory().getDecimalDecoder();
        this.getAttributeAs(n, decimalDecoder);
        return decimalDecoder.getValue();
    }

    public QName getAttributeAsQName(int n) throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder qNameDecoder = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getAttributeAs(n, qNameDecoder);
        return qNameDecoder.getValue();
    }

    public final void getAttributeAs(int n, TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        String string = this.getAttributeValue(n);
        string = Stax2Util.trimSpaces(string);
        try {
            if (string == null) {
                typedValueDecoder.handleEmptyValue();
            } else {
                typedValueDecoder.decode(string);
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw this._constructTypeException(illegalArgumentException, string);
        }
    }

    public int[] getAttributeAsIntArray(int n) throws XMLStreamException {
        ValueDecoderFactory.IntArrayDecoder intArrayDecoder = this._decoderFactory().getIntArrayDecoder();
        this._getAttributeAsArray(intArrayDecoder, this.getAttributeValue(n));
        return intArrayDecoder.getValues();
    }

    public long[] getAttributeAsLongArray(int n) throws XMLStreamException {
        ValueDecoderFactory.LongArrayDecoder longArrayDecoder = this._decoderFactory().getLongArrayDecoder();
        this._getAttributeAsArray(longArrayDecoder, this.getAttributeValue(n));
        return longArrayDecoder.getValues();
    }

    public float[] getAttributeAsFloatArray(int n) throws XMLStreamException {
        ValueDecoderFactory.FloatArrayDecoder floatArrayDecoder = this._decoderFactory().getFloatArrayDecoder();
        this._getAttributeAsArray(floatArrayDecoder, this.getAttributeValue(n));
        return floatArrayDecoder.getValues();
    }

    public double[] getAttributeAsDoubleArray(int n) throws XMLStreamException {
        ValueDecoderFactory.DoubleArrayDecoder doubleArrayDecoder = this._decoderFactory().getDoubleArrayDecoder();
        this._getAttributeAsArray(doubleArrayDecoder, this.getAttributeValue(n));
        return doubleArrayDecoder.getValues();
    }

    public int getAttributeAsArray(int n, TypedArrayDecoder typedArrayDecoder) throws XMLStreamException {
        return this._getAttributeAsArray(typedArrayDecoder, this.getAttributeValue(n));
    }

    protected int _getAttributeAsArray(TypedArrayDecoder typedArrayDecoder, String string) throws XMLStreamException {
        int n;
        block5: {
            int n2 = 0;
            int n3 = 0;
            int n4 = string.length();
            String string2 = null;
            n = 0;
            try {
                while (n2 < n4) {
                    while (string.charAt(n2) <= ' ') {
                        if (++n2 < n4) continue;
                        break block5;
                    }
                    n3 = n2++;
                    while (n2 < n4 && string.charAt(n2) > ' ') {
                        ++n2;
                    }
                    int n5 = n2++;
                    string2 = string.substring(n3, n5);
                    ++n;
                    if (!typedArrayDecoder.decodeValue(string2) || this.checkExpand(typedArrayDecoder)) continue;
                    break;
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                Location location = this.getLocation();
                throw new TypedXMLStreamException(string2, illegalArgumentException.getMessage(), location, illegalArgumentException);
            }
        }
        return n;
    }

    private final boolean checkExpand(TypedArrayDecoder typedArrayDecoder) {
        if (typedArrayDecoder instanceof ValueDecoderFactory.BaseArrayDecoder) {
            ((ValueDecoderFactory.BaseArrayDecoder)typedArrayDecoder).expand();
            return true;
        }
        return false;
    }

    public byte[] getAttributeAsBinary(int n) throws XMLStreamException {
        return this.getAttributeAsBinary(n, Base64Variants.getDefaultVariant());
    }

    public byte[] getAttributeAsBinary(int n, Base64Variant base64Variant) throws XMLStreamException {
        String string = this.getAttributeValue(n);
        StringBase64Decoder stringBase64Decoder = this._base64Decoder();
        stringBase64Decoder.init(base64Variant, true, string);
        try {
            return stringBase64Decoder.decodeCompletely();
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw this._constructTypeException(illegalArgumentException, string);
        }
    }

    public Object getFeature(String string) {
        throw new IllegalArgumentException("Unrecognized feature \"" + string + "\"");
    }

    public void setFeature(String string, Object object) {
        throw new IllegalArgumentException("Unrecognized feature \"" + string + "\"");
    }

    public void skipElement() throws XMLStreamException {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        int n = 1;
        while (true) {
            int n2;
            if ((n2 = this.next()) == 1) {
                ++n;
                continue;
            }
            if (n2 == 2 && --n == 0) break;
        }
    }

    public AttributeInfo getAttributeInfo() throws XMLStreamException {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        return this;
    }

    public int findAttributeIndex(String string, String string2) {
        if (this._currEvent != 1) {
            this.reportWrongState(1);
        }
        Element element = (Element)this._currNode;
        NamedNodeMap namedNodeMap = element.getAttributes();
        if (string != null && string.length() == 0) {
            string = null;
        }
        int n = namedNodeMap.getLength();
        for (int i = 0; i < n; ++i) {
            boolean bl;
            Node node = namedNodeMap.item(i);
            String string3 = this._safeGetLocalName(node);
            if (!string2.equals(string3)) continue;
            String string4 = node.getNamespaceURI();
            boolean bl2 = bl = string4 == null || string4.length() == 0;
            if (!(string == null ? bl : !bl && string.equals(string4))) continue;
            return i;
        }
        return -1;
    }

    public int getIdAttributeIndex() {
        return -1;
    }

    public int getNotationAttributeIndex() {
        return -1;
    }

    public DTDInfo getDTDInfo() throws XMLStreamException {
        if (this._currEvent != 11) {
            return null;
        }
        return this;
    }

    public final LocationInfo getLocationInfo() {
        return this;
    }

    public int getText(Writer writer, boolean bl) throws IOException, XMLStreamException {
        String string = this.getText();
        writer.write(string);
        return string.length();
    }

    public int getDepth() {
        return this._depth;
    }

    public boolean isEmptyElement() throws XMLStreamException {
        return false;
    }

    public NamespaceContext getNonTransientNamespaceContext() {
        return EmptyNamespaceContext.getInstance();
    }

    public String getPrefixedName() {
        switch (this._currEvent) {
            case 1: 
            case 2: {
                String string = this._currNode.getPrefix();
                String string2 = this._safeGetLocalName(this._currNode);
                if (string == null) {
                    return this._internName(string2);
                }
                StringBuffer stringBuffer = new StringBuffer(string2.length() + 1 + string.length());
                stringBuffer.append(string);
                stringBuffer.append(':');
                stringBuffer.append(string2);
                return this._internName(stringBuffer.toString());
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

    public void closeCompletely() throws XMLStreamException {
    }

    public Object getProcessedDTD() {
        return null;
    }

    public String getDTDRootName() {
        if (this._currEvent == 11) {
            return this._internName(((DocumentType)this._currNode).getName());
        }
        return null;
    }

    public String getDTDPublicId() {
        if (this._currEvent == 11) {
            return ((DocumentType)this._currNode).getPublicId();
        }
        return null;
    }

    public String getDTDSystemId() {
        if (this._currEvent == 11) {
            return ((DocumentType)this._currNode).getSystemId();
        }
        return null;
    }

    public String getDTDInternalSubset() {
        return null;
    }

    public DTDValidationSchema getProcessedDTDSchema() {
        return null;
    }

    public long getStartingByteOffset() {
        return -1L;
    }

    public long getStartingCharOffset() {
        return 0L;
    }

    public long getEndingByteOffset() throws XMLStreamException {
        return -1L;
    }

    public long getEndingCharOffset() throws XMLStreamException {
        return -1L;
    }

    public XMLStreamLocation2 getStartLocation() {
        return XMLStreamLocation2.NOT_AVAILABLE;
    }

    public XMLStreamLocation2 getCurrentLocation() {
        return XMLStreamLocation2.NOT_AVAILABLE;
    }

    public final XMLStreamLocation2 getEndLocation() throws XMLStreamException {
        return XMLStreamLocation2.NOT_AVAILABLE;
    }

    public XMLValidator validateAgainst(XMLValidationSchema xMLValidationSchema) throws XMLStreamException {
        return null;
    }

    public XMLValidator stopValidatingAgainst(XMLValidationSchema xMLValidationSchema) throws XMLStreamException {
        return null;
    }

    public XMLValidator stopValidatingAgainst(XMLValidator xMLValidator) throws XMLStreamException {
        return null;
    }

    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler validationProblemHandler) {
        return null;
    }

    protected void coalesceText(int n) {
        short s;
        Node node;
        this._textBuffer.reset();
        this._textBuffer.append(this._currNode.getNodeValue());
        while ((node = this._currNode.getNextSibling()) != null && ((s = node.getNodeType()) == 3 || s == 4)) {
            this._currNode = node;
            this._textBuffer.append(this._currNode.getNodeValue());
        }
        this._coalescedText = this._textBuffer.get();
        this._currEvent = 4;
    }

    private QName _constructQName(String string, String string2, String string3) {
        return new QName(this._internNsURI(string), this._internName(string2), this._internName(string3));
    }

    private void _calcNsAndAttrLists(boolean bl) {
        NamedNodeMap namedNodeMap = this._currNode.getAttributes();
        int n = namedNodeMap.getLength();
        if (n == 0) {
            this._attrList = this._nsDeclList = Collections.EMPTY_LIST;
            return;
        }
        if (!this._cfgNsAware) {
            this._attrList = new ArrayList(n);
            for (int i = 0; i < n; ++i) {
                this._attrList.add(namedNodeMap.item(i));
            }
            this._nsDeclList = Collections.EMPTY_LIST;
            return;
        }
        ArrayList<Node> arrayList = null;
        ArrayList<String> arrayList2 = null;
        for (int i = 0; i < n; ++i) {
            Node node = namedNodeMap.item(i);
            String string = node.getPrefix();
            if (string == null || string.length() == 0) {
                if (!"xmlns".equals(node.getLocalName())) {
                    if (!bl) continue;
                    if (arrayList == null) {
                        arrayList = new ArrayList<Node>(n - i);
                    }
                    arrayList.add(node);
                    continue;
                }
                string = null;
            } else {
                if (!"xmlns".equals(string)) {
                    if (!bl) continue;
                    if (arrayList == null) {
                        arrayList = new ArrayList(n - i);
                    }
                    arrayList.add(node);
                    continue;
                }
                string = node.getLocalName();
            }
            if (arrayList2 == null) {
                arrayList2 = new ArrayList<String>((n - i) * 2);
            }
            arrayList2.add(this._internName(string));
            arrayList2.add(this._internNsURI(node.getNodeValue()));
        }
        this._attrList = arrayList == null ? Collections.EMPTY_LIST : arrayList;
        this._nsDeclList = arrayList2 == null ? Collections.EMPTY_LIST : arrayList2;
    }

    private void handleIllegalAttrIndex(int n) {
        Element element = (Element)this._currNode;
        NamedNodeMap namedNodeMap = element.getAttributes();
        int n2 = namedNodeMap.getLength();
        String string = "Illegal attribute index " + n + "; element <" + element.getNodeName() + "> has " + (n2 == 0 ? "no" : String.valueOf(n2)) + " attributes";
        throw new IllegalArgumentException(string);
    }

    private void handleIllegalNsIndex(int n) {
        String string = "Illegal namespace declaration index " + n + " (has " + this.getNamespaceCount() + " ns declarations)";
        throw new IllegalArgumentException(string);
    }

    private String _safeGetLocalName(Node node) {
        String string = node.getLocalName();
        if (string == null) {
            string = node.getNodeName();
        }
        return string;
    }

    protected void reportWrongState(int n) {
        throw new IllegalStateException(this.findErrorDesc(n, this._currEvent));
    }

    protected void reportParseProblem(int n) throws XMLStreamException {
        this.throwStreamException(this.findErrorDesc(n, this._currEvent));
    }

    protected void throwStreamException(String string) throws XMLStreamException {
        this.throwStreamException(string, this.getErrorLocation());
    }

    protected Location getErrorLocation() {
        Location location = this.getCurrentLocation();
        if (location == null) {
            location = this.getLocation();
        }
        return location;
    }

    protected TypedXMLStreamException _constructTypeException(IllegalArgumentException illegalArgumentException, String string) {
        XMLStreamLocation2 xMLStreamLocation2;
        String string2 = illegalArgumentException.getMessage();
        if (string2 == null) {
            string2 = "";
        }
        if ((xMLStreamLocation2 = this.getStartLocation()) == null) {
            return new TypedXMLStreamException(string, string2, illegalArgumentException);
        }
        return new TypedXMLStreamException(string, string2, xMLStreamLocation2);
    }

    protected TypedXMLStreamException _constructTypeException(String string, String string2) {
        XMLStreamLocation2 xMLStreamLocation2 = this.getStartLocation();
        if (xMLStreamLocation2 == null) {
            return new TypedXMLStreamException(string2, string);
        }
        return new TypedXMLStreamException(string2, string, xMLStreamLocation2);
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

    protected String findErrorDesc(int n, int n2) {
        String string = Stax2Util.eventTypeDesc(n2);
        switch (n) {
            case 1: {
                return "Current event " + string + ", needs to be START_ELEMENT";
            }
            case 2: {
                return "Current event " + string + ", needs to be START_ELEMENT or END_ELEMENT";
            }
            case 7: {
                return "Current event (" + string + ") has no local name";
            }
            case 3: {
                return "Current event (" + string + ") needs to be PROCESSING_INSTRUCTION";
            }
            case 4: {
                return "Current event (" + string + ") not a textual event";
            }
            case 6: {
                return "Current event (" + string + " not START_ELEMENT, END_ELEMENT, CHARACTERS or CDATA";
            }
            case 5: {
                return "Current event " + string + ", needs to be one of CHARACTERS, CDATA, SPACE or COMMENT";
            }
        }
        return "Internal error (unrecognized error type: " + n + ")";
    }

    protected String _internName(String string) {
        if (string == null) {
            return "";
        }
        return this._cfgInternNames ? string.intern() : string;
    }

    protected String _internNsURI(String string) {
        if (string == null) {
            return "";
        }
        return this._cfgInternNsURIs ? string.intern() : string;
    }
}

