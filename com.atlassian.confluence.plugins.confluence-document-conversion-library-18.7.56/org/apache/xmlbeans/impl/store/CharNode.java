/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import java.io.PrintStream;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.store.CharUtil;
import org.apache.xmlbeans.impl.store.Cur;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.TextNode;
import org.apache.xmlbeans.impl.store.Xobj;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

abstract class CharNode
implements DomImpl.Dom,
Node,
CharacterData {
    private Locale _locale;
    CharNode _next;
    CharNode _prev;
    private Object _src;
    int _off;
    int _cch;

    public CharNode(Locale l) {
        assert (l != null);
        this._locale = l;
    }

    @Override
    public QName getQName() {
        return null;
    }

    @Override
    public Locale locale() {
        assert (this.isValid());
        return this._locale == null ? ((DomImpl.Dom)this._src).locale() : this._locale;
    }

    public void setChars(Object src, int off, int cch) {
        assert (CharUtil.isValid(src, off, cch));
        assert (this._locale != null || this._src instanceof DomImpl.Dom);
        if (this._locale == null) {
            this._locale = ((DomImpl.Dom)this._src).locale();
        }
        this._src = src;
        this._off = off;
        this._cch = cch;
    }

    public DomImpl.Dom getDom() {
        assert (this.isValid());
        if (this._src instanceof DomImpl.Dom) {
            return (DomImpl.Dom)this._src;
        }
        return null;
    }

    public void setDom(DomImpl.Dom d) {
        assert (d != null);
        this._src = d;
        this._locale = null;
    }

    @Override
    public Cur tempCur() {
        assert (this.isValid());
        if (!(this._src instanceof DomImpl.Dom)) {
            return null;
        }
        Cur c = this.locale().tempCur();
        c.moveToCharNode(this);
        return c;
    }

    private boolean isValid() {
        return this._src instanceof DomImpl.Dom == (this._locale == null);
    }

    public static boolean isOnList(CharNode nodes, CharNode node) {
        assert (node != null);
        CharNode cn = nodes;
        while (cn != null) {
            if (cn == node) {
                return true;
            }
            cn = cn._next;
        }
        return false;
    }

    public static CharNode remove(CharNode nodes, CharNode node) {
        assert (CharNode.isOnList(nodes, node));
        if (nodes == node) {
            nodes = node._next;
        } else {
            node._prev._next = node._next;
        }
        if (node._next != null) {
            node._next._prev = node._prev;
        }
        node._next = null;
        node._prev = null;
        return nodes;
    }

    public static CharNode insertNode(CharNode nodes, CharNode newNode, CharNode before) {
        assert (!CharNode.isOnList(nodes, newNode));
        assert (before == null || CharNode.isOnList(nodes, before));
        assert (newNode != null);
        assert (newNode._prev == null && newNode._next == null);
        if (nodes == null) {
            assert (before == null);
            nodes = newNode;
        } else if (nodes == before) {
            nodes._prev = newNode;
            newNode._next = nodes;
            nodes = newNode;
        } else {
            CharNode n = nodes;
            while (n._next != before) {
                n = n._next;
            }
            newNode._next = n._next;
            if (newNode._next != null) {
                n._next._prev = newNode;
            }
            newNode._prev = n;
            n._next = newNode;
        }
        return nodes;
    }

    public static CharNode appendNode(CharNode nodes, CharNode newNode) {
        return CharNode.insertNode(nodes, newNode, null);
    }

    public static CharNode appendNodes(CharNode nodes, CharNode newNodes) {
        assert (newNodes != null);
        assert (newNodes._prev == null);
        if (nodes == null) {
            return newNodes;
        }
        CharNode n = nodes;
        while (n._next != null) {
            n = n._next;
        }
        n._next = newNodes;
        newNodes._prev = n;
        return nodes;
    }

    public static CharNode copyNodes(CharNode nodes, Object newSrc) {
        TextNode newNodes = null;
        TextNode n = null;
        while (nodes != null) {
            TextNode newNode = nodes instanceof TextNode ? nodes.locale().createTextNode() : nodes.locale().createCdataNode();
            newNode.setChars(newSrc, nodes._off, nodes._cch);
            if (newNodes == null) {
                newNodes = newNode;
            }
            if (n != null) {
                n._next = newNode;
                newNode._prev = n;
            }
            n = newNode;
            nodes = nodes._next;
        }
        return newNodes;
    }

    @Override
    public boolean nodeCanHavePrefixUri() {
        return false;
    }

    public boolean isNodeAftertext() {
        assert (this._src instanceof Xobj) : "this method is to only be used for nodes backed up by Xobjs";
        Xobj src = (Xobj)this._src;
        return src._charNodesValue == null ? true : (src._charNodesAfter == null ? false : CharNode.isOnList(src._charNodesAfter, this));
    }

    @Override
    public void dump(PrintStream o, Object ref) {
        if (this._src instanceof DomImpl.Dom) {
            ((DomImpl.Dom)this._src).dump(o, ref);
        } else {
            o.println("Lonely CharNode: \"" + CharUtil.getString(this._src, this._off, this._cch) + "\"");
        }
    }

    @Override
    public void dump(PrintStream o) {
        this.dump(o, this);
    }

    @Override
    public void dump() {
        this.dump(System.out);
    }

    @Override
    public Node appendChild(Node newChild) {
        return DomImpl._node_appendChild(this, newChild);
    }

    @Override
    public Node cloneNode(boolean deep) {
        return DomImpl._node_cloneNode(this, deep);
    }

    @Override
    public NamedNodeMap getAttributes() {
        return null;
    }

    @Override
    public NodeList getChildNodes() {
        return DomImpl._emptyNodeList;
    }

    @Override
    public Node getParentNode() {
        return DomImpl._node_getParentNode(this);
    }

    @Override
    public Node removeChild(Node oldChild) {
        return DomImpl._node_removeChild(this, oldChild);
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
    public String getLocalName() {
        return DomImpl._node_getLocalName(this);
    }

    @Override
    public String getNamespaceURI() {
        return DomImpl._node_getNamespaceURI(this);
    }

    @Override
    public Node getNextSibling() {
        return DomImpl._node_getNextSibling(this);
    }

    @Override
    public String getNodeName() {
        return DomImpl._node_getNodeName(this);
    }

    @Override
    public short getNodeType() {
        return DomImpl._node_getNodeType(this);
    }

    @Override
    public String getNodeValue() {
        return DomImpl._node_getNodeValue(this);
    }

    @Override
    public Document getOwnerDocument() {
        return DomImpl._node_getOwnerDocument(this);
    }

    @Override
    public String getPrefix() {
        return DomImpl._node_getPrefix(this);
    }

    @Override
    public Node getPreviousSibling() {
        return DomImpl._node_getPreviousSibling(this);
    }

    @Override
    public boolean hasAttributes() {
        return false;
    }

    @Override
    public boolean hasChildNodes() {
        return false;
    }

    @Override
    public Node insertBefore(Node newChild, Node refChild) {
        return DomImpl._node_insertBefore(this, newChild, refChild);
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return DomImpl._node_isSupported(this, feature, version);
    }

    @Override
    public void normalize() {
        DomImpl._node_normalize(this);
    }

    @Override
    public Node replaceChild(Node newChild, Node oldChild) {
        return DomImpl._node_replaceChild(this, newChild, oldChild);
    }

    @Override
    public void setNodeValue(String nodeValue) {
        DomImpl._node_setNodeValue(this, nodeValue);
    }

    @Override
    public void setPrefix(String prefix) {
        DomImpl._node_setPrefix(this, prefix);
    }

    @Override
    public Object getUserData(String key) {
        return DomImpl._node_getUserData(this, key);
    }

    @Override
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return DomImpl._node_setUserData(this, key, data, handler);
    }

    @Override
    public Object getFeature(String feature, String version) {
        return DomImpl._node_getFeature(this, feature, version);
    }

    @Override
    public boolean isEqualNode(Node arg) {
        return DomImpl._node_isEqualNode(this, arg);
    }

    @Override
    public boolean isSameNode(Node arg) {
        return DomImpl._node_isSameNode(this, arg);
    }

    @Override
    public String lookupNamespaceURI(String prefix) {
        return DomImpl._node_lookupNamespaceURI(this, prefix);
    }

    @Override
    public String lookupPrefix(String namespaceURI) {
        return DomImpl._node_lookupPrefix(this, namespaceURI);
    }

    @Override
    public boolean isDefaultNamespace(String namespaceURI) {
        return DomImpl._node_isDefaultNamespace(this, namespaceURI);
    }

    @Override
    public void setTextContent(String textContent) {
        DomImpl._node_setTextContent(this, textContent);
    }

    @Override
    public String getTextContent() {
        return DomImpl._node_getTextContent(this);
    }

    @Override
    public short compareDocumentPosition(Node other) {
        return DomImpl._node_compareDocumentPosition(this, other);
    }

    @Override
    public String getBaseURI() {
        return DomImpl._node_getBaseURI(this);
    }

    @Override
    public void appendData(String arg) {
        DomImpl._characterData_appendData(this, arg);
    }

    @Override
    public void deleteData(int offset, int count) {
        DomImpl._characterData_deleteData(this, offset, count);
    }

    @Override
    public String getData() {
        return DomImpl._characterData_getData(this);
    }

    @Override
    public int getLength() {
        return DomImpl._characterData_getLength(this);
    }

    @Override
    public void insertData(int offset, String arg) {
        DomImpl._characterData_insertData(this, offset, arg);
    }

    @Override
    public void replaceData(int offset, int count, String arg) {
        DomImpl._characterData_replaceData(this, offset, count, arg);
    }

    @Override
    public void setData(String data) {
        DomImpl._characterData_setData(this, data);
    }

    @Override
    public String substringData(int offset, int count) {
        return DomImpl._characterData_substringData(this, offset, count);
    }

    Object getObject() {
        return this._src;
    }
}

