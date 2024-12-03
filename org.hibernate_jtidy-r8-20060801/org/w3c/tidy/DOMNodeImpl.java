/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.w3c.tidy.DOMAttrMapImpl;
import org.w3c.tidy.DOMNodeListImpl;
import org.w3c.tidy.Node;
import org.w3c.tidy.TidyUtils;

public class DOMNodeImpl
implements org.w3c.dom.Node {
    protected Node adaptee;

    protected DOMNodeImpl(Node adaptee) {
        this.adaptee = adaptee;
    }

    public String getNodeValue() {
        String value = "";
        if ((this.adaptee.type == 4 || this.adaptee.type == 8 || this.adaptee.type == 2 || this.adaptee.type == 3) && this.adaptee.textarray != null && this.adaptee.start < this.adaptee.end) {
            value = TidyUtils.getString(this.adaptee.textarray, this.adaptee.start, this.adaptee.end - this.adaptee.start);
        }
        return value;
    }

    public void setNodeValue(String nodeValue) {
        if (this.adaptee.type == 4 || this.adaptee.type == 8 || this.adaptee.type == 2 || this.adaptee.type == 3) {
            byte[] textarray = TidyUtils.getBytes(nodeValue);
            this.adaptee.textarray = textarray;
            this.adaptee.start = 0;
            this.adaptee.end = textarray.length;
        }
    }

    public String getNodeName() {
        return this.adaptee.element;
    }

    public short getNodeType() {
        short result = -1;
        switch (this.adaptee.type) {
            case 0: {
                result = 9;
                break;
            }
            case 1: {
                result = 10;
                break;
            }
            case 2: {
                result = 8;
                break;
            }
            case 3: {
                result = 7;
                break;
            }
            case 4: {
                result = 3;
                break;
            }
            case 8: {
                result = 4;
                break;
            }
            case 5: 
            case 7: {
                result = 1;
            }
        }
        return result;
    }

    public org.w3c.dom.Node getParentNode() {
        if (this.adaptee.parent != null) {
            return this.adaptee.parent.getAdapter();
        }
        return null;
    }

    public NodeList getChildNodes() {
        return new DOMNodeListImpl(this.adaptee);
    }

    public org.w3c.dom.Node getFirstChild() {
        if (this.adaptee.content != null) {
            return this.adaptee.content.getAdapter();
        }
        return null;
    }

    public org.w3c.dom.Node getLastChild() {
        if (this.adaptee.last != null) {
            return this.adaptee.last.getAdapter();
        }
        return null;
    }

    public org.w3c.dom.Node getPreviousSibling() {
        if (this.adaptee.prev != null) {
            return this.adaptee.prev.getAdapter();
        }
        return null;
    }

    public org.w3c.dom.Node getNextSibling() {
        if (this.adaptee.next != null) {
            return this.adaptee.next.getAdapter();
        }
        return null;
    }

    public NamedNodeMap getAttributes() {
        return new DOMAttrMapImpl(this.adaptee.attributes);
    }

    public Document getOwnerDocument() {
        Node node = this.adaptee;
        if (node != null && node.type == 0) {
            return null;
        }
        while (node != null && node.type != 0) {
            node = node.parent;
        }
        if (node != null) {
            return (Document)node.getAdapter();
        }
        return null;
    }

    public org.w3c.dom.Node insertBefore(org.w3c.dom.Node newChild, org.w3c.dom.Node refChild) {
        if (newChild == null) {
            return null;
        }
        if (!(newChild instanceof DOMNodeImpl)) {
            throw new DOMException(4, "newChild not instanceof DOMNodeImpl");
        }
        DOMNodeImpl newCh = (DOMNodeImpl)newChild;
        if (this.adaptee.type == 0 ? newCh.adaptee.type != 1 && newCh.adaptee.type != 3 : this.adaptee.type == 5 && newCh.adaptee.type != 5 && newCh.adaptee.type != 7 && newCh.adaptee.type != 2 && newCh.adaptee.type != 4 && newCh.adaptee.type != 8) {
            throw new DOMException(3, "newChild cannot be a child of this node");
        }
        if (refChild == null) {
            this.adaptee.insertNodeAtEnd(newCh.adaptee);
            if (this.adaptee.type == 7) {
                this.adaptee.setType((short)5);
            }
        } else {
            Node ref = this.adaptee.content;
            while (ref != null && ref.getAdapter() != refChild) {
                ref = ref.next;
            }
            if (ref == null) {
                throw new DOMException(8, "refChild not found");
            }
            Node.insertNodeBeforeElement(ref, newCh.adaptee);
        }
        return newChild;
    }

    public org.w3c.dom.Node replaceChild(org.w3c.dom.Node newChild, org.w3c.dom.Node oldChild) {
        if (newChild == null) {
            return null;
        }
        if (!(newChild instanceof DOMNodeImpl)) {
            throw new DOMException(4, "newChild not instanceof DOMNodeImpl");
        }
        DOMNodeImpl newCh = (DOMNodeImpl)newChild;
        if (this.adaptee.type == 0 ? newCh.adaptee.type != 1 && newCh.adaptee.type != 3 : this.adaptee.type == 5 && newCh.adaptee.type != 5 && newCh.adaptee.type != 7 && newCh.adaptee.type != 2 && newCh.adaptee.type != 4 && newCh.adaptee.type != 8) {
            throw new DOMException(3, "newChild cannot be a child of this node");
        }
        if (oldChild == null) {
            throw new DOMException(8, "oldChild not found");
        }
        Node ref = this.adaptee.content;
        while (ref != null && ref.getAdapter() != oldChild) {
            ref = ref.next;
        }
        if (ref == null) {
            throw new DOMException(8, "oldChild not found");
        }
        newCh.adaptee.next = ref.next;
        newCh.adaptee.prev = ref.prev;
        newCh.adaptee.last = ref.last;
        newCh.adaptee.parent = ref.parent;
        newCh.adaptee.content = ref.content;
        if (ref.parent != null) {
            if (ref.parent.content == ref) {
                ref.parent.content = newCh.adaptee;
            }
            if (ref.parent.last == ref) {
                ref.parent.last = newCh.adaptee;
            }
        }
        if (ref.prev != null) {
            ref.prev.next = newCh.adaptee;
        }
        if (ref.next != null) {
            ref.next.prev = newCh.adaptee;
        }
        Node n = ref.content;
        while (n != null) {
            if (n.parent == ref) {
                n.parent = newCh.adaptee;
            }
            n = n.next;
        }
        return oldChild;
    }

    public org.w3c.dom.Node removeChild(org.w3c.dom.Node oldChild) {
        if (oldChild == null) {
            return null;
        }
        Node ref = this.adaptee.content;
        while (ref != null && ref.getAdapter() != oldChild) {
            ref = ref.next;
        }
        if (ref == null) {
            throw new DOMException(8, "refChild not found");
        }
        Node.discardElement(ref);
        if (this.adaptee.content == null && this.adaptee.type == 5) {
            this.adaptee.setType((short)7);
        }
        return oldChild;
    }

    public org.w3c.dom.Node appendChild(org.w3c.dom.Node newChild) {
        if (newChild == null) {
            return null;
        }
        if (!(newChild instanceof DOMNodeImpl)) {
            throw new DOMException(4, "newChild not instanceof DOMNodeImpl");
        }
        DOMNodeImpl newCh = (DOMNodeImpl)newChild;
        if (this.adaptee.type == 0 ? newCh.adaptee.type != 1 && newCh.adaptee.type != 3 : this.adaptee.type == 5 && newCh.adaptee.type != 5 && newCh.adaptee.type != 7 && newCh.adaptee.type != 2 && newCh.adaptee.type != 4 && newCh.adaptee.type != 8) {
            throw new DOMException(3, "newChild cannot be a child of this node");
        }
        this.adaptee.insertNodeAtEnd(newCh.adaptee);
        if (this.adaptee.type == 7) {
            this.adaptee.setType((short)5);
        }
        return newChild;
    }

    public boolean hasChildNodes() {
        return this.adaptee.content != null;
    }

    public org.w3c.dom.Node cloneNode(boolean deep) {
        Node node = this.adaptee.cloneNode(deep);
        node.parent = null;
        return node.getAdapter();
    }

    public void normalize() {
    }

    public boolean supports(String feature, String version) {
        return this.isSupported(feature, version);
    }

    public String getNamespaceURI() {
        return null;
    }

    public String getPrefix() {
        return null;
    }

    public void setPrefix(String prefix) throws DOMException {
    }

    public String getLocalName() {
        return this.getNodeName();
    }

    public boolean isSupported(String feature, String version) {
        return false;
    }

    public boolean hasAttributes() {
        return this.adaptee.attributes != null;
    }

    public short compareDocumentPosition(org.w3c.dom.Node other) throws DOMException {
        throw new DOMException(9, "DOM method not supported");
    }

    public String getBaseURI() {
        return null;
    }

    public Object getFeature(String feature, String version) {
        return null;
    }

    public String getTextContent() throws DOMException {
        return null;
    }

    public Object getUserData(String key) {
        return null;
    }

    public boolean isDefaultNamespace(String namespaceURI) {
        return false;
    }

    public boolean isEqualNode(org.w3c.dom.Node arg) {
        return false;
    }

    public boolean isSameNode(org.w3c.dom.Node other) {
        return false;
    }

    public String lookupNamespaceURI(String prefix) {
        return null;
    }

    public String lookupPrefix(String namespaceURI) {
        return null;
    }

    public void setTextContent(String textContent) throws DOMException {
        throw new DOMException(7, "Node is read only");
    }

    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return null;
    }
}

