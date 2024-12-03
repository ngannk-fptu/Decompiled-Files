/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.dom;

import java.util.ArrayList;
import java.util.List;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMMessageFormatter;
import org.htmlunit.cyberneko.xerces.dom.NodeImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapImpl
implements NamedNodeMap {
    protected short flags;
    protected static final short READONLY = 1;
    protected static final short CHANGED = 2;
    protected static final short HASDEFAULTS = 4;
    protected List<Node> nodes;
    protected final NodeImpl ownerNode;

    protected NamedNodeMapImpl(NodeImpl ownerNode) {
        this.ownerNode = ownerNode;
    }

    @Override
    public int getLength() {
        return this.nodes != null ? this.nodes.size() : 0;
    }

    @Override
    public Node item(int index) {
        return this.nodes != null && index < this.nodes.size() ? this.nodes.get(index) : null;
    }

    @Override
    public Node getNamedItem(String name) {
        int i = this.findNamePoint(name);
        return i < 0 ? null : this.nodes.get(i);
    }

    @Override
    public Node getNamedItemNS(String namespaceURI, String localName) {
        int i = this.findNamePoint(namespaceURI, localName);
        return i < 0 ? null : this.nodes.get(i);
    }

    @Override
    public Node setNamedItem(Node arg) throws DOMException {
        CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
        if (ownerDocument.errorChecking && arg.getOwnerDocument() != ownerDocument) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
            throw new DOMException(4, msg);
        }
        int i = this.findNamePoint(arg.getNodeName());
        NodeImpl previous = null;
        if (i >= 0) {
            previous = (NodeImpl)this.nodes.get(i);
            this.nodes.set(i, arg);
        } else {
            i = -1 - i;
            if (null == this.nodes) {
                this.nodes = new ArrayList<Node>(5);
            }
            this.nodes.add(i, arg);
        }
        return previous;
    }

    @Override
    public Node setNamedItemNS(Node arg) throws DOMException {
        CoreDocumentImpl ownerDocument = this.ownerNode.ownerDocument();
        if (ownerDocument.errorChecking && arg.getOwnerDocument() != ownerDocument) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
            throw new DOMException(4, msg);
        }
        int i = this.findNamePoint(arg.getNamespaceURI(), arg.getLocalName());
        NodeImpl previous = null;
        if (i >= 0) {
            previous = (NodeImpl)this.nodes.get(i);
            this.nodes.set(i, arg);
        } else {
            i = this.findNamePoint(arg.getNodeName());
            if (i >= 0) {
                previous = (NodeImpl)this.nodes.get(i);
                this.nodes.add(i, arg);
            } else {
                i = -1 - i;
                if (null == this.nodes) {
                    this.nodes = new ArrayList<Node>(5);
                }
                this.nodes.add(i, arg);
            }
        }
        return previous;
    }

    @Override
    public Node removeNamedItem(String name) throws DOMException {
        int i = this.findNamePoint(name);
        if (i < 0) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, msg);
        }
        NodeImpl n = (NodeImpl)this.nodes.get(i);
        this.nodes.remove(i);
        return n;
    }

    @Override
    public Node removeNamedItemNS(String namespaceURI, String name) throws DOMException {
        int i = this.findNamePoint(namespaceURI, name);
        if (i < 0) {
            String msg = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, msg);
        }
        NodeImpl n = (NodeImpl)this.nodes.get(i);
        this.nodes.remove(i);
        return n;
    }

    public NamedNodeMapImpl cloneMap(NodeImpl ownerNode) {
        NamedNodeMapImpl newmap = new NamedNodeMapImpl(ownerNode);
        newmap.cloneContent(this);
        return newmap;
    }

    protected void cloneContent(NamedNodeMapImpl srcmap) {
        int size;
        List<Node> srcnodes = srcmap.nodes;
        if (srcnodes != null && (size = srcnodes.size()) != 0) {
            if (this.nodes == null) {
                this.nodes = new ArrayList<Node>(size);
            } else {
                this.nodes.clear();
            }
            for (int i = 0; i < size; ++i) {
                NodeImpl n = (NodeImpl)srcmap.nodes.get(i);
                NodeImpl clone = (NodeImpl)n.cloneNode(true);
                clone.isSpecified(n.isSpecified());
                this.nodes.add(clone);
            }
        }
    }

    protected void setOwnerDocument(CoreDocumentImpl doc) {
        if (this.nodes != null) {
            int size = this.nodes.size();
            for (int i = 0; i < size; ++i) {
                ((NodeImpl)this.item(i)).setOwnerDocument(doc);
            }
        }
    }

    final boolean changed() {
        return (this.flags & 2) != 0;
    }

    final void changed(boolean value) {
        this.flags = (short)(value ? this.flags | 2 : this.flags & 0xFFFFFFFD);
    }

    final boolean hasDefaults() {
        return (this.flags & 4) != 0;
    }

    final void hasDefaults(boolean value) {
        this.flags = (short)(value ? this.flags | 4 : this.flags & 0xFFFFFFFB);
    }

    protected int findNamePoint(String name) {
        int i = 0;
        if (this.nodes != null) {
            int first = 0;
            int last = this.nodes.size() - 1;
            while (first <= last) {
                i = (first + last) / 2;
                int test = name.compareTo(this.nodes.get(i).getNodeName());
                if (test == 0) {
                    return i;
                }
                if (test < 0) {
                    last = i - 1;
                    continue;
                }
                first = i + 1;
            }
            if (first > i) {
                i = first;
            }
        }
        return -1 - i;
    }

    protected int findNamePoint(String namespaceURI, String name) {
        if (this.nodes == null || name == null) {
            return -1;
        }
        int size = this.nodes.size();
        for (int i = 0; i < size; ++i) {
            NodeImpl a = (NodeImpl)this.nodes.get(i);
            String aNamespaceURI = a.getNamespaceURI();
            String aLocalName = a.getLocalName();
            if (!(namespaceURI == null ? aNamespaceURI == null && (name.equals(aLocalName) || aLocalName == null && name.equals(a.getNodeName())) : namespaceURI.equals(aNamespaceURI) && name.equals(aLocalName))) continue;
            return i;
        }
        return -1;
    }

    protected boolean precedes(Node a, Node b) {
        if (this.nodes != null) {
            for (Node node : this.nodes) {
                if (node == a) {
                    return true;
                }
                if (node != b) continue;
                return false;
            }
        }
        return false;
    }

    protected void removeItem(int index) {
        if (this.nodes != null && index < this.nodes.size()) {
            this.nodes.remove(index);
        }
    }

    protected Node getItem(int index) {
        if (this.nodes != null) {
            return this.nodes.get(index);
        }
        return null;
    }

    protected int addItem(Node arg) {
        int i = this.findNamePoint(arg.getNamespaceURI(), arg.getLocalName());
        if (i >= 0) {
            this.nodes.set(i, arg);
        } else {
            i = this.findNamePoint(arg.getNodeName());
            if (i >= 0) {
                this.nodes.add(i, arg);
            } else {
                i = -1 - i;
                if (null == this.nodes) {
                    this.nodes = new ArrayList<Node>(5);
                }
                this.nodes.add(i, arg);
            }
        }
        return i;
    }

    protected int getNamedItemIndex(String namespaceURI, String localName) {
        return this.findNamePoint(namespaceURI, localName);
    }

    public void removeAll() {
        if (this.nodes != null) {
            this.nodes.clear();
        }
    }
}

