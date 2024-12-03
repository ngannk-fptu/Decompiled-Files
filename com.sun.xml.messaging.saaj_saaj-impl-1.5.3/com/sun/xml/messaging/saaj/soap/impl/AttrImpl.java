/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.Node
 *  javax.xml.soap.SOAPElement
 *  javax.xml.soap.SOAPException
 */
package com.sun.xml.messaging.saaj.soap.impl;

import javax.xml.soap.Node;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class AttrImpl
implements Attr,
Node {
    private SOAPElement soapElement;
    final Attr delegate;

    AttrImpl(SOAPElement element, Attr attr) {
        this.soapElement = element;
        this.delegate = attr;
    }

    @Override
    public String getNodeName() {
        return this.delegate.getNodeName();
    }

    @Override
    public String getNodeValue() throws DOMException {
        return this.delegate.getNodeValue();
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        this.delegate.setNodeValue(nodeValue);
    }

    @Override
    public short getNodeType() {
        return this.delegate.getNodeType();
    }

    @Override
    public org.w3c.dom.Node getParentNode() {
        return this.delegate.getParentNode();
    }

    @Override
    public NodeList getChildNodes() {
        return this.delegate.getChildNodes();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public org.w3c.dom.Node getFirstChild() {
        return this.delegate.getFirstChild();
    }

    @Override
    public boolean getSpecified() {
        return this.delegate.getSpecified();
    }

    @Override
    public org.w3c.dom.Node getLastChild() {
        return this.delegate.getLastChild();
    }

    @Override
    public org.w3c.dom.Node getPreviousSibling() {
        return this.delegate.getPreviousSibling();
    }

    @Override
    public org.w3c.dom.Node getNextSibling() {
        return this.delegate.getNextSibling();
    }

    @Override
    public String getValue() {
        return this.delegate.getValue();
    }

    @Override
    public NamedNodeMap getAttributes() {
        return this.delegate.getAttributes();
    }

    @Override
    public Document getOwnerDocument() {
        return this.soapElement.getOwnerDocument();
    }

    @Override
    public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) throws DOMException {
        return this.delegate.insertBefore(newChild, refChild);
    }

    @Override
    public void setValue(String value) throws DOMException {
        this.delegate.setValue(value);
    }

    @Override
    public Element getOwnerElement() {
        return this.soapElement;
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return this.delegate.getSchemaTypeInfo();
    }

    @Override
    public boolean isId() {
        return this.delegate.isId();
    }

    @Override
    public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) throws DOMException {
        return this.delegate.replaceChild(newChild, oldChild);
    }

    @Override
    public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) throws DOMException {
        return this.delegate.removeChild(oldChild);
    }

    @Override
    public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) throws DOMException {
        return this.delegate.appendChild(newChild);
    }

    @Override
    public boolean hasChildNodes() {
        return this.delegate.hasChildNodes();
    }

    @Override
    public org.w3c.dom.Node cloneNode(boolean deep) {
        return this.delegate.cloneNode(deep);
    }

    @Override
    public void normalize() {
        this.delegate.normalize();
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return this.delegate.isSupported(feature, version);
    }

    @Override
    public String getNamespaceURI() {
        return this.delegate.getNamespaceURI();
    }

    @Override
    public String getPrefix() {
        return this.delegate.getPrefix();
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        this.delegate.setPrefix(prefix);
    }

    @Override
    public String getLocalName() {
        return this.delegate.getLocalName();
    }

    @Override
    public boolean hasAttributes() {
        return this.delegate.hasAttributes();
    }

    @Override
    public String getBaseURI() {
        return this.delegate.getBaseURI();
    }

    @Override
    public short compareDocumentPosition(org.w3c.dom.Node other) throws DOMException {
        return this.delegate.compareDocumentPosition(other);
    }

    @Override
    public String getTextContent() throws DOMException {
        return this.delegate.getTextContent();
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        this.delegate.setTextContent(textContent);
    }

    @Override
    public boolean isSameNode(org.w3c.dom.Node other) {
        return this.delegate.isSameNode(other);
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        return this.delegate.lookupPrefix(namespaceURI);
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return this.delegate.isDefaultNamespace(namespaceURI);
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        return this.delegate.lookupNamespaceURI(prefix);
    }

    @Override
    public boolean isEqualNode(org.w3c.dom.Node arg) {
        return this.delegate.isEqualNode(arg);
    }

    @Override
    public Object getFeature(String feature, String version) {
        return this.delegate.getFeature(feature, version);
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return this.delegate.setUserData(key, data, handler);
    }

    @Override
    public Object getUserData(String key) {
        return this.delegate.getUserData(key);
    }

    public void setParentElement(SOAPElement parent) throws SOAPException {
        this.soapElement = parent;
    }

    public SOAPElement getParentElement() {
        return this.soapElement;
    }

    public void detachNode() {
        this.soapElement.removeAttributeNode(this.delegate);
    }

    public void recycleNode() {
        this.detachNode();
    }
}

