/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.messaging.saaj.soap.impl.NodeListImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class SOAPDocumentFragment
implements DocumentFragment {
    private SOAPDocumentImpl soapDocument;
    private DocumentFragment documentFragment;

    public SOAPDocumentFragment(SOAPDocumentImpl ownerDoc) {
        this.soapDocument = ownerDoc;
        this.documentFragment = this.soapDocument.getDomDocument().createDocumentFragment();
    }

    public SOAPDocumentFragment(SOAPDocumentImpl soapDocument, DocumentFragment documentFragment) {
        this.soapDocument = soapDocument;
        this.documentFragment = documentFragment;
    }

    public SOAPDocumentFragment() {
    }

    @Override
    public boolean hasAttributes() {
        return this.documentFragment.hasAttributes();
    }

    @Override
    public boolean isSameNode(Node other) {
        return this.documentFragment.isSameNode(this.getDomNode(other));
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        return this.documentFragment.lookupNamespaceURI(prefix);
    }

    @Override
    public Node getParentNode() {
        return this.soapDocument.findIfPresent(this.documentFragment.getParentNode());
    }

    @Override
    public Node getFirstChild() {
        return this.soapDocument.findIfPresent(this.documentFragment.getFirstChild());
    }

    @Override
    public Object getUserData(String key) {
        return this.documentFragment.getUserData(key);
    }

    @Override
    public String getTextContent() throws DOMException {
        return this.documentFragment.getTextContent();
    }

    @Override
    public short getNodeType() {
        return this.documentFragment.getNodeType();
    }

    public Node getDomNode(Node node) {
        return this.soapDocument.getDomNode(node);
    }

    @Override
    public Node appendChild(Node newChild) throws DOMException {
        Node node = this.soapDocument.importNode(newChild, true);
        return this.soapDocument.findIfPresent(this.documentFragment.appendChild(this.getDomNode(node)));
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        return this.soapDocument.findIfPresent(this.documentFragment.removeChild(this.getDomNode(oldChild)));
    }

    @Override
    public NamedNodeMap getAttributes() {
        return this.documentFragment.getAttributes();
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        return this.documentFragment.compareDocumentPosition(this.getDomNode(other));
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        this.documentFragment.setTextContent(textContent);
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        Node node = this.soapDocument.importNode(newChild, true);
        return this.soapDocument.findIfPresent(this.documentFragment.insertBefore(this.getDomNode(node), this.getDomNode(refChild)));
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return this.documentFragment.setUserData(key, data, handler);
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return this.documentFragment.isDefaultNamespace(namespaceURI);
    }

    @Override
    public Node getLastChild() {
        return this.soapDocument.findIfPresent(this.documentFragment.getLastChild());
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        this.documentFragment.setPrefix(prefix);
    }

    @Override
    public String getNodeName() {
        return this.documentFragment.getNodeName();
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        this.documentFragment.setNodeValue(nodeValue);
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        Node node = this.soapDocument.importNode(newChild, true);
        return this.soapDocument.findIfPresent(this.documentFragment.replaceChild(this.getDomNode(node), this.getDomNode(oldChild)));
    }

    @Override
    public String getLocalName() {
        return this.documentFragment.getLocalName();
    }

    @Override
    public void normalize() {
        this.documentFragment.normalize();
    }

    @Override
    public Node cloneNode(boolean deep) {
        Node node = this.documentFragment.cloneNode(deep);
        this.soapDocument.registerChildNodes(node, deep);
        return this.soapDocument.findIfPresent(node);
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return this.documentFragment.isSupported(feature, version);
    }

    @Override
    public boolean isEqualNode(Node arg) {
        return this.documentFragment.isEqualNode(this.getDomNode(arg));
    }

    @Override
    public boolean hasChildNodes() {
        return this.documentFragment.hasChildNodes();
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        return this.documentFragment.lookupPrefix(namespaceURI);
    }

    @Override
    public String getNodeValue() throws DOMException {
        return this.documentFragment.getNodeValue();
    }

    @Override
    public Document getOwnerDocument() {
        return this.soapDocument;
    }

    @Override
    public Object getFeature(String feature, String version) {
        return this.documentFragment.getFeature(feature, version);
    }

    @Override
    public Node getPreviousSibling() {
        return this.soapDocument.findIfPresent(this.documentFragment.getPreviousSibling());
    }

    @Override
    public NodeList getChildNodes() {
        return new NodeListImpl(this.soapDocument, this.documentFragment.getChildNodes());
    }

    @Override
    public String getBaseURI() {
        return this.documentFragment.getBaseURI();
    }

    @Override
    public Node getNextSibling() {
        return this.soapDocument.findIfPresent(this.documentFragment.getNextSibling());
    }

    @Override
    public String getPrefix() {
        return this.documentFragment.getPrefix();
    }

    @Override
    public String getNamespaceURI() {
        return this.documentFragment.getNamespaceURI();
    }

    public Document getSoapDocument() {
        return this.soapDocument;
    }

    public Node getDomNode() {
        return this.documentFragment;
    }
}

