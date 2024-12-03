/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.sax;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.Encoder;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.util.LocalNameQualifiedNamesMap;
import java.io.IOException;
import org.jvnet.fastinfoset.FastInfosetException;
import org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import org.jvnet.fastinfoset.sax.FastInfosetWriter;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class SAXDocumentSerializer
extends Encoder
implements FastInfosetWriter {
    protected boolean _elementHasNamespaces = false;
    protected boolean _charactersAsCDATA = false;

    protected SAXDocumentSerializer(boolean v) {
        super(v);
    }

    public SAXDocumentSerializer() {
    }

    @Override
    public void reset() {
        super.reset();
        this._elementHasNamespaces = false;
        this._charactersAsCDATA = false;
    }

    @Override
    public final void startDocument() throws SAXException {
        try {
            this.reset();
            this.encodeHeader(false);
            this.encodeInitialVocabulary();
        }
        catch (IOException e) {
            throw new SAXException("startDocument", e);
        }
    }

    @Override
    public final void endDocument() throws SAXException {
        try {
            this.encodeDocumentTermination();
        }
        catch (IOException e) {
            throw new SAXException("endDocument", e);
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        try {
            if (!this._elementHasNamespaces) {
                this.encodeTermination();
                this.mark();
                this._elementHasNamespaces = true;
                this.write(56);
            }
            this.encodeNamespaceAttribute(prefix, uri);
        }
        catch (IOException e) {
            throw new SAXException("startElement", e);
        }
    }

    @Override
    public final void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        int attributeCount = atts != null && atts.getLength() > 0 ? this.countAttributes(atts) : 0;
        try {
            if (this._elementHasNamespaces) {
                this._elementHasNamespaces = false;
                if (attributeCount > 0) {
                    int n = this._markIndex;
                    this._octetBuffer[n] = (byte)(this._octetBuffer[n] | 0x40);
                }
                this.resetMark();
                this.write(240);
                this._b = 0;
            } else {
                this.encodeTermination();
                this._b = 0;
                if (attributeCount > 0) {
                    this._b |= 0x40;
                }
            }
            this.encodeElement(namespaceURI, qName, localName);
            if (attributeCount > 0) {
                this.encodeAttributes(atts);
            }
        }
        catch (IOException e) {
            throw new SAXException("startElement", e);
        }
        catch (FastInfosetException e) {
            throw new SAXException("startElement", e);
        }
    }

    @Override
    public final void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            this.encodeElementTermination();
        }
        catch (IOException e) {
            throw new SAXException("endElement", e);
        }
    }

    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        if (this.getIgnoreWhiteSpaceTextContent() && SAXDocumentSerializer.isWhiteSpace(ch, start, length)) {
            return;
        }
        try {
            this.encodeTermination();
            if (!this._charactersAsCDATA) {
                this.encodeCharacters(ch, start, length);
            } else {
                this.encodeCIIBuiltInAlgorithmDataAsCDATA(ch, start, length);
            }
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (this.getIgnoreWhiteSpaceTextContent()) {
            return;
        }
        this.characters(ch, start, length);
    }

    @Override
    public final void processingInstruction(String target, String data) throws SAXException {
        try {
            if (this.getIgnoreProcesingInstructions()) {
                return;
            }
            if (target.length() == 0) {
                throw new SAXException(CommonResourceBundle.getInstance().getString("message.processingInstructionTargetIsEmpty"));
            }
            this.encodeTermination();
            this.encodeProcessingInstruction(target, data);
        }
        catch (IOException e) {
            throw new SAXException("processingInstruction", e);
        }
    }

    @Override
    public final void setDocumentLocator(Locator locator) {
    }

    @Override
    public final void skippedEntity(String name) throws SAXException {
    }

    @Override
    public final void comment(char[] ch, int start, int length) throws SAXException {
        try {
            if (this.getIgnoreComments()) {
                return;
            }
            this.encodeTermination();
            this.encodeComment(ch, start, length);
        }
        catch (IOException e) {
            throw new SAXException("startElement", e);
        }
    }

    @Override
    public final void startCDATA() throws SAXException {
        this._charactersAsCDATA = true;
    }

    @Override
    public final void endCDATA() throws SAXException {
        this._charactersAsCDATA = false;
    }

    @Override
    public final void startDTD(String name, String publicId, String systemId) throws SAXException {
        if (this.getIgnoreDTD()) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeDocumentTypeDeclaration(publicId, systemId);
            this.encodeElementTermination();
        }
        catch (IOException e) {
            throw new SAXException("startDTD", e);
        }
    }

    @Override
    public final void endDTD() throws SAXException {
    }

    @Override
    public final void startEntity(String name) throws SAXException {
    }

    @Override
    public final void endEntity(String name) throws SAXException {
    }

    @Override
    public final void octets(String URI2, int id, byte[] b, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeNonIdentifyingStringOnThirdBit(URI2, id, b, start, length);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void object(String URI2, int id, Object data) throws SAXException {
        try {
            this.encodeTermination();
            this.encodeNonIdentifyingStringOnThirdBit(URI2, id, data);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void bytes(byte[] b, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIOctetAlgorithmData(1, b, start, length);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void shorts(short[] s, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(2, s, start, length);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void ints(int[] i, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(3, i, start, length);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void longs(long[] l, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(4, l, start, length);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void booleans(boolean[] b, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(5, b, start, length);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void floats(float[] f, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(6, f, start, length);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public final void doubles(double[] d, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(7, d, start, length);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void uuids(long[] msblsb, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            this.encodeCIIBuiltInAlgorithmData(8, msblsb, start, length);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void numericCharacters(char[] ch, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
            this.encodeNumericFourBitCharacters(ch, start, length, addToTable);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void dateTimeCharacters(char[] ch, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
            this.encodeDateTimeFourBitCharacters(ch, start, length, addToTable);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void alphabetCharacters(String alphabet, char[] ch, int start, int length) throws SAXException {
        if (length <= 0) {
            return;
        }
        try {
            this.encodeTermination();
            boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
            this.encodeAlphabetCharacters(alphabet, ch, start, length, addToTable);
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length, boolean index) throws SAXException {
        if (length <= 0) {
            return;
        }
        if (this.getIgnoreWhiteSpaceTextContent() && SAXDocumentSerializer.isWhiteSpace(ch, start, length)) {
            return;
        }
        try {
            this.encodeTermination();
            if (!this._charactersAsCDATA) {
                this.encodeNonIdentifyingStringOnThirdBit(ch, start, length, this._v.characterContentChunk, index, true);
            } else {
                this.encodeCIIBuiltInAlgorithmDataAsCDATA(ch, start, length);
            }
        }
        catch (IOException e) {
            throw new SAXException(e);
        }
        catch (FastInfosetException e) {
            throw new SAXException(e);
        }
    }

    protected final int countAttributes(Attributes atts) {
        int count = 0;
        for (int i = 0; i < atts.getLength(); ++i) {
            String uri = atts.getURI(i);
            if (uri == "http://www.w3.org/2000/xmlns/" || uri.equals("http://www.w3.org/2000/xmlns/")) continue;
            ++count;
        }
        return count;
    }

    protected void encodeAttributes(Attributes atts) throws IOException, FastInfosetException {
        if (atts instanceof EncodingAlgorithmAttributes) {
            EncodingAlgorithmAttributes eAtts = (EncodingAlgorithmAttributes)atts;
            for (int i = 0; i < eAtts.getLength(); ++i) {
                if (!this.encodeAttribute(atts.getURI(i), atts.getQName(i), atts.getLocalName(i))) continue;
                Object data = eAtts.getAlgorithmData(i);
                if (data == null) {
                    String value = eAtts.getValue(i);
                    boolean addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
                    boolean mustBeAddedToTable = eAtts.getToIndex(i);
                    String alphabet = eAtts.getAlpababet(i);
                    if (alphabet == null) {
                        this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, mustBeAddedToTable);
                        continue;
                    }
                    if (alphabet == "0123456789-:TZ ") {
                        this.encodeDateTimeNonIdentifyingStringOnFirstBit(value, addToTable, mustBeAddedToTable);
                        continue;
                    }
                    if (alphabet == "0123456789-+.E ") {
                        this.encodeNumericNonIdentifyingStringOnFirstBit(value, addToTable, mustBeAddedToTable);
                        continue;
                    }
                    this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, mustBeAddedToTable);
                    continue;
                }
                this.encodeNonIdentifyingStringOnFirstBit(eAtts.getAlgorithmURI(i), eAtts.getAlgorithmIndex(i), data);
            }
        } else {
            for (int i = 0; i < atts.getLength(); ++i) {
                if (!this.encodeAttribute(atts.getURI(i), atts.getQName(i), atts.getLocalName(i))) continue;
                String value = atts.getValue(i);
                boolean addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
                this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, false);
            }
        }
        this._b = 240;
        this._terminate = true;
    }

    protected void encodeElement(String namespaceURI, String qName, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(qName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; ++i) {
                QualifiedName n = names[i];
                if (namespaceURI != n.namespaceName && !namespaceURI.equals(n.namespaceName)) continue;
                this.encodeNonZeroIntegerOnThirdBit(names[i].index);
                return;
            }
        }
        this.encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, SAXDocumentSerializer.getPrefixFromQualifiedName(qName), localName, entry);
    }

    protected boolean encodeAttribute(String namespaceURI, String qName, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(qName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; ++i) {
                if (namespaceURI != names[i].namespaceName && !namespaceURI.equals(names[i].namespaceName)) continue;
                this.encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
                return true;
            }
        }
        return this.encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, SAXDocumentSerializer.getPrefixFromQualifiedName(qName), localName, entry);
    }
}

