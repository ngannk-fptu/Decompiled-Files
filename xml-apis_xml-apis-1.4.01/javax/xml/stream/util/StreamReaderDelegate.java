/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream.util;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class StreamReaderDelegate
implements XMLStreamReader {
    private XMLStreamReader reader;

    public StreamReaderDelegate() {
    }

    public StreamReaderDelegate(XMLStreamReader xMLStreamReader) {
        this.reader = xMLStreamReader;
    }

    public void setParent(XMLStreamReader xMLStreamReader) {
        this.reader = xMLStreamReader;
    }

    public XMLStreamReader getParent() {
        return this.reader;
    }

    public int next() throws XMLStreamException {
        return this.reader.next();
    }

    public int nextTag() throws XMLStreamException {
        return this.reader.nextTag();
    }

    public String getElementText() throws XMLStreamException {
        return this.reader.getElementText();
    }

    public void require(int n, String string, String string2) throws XMLStreamException {
        this.reader.require(n, string, string2);
    }

    public boolean hasNext() throws XMLStreamException {
        return this.reader.hasNext();
    }

    public void close() throws XMLStreamException {
        this.reader.close();
    }

    public String getNamespaceURI(String string) {
        return this.reader.getNamespaceURI(string);
    }

    public NamespaceContext getNamespaceContext() {
        return this.reader.getNamespaceContext();
    }

    public boolean isStartElement() {
        return this.reader.isStartElement();
    }

    public boolean isEndElement() {
        return this.reader.isEndElement();
    }

    public boolean isCharacters() {
        return this.reader.isCharacters();
    }

    public boolean isWhiteSpace() {
        return this.reader.isWhiteSpace();
    }

    public String getAttributeValue(String string, String string2) {
        return this.reader.getAttributeValue(string, string2);
    }

    public int getAttributeCount() {
        return this.reader.getAttributeCount();
    }

    public QName getAttributeName(int n) {
        return this.reader.getAttributeName(n);
    }

    public String getAttributePrefix(int n) {
        return this.reader.getAttributePrefix(n);
    }

    public String getAttributeNamespace(int n) {
        return this.reader.getAttributeNamespace(n);
    }

    public String getAttributeLocalName(int n) {
        return this.reader.getAttributeLocalName(n);
    }

    public String getAttributeType(int n) {
        return this.reader.getAttributeType(n);
    }

    public String getAttributeValue(int n) {
        return this.reader.getAttributeValue(n);
    }

    public boolean isAttributeSpecified(int n) {
        return this.reader.isAttributeSpecified(n);
    }

    public int getNamespaceCount() {
        return this.reader.getNamespaceCount();
    }

    public String getNamespacePrefix(int n) {
        return this.reader.getNamespacePrefix(n);
    }

    public String getNamespaceURI(int n) {
        return this.reader.getNamespaceURI(n);
    }

    public int getEventType() {
        return this.reader.getEventType();
    }

    public String getText() {
        return this.reader.getText();
    }

    public int getTextCharacters(int n, char[] cArray, int n2, int n3) throws XMLStreamException {
        return this.reader.getTextCharacters(n, cArray, n2, n3);
    }

    public char[] getTextCharacters() {
        return this.reader.getTextCharacters();
    }

    public int getTextStart() {
        return this.reader.getTextStart();
    }

    public int getTextLength() {
        return this.reader.getTextLength();
    }

    public String getEncoding() {
        return this.reader.getEncoding();
    }

    public boolean hasText() {
        return this.reader.hasText();
    }

    public Location getLocation() {
        return this.reader.getLocation();
    }

    public QName getName() {
        return this.reader.getName();
    }

    public String getLocalName() {
        return this.reader.getLocalName();
    }

    public boolean hasName() {
        return this.reader.hasName();
    }

    public String getNamespaceURI() {
        return this.reader.getNamespaceURI();
    }

    public String getPrefix() {
        return this.reader.getPrefix();
    }

    public String getVersion() {
        return this.reader.getVersion();
    }

    public boolean isStandalone() {
        return this.reader.isStandalone();
    }

    public boolean standaloneSet() {
        return this.reader.standaloneSet();
    }

    public String getCharacterEncodingScheme() {
        return this.reader.getCharacterEncodingScheme();
    }

    public String getPITarget() {
        return this.reader.getPITarget();
    }

    public String getPIData() {
        return this.reader.getPIData();
    }

    public Object getProperty(String string) throws IllegalArgumentException {
        return this.reader.getProperty(string);
    }
}

