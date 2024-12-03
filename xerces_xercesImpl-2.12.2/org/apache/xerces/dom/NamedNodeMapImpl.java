/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapImpl
implements NamedNodeMap,
Serializable {
    static final long serialVersionUID = -7039242451046758020L;
    protected short flags;
    protected static final short READONLY = 1;
    protected static final short CHANGED = 2;
    protected static final short HASDEFAULTS = 4;
    protected List nodes;
    protected NodeImpl ownerNode;

    protected NamedNodeMapImpl(NodeImpl nodeImpl) {
        this.ownerNode = nodeImpl;
    }

    @Override
    public int getLength() {
        return this.nodes != null ? this.nodes.size() : 0;
    }

    @Override
    public Node item(int n) {
        return this.nodes != null && n < this.nodes.size() ? (Node)this.nodes.get(n) : null;
    }

    @Override
    public Node getNamedItem(String string) {
        int n = this.findNamePoint(string, 0);
        return n < 0 ? null : (Node)this.nodes.get(n);
    }

    @Override
    public Node getNamedItemNS(String string, String string2) {
        int n = this.findNamePoint(string, string2);
        return n < 0 ? null : (Node)this.nodes.get(n);
    }

    @Override
    public Node setNamedItem(Node node) throws DOMException {
        CoreDocumentImpl coreDocumentImpl = this.ownerNode.ownerDocument();
        if (coreDocumentImpl.errorChecking) {
            if (this.isReadOnly()) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, string);
            }
            if (node.getOwnerDocument() != coreDocumentImpl) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(4, string);
            }
        }
        int n = this.findNamePoint(node.getNodeName(), 0);
        NodeImpl nodeImpl = null;
        if (n >= 0) {
            nodeImpl = (NodeImpl)this.nodes.get(n);
            this.nodes.set(n, node);
        } else {
            n = -1 - n;
            if (null == this.nodes) {
                this.nodes = new ArrayList(5);
            }
            this.nodes.add(n, node);
        }
        return nodeImpl;
    }

    @Override
    public Node setNamedItemNS(Node node) throws DOMException {
        CoreDocumentImpl coreDocumentImpl = this.ownerNode.ownerDocument();
        if (coreDocumentImpl.errorChecking) {
            if (this.isReadOnly()) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
                throw new DOMException(7, string);
            }
            if (node.getOwnerDocument() != coreDocumentImpl) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "WRONG_DOCUMENT_ERR", null);
                throw new DOMException(4, string);
            }
        }
        int n = this.findNamePoint(node.getNamespaceURI(), node.getLocalName());
        NodeImpl nodeImpl = null;
        if (n >= 0) {
            nodeImpl = (NodeImpl)this.nodes.get(n);
            this.nodes.set(n, node);
        } else {
            n = this.findNamePoint(node.getNodeName(), 0);
            if (n >= 0) {
                nodeImpl = (NodeImpl)this.nodes.get(n);
                this.nodes.add(n, node);
            } else {
                n = -1 - n;
                if (null == this.nodes) {
                    this.nodes = new ArrayList(5);
                }
                this.nodes.add(n, node);
            }
        }
        return nodeImpl;
    }

    @Override
    public Node removeNamedItem(String string) throws DOMException {
        if (this.isReadOnly()) {
            String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string2);
        }
        int n = this.findNamePoint(string, 0);
        if (n < 0) {
            String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, string3);
        }
        NodeImpl nodeImpl = (NodeImpl)this.nodes.get(n);
        this.nodes.remove(n);
        return nodeImpl;
    }

    @Override
    public Node removeNamedItemNS(String string, String string2) throws DOMException {
        if (this.isReadOnly()) {
            String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NO_MODIFICATION_ALLOWED_ERR", null);
            throw new DOMException(7, string3);
        }
        int n = this.findNamePoint(string, string2);
        if (n < 0) {
            String string4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_FOUND_ERR", null);
            throw new DOMException(8, string4);
        }
        NodeImpl nodeImpl = (NodeImpl)this.nodes.get(n);
        this.nodes.remove(n);
        return nodeImpl;
    }

    public NamedNodeMapImpl cloneMap(NodeImpl nodeImpl) {
        NamedNodeMapImpl namedNodeMapImpl = new NamedNodeMapImpl(nodeImpl);
        namedNodeMapImpl.cloneContent(this);
        return namedNodeMapImpl;
    }

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
                NodeImpl nodeImpl = (NodeImpl)namedNodeMapImpl.nodes.get(i);
                NodeImpl nodeImpl2 = (NodeImpl)nodeImpl.cloneNode(true);
                nodeImpl2.isSpecified(nodeImpl.isSpecified());
                this.nodes.add(nodeImpl2);
            }
        }
    }

    void setReadOnly(boolean bl, boolean bl2) {
        this.isReadOnly(bl);
        if (bl2 && this.nodes != null) {
            for (int i = this.nodes.size() - 1; i >= 0; --i) {
                ((NodeImpl)this.nodes.get(i)).setReadOnly(bl, bl2);
            }
        }
    }

    boolean getReadOnly() {
        return this.isReadOnly();
    }

    protected void setOwnerDocument(CoreDocumentImpl coreDocumentImpl) {
        if (this.nodes != null) {
            int n = this.nodes.size();
            for (int i = 0; i < n; ++i) {
                ((NodeImpl)this.item(i)).setOwnerDocument(coreDocumentImpl);
            }
        }
    }

    final boolean isReadOnly() {
        return (this.flags & 1) != 0;
    }

    final void isReadOnly(boolean bl) {
        this.flags = (short)(bl ? this.flags | 1 : this.flags & 0xFFFFFFFE);
    }

    final boolean changed() {
        return (this.flags & 2) != 0;
    }

    final void changed(boolean bl) {
        this.flags = (short)(bl ? this.flags | 2 : this.flags & 0xFFFFFFFD);
    }

    final boolean hasDefaults() {
        return (this.flags & 4) != 0;
    }

    final void hasDefaults(boolean bl) {
        this.flags = (short)(bl ? this.flags | 4 : this.flags & 0xFFFFFFFB);
    }

    protected int findNamePoint(String string, int n) {
        int n2 = 0;
        if (this.nodes != null) {
            int n3 = n;
            int n4 = this.nodes.size() - 1;
            while (n3 <= n4) {
                n2 = (n3 + n4) / 2;
                int n5 = string.compareTo(((Node)this.nodes.get(n2)).getNodeName());
                if (n5 == 0) {
                    return n2;
                }
                if (n5 < 0) {
                    n4 = n2 - 1;
                    continue;
                }
                n3 = n2 + 1;
            }
            if (n3 > n2) {
                n2 = n3;
            }
        }
        return -1 - n2;
    }

    protected int findNamePoint(String string, String string2) {
        if (this.nodes == null) {
            return -1;
        }
        if (string2 == null) {
            return -1;
        }
        int n = this.nodes.size();
        for (int i = 0; i < n; ++i) {
            NodeImpl nodeImpl = (NodeImpl)this.nodes.get(i);
            String string3 = nodeImpl.getNamespaceURI();
            String string4 = nodeImpl.getLocalName();
            if (!(string == null ? string3 == null && (string2.equals(string4) || string4 == null && string2.equals(nodeImpl.getNodeName())) : string.equals(string3) && string2.equals(string4))) continue;
            return i;
        }
        return -1;
    }

    protected boolean precedes(Node node, Node node2) {
        if (this.nodes != null) {
            int n = this.nodes.size();
            for (int i = 0; i < n; ++i) {
                Node node3 = (Node)this.nodes.get(i);
                if (node3 == node) {
                    return true;
                }
                if (node3 != node2) continue;
                return false;
            }
        }
        return false;
    }

    protected void removeItem(int n) {
        if (this.nodes != null && n < this.nodes.size()) {
            this.nodes.remove(n);
        }
    }

    protected Object getItem(int n) {
        if (this.nodes != null) {
            return this.nodes.get(n);
        }
        return null;
    }

    protected int addItem(Node node) {
        int n = this.findNamePoint(node.getNamespaceURI(), node.getLocalName());
        if (n >= 0) {
            this.nodes.set(n, node);
        } else {
            n = this.findNamePoint(node.getNodeName(), 0);
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
        return n;
    }

    protected ArrayList cloneMap(ArrayList arrayList) {
        if (arrayList == null) {
            arrayList = new ArrayList(5);
        }
        arrayList.clear();
        if (this.nodes != null) {
            int n = this.nodes.size();
            for (int i = 0; i < n; ++i) {
                arrayList.add(this.nodes.get(i));
            }
        }
        return arrayList;
    }

    protected int getNamedItemIndex(String string, String string2) {
        return this.findNamePoint(string, string2);
    }

    public void removeAll() {
        if (this.nodes != null) {
            this.nodes.clear();
        }
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.nodes != null) {
            this.nodes = new ArrayList((Vector)this.nodes);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        List list = this.nodes;
        try {
            if (list != null) {
                this.nodes = new Vector(list);
            }
            objectOutputStream.defaultWriteObject();
        }
        finally {
            this.nodes = list;
        }
    }
}

