/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import org.htmlunit.cyberneko.xerces.dom.AttrImpl;
import org.htmlunit.cyberneko.xerces.dom.AttrNSImpl;
import org.htmlunit.cyberneko.xerces.dom.AttributeMap;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMMessageFormatter;
import org.htmlunit.cyberneko.xerces.dom.DeepNodeListImpl;
import org.htmlunit.cyberneko.xerces.dom.ParentNode;
import org.htmlunit.cyberneko.xerces.util.URI;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

public class ElementImpl
extends ParentNode
implements Element,
TypeInfo {
    protected String name;
    protected AttributeMap attributes;

    public ElementImpl(CoreDocumentImpl ownerDoc, String name) {
        super(ownerDoc);
        this.name = name;
        this.needsSyncData(true);
    }

    void rename(String name) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument.errorChecking) {
            int colon1 = name.indexOf(58);
            if (colon1 != -1) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException(14, msg);
            }
            if (!CoreDocumentImpl.isXMLName(name, this.ownerDocument.isXML11Version())) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                throw new DOMException(5, msg);
            }
        }
        this.name = name;
    }

    @Override
    public short getNodeType() {
        return 1;
    }

    @Override
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }

    @Override
    public NamedNodeMap getAttributes() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            this.attributes = new AttributeMap(this);
        }
        return this.attributes;
    }

    @Override
    public Node cloneNode(boolean deep) {
        ElementImpl newnode = (ElementImpl)super.cloneNode(deep);
        if (this.attributes != null) {
            newnode.attributes = (AttributeMap)this.attributes.cloneMap(newnode);
        }
        return newnode;
    }

    @Override
    public String getBaseURI() {
        String uri;
        Attr attrNode;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes != null && (attrNode = this.getXMLBaseAttribute()) != null && (uri = attrNode.getNodeValue()).length() != 0) {
            try {
                String parentBaseURI;
                URI _uri = new URI(uri, true);
                if (_uri.isAbsoluteURI()) {
                    return _uri.toString();
                }
                String string = parentBaseURI = this.ownerNode != null ? this.ownerNode.getBaseURI() : null;
                if (parentBaseURI != null) {
                    try {
                        URI _parentBaseURI = new URI(parentBaseURI);
                        _uri.absolutize(_parentBaseURI);
                        return _uri.toString();
                    }
                    catch (URI.MalformedURIException ex) {
                        return null;
                    }
                }
                return null;
            }
            catch (URI.MalformedURIException ex) {
                return null;
            }
        }
        return this.ownerNode != null ? this.ownerNode.getBaseURI() : null;
    }

    protected Attr getXMLBaseAttribute() {
        return (Attr)this.attributes.getNamedItem("xml:base");
    }

    @Override
    protected void setOwnerDocument(CoreDocumentImpl doc) {
        super.setOwnerDocument(doc);
        if (this.attributes != null) {
            this.attributes.setOwnerDocument(doc);
        }
    }

    @Override
    public String getAttribute(String name) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return "";
        }
        Attr attr = (Attr)this.attributes.getNamedItem(name);
        return attr == null ? "" : attr.getValue();
    }

    @Override
    public Attr getAttributeNode(String name) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return null;
        }
        return (Attr)this.attributes.getNamedItem(name);
    }

    @Override
    public NodeList getElementsByTagName(String tagname) {
        return new DeepNodeListImpl(this, tagname);
    }

    @Override
    public String getTagName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }

    @Override
    public void removeAttribute(String name) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return;
        }
        this.attributes.internalRemoveNamedItem(name, false);
    }

    @Override
    public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, msg);
        }
        return (Attr)this.attributes.removeItem(oldAttr);
    }

    @Override
    public void setAttribute(String name, String value) {
        Attr newAttr;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if ((newAttr = this.getAttributeNode(name)) == null) {
            newAttr = this.getOwnerDocument().createAttribute(name);
            if (this.attributes == null) {
                this.attributes = new AttributeMap(this);
            }
            newAttr.setNodeValue(value);
            this.attributes.setNamedItem(newAttr);
        } else {
            newAttr.setNodeValue(value);
        }
    }

    @Override
    public Attr setAttributeNode(Attr newAttr) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument.errorChecking && newAttr.getOwnerDocument() != this.ownerDocument) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
            throw new DOMException(4, msg);
        }
        if (this.attributes == null) {
            this.attributes = new AttributeMap(this);
        }
        return (Attr)this.attributes.setNamedItem(newAttr);
    }

    @Override
    public String getAttributeNS(String namespaceURI, String localName) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return "";
        }
        Attr attr = (Attr)this.attributes.getNamedItemNS(namespaceURI, localName);
        return attr == null ? "" : attr.getValue();
    }

    @Override
    public void setAttributeNS(String namespaceURI, String qualifiedName, String value) {
        String localName;
        String prefix;
        int index;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if ((index = qualifiedName.indexOf(58)) < 0) {
            prefix = null;
            localName = qualifiedName;
        } else {
            prefix = qualifiedName.substring(0, index);
            localName = qualifiedName.substring(index + 1);
        }
        Attr newAttr = this.getAttributeNodeNS(namespaceURI, localName);
        if (newAttr == null) {
            newAttr = this.getOwnerDocument().createAttributeNS(namespaceURI, qualifiedName);
            if (this.attributes == null) {
                this.attributes = new AttributeMap(this);
            }
            newAttr.setNodeValue(value);
            this.attributes.setNamedItemNS(newAttr);
        } else {
            if (newAttr instanceof AttrNSImpl) {
                ((AttrNSImpl)newAttr).name = prefix != null ? prefix + ":" + localName : localName;
            } else {
                newAttr = ((CoreDocumentImpl)this.getOwnerDocument()).createAttributeNS(namespaceURI, qualifiedName, localName);
                this.attributes.setNamedItemNS(newAttr);
            }
            newAttr.setNodeValue(value);
        }
    }

    @Override
    public void removeAttributeNS(String namespaceURI, String localName) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return;
        }
        this.attributes.internalRemoveNamedItemNS(namespaceURI, this.name, false);
    }

    @Override
    public Attr getAttributeNodeNS(String namespaceURI, String localName) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return null;
        }
        return (Attr)this.attributes.getNamedItemNS(namespaceURI, localName);
    }

    @Override
    public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument.errorChecking && newAttr.getOwnerDocument() != this.ownerDocument) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
            throw new DOMException(4, msg);
        }
        if (this.attributes == null) {
            this.attributes = new AttributeMap(this);
        }
        return (Attr)this.attributes.setNamedItemNS(newAttr);
    }

    @Override
    public boolean hasAttributes() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.attributes != null && this.attributes.getLength() != 0;
    }

    @Override
    public boolean hasAttribute(String name) {
        return this.getAttributeNode(name) != null;
    }

    @Override
    public boolean hasAttributeNS(String namespaceURI, String localName) {
        return this.getAttributeNodeNS(namespaceURI, localName) != null;
    }

    @Override
    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        return new DeepNodeListImpl(this, namespaceURI, localName);
    }

    @Override
    public boolean isEqualNode(Node arg) {
        if (!super.isEqualNode(arg)) {
            return false;
        }
        boolean hasAttrs = this.hasAttributes();
        if (hasAttrs != arg.hasAttributes()) {
            return false;
        }
        if (hasAttrs) {
            NamedNodeMap map1 = this.getAttributes();
            NamedNodeMap map2 = arg.getAttributes();
            int len = map1.getLength();
            if (len != map2.getLength()) {
                return false;
            }
            for (int i = 0; i < len; ++i) {
                Node n2;
                Node n1 = map1.item(i);
                if (!(n1.getLocalName() == null ? (n2 = map2.getNamedItem(n1.getNodeName())) == null || !n1.isEqualNode(n2) : (n2 = map2.getNamedItemNS(n1.getNamespaceURI(), n1.getLocalName())) == null || !n1.isEqualNode(n2))) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public void setIdAttributeNode(Attr at, boolean makeId) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument.errorChecking && at.getOwnerElement() != this) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, msg);
        }
        ((AttrImpl)at).isIdAttribute(makeId);
        if (!makeId) {
            this.ownerDocument.removeIdentifier(at.getValue());
        } else {
            this.ownerDocument.putIdentifier(at.getValue(), this);
        }
    }

    @Override
    public void setIdAttribute(String name, boolean makeId) {
        Attr at;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if ((at = this.getAttributeNode(name)) == null) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, msg);
        }
        if (this.ownerDocument.errorChecking && at.getOwnerElement() != this) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, msg);
        }
        ((AttrImpl)at).isIdAttribute(makeId);
        if (!makeId) {
            this.ownerDocument.removeIdentifier(at.getValue());
        } else {
            this.ownerDocument.putIdentifier(at.getValue(), this);
        }
    }

    @Override
    public void setIdAttributeNS(String namespaceURI, String localName, boolean makeId) {
        Attr at;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if ((at = this.getAttributeNodeNS(namespaceURI, localName)) == null) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, msg);
        }
        if (this.ownerDocument.errorChecking && at.getOwnerElement() != this) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, msg);
        }
        ((AttrImpl)at).isIdAttribute(makeId);
        if (!makeId) {
            this.ownerDocument.removeIdentifier(at.getValue());
        } else {
            this.ownerDocument.putIdentifier(at.getValue(), this);
        }
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
    public boolean isDerivedFrom(String typeNamespaceArg, String typeNameArg, int derivationMethod) {
        return false;
    }

    @Override
    public TypeInfo getSchemaTypeInfo() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this;
    }

    @Override
    protected void synchronizeData() {
        this.needsSyncData(false);
    }

    void moveSpecifiedAttributes(ElementImpl el) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (el.hasAttributes()) {
            if (this.attributes == null) {
                this.attributes = new AttributeMap(this);
            }
            this.attributes.moveSpecifiedAttributes(el.attributes);
        }
    }
}

