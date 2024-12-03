/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import java.io.Serializable;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractNode;
import org.apache.batik.dom.AbstractText;
import org.apache.batik.dom.ExtendedNode;
import org.apache.batik.dom.events.DOMMutationEvent;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractParentNode
extends AbstractNode {
    protected ChildNodes childNodes;

    @Override
    public NodeList getChildNodes() {
        return this.childNodes == null ? (this.childNodes = new ChildNodes()) : this.childNodes;
    }

    @Override
    public Node getFirstChild() {
        return this.childNodes == null ? null : this.childNodes.firstChild;
    }

    @Override
    public Node getLastChild() {
        return this.childNodes == null ? null : this.childNodes.lastChild;
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        if (refChild != null && (this.childNodes == null || refChild.getParentNode() != this)) {
            throw this.createDOMException((short)8, "child.missing", new Object[]{(int)refChild.getNodeType(), refChild.getNodeName()});
        }
        this.checkAndRemove(newChild, false);
        if (newChild.getNodeType() == 11) {
            Node n = newChild.getFirstChild();
            while (n != null) {
                Node ns = n.getNextSibling();
                this.insertBefore(n, refChild);
                n = ns;
            }
            return newChild;
        }
        if (this.childNodes == null) {
            this.childNodes = new ChildNodes();
        }
        ExtendedNode n = this.childNodes.insert((ExtendedNode)newChild, (ExtendedNode)refChild);
        n.setParentNode(this);
        this.nodeAdded(n);
        this.fireDOMNodeInsertedEvent(n);
        this.fireDOMSubtreeModifiedEvent();
        return n;
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        if (this.childNodes == null || oldChild.getParentNode() != this) {
            throw this.createDOMException((short)8, "child.missing", new Object[]{(int)oldChild.getNodeType(), oldChild.getNodeName()});
        }
        this.checkAndRemove(newChild, true);
        if (newChild.getNodeType() == 11) {
            Node n = newChild.getLastChild();
            if (n == null) {
                return newChild;
            }
            Node ps = n.getPreviousSibling();
            this.replaceChild(n, oldChild);
            Node ns = n;
            n = ps;
            while (n != null) {
                ps = n.getPreviousSibling();
                this.insertBefore(n, ns);
                ns = n;
                n = ps;
            }
            return newChild;
        }
        this.fireDOMNodeRemovedEvent(oldChild);
        this.getCurrentDocument().nodeToBeRemoved(oldChild);
        this.nodeToBeRemoved(oldChild);
        ExtendedNode n = (ExtendedNode)newChild;
        ExtendedNode o = this.childNodes.replace(n, (ExtendedNode)oldChild);
        n.setParentNode(this);
        o.setParentNode(null);
        this.nodeAdded(n);
        this.fireDOMNodeInsertedEvent(n);
        this.fireDOMSubtreeModifiedEvent();
        return n;
    }

    @Override
    public Node removeChild(Node oldChild) throws DOMException {
        if (this.childNodes == null || oldChild.getParentNode() != this) {
            throw this.createDOMException((short)8, "child.missing", new Object[]{(int)oldChild.getNodeType(), oldChild.getNodeName()});
        }
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        this.fireDOMNodeRemovedEvent(oldChild);
        this.getCurrentDocument().nodeToBeRemoved(oldChild);
        this.nodeToBeRemoved(oldChild);
        ExtendedNode result = this.childNodes.remove((ExtendedNode)oldChild);
        result.setParentNode(null);
        this.fireDOMSubtreeModifiedEvent();
        return result;
    }

    @Override
    public Node appendChild(Node newChild) throws DOMException {
        this.checkAndRemove(newChild, false);
        if (newChild.getNodeType() == 11) {
            Node n = newChild.getFirstChild();
            while (n != null) {
                Node ns = n.getNextSibling();
                this.appendChild(n);
                n = ns;
            }
            return newChild;
        }
        if (this.childNodes == null) {
            this.childNodes = new ChildNodes();
        }
        ExtendedNode n = this.childNodes.append((ExtendedNode)newChild);
        n.setParentNode(this);
        this.nodeAdded(n);
        this.fireDOMNodeInsertedEvent(n);
        this.fireDOMSubtreeModifiedEvent();
        return n;
    }

    @Override
    public boolean hasChildNodes() {
        return this.childNodes != null && this.childNodes.getLength() != 0;
    }

    @Override
    public void normalize() {
        Node p = this.getFirstChild();
        if (p != null) {
            p.normalize();
            Node n = p.getNextSibling();
            while (n != null) {
                if (p.getNodeType() == 3 && n.getNodeType() == 3) {
                    String s = p.getNodeValue() + n.getNodeValue();
                    AbstractText at = (AbstractText)p;
                    at.setNodeValue(s);
                    this.removeChild(n);
                    n = p.getNextSibling();
                    continue;
                }
                n.normalize();
                p = n;
                n = n.getNextSibling();
            }
        }
    }

    public NodeList getElementsByTagName(String name) {
        if (name == null) {
            return EMPTY_NODE_LIST;
        }
        AbstractDocument ad = this.getCurrentDocument();
        ElementsByTagName result = ad.getElementsByTagName(this, name);
        if (result == null) {
            result = new ElementsByTagName(name);
            ad.putElementsByTagName(this, name, result);
        }
        return result;
    }

    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
        AbstractDocument ad;
        ElementsByTagNameNS result;
        if (localName == null) {
            return EMPTY_NODE_LIST;
        }
        if (namespaceURI != null && namespaceURI.length() == 0) {
            namespaceURI = null;
        }
        if ((result = (ad = this.getCurrentDocument()).getElementsByTagNameNS(this, namespaceURI, localName)) == null) {
            result = new ElementsByTagNameNS(namespaceURI, localName);
            ad.putElementsByTagNameNS(this, namespaceURI, localName, result);
        }
        return result;
    }

    @Override
    public String getTextContent() {
        StringBuffer sb = new StringBuffer();
        block3: for (Node n = this.getFirstChild(); n != null; n = n.getNextSibling()) {
            switch (n.getNodeType()) {
                case 7: 
                case 8: {
                    continue block3;
                }
                default: {
                    sb.append(((AbstractNode)n).getTextContent());
                }
            }
        }
        return sb.toString();
    }

    @Override
    public void fireDOMNodeInsertedIntoDocumentEvent() {
        AbstractDocument doc = this.getCurrentDocument();
        if (doc.getEventsEnabled()) {
            super.fireDOMNodeInsertedIntoDocumentEvent();
            for (Node n = this.getFirstChild(); n != null; n = n.getNextSibling()) {
                ((AbstractNode)n).fireDOMNodeInsertedIntoDocumentEvent();
            }
        }
    }

    @Override
    public void fireDOMNodeRemovedFromDocumentEvent() {
        AbstractDocument doc = this.getCurrentDocument();
        if (doc.getEventsEnabled()) {
            super.fireDOMNodeRemovedFromDocumentEvent();
            for (Node n = this.getFirstChild(); n != null; n = n.getNextSibling()) {
                ((AbstractNode)n).fireDOMNodeRemovedFromDocumentEvent();
            }
        }
    }

    protected void nodeAdded(Node n) {
    }

    protected void nodeToBeRemoved(Node n) {
    }

    @Override
    protected Node deepExport(Node n, AbstractDocument d) {
        super.deepExport(n, d);
        for (Node p = this.getFirstChild(); p != null; p = p.getNextSibling()) {
            Node t = ((AbstractNode)p).deepExport(p.cloneNode(false), d);
            n.appendChild(t);
        }
        return n;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        super.deepCopyInto(n);
        for (Node p = this.getFirstChild(); p != null; p = p.getNextSibling()) {
            Node t = p.cloneNode(true);
            n.appendChild(t);
        }
        return n;
    }

    protected void fireDOMSubtreeModifiedEvent() {
        AbstractDocument doc = this.getCurrentDocument();
        if (doc.getEventsEnabled()) {
            DOMMutationEvent ev = (DOMMutationEvent)doc.createEvent("MutationEvents");
            ev.initMutationEventNS("http://www.w3.org/2001/xml-events", "DOMSubtreeModified", true, false, null, null, null, null, (short)1);
            this.dispatchEvent(ev);
        }
    }

    protected void fireDOMNodeInsertedEvent(Node node) {
        AbstractDocument doc = this.getCurrentDocument();
        if (doc.getEventsEnabled()) {
            DOMMutationEvent ev = (DOMMutationEvent)doc.createEvent("MutationEvents");
            ev.initMutationEventNS("http://www.w3.org/2001/xml-events", "DOMNodeInserted", true, false, this, null, null, null, (short)2);
            AbstractNode n = (AbstractNode)node;
            n.dispatchEvent(ev);
            n.fireDOMNodeInsertedIntoDocumentEvent();
        }
    }

    protected void fireDOMNodeRemovedEvent(Node node) {
        AbstractDocument doc = this.getCurrentDocument();
        if (doc.getEventsEnabled()) {
            DOMMutationEvent ev = (DOMMutationEvent)doc.createEvent("MutationEvents");
            ev.initMutationEventNS("http://www.w3.org/2001/xml-events", "DOMNodeRemoved", true, false, this, null, null, null, (short)3);
            AbstractNode n = (AbstractNode)node;
            n.dispatchEvent(ev);
            n.fireDOMNodeRemovedFromDocumentEvent();
        }
    }

    protected void checkAndRemove(Node n, boolean replace) {
        this.checkChildType(n, replace);
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        if (n.getOwnerDocument() != this.getCurrentDocument()) {
            throw this.createDOMException((short)4, "node.from.wrong.document", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        if (this == n) {
            throw this.createDOMException((short)3, "add.self", new Object[]{this.getNodeName()});
        }
        Node np = n.getParentNode();
        if (np == null) {
            return;
        }
        for (Node pn = this; pn != null; pn = pn.getParentNode()) {
            if (pn != n) continue;
            throw this.createDOMException((short)3, "add.ancestor", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        np.removeChild(n);
    }

    protected class ChildNodes
    implements NodeList,
    Serializable {
        protected ExtendedNode firstChild;
        protected ExtendedNode lastChild;
        protected int children;
        protected int elementChildren;

        @Override
        public Node item(int index) {
            if (index < 0 || index >= this.children) {
                return null;
            }
            if (index < this.children >> 1) {
                Node n = this.firstChild;
                for (int i = 0; i < index; ++i) {
                    n = n.getNextSibling();
                }
                return n;
            }
            Node n = this.lastChild;
            for (int i = this.children - 1; i > index; --i) {
                n = n.getPreviousSibling();
            }
            return n;
        }

        @Override
        public int getLength() {
            return this.children;
        }

        public ExtendedNode append(ExtendedNode n) {
            if (this.lastChild == null) {
                this.firstChild = n;
            } else {
                this.lastChild.setNextSibling(n);
                n.setPreviousSibling(this.lastChild);
            }
            this.lastChild = n;
            ++this.children;
            if (n.getNodeType() == 1) {
                ++this.elementChildren;
            }
            return n;
        }

        public ExtendedNode insert(ExtendedNode n, ExtendedNode r) {
            if (r == null) {
                return this.append(n);
            }
            if (r == this.firstChild) {
                this.firstChild.setPreviousSibling(n);
                n.setNextSibling(this.firstChild);
                this.firstChild = n;
                ++this.children;
                if (n.getNodeType() == 1) {
                    ++this.elementChildren;
                }
                return n;
            }
            if (r == this.lastChild) {
                ExtendedNode ps = (ExtendedNode)r.getPreviousSibling();
                ps.setNextSibling(n);
                r.setPreviousSibling(n);
                n.setNextSibling(r);
                n.setPreviousSibling(ps);
                ++this.children;
                if (n.getNodeType() == 1) {
                    ++this.elementChildren;
                }
                return n;
            }
            ExtendedNode ps = (ExtendedNode)r.getPreviousSibling();
            if (ps.getNextSibling() == r && ps.getParentNode() == r.getParentNode()) {
                ps.setNextSibling(n);
                n.setPreviousSibling(ps);
                n.setNextSibling(r);
                r.setPreviousSibling(n);
                ++this.children;
                if (n.getNodeType() == 1) {
                    ++this.elementChildren;
                }
                return n;
            }
            throw AbstractParentNode.this.createDOMException((short)8, "child.missing", new Object[]{(int)r.getNodeType(), r.getNodeName()});
        }

        public ExtendedNode replace(ExtendedNode n, ExtendedNode o) {
            if (o == this.firstChild) {
                ExtendedNode t = (ExtendedNode)this.firstChild.getNextSibling();
                n.setNextSibling(t);
                if (o == this.lastChild) {
                    this.lastChild = n;
                } else {
                    t.setPreviousSibling(n);
                }
                this.firstChild.setNextSibling(null);
                this.firstChild = n;
                if (o.getNodeType() == 1) {
                    --this.elementChildren;
                }
                if (n.getNodeType() == 1) {
                    ++this.elementChildren;
                }
                return o;
            }
            if (o == this.lastChild) {
                ExtendedNode t = (ExtendedNode)this.lastChild.getPreviousSibling();
                n.setPreviousSibling(t);
                t.setNextSibling(n);
                this.lastChild.setPreviousSibling(null);
                this.lastChild = n;
                if (o.getNodeType() == 1) {
                    --this.elementChildren;
                }
                if (n.getNodeType() == 1) {
                    ++this.elementChildren;
                }
                return o;
            }
            ExtendedNode ps = (ExtendedNode)o.getPreviousSibling();
            ExtendedNode ns = (ExtendedNode)o.getNextSibling();
            if (ps.getNextSibling() == o && ns.getPreviousSibling() == o && ps.getParentNode() == o.getParentNode() && ns.getParentNode() == o.getParentNode()) {
                ps.setNextSibling(n);
                n.setPreviousSibling(ps);
                n.setNextSibling(ns);
                ns.setPreviousSibling(n);
                o.setPreviousSibling(null);
                o.setNextSibling(null);
                if (o.getNodeType() == 1) {
                    --this.elementChildren;
                }
                if (n.getNodeType() == 1) {
                    ++this.elementChildren;
                }
                return o;
            }
            throw AbstractParentNode.this.createDOMException((short)8, "child.missing", new Object[]{(int)o.getNodeType(), o.getNodeName()});
        }

        public ExtendedNode remove(ExtendedNode n) {
            if (n == this.firstChild) {
                if (n == this.lastChild) {
                    this.firstChild = null;
                    this.lastChild = null;
                    --this.children;
                    if (n.getNodeType() == 1) {
                        --this.elementChildren;
                    }
                    return n;
                }
                this.firstChild = (ExtendedNode)this.firstChild.getNextSibling();
                this.firstChild.setPreviousSibling(null);
                n.setNextSibling(null);
                if (n.getNodeType() == 1) {
                    --this.elementChildren;
                }
                --this.children;
                return n;
            }
            if (n == this.lastChild) {
                this.lastChild = (ExtendedNode)this.lastChild.getPreviousSibling();
                this.lastChild.setNextSibling(null);
                n.setPreviousSibling(null);
                --this.children;
                if (n.getNodeType() == 1) {
                    --this.elementChildren;
                }
                return n;
            }
            ExtendedNode ps = (ExtendedNode)n.getPreviousSibling();
            ExtendedNode ns = (ExtendedNode)n.getNextSibling();
            if (ps.getNextSibling() == n && ns.getPreviousSibling() == n && ps.getParentNode() == n.getParentNode() && ns.getParentNode() == n.getParentNode()) {
                ps.setNextSibling(ns);
                ns.setPreviousSibling(ps);
                n.setPreviousSibling(null);
                n.setNextSibling(null);
                --this.children;
                if (n.getNodeType() == 1) {
                    --this.elementChildren;
                }
                return n;
            }
            throw AbstractParentNode.this.createDOMException((short)8, "child.missing", new Object[]{(int)n.getNodeType(), n.getNodeName()});
        }
    }

    protected class ElementsByTagNameNS
    implements NodeList {
        protected Node[] table;
        protected int size = -1;
        protected String namespaceURI;
        protected String localName;

        public ElementsByTagNameNS(String ns, String ln) {
            this.namespaceURI = ns;
            this.localName = ln;
        }

        @Override
        public Node item(int index) {
            if (this.size == -1) {
                this.initialize();
            }
            if (this.table == null || index < 0 || index > this.size) {
                return null;
            }
            return this.table[index];
        }

        @Override
        public int getLength() {
            if (this.size == -1) {
                this.initialize();
            }
            return this.size;
        }

        public void invalidate() {
            this.size = -1;
        }

        protected void append(Node n) {
            if (this.table == null) {
                this.table = new Node[11];
            } else if (this.size == this.table.length - 1) {
                Node[] t = new Node[this.table.length * 2 + 1];
                System.arraycopy(this.table, 0, t, 0, this.size);
                this.table = t;
            }
            this.table[this.size++] = n;
        }

        protected void initialize() {
            this.size = 0;
            for (Node n = AbstractParentNode.this.getFirstChild(); n != null; n = n.getNextSibling()) {
                this.initialize(n);
            }
        }

        private void initialize(Node node) {
            if (node.getNodeType() == 1) {
                String nm;
                String ns = node.getNamespaceURI();
                String string = nm = ns == null ? node.getNodeName() : node.getLocalName();
                if (this.nsMatch(this.namespaceURI, node.getNamespaceURI()) && (this.localName.equals("*") || this.localName.equals(nm))) {
                    this.append(node);
                }
            }
            for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
                this.initialize(n);
            }
        }

        private boolean nsMatch(String s1, String s2) {
            if (s1 == null && s2 == null) {
                return true;
            }
            if (s1 == null || s2 == null) {
                return false;
            }
            if (s1.equals("*")) {
                return true;
            }
            return s1.equals(s2);
        }
    }

    protected class ElementsByTagName
    implements NodeList {
        protected Node[] table;
        protected int size = -1;
        protected String name;

        public ElementsByTagName(String n) {
            this.name = n;
        }

        @Override
        public Node item(int index) {
            if (this.size == -1) {
                this.initialize();
            }
            if (this.table == null || index < 0 || index >= this.size) {
                return null;
            }
            return this.table[index];
        }

        @Override
        public int getLength() {
            if (this.size == -1) {
                this.initialize();
            }
            return this.size;
        }

        public void invalidate() {
            this.size = -1;
        }

        protected void append(Node n) {
            if (this.table == null) {
                this.table = new Node[11];
            } else if (this.size == this.table.length - 1) {
                Node[] t = new Node[this.table.length * 2 + 1];
                System.arraycopy(this.table, 0, t, 0, this.size);
                this.table = t;
            }
            this.table[this.size++] = n;
        }

        protected void initialize() {
            this.size = 0;
            for (Node n = AbstractParentNode.this.getFirstChild(); n != null; n = n.getNextSibling()) {
                this.initialize(n);
            }
        }

        private void initialize(Node node) {
            if (node.getNodeType() == 1) {
                String nm = node.getNodeName();
                if (this.name.equals("*") || this.name.equals(nm)) {
                    this.append(node);
                }
            }
            for (Node n = node.getFirstChild(); n != null; n = n.getNextSibling()) {
                this.initialize(n);
            }
        }
    }
}

