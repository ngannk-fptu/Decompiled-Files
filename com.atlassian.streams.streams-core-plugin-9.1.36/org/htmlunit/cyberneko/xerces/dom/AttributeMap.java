/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import java.util.ArrayList;
import java.util.List;
import org.htmlunit.cyberneko.xerces.dom.AttrImpl;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMMessageFormatter;
import org.htmlunit.cyberneko.xerces.dom.ElementImpl;
import org.htmlunit.cyberneko.xerces.dom.NamedNodeMapImpl;
import org.htmlunit.cyberneko.xerces.dom.NodeImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class AttributeMap
extends NamedNodeMapImpl {
    protected AttributeMap(ElementImpl ownerNode) {
        super(ownerNode);
    }

    @Override
    public Node setNamedItem(Node arg) throws DOMException {
        AttrImpl argn;
        boolean errCheck = this.ownerNode.ownerDocument().errorChecking;
        if (errCheck) {
            if (arg.getOwnerDocument() != this.ownerNode.ownerDocument()) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(4, msg);
            }
            if (arg.getNodeType() != 2) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException(3, msg);
            }
        }
        if ((argn = (AttrImpl)arg).isOwned()) {
            if (errCheck && argn.getOwnerElement() != this.ownerNode) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INUSE_ATTRIBUTE_ERR", null);
                throw new DOMException(10, msg);
            }
            return arg;
        }
        argn.ownerNode = this.ownerNode;
        argn.isOwned(true);
        int i = this.findNamePoint(argn.getNodeName());
        AttrImpl previous = null;
        if (i >= 0) {
            previous = (AttrImpl)this.nodes.get(i);
            this.nodes.set(i, arg);
            previous.ownerNode = this.ownerNode.ownerDocument();
            previous.isOwned(false);
            previous.isSpecified(true);
        } else {
            i = -1 - i;
            if (null == this.nodes) {
                this.nodes = new ArrayList(5);
            }
            this.nodes.add(i, arg);
        }
        this.ownerNode.ownerDocument().setAttrNode(argn, previous);
        if (!argn.isNormalized()) {
            this.ownerNode.isNormalized(false);
        }
        return previous;
    }

    @Override
    public Node setNamedItemNS(Node arg) throws DOMException {
        AttrImpl argn;
        boolean errCheck = this.ownerNode.ownerDocument().errorChecking;
        if (errCheck) {
            if (arg.getOwnerDocument() != this.ownerNode.ownerDocument()) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(4, msg);
            }
            if (arg.getNodeType() != 2) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new DOMException(3, msg);
            }
        }
        if ((argn = (AttrImpl)arg).isOwned()) {
            if (errCheck && argn.getOwnerElement() != this.ownerNode) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INUSE_ATTRIBUTE_ERR", null);
                throw new DOMException(10, msg);
            }
            return arg;
        }
        argn.ownerNode = this.ownerNode;
        argn.isOwned(true);
        int i = this.findNamePoint(argn.getNamespaceURI(), argn.getLocalName());
        AttrImpl previous = null;
        if (i >= 0) {
            previous = (AttrImpl)this.nodes.get(i);
            this.nodes.set(i, arg);
            previous.ownerNode = this.ownerNode.ownerDocument();
            previous.isOwned(false);
            previous.isSpecified(true);
        } else {
            i = this.findNamePoint(arg.getNodeName());
            if (i >= 0) {
                previous = (AttrImpl)this.nodes.get(i);
                this.nodes.add(i, arg);
            } else {
                i = -1 - i;
                if (null == this.nodes) {
                    this.nodes = new ArrayList(5);
                }
                this.nodes.add(i, arg);
            }
        }
        this.ownerNode.ownerDocument().setAttrNode(argn, previous);
        if (!argn.isNormalized()) {
            this.ownerNode.isNormalized(false);
        }
        return previous;
    }

    @Override
    public Node removeNamedItem(String name) throws DOMException {
        return this.internalRemoveNamedItem(name, true);
    }

    protected Node removeItem(Node item) throws DOMException {
        int index = -1;
        if (this.nodes != null) {
            int size = this.nodes.size();
            for (int i = 0; i < size; ++i) {
                if (this.nodes.get(i) != item) continue;
                index = i;
                break;
            }
        }
        if (index < 0) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, msg);
        }
        return this.remove((AttrImpl)item, index);
    }

    protected final Node internalRemoveNamedItem(String name, boolean raiseEx) {
        int i = this.findNamePoint(name);
        if (i < 0) {
            if (raiseEx) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
                throw new DOMException(8, msg);
            }
            return null;
        }
        return this.remove((AttrImpl)this.nodes.get(i), i);
    }

    private Node remove(AttrImpl attr, int index) {
        CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
        String name = attr.getNodeName();
        if (attr.isIdAttribute()) {
            ownerDocument.removeIdentifier(attr.getValue());
        }
        this.nodes.remove(index);
        attr.ownerNode = ownerDocument;
        attr.isOwned(false);
        attr.isSpecified(true);
        attr.isIdAttribute(false);
        ownerDocument.removedAttrNode(attr, this.ownerNode, name);
        return attr;
    }

    @Override
    public Node removeNamedItemNS(String namespaceURI, String name) throws DOMException {
        return this.internalRemoveNamedItemNS(namespaceURI, name, true);
    }

    protected final Node internalRemoveNamedItemNS(String namespaceURI, String name, boolean raiseEx) {
        CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
        int i = this.findNamePoint(namespaceURI, name);
        if (i < 0) {
            if (raiseEx) {
                String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
                throw new DOMException(8, msg);
            }
            return null;
        }
        AttrImpl n = (AttrImpl)this.nodes.get(i);
        if (n.isIdAttribute()) {
            ownerDocument.removeIdentifier(n.getValue());
        }
        this.nodes.remove(i);
        n.ownerNode = ownerDocument;
        n.isOwned(false);
        n.isSpecified(true);
        n.isIdAttribute(false);
        ownerDocument.removedAttrNode(n, this.ownerNode, name);
        return n;
    }

    @Override
    public NamedNodeMapImpl cloneMap(NodeImpl ownerNode) {
        AttributeMap newmap = new AttributeMap((ElementImpl)ownerNode);
        newmap.hasDefaults(this.hasDefaults());
        newmap.cloneContent(this);
        return newmap;
    }

    @Override
    protected void cloneContent(NamedNodeMapImpl srcmap) {
        int size;
        List<Node> srcnodes = srcmap.nodes;
        if (srcnodes != null && (size = srcnodes.size()) != 0) {
            if (this.nodes == null) {
                this.nodes = new ArrayList(size);
            } else {
                this.nodes.clear();
            }
            for (Node srcnode : srcnodes) {
                NodeImpl n = (NodeImpl)srcnode;
                NodeImpl clone = (NodeImpl)n.cloneNode(true);
                clone.isSpecified(n.isSpecified());
                this.nodes.add(clone);
                clone.ownerNode = this.ownerNode;
                clone.isOwned(true);
            }
        }
    }

    void moveSpecifiedAttributes(AttributeMap srcmap) {
        int nsize = srcmap.nodes != null ? srcmap.nodes.size() : 0;
        for (int i = nsize - 1; i >= 0; --i) {
            AttrImpl attr = (AttrImpl)srcmap.nodes.get(i);
            if (!attr.isSpecified()) continue;
            srcmap.remove(attr, i);
            if (attr.getLocalName() != null) {
                this.setNamedItem(attr);
                continue;
            }
            this.setNamedItemNS(attr);
        }
    }

    @Override
    protected final int addItem(Node arg) {
        AttrImpl argn = (AttrImpl)arg;
        argn.ownerNode = this.ownerNode;
        argn.isOwned(true);
        int i = this.findNamePoint(argn.getNamespaceURI(), argn.getLocalName());
        if (i >= 0) {
            this.nodes.set(i, arg);
        } else {
            i = this.findNamePoint(argn.getNodeName());
            if (i >= 0) {
                this.nodes.add(i, arg);
            } else {
                i = -1 - i;
                if (null == this.nodes) {
                    this.nodes = new ArrayList(5);
                }
                this.nodes.add(i, arg);
            }
        }
        this.ownerNode.ownerDocument().setAttrNode(argn, null);
        return i;
    }
}

