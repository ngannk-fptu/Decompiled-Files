/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import java.io.PrintStream;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import org.apache.xmlbeans.impl.soap.MimeHeader;
import org.apache.xmlbeans.impl.soap.SOAPEnvelope;
import org.apache.xmlbeans.impl.soap.SOAPPart;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.SoapPartDocXobj;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.w3c.dom.UserDataHandler;

class SoapPartDom
extends SOAPPart
implements DomImpl.Dom,
Document,
NodeList {
    SoapPartDocXobj _docXobj;

    SoapPartDom(SoapPartDocXobj docXobj) {
        this._docXobj = docXobj;
    }

    @Override
    public int nodeType() {
        return 9;
    }

    @Override
    public Locale locale() {
        return this._docXobj._locale;
    }

    @Override
    public Cur tempCur() {
        return this._docXobj.tempCur();
    }

    @Override
    public QName getQName() {
        return this._docXobj._name;
    }

    @Override
    public void dump() {
        this.dump(System.out);
    }

    @Override
    public void dump(PrintStream o) {
        this._docXobj.dump(o);
    }

    @Override
    public void dump(PrintStream o, Object ref) {
        this._docXobj.dump(o, ref);
    }

    public String name() {
        return "#document";
    }

    @Override
    public Node appendChild(Node newChild) {
        return DomImpl._node_appendChild(this, newChild);
    }

    @Override
    public Node cloneNode(boolean deep) {
        return DomImpl._node_cloneNode(this, deep);
    }

    @Override
    public NamedNodeMap getAttributes() {
        return null;
    }

    @Override
    public NodeList getChildNodes() {
        return this;
    }

    @Override
    public Node getParentNode() {
        return DomImpl._node_getParentNode(this);
    }

    @Override
    public Node removeChild(Node oldChild) {
        return DomImpl._node_removeChild(this, oldChild);
    }

    @Override
    public Node getFirstChild() {
        return DomImpl._node_getFirstChild(this);
    }

    @Override
    public Node getLastChild() {
        return DomImpl._node_getLastChild(this);
    }

    @Override
    public String getLocalName() {
        return DomImpl._node_getLocalName(this);
    }

    @Override
    public String getNamespaceURI() {
        return DomImpl._node_getNamespaceURI(this);
    }

    @Override
    public Node getNextSibling() {
        return DomImpl._node_getNextSibling(this);
    }

    @Override
    public String getNodeName() {
        return DomImpl._node_getNodeName(this);
    }

    @Override
    public short getNodeType() {
        return DomImpl._node_getNodeType(this);
    }

    @Override
    public String getNodeValue() {
        return DomImpl._node_getNodeValue(this);
    }

    @Override
    public Document getOwnerDocument() {
        return DomImpl._node_getOwnerDocument(this);
    }

    @Override
    public String getPrefix() {
        return DomImpl._node_getPrefix(this);
    }

    @Override
    public Node getPreviousSibling() {
        return DomImpl._node_getPreviousSibling(this);
    }

    @Override
    public boolean hasAttributes() {
        return DomImpl._node_hasAttributes(this);
    }

    @Override
    public boolean hasChildNodes() {
        return DomImpl._node_hasChildNodes(this);
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) {
        return DomImpl._node_insertBefore(this, newChild, refChild);
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return DomImpl._node_isSupported(this, feature, version);
    }

    @Override
    public void normalize() {
        DomImpl._node_normalize(this);
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) {
        return DomImpl._node_replaceChild(this, newChild, oldChild);
    }

    @Override
    public void setNodeValue(String nodeValue) {
        DomImpl._node_setNodeValue(this, nodeValue);
    }

    @Override
    public void setPrefix(String prefix) {
        DomImpl._node_setPrefix(this, prefix);
    }

    @Override
    public Object getUserData(String key) {
        return DomImpl._node_getUserData(this, key);
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return DomImpl._node_setUserData(this, key, data, handler);
    }

    @Override
    public Object getFeature(String feature, String version) {
        return DomImpl._node_getFeature(this, feature, version);
    }

    @Override
    public boolean isEqualNode(Node arg) {
        return DomImpl._node_isEqualNode(this, arg);
    }

    @Override
    public boolean isSameNode(Node arg) {
        return DomImpl._node_isSameNode(this, arg);
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        return DomImpl._node_lookupNamespaceURI(this, prefix);
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        return DomImpl._node_lookupPrefix(this, namespaceURI);
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return DomImpl._node_isDefaultNamespace(this, namespaceURI);
    }

    @Override
    public void setTextContent(String textContent) {
        DomImpl._node_setTextContent(this, textContent);
    }

    @Override
    public String getTextContent() {
        return DomImpl._node_getTextContent(this);
    }

    @Override
    public short compareDocumentPosition(Node other) {
        return DomImpl._node_compareDocumentPosition(this, other);
    }

    @Override
    public String getBaseURI() {
        return DomImpl._node_getBaseURI(this);
    }

    @Override
    public Node adoptNode(Node source) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public String getDocumentURI() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public DOMConfiguration getDomConfig() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public String getInputEncoding() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public boolean getStrictErrorChecking() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public String getXmlEncoding() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public boolean getXmlStandalone() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public String getXmlVersion() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public void normalizeDocument() {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public Node renameNode(Node n, String namespaceURI, String qualifiedName) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public void setDocumentURI(String documentURI) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public void setStrictErrorChecking(boolean strictErrorChecking) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public void setXmlStandalone(boolean xmlStandalone) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public void setXmlVersion(String xmlVersion) {
        throw new RuntimeException("DOM Level 3 Not implemented");
    }

    @Override
    public Attr createAttribute(String name) {
        return DomImpl._document_createAttribute(this, name);
    }

    @Override
    public Attr createAttributeNS(String namespaceURI, String qualifiedName) {
        return DomImpl._document_createAttributeNS(this, namespaceURI, qualifiedName);
    }

    @Override
    public CDATASection createCDATASection(String data) {
        return DomImpl._document_createCDATASection(this, data);
    }

    @Override
    public Comment createComment(String data) {
        return DomImpl._document_createComment(this, data);
    }

    @Override
    public DocumentFragment createDocumentFragment() {
        return DomImpl._document_createDocumentFragment(this);
    }

    @Override
    public Element createElement(String tagName) {
        return DomImpl._document_createElement(this, tagName);
    }

    @Override
    public Element createElementNS(String namespaceURI, String qualifiedName) {
        return DomImpl._document_createElementNS(this, namespaceURI, qualifiedName);
    }

    @Override
    public EntityReference createEntityReference(String name) {
        return DomImpl._document_createEntityReference(this, name);
    }

    @Override
    public ProcessingInstruction createProcessingInstruction(String target, String data) {
        return DomImpl._document_createProcessingInstruction(this, target, data);
    }

    @Override
    public Text createTextNode(String data) {
        return DomImpl._document_createTextNode(this, data);
    }

    @Override
    public DocumentType getDoctype() {
        return DomImpl._document_getDoctype(this);
    }

    @Override
    public Element getDocumentElement() {
        return DomImpl._document_getDocumentElement(this);
    }

    @Override
    public Element getElementById(String elementId) {
        return DomImpl._document_getElementById(this, elementId);
    }

    @Override
    public NodeList getElementsByTagName(String tagname) {
        return DomImpl._document_getElementsByTagName(this, tagname);
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return DomImpl._document_getElementsByTagNameNS(this, namespaceURI, localName);
    }

    @Override
    public DOMImplementation getImplementation() {
        return DomImpl._document_getImplementation(this);
    }

    @Override
    public Node importNode(Node importedNode, boolean deep) {
        return DomImpl._document_importNode(this, importedNode, deep);
    }

    @Override
    public int getLength() {
        return DomImpl._childNodes_getLength(this);
    }

    @Override
    public Node item(int i) {
        return DomImpl._childNodes_item(this, i);
    }

    @Override
    public void removeAllMimeHeaders() {
        DomImpl._soapPart_removeAllMimeHeaders(this);
    }

    @Override
    public void removeMimeHeader(String name) {
        DomImpl._soapPart_removeMimeHeader(this, name);
    }

    @Override
    public Iterator<MimeHeader> getAllMimeHeaders() {
        return DomImpl._soapPart_getAllMimeHeaders(this);
    }

    @Override
    public SOAPEnvelope getEnvelope() {
        return DomImpl._soapPart_getEnvelope(this);
    }

    @Override
    public Source getContent() {
        return DomImpl._soapPart_getContent(this);
    }

    @Override
    public void setContent(Source source) {
        DomImpl._soapPart_setContent(this, source);
    }

    @Override
    public String[] getMimeHeader(String name) {
        return DomImpl._soapPart_getMimeHeader(this, name);
    }

    @Override
    public void addMimeHeader(String name, String value) {
        DomImpl._soapPart_addMimeHeader(this, name, value);
    }

    @Override
    public void setMimeHeader(String name, String value) {
        DomImpl._soapPart_setMimeHeader(this, name, value);
    }

    @Override
    public Iterator<MimeHeader> getMatchingMimeHeaders(String[] names) {
        return DomImpl._soapPart_getMatchingMimeHeaders(this, names);
    }

    @Override
    public Iterator<MimeHeader> getNonMatchingMimeHeaders(String[] names) {
        return DomImpl._soapPart_getNonMatchingMimeHeaders(this, names);
    }

    @Override
    public boolean nodeCanHavePrefixUri() {
        return true;
    }
}

