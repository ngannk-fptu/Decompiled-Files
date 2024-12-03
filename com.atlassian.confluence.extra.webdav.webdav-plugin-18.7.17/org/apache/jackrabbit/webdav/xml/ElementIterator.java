/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.xml;

import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.xml.namespace.QName;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ElementIterator
implements Iterator<Element> {
    private static Logger log = LoggerFactory.getLogger(ElementIterator.class);
    private final Namespace namespace;
    private final String localName;
    private final QName qName;
    private Element next;

    public ElementIterator(Element parent, String localName, Namespace namespace) {
        this.localName = localName;
        this.namespace = namespace;
        this.qName = null;
        this.seek(parent);
    }

    public ElementIterator(Element parent, QName qname) {
        this.localName = null;
        this.namespace = null;
        this.qName = qname;
        this.seek(parent);
    }

    public ElementIterator(Element parent) {
        this(parent, null, null);
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not implemented.");
    }

    @Override
    public boolean hasNext() {
        return this.next != null;
    }

    @Override
    public Element next() {
        return this.nextElement();
    }

    public Element nextElement() {
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        Element ret = this.next;
        this.seek();
        return ret;
    }

    private void seek(Element parent) {
        NodeList nodeList = parent.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node n = nodeList.item(i);
            if (!this.matchesName(n)) continue;
            this.next = (Element)n;
            return;
        }
    }

    private void seek() {
        for (Node n = this.next.getNextSibling(); n != null; n = n.getNextSibling()) {
            if (!this.matchesName(n)) continue;
            this.next = (Element)n;
            return;
        }
        this.next = null;
    }

    private boolean matchesName(Node n) {
        if (!DomUtil.isElement(n)) {
            return false;
        }
        if (this.qName != null) {
            return DomUtil.matches(n, this.qName);
        }
        return DomUtil.matches(n, this.localName, this.namespace);
    }
}

