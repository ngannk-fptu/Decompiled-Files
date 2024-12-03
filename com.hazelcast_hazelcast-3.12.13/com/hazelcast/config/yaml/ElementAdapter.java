/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.yaml;

import com.hazelcast.config.yaml.EmptyNamedNodeMap;
import com.hazelcast.config.yaml.EmptyNodeList;
import com.hazelcast.config.yaml.NamedNodeMapAdapter;
import com.hazelcast.config.yaml.NodeListMappingAdapter;
import com.hazelcast.config.yaml.NodeListScalarAdapter;
import com.hazelcast.config.yaml.NodeListSequenceAdapter;
import com.hazelcast.config.yaml.W3cDomUtil;
import com.hazelcast.internal.yaml.MutableYamlScalar;
import com.hazelcast.internal.yaml.YamlCollection;
import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlScalar;
import com.hazelcast.internal.yaml.YamlSequence;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class ElementAdapter
implements Element {
    private final YamlNode yamlNode;

    ElementAdapter(YamlNode yamlNode) {
        this.yamlNode = yamlNode;
    }

    public YamlNode getYamlNode() {
        return this.yamlNode;
    }

    @Override
    public String getNodeName() {
        return this.yamlNode.nodeName();
    }

    @Override
    public String getNodeValue() throws DOMException {
        if (this.yamlNode instanceof YamlScalar) {
            Object nodeValue = ((YamlScalar)this.yamlNode).nodeValue();
            return nodeValue != null ? nodeValue.toString() : null;
        }
        return null;
    }

    @Override
    public void setNodeValue(String nodeValue) throws DOMException {
        if (!(this.yamlNode instanceof MutableYamlScalar)) {
            throw new UnsupportedOperationException();
        }
        ((MutableYamlScalar)this.yamlNode).setValue(nodeValue);
    }

    @Override
    public short getNodeType() {
        return 1;
    }

    @Override
    public Node getParentNode() {
        return W3cDomUtil.asW3cNode(this.yamlNode.parent());
    }

    @Override
    public NodeList getChildNodes() {
        if (!this.hasChildNodes()) {
            return EmptyNodeList.emptyNodeList();
        }
        if (this.yamlNode instanceof YamlMapping) {
            return new NodeListMappingAdapter((YamlMapping)this.yamlNode);
        }
        if (this.yamlNode instanceof YamlSequence) {
            return new NodeListSequenceAdapter((YamlSequence)this.yamlNode);
        }
        return new NodeListScalarAdapter((YamlScalar)this.yamlNode);
    }

    @Override
    public Node getFirstChild() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node getLastChild() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node getPreviousSibling() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node getNextSibling() {
        throw new UnsupportedOperationException();
    }

    @Override
    public NamedNodeMap getAttributes() {
        if (this.yamlNode instanceof YamlMapping) {
            return new NamedNodeMapAdapter((YamlMapping)this.yamlNode);
        }
        return EmptyNamedNodeMap.emptyNamedNodeMap();
    }

    @Override
    public Document getOwnerDocument() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node appendChild(Node newChild) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasChildNodes() {
        return this.yamlNode instanceof YamlCollection && ((YamlCollection)this.yamlNode).childCount() > 0 || this.yamlNode instanceof YamlScalar;
    }

    @Override
    public Node cloneNode(boolean deep) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void normalize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSupported(String feature, String version) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getNamespaceURI() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getPrefix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getLocalName() {
        return this.getNodeName();
    }

    @Override
    public boolean hasAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getBaseURI() {
        throw new UnsupportedOperationException();
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTextContent() throws DOMException {
        return this.getNodeValue();
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSameNode(Node other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEqualNode(Node arg) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getFeature(String feature, String version) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getUserData(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getTagName() {
        return this.getNodeName();
    }

    @Override
    public String getAttribute(String name) {
        YamlScalar yamlScalar;
        if (this.yamlNode instanceof YamlMapping && (yamlScalar = ((YamlMapping)this.yamlNode).childAsScalar(name)) != null) {
            return yamlScalar.nodeValue().toString();
        }
        return "";
    }

    @Override
    public void setAttribute(String name, String value) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttribute(String name) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr getAttributeNode(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeList getElementsByTagName(String name) {
        Node element = this.getAttributes().getNamedItem(name);
        return W3cDomUtil.asNodeList(element);
    }

    @Override
    public String getAttributeNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeAttributeNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr getAttributeNodeNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasAttribute(String name) {
        return this.getAttributes().getNamedItem(name) != null;
    }

    @Override
    public boolean hasAttributeNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIdAttribute(String name, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIdAttributeNS(String namespaceURI, String localName, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIdAttributeNode(Attr idAttr, boolean isId) throws DOMException {
        throw new UnsupportedOperationException();
    }
}

