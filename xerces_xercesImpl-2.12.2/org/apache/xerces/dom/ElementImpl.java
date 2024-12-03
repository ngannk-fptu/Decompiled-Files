/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.AttrNSImpl;
import org.apache.xerces.dom.AttributeMap;
import org.apache.xerces.dom.ChildNode;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.DeepNodeListImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xerces.dom.ElementDefinitionImpl;
import org.apache.xerces.dom.NamedNodeMapImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.ParentNode;
import org.apache.xerces.util.URI;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.ElementTraversal;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.TypeInfo;

public class ElementImpl
extends ParentNode
implements Element,
ElementTraversal,
TypeInfo {
    static final long serialVersionUID = 3717253516652722278L;
    protected String name;
    protected AttributeMap attributes;

    public ElementImpl(CoreDocumentImpl coreDocumentImpl, String string) {
        super(coreDocumentImpl);
        this.name = string;
        this.needsSyncData(true);
    }

    protected ElementImpl() {
    }

    void rename(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument.errorChecking) {
            int n = string.indexOf(58);
            if (n != -1) {
                String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NAMESPACE_ERR", null);
                throw new DOMException(14, string2);
            }
            if (!CoreDocumentImpl.isXMLName(string, this.ownerDocument.isXML11Version())) {
                String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_CHARACTER_ERR", null);
                throw new DOMException(5, string3);
            }
        }
        this.name = string;
        this.reconcileDefaultAttributes();
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
            this.attributes = new AttributeMap(this, null);
        }
        return this.attributes;
    }

    @Override
    public Node cloneNode(boolean bl) {
        ElementImpl elementImpl = (ElementImpl)super.cloneNode(bl);
        if (this.attributes != null) {
            elementImpl.attributes = (AttributeMap)this.attributes.cloneMap(elementImpl);
        }
        return elementImpl;
    }

    @Override
    public String getBaseURI() {
        String string;
        Attr attr;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes != null && (attr = this.getXMLBaseAttribute()) != null && (string = attr.getNodeValue()).length() != 0) {
            try {
                String string2;
                URI uRI = new URI(string, true);
                if (uRI.isAbsoluteURI()) {
                    return uRI.toString();
                }
                String string3 = string2 = this.ownerNode != null ? this.ownerNode.getBaseURI() : null;
                if (string2 != null) {
                    try {
                        URI uRI2 = new URI(string2);
                        uRI.absolutize(uRI2);
                        return uRI.toString();
                    }
                    catch (URI.MalformedURIException malformedURIException) {
                        return null;
                    }
                }
                return null;
            }
            catch (URI.MalformedURIException malformedURIException) {
                return null;
            }
        }
        return this.ownerNode != null ? this.ownerNode.getBaseURI() : null;
    }

    protected Attr getXMLBaseAttribute() {
        return (Attr)this.attributes.getNamedItem("xml:base");
    }

    @Override
    protected void setOwnerDocument(CoreDocumentImpl coreDocumentImpl) {
        super.setOwnerDocument(coreDocumentImpl);
        if (this.attributes != null) {
            this.attributes.setOwnerDocument(coreDocumentImpl);
        }
    }

    @Override
    public String getAttribute(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return "";
        }
        Attr attr = (Attr)this.attributes.getNamedItem(string);
        return attr == null ? "" : attr.getValue();
    }

    @Override
    public Attr getAttributeNode(String string) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return null;
        }
        return (Attr)this.attributes.getNamedItem(string);
    }

    @Override
    public NodeList getElementsByTagName(String string) {
        return new DeepNodeListImpl(this, string);
    }

    @Override
    public String getTagName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.name;
    }

    @Override
    public void normalize() {
        if (this.isNormalized()) {
            return;
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        ChildNode childNode = this.firstChild;
        while (childNode != null) {
            ChildNode childNode2 = childNode.nextSibling;
            if (childNode.getNodeType() == 3) {
                if (childNode2 != null && childNode2.getNodeType() == 3) {
                    ((Text)((Object)childNode)).appendData(childNode2.getNodeValue());
                    this.removeChild(childNode2);
                    childNode2 = childNode;
                } else if (childNode.getNodeValue() == null || childNode.getNodeValue().length() == 0) {
                    this.removeChild(childNode);
                }
            } else if (childNode.getNodeType() == 1) {
                childNode.normalize();
            }
            childNode = childNode2;
        }
        if (this.attributes != null) {
            for (int i = 0; i < this.attributes.getLength(); ++i) {
                Node node = this.attributes.item(i);
                node.normalize();
            }
        }
        this.isNormalized(true);
    }

    @Override
    public void removeAttribute(String string) {
        if (this.ownerDocument.errorChecking && this.isReadOnly()) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string2);
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return;
        }
        this.attributes.safeRemoveNamedItem(string);
    }

    @Override
    public Attr removeAttributeNode(Attr attr) throws DOMException {
        if (this.ownerDocument.errorChecking && this.isReadOnly()) {
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string);
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, string);
        }
        return (Attr)this.attributes.removeItem(attr, true);
    }

    @Override
    public void setAttribute(String string, String string2) {
        Attr attr;
        if (this.ownerDocument.errorChecking && this.isReadOnly()) {
            String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string3);
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if ((attr = this.getAttributeNode(string)) == null) {
            attr = this.getOwnerDocument().createAttribute(string);
            if (this.attributes == null) {
                this.attributes = new AttributeMap(this, null);
            }
            attr.setNodeValue(string2);
            this.attributes.setNamedItem(attr);
        } else {
            attr.setNodeValue(string2);
        }
    }

    @Override
    public Attr setAttributeNode(Attr attr) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, string);
            }
            if (attr.getOwnerDocument() != this.ownerDocument) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(4, string);
            }
        }
        if (this.attributes == null) {
            this.attributes = new AttributeMap(this, null);
        }
        return (Attr)this.attributes.setNamedItem(attr);
    }

    @Override
    public String getAttributeNS(String string, String string2) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return "";
        }
        Attr attr = (Attr)this.attributes.getNamedItemNS(string, string2);
        return attr == null ? "" : attr.getValue();
    }

    @Override
    public void setAttributeNS(String string, String string2, String string3) {
        String string4;
        String string5;
        int n;
        if (this.ownerDocument.errorChecking && this.isReadOnly()) {
            String string6 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string6);
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if ((n = string2.indexOf(58)) < 0) {
            string5 = null;
            string4 = string2;
        } else {
            string5 = string2.substring(0, n);
            string4 = string2.substring(n + 1);
        }
        Attr attr = this.getAttributeNodeNS(string, string4);
        if (attr == null) {
            attr = this.getOwnerDocument().createAttributeNS(string, string2);
            if (this.attributes == null) {
                this.attributes = new AttributeMap(this, null);
            }
            attr.setNodeValue(string3);
            this.attributes.setNamedItemNS(attr);
        } else {
            if (attr instanceof AttrNSImpl) {
                ((AttrNSImpl)attr).name = string5 != null ? string5 + ":" + string4 : string4;
            } else {
                attr = ((CoreDocumentImpl)this.getOwnerDocument()).createAttributeNS(string, string2, string4);
                this.attributes.setNamedItemNS(attr);
            }
            attr.setNodeValue(string3);
        }
    }

    @Override
    public void removeAttributeNS(String string, String string2) {
        if (this.ownerDocument.errorChecking && this.isReadOnly()) {
            String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string3);
        }
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return;
        }
        this.attributes.safeRemoveNamedItemNS(string, string2);
    }

    @Override
    public Attr getAttributeNodeNS(String string, String string2) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return null;
        }
        return (Attr)this.attributes.getNamedItemNS(string, string2);
    }

    @Override
    public Attr setAttributeNodeNS(Attr attr) throws DOMException {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, string);
            }
            if (attr.getOwnerDocument() != this.ownerDocument) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(4, string);
            }
        }
        if (this.attributes == null) {
            this.attributes = new AttributeMap(this, null);
        }
        return (Attr)this.attributes.setNamedItemNS(attr);
    }

    protected int setXercesAttributeNode(Attr attr) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            this.attributes = new AttributeMap(this, null);
        }
        return this.attributes.addItem(attr);
    }

    protected int getXercesAttribute(String string, String string2) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.attributes == null) {
            return -1;
        }
        return this.attributes.getNamedItemIndex(string, string2);
    }

    @Override
    public boolean hasAttributes() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.attributes != null && this.attributes.getLength() != 0;
    }

    @Override
    public boolean hasAttribute(String string) {
        return this.getAttributeNode(string) != null;
    }

    @Override
    public boolean hasAttributeNS(String string, String string2) {
        return this.getAttributeNodeNS(string, string2) != null;
    }

    @Override
    public NodeList getElementsByTagNameNS(String string, String string2) {
        return new DeepNodeListImpl(this, string, string2);
    }

    @Override
    public boolean isEqualNode(Node node) {
        if (!super.isEqualNode(node)) {
            return false;
        }
        boolean bl = this.hasAttributes();
        if (bl != ((Element)node).hasAttributes()) {
            return false;
        }
        if (bl) {
            NamedNodeMap namedNodeMap = this.getAttributes();
            NamedNodeMap namedNodeMap2 = ((Element)node).getAttributes();
            int n = namedNodeMap.getLength();
            if (n != namedNodeMap2.getLength()) {
                return false;
            }
            for (int i = 0; i < n; ++i) {
                Node node2;
                Node node3 = namedNodeMap.item(i);
                if (!(node3.getLocalName() == null ? (node2 = namedNodeMap2.getNamedItem(node3.getNodeName())) == null || !((NodeImpl)node3).isEqualNode(node2) : (node2 = namedNodeMap2.getNamedItemNS(node3.getNamespaceURI(), node3.getLocalName())) == null || !((NodeImpl)node3).isEqualNode(node2))) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public void setIdAttributeNode(Attr attr, boolean bl) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, string);
            }
            if (attr.getOwnerElement() != this) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
                throw new DOMException(8, string);
            }
        }
        ((AttrImpl)attr).isIdAttribute(bl);
        if (!bl) {
            this.ownerDocument.removeIdentifier(attr.getValue());
        } else {
            this.ownerDocument.putIdentifier(attr.getValue(), this);
        }
    }

    @Override
    public void setIdAttribute(String string, boolean bl) {
        Attr attr;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if ((attr = this.getAttributeNode(string)) == null) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, string2);
        }
        if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, string3);
            }
            if (attr.getOwnerElement() != this) {
                String string4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
                throw new DOMException(8, string4);
            }
        }
        ((AttrImpl)attr).isIdAttribute(bl);
        if (!bl) {
            this.ownerDocument.removeIdentifier(attr.getValue());
        } else {
            this.ownerDocument.putIdentifier(attr.getValue(), this);
        }
    }

    @Override
    public void setIdAttributeNS(String string, String string2, boolean bl) {
        Attr attr;
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if ((attr = this.getAttributeNodeNS(string, string2)) == null) {
            String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, string3);
        }
        if (this.ownerDocument.errorChecking) {
            if (this.isReadOnly()) {
                String string4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, string4);
            }
            if (attr.getOwnerElement() != this) {
                String string5 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
                throw new DOMException(8, string5);
            }
        }
        ((AttrImpl)attr).isIdAttribute(bl);
        if (!bl) {
            this.ownerDocument.removeIdentifier(attr.getValue());
        } else {
            this.ownerDocument.putIdentifier(attr.getValue(), this);
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
    public boolean isDerivedFrom(String string, String string2, int n) {
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
    public void setReadOnly(boolean bl, boolean bl2) {
        super.setReadOnly(bl, bl2);
        if (this.attributes != null) {
            this.attributes.setReadOnly(bl, true);
        }
    }

    @Override
    protected void synchronizeData() {
        this.needsSyncData(false);
        boolean bl = this.ownerDocument.getMutationEvents();
        this.ownerDocument.setMutationEvents(false);
        this.setupDefaultAttributes();
        this.ownerDocument.setMutationEvents(bl);
    }

    void moveSpecifiedAttributes(ElementImpl elementImpl) {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        if (elementImpl.hasAttributes()) {
            if (this.attributes == null) {
                this.attributes = new AttributeMap(this, null);
            }
            this.attributes.moveSpecifiedAttributes(elementImpl.attributes);
        }
    }

    protected void setupDefaultAttributes() {
        NamedNodeMapImpl namedNodeMapImpl = this.getDefaultAttributes();
        if (namedNodeMapImpl != null) {
            this.attributes = new AttributeMap(this, namedNodeMapImpl);
        }
    }

    protected void reconcileDefaultAttributes() {
        if (this.attributes != null) {
            NamedNodeMapImpl namedNodeMapImpl = this.getDefaultAttributes();
            this.attributes.reconcileDefaults(namedNodeMapImpl);
        }
    }

    protected NamedNodeMapImpl getDefaultAttributes() {
        DocumentTypeImpl documentTypeImpl = (DocumentTypeImpl)this.ownerDocument.getDoctype();
        if (documentTypeImpl == null) {
            return null;
        }
        ElementDefinitionImpl elementDefinitionImpl = (ElementDefinitionImpl)documentTypeImpl.getElements().getNamedItem(this.getNodeName());
        if (elementDefinitionImpl == null) {
            return null;
        }
        return (NamedNodeMapImpl)elementDefinitionImpl.getAttributes();
    }

    @Override
    public final int getChildElementCount() {
        int n = 0;
        Element element = this.getFirstElementChild();
        while (element != null) {
            ++n;
            element = ((ElementImpl)element).getNextElementSibling();
        }
        return n;
    }

    @Override
    public final Element getFirstElementChild() {
        block4: for (Node node = this.getFirstChild(); node != null; node = node.getNextSibling()) {
            switch (node.getNodeType()) {
                case 1: {
                    return (Element)node;
                }
                case 5: {
                    Element element = this.getFirstElementChild(node);
                    if (element == null) continue block4;
                    return element;
                }
            }
        }
        return null;
    }

    @Override
    public final Element getLastElementChild() {
        block4: for (Node node = this.getLastChild(); node != null; node = node.getPreviousSibling()) {
            switch (node.getNodeType()) {
                case 1: {
                    return (Element)node;
                }
                case 5: {
                    Element element = this.getLastElementChild(node);
                    if (element == null) continue block4;
                    return element;
                }
            }
        }
        return null;
    }

    @Override
    public final Element getNextElementSibling() {
        Node node = this.getNextLogicalSibling(this);
        while (node != null) {
            switch (node.getNodeType()) {
                case 1: {
                    return (Element)node;
                }
                case 5: {
                    Element element = this.getFirstElementChild(node);
                    if (element == null) break;
                    return element;
                }
            }
            node = this.getNextLogicalSibling(node);
        }
        return null;
    }

    @Override
    public final Element getPreviousElementSibling() {
        Node node = this.getPreviousLogicalSibling(this);
        while (node != null) {
            switch (node.getNodeType()) {
                case 1: {
                    return (Element)node;
                }
                case 5: {
                    Element element = this.getLastElementChild(node);
                    if (element == null) break;
                    return element;
                }
            }
            node = this.getPreviousLogicalSibling(node);
        }
        return null;
    }

    private Element getFirstElementChild(Node node) {
        Node node2 = node;
        while (node != null) {
            if (node.getNodeType() == 1) {
                return (Element)node;
            }
            Node node3 = node.getFirstChild();
            while (node3 == null && node2 != node) {
                node3 = node.getNextSibling();
                if (node3 != null || (node = node.getParentNode()) != null && node2 != node) continue;
                return null;
            }
            node = node3;
        }
        return null;
    }

    private Element getLastElementChild(Node node) {
        Node node2 = node;
        while (node != null) {
            if (node.getNodeType() == 1) {
                return (Element)node;
            }
            Node node3 = node.getLastChild();
            while (node3 == null && node2 != node) {
                node3 = node.getPreviousSibling();
                if (node3 != null || (node = node.getParentNode()) != null && node2 != node) continue;
                return null;
            }
            node = node3;
        }
        return null;
    }

    private Node getNextLogicalSibling(Node node) {
        Node node2 = node.getNextSibling();
        if (node2 == null) {
            for (Node node3 = node.getParentNode(); node3 != null && node3.getNodeType() == 5 && (node2 = node3.getNextSibling()) == null; node3 = node3.getParentNode()) {
            }
        }
        return node2;
    }

    private Node getPreviousLogicalSibling(Node node) {
        Node node2 = node.getPreviousSibling();
        if (node2 == null) {
            for (Node node3 = node.getParentNode(); node3 != null && node3.getNodeType() == 5 && (node2 = node3.getPreviousSibling()) == null; node3 = node3.getParentNode()) {
            }
        }
        return node2;
    }
}

