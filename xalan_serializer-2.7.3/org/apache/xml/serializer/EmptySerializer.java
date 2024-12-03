/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;
import org.apache.xml.serializer.DOMSerializer;
import org.apache.xml.serializer.NamespaceMappings;
import org.apache.xml.serializer.SerializationHandler;
import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class EmptySerializer
implements SerializationHandler {
    protected static final String ERR = "EmptySerializer method not over-ridden";

    protected void couldThrowIOException() throws IOException {
    }

    protected void couldThrowSAXException() throws SAXException {
    }

    protected void couldThrowSAXException(char[] chars, int off, int len) throws SAXException {
    }

    protected void couldThrowSAXException(String elemQName) throws SAXException {
    }

    protected void couldThrowException() throws Exception {
    }

    void aMethodIsCalled() {
    }

    @Override
    public ContentHandler asContentHandler() throws IOException {
        this.couldThrowIOException();
        return null;
    }

    @Override
    public void setContentHandler(ContentHandler ch) {
        this.aMethodIsCalled();
    }

    @Override
    public void close() {
        this.aMethodIsCalled();
    }

    @Override
    public Properties getOutputFormat() {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public OutputStream getOutputStream() {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public Writer getWriter() {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public boolean reset() {
        this.aMethodIsCalled();
        return false;
    }

    @Override
    public void serialize(Node node) throws IOException {
        this.couldThrowIOException();
    }

    @Override
    public void setCdataSectionElements(Vector URI_and_localNames) {
        this.aMethodIsCalled();
    }

    @Override
    public boolean setEscaping(boolean escape) throws SAXException {
        this.couldThrowSAXException();
        return false;
    }

    @Override
    public void setIndent(boolean indent) {
        this.aMethodIsCalled();
    }

    @Override
    public void setIndentAmount(int spaces) {
        this.aMethodIsCalled();
    }

    @Override
    public void setOutputFormat(Properties format) {
        this.aMethodIsCalled();
    }

    @Override
    public void setOutputStream(OutputStream output) {
        this.aMethodIsCalled();
    }

    @Override
    public void setVersion(String version) {
        this.aMethodIsCalled();
    }

    @Override
    public void setWriter(Writer writer) {
        this.aMethodIsCalled();
    }

    @Override
    public void setTransformer(Transformer transformer) {
        this.aMethodIsCalled();
    }

    @Override
    public Transformer getTransformer() {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public void flushPending() throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void addAttribute(String uri, String localName, String rawName, String type, String value, boolean XSLAttribute) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void addAttributes(Attributes atts) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void addAttribute(String name, String value) {
        this.aMethodIsCalled();
    }

    @Override
    public void characters(String chars) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void endElement(String elemName) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void startDocument() throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void startElement(String uri, String localName, String qName) throws SAXException {
        this.couldThrowSAXException(qName);
    }

    @Override
    public void startElement(String qName) throws SAXException {
        this.couldThrowSAXException(qName);
    }

    @Override
    public void namespaceAfterStartElement(String uri, String prefix) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public boolean startPrefixMapping(String prefix, String uri, boolean shouldFlush) throws SAXException {
        this.couldThrowSAXException();
        return false;
    }

    @Override
    public void entityReference(String entityName) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public NamespaceMappings getNamespaceMappings() {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public String getPrefix(String uri) {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public String getNamespaceURI(String name, boolean isElement) {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public String getNamespaceURIFromPrefix(String prefix) {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public void setDocumentLocator(Locator arg0) {
        this.aMethodIsCalled();
    }

    @Override
    public void endDocument() throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void startPrefixMapping(String arg0, String arg1) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void endPrefixMapping(String arg0) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void endElement(String arg0, String arg1, String arg2) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
        this.couldThrowSAXException(arg0, arg1, arg2);
    }

    @Override
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void processingInstruction(String arg0, String arg1) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void skippedEntity(String arg0) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void comment(String comment) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void startDTD(String arg0, String arg1, String arg2) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void endDTD() throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void startEntity(String arg0) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void endEntity(String arg0) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void startCDATA() throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void endCDATA() throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void comment(char[] arg0, int arg1, int arg2) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public String getDoctypePublic() {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public String getDoctypeSystem() {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public String getEncoding() {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public boolean getIndent() {
        this.aMethodIsCalled();
        return false;
    }

    @Override
    public int getIndentAmount() {
        this.aMethodIsCalled();
        return 0;
    }

    @Override
    public String getMediaType() {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public boolean getOmitXMLDeclaration() {
        this.aMethodIsCalled();
        return false;
    }

    @Override
    public String getStandalone() {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public String getVersion() {
        this.aMethodIsCalled();
        return null;
    }

    public void setCdataSectionElements(Hashtable h) throws Exception {
        this.couldThrowException();
    }

    @Override
    public void setDoctype(String system, String pub) {
        this.aMethodIsCalled();
    }

    @Override
    public void setDoctypePublic(String doctype) {
        this.aMethodIsCalled();
    }

    @Override
    public void setDoctypeSystem(String doctype) {
        this.aMethodIsCalled();
    }

    @Override
    public void setEncoding(String encoding) {
        this.aMethodIsCalled();
    }

    @Override
    public void setMediaType(String mediatype) {
        this.aMethodIsCalled();
    }

    @Override
    public void setOmitXMLDeclaration(boolean b) {
        this.aMethodIsCalled();
    }

    @Override
    public void setStandalone(String standalone) {
        this.aMethodIsCalled();
    }

    @Override
    public void elementDecl(String arg0, String arg1) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void attributeDecl(String arg0, String arg1, String arg2, String arg3, String arg4) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void internalEntityDecl(String arg0, String arg1) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void externalEntityDecl(String arg0, String arg1, String arg2) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void warning(SAXParseException arg0) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void error(SAXParseException arg0) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void fatalError(SAXParseException arg0) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public DOMSerializer asDOMSerializer() throws IOException {
        this.couldThrowIOException();
        return null;
    }

    @Override
    public void setNamespaceMappings(NamespaceMappings mappings) {
        this.aMethodIsCalled();
    }

    @Override
    public void setSourceLocator(SourceLocator locator) {
        this.aMethodIsCalled();
    }

    @Override
    public void addUniqueAttribute(String name, String value, int flags) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void characters(Node node) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void addXSLAttribute(String qName, String value, String uri) {
        this.aMethodIsCalled();
    }

    @Override
    public void addAttribute(String uri, String localName, String rawName, String type, String value) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void notationDecl(String arg0, String arg1, String arg2) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void unparsedEntityDecl(String arg0, String arg1, String arg2, String arg3) throws SAXException {
        this.couldThrowSAXException();
    }

    @Override
    public void setDTDEntityExpansion(boolean expand) {
        this.aMethodIsCalled();
    }

    @Override
    public String getOutputProperty(String name) {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public String getOutputPropertyDefault(String name) {
        this.aMethodIsCalled();
        return null;
    }

    @Override
    public void setOutputProperty(String name, String val) {
        this.aMethodIsCalled();
    }

    @Override
    public void setOutputPropertyDefault(String name, String val) {
        this.aMethodIsCalled();
    }

    @Override
    public Object asDOM3Serializer() throws IOException {
        this.couldThrowIOException();
        return null;
    }
}

