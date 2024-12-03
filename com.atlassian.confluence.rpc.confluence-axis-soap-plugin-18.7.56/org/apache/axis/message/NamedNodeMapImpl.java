/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import java.util.Iterator;
import java.util.Vector;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.axis.InternalException;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapImpl
implements NamedNodeMap {
    protected Vector nodes = new Vector();
    private static Document doc = null;

    public Node getNamedItem(String name) {
        if (name == null) {
            Thread.dumpStack();
            throw new IllegalArgumentException("local name = null");
        }
        Iterator iter = this.nodes.iterator();
        while (iter.hasNext()) {
            Attr attr = (Attr)iter.next();
            if (!name.equals(attr.getName())) continue;
            return attr;
        }
        return null;
    }

    public Node setNamedItem(Node arg) throws DOMException {
        String name = arg.getNodeName();
        if (name == null) {
            Thread.dumpStack();
            throw new IllegalArgumentException("local name = null");
        }
        for (int i = 0; i < this.nodes.size(); ++i) {
            Attr attr = (Attr)this.nodes.get(i);
            if (!name.equals(attr.getName())) continue;
            this.nodes.remove(i);
            this.nodes.add(i, arg);
            return attr;
        }
        this.nodes.add(arg);
        return null;
    }

    public Node removeNamedItem(String name) throws DOMException {
        if (name == null) {
            Thread.dumpStack();
            throw new IllegalArgumentException("local name = null");
        }
        for (int i = 0; i < this.nodes.size(); ++i) {
            Attr attr = (Attr)this.nodes.get(i);
            if (!name.equals(attr.getLocalName())) continue;
            this.nodes.remove(i);
            return attr;
        }
        return null;
    }

    public Node item(int index) {
        return this.nodes != null && index < this.nodes.size() ? (Node)this.nodes.elementAt(index) : null;
    }

    public int getLength() {
        return this.nodes != null ? this.nodes.size() : 0;
    }

    public Node getNamedItemNS(String namespaceURI, String localName) {
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        if (localName == null) {
            Thread.dumpStack();
            throw new IllegalArgumentException("local name = null");
        }
        Iterator iter = this.nodes.iterator();
        while (iter.hasNext()) {
            Attr attr = (Attr)iter.next();
            if (!namespaceURI.equals(attr.getNamespaceURI()) || !localName.equals(attr.getLocalName())) continue;
            return attr;
        }
        return null;
    }

    public Node setNamedItemNS(Node arg) throws DOMException {
        String namespaceURI = arg.getNamespaceURI();
        String localName = arg.getLocalName();
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        if (localName == null) {
            Thread.dumpStack();
            throw new IllegalArgumentException("local name = null");
        }
        for (int i = 0; i < this.nodes.size(); ++i) {
            Attr attr = (Attr)this.nodes.get(i);
            if (!namespaceURI.equals(attr.getNamespaceURI()) || !namespaceURI.equals(attr.getLocalName())) continue;
            this.nodes.remove(i);
            this.nodes.add(i, arg);
            return attr;
        }
        this.nodes.add(arg);
        return null;
    }

    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        if (localName == null) {
            Thread.dumpStack();
            throw new IllegalArgumentException("local name = null");
        }
        for (int i = 0; i < this.nodes.size(); ++i) {
            Attr attr = (Attr)this.nodes.get(i);
            if (!namespaceURI.equals(attr.getNamespaceURI()) || !localName.equals(attr.getLocalName())) continue;
            this.nodes.remove(i);
            return attr;
        }
        return null;
    }

    static {
        try {
            Document doc = XMLUtils.newDocument();
        }
        catch (ParserConfigurationException e) {
            throw new InternalException(e);
        }
    }
}

