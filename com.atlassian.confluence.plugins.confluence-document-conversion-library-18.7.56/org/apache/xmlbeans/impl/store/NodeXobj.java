/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.Xobj;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

abstract class NodeXobj
extends Xobj
implements DomImpl.Dom,
Node,
NodeList {
    NodeXobj(Locale l, int kind, int domType) {
        super(l, kind, domType);
    }

    @Override
    DomImpl.Dom getDom() {
        return this;
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
    public boolean nodeCanHavePrefixUri() {
        return false;
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
}

