/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.Encoder;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.stax.StAXManager;
import com.sun.xml.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.fastinfoset.util.NamespaceContextImplementation;
import java.io.IOException;
import java.io.OutputStream;
import java.util.EmptyStackException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.fastinfoset.stax.LowLevelFastInfosetStreamWriter;

public class StAXDocumentSerializer
extends Encoder
implements XMLStreamWriter,
LowLevelFastInfosetStreamWriter {
    protected StAXManager _manager;
    protected String _encoding;
    protected String _currentLocalName;
    protected String _currentUri;
    protected String _currentPrefix;
    protected boolean _inStartElement = false;
    protected boolean _isEmptyElement = false;
    protected String[] _attributesArray = new String[64];
    protected int _attributesArrayIndex = 0;
    protected boolean[] _nsSupportContextStack = new boolean[32];
    protected int _stackCount = -1;
    protected NamespaceContextImplementation _nsContext = new NamespaceContextImplementation();
    protected String[] _namespacesArray = new String[16];
    protected int _namespacesArrayIndex = 0;

    public StAXDocumentSerializer() {
        super(true);
        this._manager = new StAXManager(2);
    }

    public StAXDocumentSerializer(OutputStream outputStream) {
        super(true);
        this.setOutputStream(outputStream);
        this._manager = new StAXManager(2);
    }

    public StAXDocumentSerializer(OutputStream outputStream, StAXManager manager) {
        super(true);
        this.setOutputStream(outputStream);
        this._manager = manager;
    }

    @Override
    public void reset() {
        super.reset();
        this._attributesArrayIndex = 0;
        this._namespacesArrayIndex = 0;
        this._nsContext.reset();
        this._stackCount = -1;
        this._currentPrefix = null;
        this._currentUri = null;
        this._currentLocalName = null;
        this._isEmptyElement = false;
        this._inStartElement = false;
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.writeStartDocument("finf", "1.0");
    }

    @Override
    public void writeStartDocument(String version) throws XMLStreamException {
        this.writeStartDocument("finf", version);
    }

    @Override
    public void writeStartDocument(String encoding, String version) throws XMLStreamException {
        this.reset();
        try {
            this.encodeHeader(false);
            this.encodeInitialVocabulary();
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        try {
            while (this._stackCount >= 0) {
                this.writeEndElement();
                --this._stackCount;
            }
            this.encodeDocumentTermination();
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void close() throws XMLStreamException {
        this.reset();
    }

    @Override
    public void flush() throws XMLStreamException {
        try {
            this._s.flush();
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void writeStartElement(String localName) throws XMLStreamException {
        this.writeStartElement("", localName, "");
    }

    @Override
    public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
        this.writeStartElement("", localName, namespaceURI);
    }

    @Override
    public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.encodeTerminationAndCurrentElement(false);
        this._inStartElement = true;
        this._isEmptyElement = false;
        this._currentLocalName = localName;
        this._currentPrefix = prefix;
        this._currentUri = namespaceURI;
        ++this._stackCount;
        if (this._stackCount == this._nsSupportContextStack.length) {
            boolean[] nsSupportContextStack = new boolean[this._stackCount * 2];
            System.arraycopy(this._nsSupportContextStack, 0, nsSupportContextStack, 0, this._nsSupportContextStack.length);
            this._nsSupportContextStack = nsSupportContextStack;
        }
        this._nsSupportContextStack[this._stackCount] = false;
    }

    @Override
    public void writeEmptyElement(String localName) throws XMLStreamException {
        this.writeEmptyElement("", localName, "");
    }

    @Override
    public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
        this.writeEmptyElement("", localName, namespaceURI);
    }

    @Override
    public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
        this.encodeTerminationAndCurrentElement(false);
        this._inStartElement = true;
        this._isEmptyElement = true;
        this._currentLocalName = localName;
        this._currentPrefix = prefix;
        this._currentUri = namespaceURI;
        ++this._stackCount;
        if (this._stackCount == this._nsSupportContextStack.length) {
            boolean[] nsSupportContextStack = new boolean[this._stackCount * 2];
            System.arraycopy(this._nsSupportContextStack, 0, nsSupportContextStack, 0, this._nsSupportContextStack.length);
            this._nsSupportContextStack = nsSupportContextStack;
        }
        this._nsSupportContextStack[this._stackCount] = false;
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        if (this._inStartElement) {
            this.encodeTerminationAndCurrentElement(false);
        }
        try {
            this.encodeElementTermination();
            if (this._nsSupportContextStack[this._stackCount--]) {
                this._nsContext.popContext();
            }
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
        catch (EmptyStackException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        this.writeAttribute("", "", localName, value);
    }

    @Override
    public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
        String prefix = "";
        if (namespaceURI.length() > 0 && ((prefix = this._nsContext.getNonDefaultPrefix(namespaceURI)) == null || prefix.length() == 0)) {
            if (namespaceURI == "http://www.w3.org/2000/xmlns/" || namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
                return;
            }
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.URIUnbound", new Object[]{namespaceURI}));
        }
        this.writeAttribute(prefix, namespaceURI, localName, value);
    }

    @Override
    public void writeAttribute(String prefix, String namespaceURI, String localName, String value) throws XMLStreamException {
        if (!this._inStartElement) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.attributeWritingNotAllowed"));
        }
        if (namespaceURI == "http://www.w3.org/2000/xmlns/" || namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
            return;
        }
        if (this._attributesArrayIndex == this._attributesArray.length) {
            String[] attributesArray = new String[this._attributesArrayIndex * 2];
            System.arraycopy(this._attributesArray, 0, attributesArray, 0, this._attributesArrayIndex);
            this._attributesArray = attributesArray;
        }
        this._attributesArray[this._attributesArrayIndex++] = namespaceURI;
        this._attributesArray[this._attributesArrayIndex++] = prefix;
        this._attributesArray[this._attributesArrayIndex++] = localName;
        this._attributesArray[this._attributesArrayIndex++] = value;
    }

    @Override
    public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
        if (prefix == null || prefix.length() == 0 || prefix.equals("xmlns")) {
            this.writeDefaultNamespace(namespaceURI);
        } else {
            if (!this._inStartElement) {
                throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.attributeWritingNotAllowed"));
            }
            if (this._namespacesArrayIndex == this._namespacesArray.length) {
                String[] namespacesArray = new String[this._namespacesArrayIndex * 2];
                System.arraycopy(this._namespacesArray, 0, namespacesArray, 0, this._namespacesArrayIndex);
                this._namespacesArray = namespacesArray;
            }
            this._namespacesArray[this._namespacesArrayIndex++] = prefix;
            this._namespacesArray[this._namespacesArrayIndex++] = namespaceURI;
            this.setPrefix(prefix, namespaceURI);
        }
    }

    @Override
    public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
        if (!this._inStartElement) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.attributeWritingNotAllowed"));
        }
        if (this._namespacesArrayIndex == this._namespacesArray.length) {
            String[] namespacesArray = new String[this._namespacesArrayIndex * 2];
            System.arraycopy(this._namespacesArray, 0, namespacesArray, 0, this._namespacesArrayIndex);
            this._namespacesArray = namespacesArray;
        }
        this._namespacesArray[this._namespacesArrayIndex++] = "";
        this._namespacesArray[this._namespacesArrayIndex++] = namespaceURI;
        this.setPrefix("", namespaceURI);
    }

    @Override
    public void writeComment(String data) throws XMLStreamException {
        try {
            if (this.getIgnoreComments()) {
                return;
            }
            this.encodeTerminationAndCurrentElement(true);
            this.encodeComment(data.toCharArray(), 0, data.length());
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void writeProcessingInstruction(String target) throws XMLStreamException {
        this.writeProcessingInstruction(target, "");
    }

    @Override
    public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
        try {
            if (this.getIgnoreProcesingInstructions()) {
                return;
            }
            this.encodeTerminationAndCurrentElement(true);
            this.encodeProcessingInstruction(target, data);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void writeCData(String text) throws XMLStreamException {
        try {
            int length = text.length();
            if (length == 0) {
                return;
            }
            if (length < this._charBuffer.length) {
                if (this.getIgnoreWhiteSpaceTextContent() && StAXDocumentSerializer.isWhiteSpace(text)) {
                    return;
                }
                this.encodeTerminationAndCurrentElement(true);
                text.getChars(0, length, this._charBuffer, 0);
                this.encodeCIIBuiltInAlgorithmDataAsCDATA(this._charBuffer, 0, length);
            } else {
                char[] ch = text.toCharArray();
                if (this.getIgnoreWhiteSpaceTextContent() && StAXDocumentSerializer.isWhiteSpace(ch, 0, length)) {
                    return;
                }
                this.encodeTerminationAndCurrentElement(true);
                this.encodeCIIBuiltInAlgorithmDataAsCDATA(ch, 0, length);
            }
        }
        catch (Exception e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void writeDTD(String dtd) throws XMLStreamException {
        throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.notImplemented"));
    }

    @Override
    public void writeEntityRef(String name) throws XMLStreamException {
        throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.notImplemented"));
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        try {
            int length = text.length();
            if (length == 0) {
                return;
            }
            if (length < this._charBuffer.length) {
                if (this.getIgnoreWhiteSpaceTextContent() && StAXDocumentSerializer.isWhiteSpace(text)) {
                    return;
                }
                this.encodeTerminationAndCurrentElement(true);
                text.getChars(0, length, this._charBuffer, 0);
                this.encodeCharacters(this._charBuffer, 0, length);
            } else {
                char[] ch = text.toCharArray();
                if (this.getIgnoreWhiteSpaceTextContent() && StAXDocumentSerializer.isWhiteSpace(ch, 0, length)) {
                    return;
                }
                this.encodeTerminationAndCurrentElement(true);
                this.encodeCharactersNoClone(ch, 0, length);
            }
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
        try {
            if (len <= 0) {
                return;
            }
            if (this.getIgnoreWhiteSpaceTextContent() && StAXDocumentSerializer.isWhiteSpace(text, start, len)) {
                return;
            }
            this.encodeTerminationAndCurrentElement(true);
            this.encodeCharacters(text, start, len);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public String getPrefix(String uri) throws XMLStreamException {
        return this._nsContext.getPrefix(uri);
    }

    @Override
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        if (this._stackCount > -1 && !this._nsSupportContextStack[this._stackCount]) {
            this._nsSupportContextStack[this._stackCount] = true;
            this._nsContext.pushContext();
        }
        this._nsContext.declarePrefix(prefix, uri);
    }

    @Override
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.setPrefix("", uri);
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        throw new UnsupportedOperationException("setNamespaceContext");
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this._nsContext;
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        if (this._manager != null) {
            return this._manager.getProperty(name);
        }
        return null;
    }

    public void setManager(StAXManager manager) {
        this._manager = manager;
    }

    public void setEncoding(String encoding) {
        this._encoding = encoding;
    }

    public void writeOctets(byte[] b, int start, int len) throws XMLStreamException {
        try {
            if (len == 0) {
                return;
            }
            this.encodeTerminationAndCurrentElement(true);
            this.encodeCIIOctetAlgorithmData(1, b, start, len);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    protected void encodeTerminationAndCurrentElement(boolean terminateAfter) throws XMLStreamException {
        try {
            this.encodeTermination();
            if (this._inStartElement) {
                int i;
                this._b = 0;
                if (this._attributesArrayIndex > 0) {
                    this._b |= 0x40;
                }
                if (this._namespacesArrayIndex > 0) {
                    this.write(this._b | 0x38);
                    i = 0;
                    while (i < this._namespacesArrayIndex) {
                        this.encodeNamespaceAttribute(this._namespacesArray[i++], this._namespacesArray[i++]);
                    }
                    this._namespacesArrayIndex = 0;
                    this.write(240);
                    this._b = 0;
                }
                if (this._currentPrefix.length() == 0) {
                    if (this._currentUri.length() == 0) {
                        this._currentUri = this._nsContext.getNamespaceURI("");
                    } else {
                        String tmpPrefix = this.getPrefix(this._currentUri);
                        if (tmpPrefix != null) {
                            this._currentPrefix = tmpPrefix;
                        }
                    }
                }
                this.encodeElementQualifiedNameOnThirdBit(this._currentUri, this._currentPrefix, this._currentLocalName);
                i = 0;
                while (i < this._attributesArrayIndex) {
                    this.encodeAttributeQualifiedNameOnSecondBit(this._attributesArray[i++], this._attributesArray[i++], this._attributesArray[i++]);
                    String value = this._attributesArray[i];
                    this._attributesArray[i++] = null;
                    boolean addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
                    this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, false);
                    this._b = 240;
                    this._terminate = true;
                }
                this._attributesArrayIndex = 0;
                this._inStartElement = false;
                if (this._isEmptyElement) {
                    this.encodeElementTermination();
                    if (this._nsSupportContextStack[this._stackCount--]) {
                        this._nsContext.popContext();
                    }
                    this._isEmptyElement = false;
                }
                if (terminateAfter) {
                    this.encodeTermination();
                }
            }
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public final void initiateLowLevelWriting() throws XMLStreamException {
        this.encodeTerminationAndCurrentElement(false);
    }

    @Override
    public final int getNextElementIndex() {
        return this._v.elementName.getNextIndex();
    }

    @Override
    public final int getNextAttributeIndex() {
        return this._v.attributeName.getNextIndex();
    }

    @Override
    public final int getLocalNameIndex() {
        return this._v.localName.getIndex();
    }

    @Override
    public final int getNextLocalNameIndex() {
        return this._v.localName.getNextIndex();
    }

    @Override
    public final void writeLowLevelTerminationAndMark() throws IOException {
        this.encodeTermination();
        this.mark();
    }

    @Override
    public final void writeLowLevelStartElementIndexed(int type, int index) throws IOException {
        this._b = type;
        this.encodeNonZeroIntegerOnThirdBit(index);
    }

    @Override
    public final boolean writeLowLevelStartElement(int type, String prefix, String localName, String namespaceURI) throws IOException {
        boolean isIndexed = this.encodeElement(type, namespaceURI, prefix, localName);
        if (!isIndexed) {
            this.encodeLiteral(type | 0x3C, namespaceURI, prefix, localName);
        }
        return isIndexed;
    }

    @Override
    public final void writeLowLevelStartNamespaces() throws IOException {
        this.write(56);
    }

    @Override
    public final void writeLowLevelNamespace(String prefix, String namespaceName) throws IOException {
        this.encodeNamespaceAttribute(prefix, namespaceName);
    }

    @Override
    public final void writeLowLevelEndNamespaces() throws IOException {
        this.write(240);
    }

    @Override
    public final void writeLowLevelStartAttributes() throws IOException {
        if (this.hasMark()) {
            int n = this._markIndex;
            this._octetBuffer[n] = (byte)(this._octetBuffer[n] | 0x40);
            this.resetMark();
        }
    }

    @Override
    public final void writeLowLevelAttributeIndexed(int index) throws IOException {
        this.encodeNonZeroIntegerOnSecondBitFirstBitZero(index);
    }

    @Override
    public final boolean writeLowLevelAttribute(String prefix, String namespaceURI, String localName) throws IOException {
        boolean isIndexed = this.encodeAttribute(namespaceURI, prefix, localName);
        if (!isIndexed) {
            this.encodeLiteral(120, namespaceURI, prefix, localName);
        }
        return isIndexed;
    }

    @Override
    public final void writeLowLevelAttributeValue(String value) throws IOException {
        boolean addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
        this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, false);
    }

    @Override
    public final void writeLowLevelStartNameLiteral(int type, String prefix, byte[] utf8LocalName, String namespaceURI) throws IOException {
        this.encodeLiteralHeader(type, namespaceURI, prefix);
        this.encodeNonZeroOctetStringLengthOnSecondBit(utf8LocalName.length);
        this.write(utf8LocalName, 0, utf8LocalName.length);
    }

    @Override
    public final void writeLowLevelStartNameLiteral(int type, String prefix, int localNameIndex, String namespaceURI) throws IOException {
        this.encodeLiteralHeader(type, namespaceURI, prefix);
        this.encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
    }

    @Override
    public final void writeLowLevelEndStartElement() throws IOException {
        if (this.hasMark()) {
            this.resetMark();
        } else {
            this._b = 240;
            this._terminate = true;
        }
    }

    @Override
    public final void writeLowLevelEndElement() throws IOException {
        this.encodeElementTermination();
    }

    @Override
    public final void writeLowLevelText(char[] text, int length) throws IOException {
        if (length == 0) {
            return;
        }
        this.encodeTermination();
        this.encodeCharacters(text, 0, length);
    }

    @Override
    public final void writeLowLevelText(String text) throws IOException {
        int length = text.length();
        if (length == 0) {
            return;
        }
        this.encodeTermination();
        if (length < this._charBuffer.length) {
            text.getChars(0, length, this._charBuffer, 0);
            this.encodeCharacters(this._charBuffer, 0, length);
        } else {
            char[] ch = text.toCharArray();
            this.encodeCharactersNoClone(ch, 0, length);
        }
    }

    @Override
    public final void writeLowLevelOctets(byte[] octets, int length) throws IOException {
        if (length == 0) {
            return;
        }
        this.encodeTermination();
        this.encodeCIIOctetAlgorithmData(1, octets, 0, length);
    }

    private boolean encodeElement(int type, String namespaceURI, String prefix, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(localName);
        for (int i = 0; i < entry._valueIndex; ++i) {
            QualifiedName name = entry._value[i];
            if (prefix != name.prefix && !prefix.equals(name.prefix) || namespaceURI != name.namespaceName && !namespaceURI.equals(name.namespaceName)) continue;
            this._b = type;
            this.encodeNonZeroIntegerOnThirdBit(name.index);
            return true;
        }
        entry.addQualifiedName(new QualifiedName(prefix, namespaceURI, localName, "", this._v.elementName.getNextIndex()));
        return false;
    }

    private boolean encodeAttribute(String namespaceURI, String prefix, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(localName);
        for (int i = 0; i < entry._valueIndex; ++i) {
            QualifiedName name = entry._value[i];
            if (prefix != name.prefix && !prefix.equals(name.prefix) || namespaceURI != name.namespaceName && !namespaceURI.equals(name.namespaceName)) continue;
            this.encodeNonZeroIntegerOnSecondBitFirstBitZero(name.index);
            return true;
        }
        entry.addQualifiedName(new QualifiedName(prefix, namespaceURI, localName, "", this._v.attributeName.getNextIndex()));
        return false;
    }

    private void encodeLiteralHeader(int type, String namespaceURI, String prefix) throws IOException {
        if (namespaceURI != "") {
            type |= 1;
            if (prefix != "") {
                type |= 2;
            }
            this.write(type);
            if (prefix != "") {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(this._v.prefix.get(prefix));
            }
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(this._v.namespaceName.get(namespaceURI));
        } else {
            this.write(type);
        }
    }

    private void encodeLiteral(int type, String namespaceURI, String prefix, String localName) throws IOException {
        this.encodeLiteralHeader(type, namespaceURI, prefix);
        int localNameIndex = this._v.localName.obtainIndex(localName);
        if (localNameIndex == -1) {
            this.encodeNonEmptyOctetStringOnSecondBit(localName);
        } else {
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
        }
    }
}

