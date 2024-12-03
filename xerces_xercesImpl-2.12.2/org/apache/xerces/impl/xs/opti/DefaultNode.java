/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.opti;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class DefaultNode
implements Node {
    @Override
    public String getNodeName() {
        return null;
    }

    @Override
    public String getNodeValue() throws DOMException {
        return null;
    }

    @Override
    public short getNodeType() {
        return -1;
    }

    @Override
    public Node getParentNode() {
        return null;
    }

    @Override
    public NodeList getChildNodes() {
        return null;
    }

    @Override
    public Node getFirstChild() {
        return null;
    }

    @Override
    public Node getLastChild() {
        return null;
    }

    @Override
    public Node getPreviousSibling() {
        return null;
    }

    @Override
    public Node getNextSibling() {
        return null;
    }

    @Override
    public NamedNodeMap getAttributes() {
        return null;
    }

    @Override
    public Document getOwnerDocument() {
        return null;
    }

    @Override
    public boolean hasChildNodes() {
        return false;
    }

    @Override
    public Node cloneNode(boolean bl) {
        return null;
    }

    @Override
    public void normalize() {
    }

    @Override
    public boolean isSupported(String string, String string2) {
        return false;
    }

    @Override
    public String getNamespaceURI() {
        return null;
    }

    @Override
    public String getPrefix() {
        return null;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getBaseURI() {
        return null;
    }

    @Override
    public boolean hasAttributes() {
        return false;
    }

    @Override
    public void setNodeValue(String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Node insertBefore(Node node, Node node2) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Node replaceChild(Node node, Node node2) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Node removeChild(Node node) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Node appendChild(Node node) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void setPrefix(String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public short compareDocumentPosition(Node node) {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public String getTextContent() throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public void setTextContent(String string) throws DOMException {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public boolean isSameNode(Node node) {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public String lookupPrefix(String string) {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public boolean isDefaultNamespace(String string) {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public String lookupNamespaceURI(String string) {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public boolean isEqualNode(Node node) {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Object getFeature(String string, String string2) {
        return null;
    }

    @Override
    public Object setUserData(String string, Object object, UserDataHandler userDataHandler) {
        throw new DOMException(9, "Method not supported");
    }

    @Override
    public Object getUserData(String string) {
        return null;
    }
}

