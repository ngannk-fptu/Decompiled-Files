/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref.dom2dtm;

import org.apache.xml.dtm.DTMException;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.UserDataHandler;

public class DOM2DTMdefaultNamespaceDeclarationNode
implements Attr,
TypeInfo {
    static final String NOT_SUPPORTED_ERR = "Unsupported operation on pseudonode";
    Element pseudoparent;
    String prefix;
    String uri;
    String nodename;
    int handle;

    DOM2DTMdefaultNamespaceDeclarationNode(Element pseudoparent, String prefix, String uri, int handle) {
        this.pseudoparent = pseudoparent;
        this.prefix = prefix;
        this.uri = uri;
        this.handle = handle;
        this.nodename = "xmlns:" + prefix;
    }

    @Override
    public String getNodeName() {
        return this.nodename;
    }

    @Override
    public String getName() {
        return this.nodename;
    }

    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2000/xmlns/";
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public String getLocalName() {
        return this.prefix;
    }

    @Override
    public String getNodeValue() {
        return this.uri;
    }

    @Override
    public String getValue() {
        return this.uri;
    }

    @Override
    public Element getOwnerElement() {
        return this.pseudoparent;
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return false;
    }

    @Override
    public boolean hasChildNodes() {
        return false;
    }

    @Override
    public boolean hasAttributes() {
        return false;
    }

    @Override
    public Node getParentNode() {
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
    public boolean getSpecified() {
        return false;
    }

    @Override
    public void normalize() {
    }

    @Override
    public NodeList getChildNodes() {
        return null;
    }

    @Override
    public NamedNodeMap getAttributes() {
        return null;
    }

    @Override
    public short getNodeType() {
        return 2;
    }

    @Override
    public void setNodeValue(String value) {
        throw new DTMException(NOT_SUPPORTED_ERR);
    }

    @Override
    public void setValue(String value) {
        throw new DTMException(NOT_SUPPORTED_ERR);
    }

    @Override
    public void setPrefix(String value) {
        throw new DTMException(NOT_SUPPORTED_ERR);
    }

    @Override
    public Node insertBefore(Node a, Node b) {
        throw new DTMException(NOT_SUPPORTED_ERR);
    }

    @Override
    public Node replaceChild(Node a, Node b) {
        throw new DTMException(NOT_SUPPORTED_ERR);
    }

    @Override
    public Node appendChild(Node a) {
        throw new DTMException(NOT_SUPPORTED_ERR);
    }

    @Override
    public Node removeChild(Node a) {
        throw new DTMException(NOT_SUPPORTED_ERR);
    }

    @Override
    public Document getOwnerDocument() {
        return this.pseudoparent.getOwnerDocument();
    }

    @Override
    public Node cloneNode(boolean deep) {
        throw new DTMException(NOT_SUPPORTED_ERR);
    }

    public int getHandleOfNode() {
        return this.handle;
    }

    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    public String getTypeNamespace() {
        return null;
    }

    @Override
    public boolean isDerivedFrom(String ns, String localName, int derivationMethod) {
        return false;
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        return this;
    }

    @Override
    public boolean isId() {
        return false;
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return this.getOwnerDocument().setUserData(key, data, handler);
    }

    @Override
    public Object getUserData(String key) {
        return this.getOwnerDocument().getUserData(key);
    }

    @Override
    public Object getFeature(String feature, String version) {
        return this.isSupported(feature, version) ? this : null;
    }

    @Override
    public boolean isEqualNode(Node arg) {
        if (arg == this) {
            return true;
        }
        if (arg.getNodeType() != this.getNodeType()) {
            return false;
        }
        if (this.getNodeName() == null ? arg.getNodeName() != null : !this.getNodeName().equals(arg.getNodeName())) {
            return false;
        }
        if (this.getLocalName() == null ? arg.getLocalName() != null : !this.getLocalName().equals(arg.getLocalName())) {
            return false;
        }
        if (this.getNamespaceURI() == null ? arg.getNamespaceURI() != null : !this.getNamespaceURI().equals(arg.getNamespaceURI())) {
            return false;
        }
        if (this.getPrefix() == null ? arg.getPrefix() != null : !this.getPrefix().equals(arg.getPrefix())) {
            return false;
        }
        return !(this.getNodeValue() == null ? arg.getNodeValue() != null : !this.getNodeValue().equals(arg.getNodeValue()));
    }

    @Override
    public String lookupNamespaceURI(String specifiedPrefix) {
        short type = this.getNodeType();
        switch (type) {
            case 1: {
                String namespace = this.getNamespaceURI();
                String prefix = this.getPrefix();
                if (namespace != null) {
                    if (specifiedPrefix == null && prefix == specifiedPrefix) {
                        return namespace;
                    }
                    if (prefix != null && prefix.equals(specifiedPrefix)) {
                        return namespace;
                    }
                }
                if (this.hasAttributes()) {
                    NamedNodeMap map = this.getAttributes();
                    int length = map.getLength();
                    for (int i = 0; i < length; ++i) {
                        Node attr = map.item(i);
                        String attrPrefix = attr.getPrefix();
                        String value = attr.getNodeValue();
                        namespace = attr.getNamespaceURI();
                        if (namespace == null || !namespace.equals("http://www.w3.org/2000/xmlns/")) continue;
                        if (specifiedPrefix == null && attr.getNodeName().equals("xmlns")) {
                            return value;
                        }
                        if (attrPrefix == null || !attrPrefix.equals("xmlns") || !attr.getLocalName().equals(specifiedPrefix)) continue;
                        return value;
                    }
                }
                return null;
            }
            case 6: 
            case 10: 
            case 11: 
            case 12: {
                return null;
            }
            case 2: {
                if (this.getOwnerElement().getNodeType() == 1) {
                    return this.getOwnerElement().lookupNamespaceURI(specifiedPrefix);
                }
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return false;
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            return null;
        }
        short type = this.getNodeType();
        switch (type) {
            case 6: 
            case 10: 
            case 11: 
            case 12: {
                return null;
            }
            case 2: {
                if (this.getOwnerElement().getNodeType() == 1) {
                    return this.getOwnerElement().lookupPrefix(namespaceURI);
                }
                return null;
            }
        }
        return null;
    }

    @Override
    public boolean isSameNode(Node other) {
        return this == other;
    }

    @Override
    public void setTextContent(String textContent) throws DOMException {
        this.setNodeValue(textContent);
    }

    @Override
    public String getTextContent() throws DOMException {
        return this.getNodeValue();
    }

    @Override
    public short compareDocumentPosition(Node other) throws DOMException {
        return 0;
    }

    @Override
    public String getBaseURI() {
        return null;
    }
}

