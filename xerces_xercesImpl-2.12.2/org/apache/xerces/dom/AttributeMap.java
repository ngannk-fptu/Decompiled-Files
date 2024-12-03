/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.util.ArrayList;
import java.util.List;
import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.AttrNSImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.NamedNodeMapImpl;
import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class AttributeMap
extends NamedNodeMapImpl {
    static final long serialVersionUID = 8872606282138665383L;

    protected AttributeMap(ElementImpl elementImpl, NamedNodeMapImpl namedNodeMapImpl) {
        super(elementImpl);
        if (namedNodeMapImpl != null) {
            this.cloneContent(namedNodeMapImpl);
            if (this.nodes != null) {
                this.hasDefaults(true);
            }
        }
    }

    @Override
    public Node setNamedItem(Node node) throws DOMException {
        AttrImpl attrImpl;
        boolean bl = this.ownerNode.ownerDocument().errorChecking;
        if (bl) {
            if (this.isReadOnly()) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, string);
            }
            if (node.getOwnerDocument() != this.ownerNode.ownerDocument()) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(4, string);
            }
            if (node.getNodeType() != 2) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException(3, string);
            }
        }
        if ((attrImpl = (AttrImpl)node).isOwned()) {
            if (bl && attrImpl.getOwnerElement() != this.ownerNode) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INUSE_ATTRIBUTE_ERR", null);
                throw new DOMException(10, string);
            }
            return node;
        }
        attrImpl.ownerNode = this.ownerNode;
        attrImpl.isOwned(true);
        int n = this.findNamePoint(attrImpl.getNodeName(), 0);
        AttrImpl attrImpl2 = null;
        if (n >= 0) {
            attrImpl2 = (AttrImpl)this.nodes.get(n);
            this.nodes.set(n, node);
            attrImpl2.ownerNode = this.ownerNode.ownerDocument();
            attrImpl2.isOwned(false);
            attrImpl2.isSpecified(true);
        } else {
            n = -1 - n;
            if (null == this.nodes) {
                this.nodes = new ArrayList(5);
            }
            this.nodes.add(n, node);
        }
        this.ownerNode.ownerDocument().setAttrNode(attrImpl, attrImpl2);
        if (!attrImpl.isNormalized()) {
            this.ownerNode.isNormalized(false);
        }
        return attrImpl2;
    }

    @Override
    public Node setNamedItemNS(Node node) throws DOMException {
        AttrImpl attrImpl;
        boolean bl = this.ownerNode.ownerDocument().errorChecking;
        if (bl) {
            if (this.isReadOnly()) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, string);
            }
            if (node.getOwnerDocument() != this.ownerNode.ownerDocument()) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(4, string);
            }
            if (node.getNodeType() != 2) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException(3, string);
            }
        }
        if ((attrImpl = (AttrImpl)node).isOwned()) {
            if (bl && attrImpl.getOwnerElement() != this.ownerNode) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INUSE_ATTRIBUTE_ERR", null);
                throw new DOMException(10, string);
            }
            return node;
        }
        attrImpl.ownerNode = this.ownerNode;
        attrImpl.isOwned(true);
        int n = this.findNamePoint(attrImpl.getNamespaceURI(), attrImpl.getLocalName());
        AttrImpl attrImpl2 = null;
        if (n >= 0) {
            attrImpl2 = (AttrImpl)this.nodes.get(n);
            this.nodes.set(n, node);
            attrImpl2.ownerNode = this.ownerNode.ownerDocument();
            attrImpl2.isOwned(false);
            attrImpl2.isSpecified(true);
        } else {
            n = this.findNamePoint(node.getNodeName(), 0);
            if (n >= 0) {
                attrImpl2 = (AttrImpl)this.nodes.get(n);
                this.nodes.add(n, node);
            } else {
                n = -1 - n;
                if (null == this.nodes) {
                    this.nodes = new ArrayList(5);
                }
                this.nodes.add(n, node);
            }
        }
        this.ownerNode.ownerDocument().setAttrNode(attrImpl, attrImpl2);
        if (!attrImpl.isNormalized()) {
            this.ownerNode.isNormalized(false);
        }
        return attrImpl2;
    }

    @Override
    public Node removeNamedItem(String string) throws DOMException {
        return this.internalRemoveNamedItem(string, true);
    }

    Node safeRemoveNamedItem(String string) {
        return this.internalRemoveNamedItem(string, false);
    }

    protected Node removeItem(Node node, boolean bl) throws DOMException {
        int n = -1;
        if (this.nodes != null) {
            int n2 = this.nodes.size();
            for (int i = 0; i < n2; ++i) {
                if (this.nodes.get(i) != node) continue;
                n = i;
                break;
            }
        }
        if (n < 0) {
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, string);
        }
        return this.remove((AttrImpl)node, n, bl);
    }

    protected final Node internalRemoveNamedItem(String string, boolean bl) {
        if (this.isReadOnly()) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string2);
        }
        int n = this.findNamePoint(string, 0);
        if (n < 0) {
            if (bl) {
                String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
                throw new DOMException(8, string3);
            }
            return null;
        }
        return this.remove((AttrImpl)this.nodes.get(n), n, true);
    }

    private final Node remove(AttrImpl attrImpl, int n, boolean bl) {
        CoreDocumentImpl coreDocumentImpl = this.ownerNode.ownerDocument();
        String string = attrImpl.getNodeName();
        if (attrImpl.isIdAttribute()) {
            coreDocumentImpl.removeIdentifier(attrImpl.getValue());
        }
        if (this.hasDefaults() && bl) {
            Node node;
            NamedNodeMapImpl namedNodeMapImpl = ((ElementImpl)this.ownerNode).getDefaultAttributes();
            if (namedNodeMapImpl != null && (node = namedNodeMapImpl.getNamedItem(string)) != null && this.findNamePoint(string, n + 1) < 0) {
                NodeImpl nodeImpl = (NodeImpl)node.cloneNode(true);
                if (node.getLocalName() != null) {
                    ((AttrNSImpl)nodeImpl).namespaceURI = attrImpl.getNamespaceURI();
                }
                nodeImpl.ownerNode = this.ownerNode;
                nodeImpl.isOwned(true);
                nodeImpl.isSpecified(false);
                this.nodes.set(n, nodeImpl);
                if (attrImpl.isIdAttribute()) {
                    coreDocumentImpl.putIdentifier(nodeImpl.getNodeValue(), (ElementImpl)this.ownerNode);
                }
            } else {
                this.nodes.remove(n);
            }
        } else {
            this.nodes.remove(n);
        }
        attrImpl.ownerNode = coreDocumentImpl;
        attrImpl.isOwned(false);
        attrImpl.isSpecified(true);
        attrImpl.isIdAttribute(false);
        coreDocumentImpl.removedAttrNode(attrImpl, this.ownerNode, string);
        return attrImpl;
    }

    @Override
    public Node removeNamedItemNS(String string, String string2) throws DOMException {
        return this.internalRemoveNamedItemNS(string, string2, true);
    }

    Node safeRemoveNamedItemNS(String string, String string2) {
        return this.internalRemoveNamedItemNS(string, string2, false);
    }

    protected final Node internalRemoveNamedItemNS(String string, String string2, boolean bl) {
        CoreDocumentImpl coreDocumentImpl = this.ownerNode.ownerDocument();
        if (coreDocumentImpl.errorChecking && this.isReadOnly()) {
            String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string3);
        }
        int n = this.findNamePoint(string, string2);
        if (n < 0) {
            if (bl) {
                String string4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
                throw new DOMException(8, string4);
            }
            return null;
        }
        AttrImpl attrImpl = (AttrImpl)this.nodes.get(n);
        if (attrImpl.isIdAttribute()) {
            coreDocumentImpl.removeIdentifier(attrImpl.getValue());
        }
        String string5 = attrImpl.getNodeName();
        if (this.hasDefaults()) {
            Node node;
            NamedNodeMapImpl namedNodeMapImpl = ((ElementImpl)this.ownerNode).getDefaultAttributes();
            if (namedNodeMapImpl != null && (node = namedNodeMapImpl.getNamedItem(string5)) != null) {
                int n2 = this.findNamePoint(string5, 0);
                if (n2 >= 0 && this.findNamePoint(string5, n2 + 1) < 0) {
                    NodeImpl nodeImpl = (NodeImpl)node.cloneNode(true);
                    nodeImpl.ownerNode = this.ownerNode;
                    if (node.getLocalName() != null) {
                        ((AttrNSImpl)nodeImpl).namespaceURI = string;
                    }
                    nodeImpl.isOwned(true);
                    nodeImpl.isSpecified(false);
                    this.nodes.set(n, nodeImpl);
                    if (nodeImpl.isIdAttribute()) {
                        coreDocumentImpl.putIdentifier(nodeImpl.getNodeValue(), (ElementImpl)this.ownerNode);
                    }
                } else {
                    this.nodes.remove(n);
                }
            } else {
                this.nodes.remove(n);
            }
        } else {
            this.nodes.remove(n);
        }
        attrImpl.ownerNode = coreDocumentImpl;
        attrImpl.isOwned(false);
        attrImpl.isSpecified(true);
        attrImpl.isIdAttribute(false);
        coreDocumentImpl.removedAttrNode(attrImpl, this.ownerNode, string2);
        return attrImpl;
    }

    @Override
    public NamedNodeMapImpl cloneMap(NodeImpl nodeImpl) {
        AttributeMap attributeMap = new AttributeMap((ElementImpl)nodeImpl, null);
        attributeMap.hasDefaults(this.hasDefaults());
        attributeMap.cloneContent(this);
        return attributeMap;
    }

    @Override
    protected void cloneContent(NamedNodeMapImpl namedNodeMapImpl) {
        int n;
        List list = namedNodeMapImpl.nodes;
        if (list != null && (n = list.size()) != 0) {
            if (this.nodes == null) {
                this.nodes = new ArrayList(n);
            } else {
                this.nodes.clear();
            }
            for (int i = 0; i < n; ++i) {
                NodeImpl nodeImpl = (NodeImpl)list.get(i);
                NodeImpl nodeImpl2 = (NodeImpl)nodeImpl.cloneNode(true);
                nodeImpl2.isSpecified(nodeImpl.isSpecified());
                this.nodes.add(nodeImpl2);
                nodeImpl2.ownerNode = this.ownerNode;
                nodeImpl2.isOwned(true);
            }
        }
    }

    void moveSpecifiedAttributes(AttributeMap attributeMap) {
        int n = attributeMap.nodes != null ? attributeMap.nodes.size() : 0;
        for (int i = n - 1; i >= 0; --i) {
            AttrImpl attrImpl = (AttrImpl)attributeMap.nodes.get(i);
            if (!attrImpl.isSpecified()) continue;
            attributeMap.remove(attrImpl, i, false);
            if (attrImpl.getLocalName() != null) {
                this.setNamedItem(attrImpl);
                continue;
            }
            this.setNamedItemNS(attrImpl);
        }
    }

    protected void reconcileDefaults(NamedNodeMapImpl namedNodeMapImpl) {
        int n;
        int n2 = this.nodes != null ? this.nodes.size() : 0;
        for (n = n2 - 1; n >= 0; --n) {
            AttrImpl attrImpl = (AttrImpl)this.nodes.get(n);
            if (attrImpl.isSpecified()) continue;
            this.remove(attrImpl, n, false);
        }
        if (namedNodeMapImpl == null) {
            return;
        }
        if (this.nodes == null || this.nodes.size() == 0) {
            this.cloneContent(namedNodeMapImpl);
        } else {
            n = namedNodeMapImpl.nodes.size();
            for (int i = 0; i < n; ++i) {
                AttrImpl attrImpl = (AttrImpl)namedNodeMapImpl.nodes.get(i);
                int n3 = this.findNamePoint(attrImpl.getNodeName(), 0);
                if (n3 >= 0) continue;
                n3 = -1 - n3;
                NodeImpl nodeImpl = (NodeImpl)attrImpl.cloneNode(true);
                nodeImpl.ownerNode = this.ownerNode;
                nodeImpl.isOwned(true);
                nodeImpl.isSpecified(false);
                this.nodes.add(n3, nodeImpl);
            }
        }
    }

    @Override
    protected final int addItem(Node node) {
        AttrImpl attrImpl = (AttrImpl)node;
        attrImpl.ownerNode = this.ownerNode;
        attrImpl.isOwned(true);
        int n = this.findNamePoint(attrImpl.getNamespaceURI(), attrImpl.getLocalName());
        if (n >= 0) {
            this.nodes.set(n, node);
        } else {
            n = this.findNamePoint(attrImpl.getNodeName(), 0);
            if (n >= 0) {
                this.nodes.add(n, node);
            } else {
                n = -1 - n;
                if (null == this.nodes) {
                    this.nodes = new ArrayList(5);
                }
                this.nodes.add(n, node);
            }
        }
        this.ownerNode.ownerDocument().setAttrNode(attrImpl, null);
        return n;
    }
}

