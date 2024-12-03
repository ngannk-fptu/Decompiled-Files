/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.DTM;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class DTMNamedNodeMap
implements NamedNodeMap {
    DTM dtm;
    int element;
    short m_count = (short)-1;

    public DTMNamedNodeMap(DTM dtm, int element) {
        this.dtm = dtm;
        this.element = element;
    }

    @Override
    public int getLength() {
        if (this.m_count == -1) {
            int count = 0;
            int n = this.dtm.getFirstAttribute(this.element);
            while (n != -1) {
                count = (short)(count + 1);
                n = this.dtm.getNextAttribute(n);
            }
            this.m_count = (short)count;
        }
        return this.m_count;
    }

    @Override
    public Node getNamedItem(String name) {
        int n = this.dtm.getFirstAttribute(this.element);
        while (n != -1) {
            if (this.dtm.getNodeName(n).equals(name)) {
                return this.dtm.getNode(n);
            }
            n = this.dtm.getNextAttribute(n);
        }
        return null;
    }

    @Override
    public Node item(int i) {
        int count = 0;
        int n = this.dtm.getFirstAttribute(this.element);
        while (n != -1) {
            if (count == i) {
                return this.dtm.getNode(n);
            }
            ++count;
            n = this.dtm.getNextAttribute(n);
        }
        return null;
    }

    @Override
    public Node setNamedItem(Node newNode) {
        throw new DTMException(7);
    }

    @Override
    public Node removeNamedItem(String name) {
        throw new DTMException(7);
    }

    @Override
    public Node getNamedItemNS(String namespaceURI, String localName) {
        Node retNode = null;
        int n = this.dtm.getFirstAttribute(this.element);
        while (n != -1) {
            if (localName.equals(this.dtm.getLocalName(n))) {
                String nsURI = this.dtm.getNamespaceURI(n);
                if (namespaceURI == null && nsURI == null || namespaceURI != null && namespaceURI.equals(nsURI)) {
                    retNode = this.dtm.getNode(n);
                    break;
                }
            }
            n = this.dtm.getNextAttribute(n);
        }
        return retNode;
    }

    @Override
    public Node setNamedItemNS(Node arg) throws DOMException {
        throw new DTMException(7);
    }

    @Override
    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        throw new DTMException(7);
    }

    public static class DTMException
    extends DOMException {
        static final long serialVersionUID = -8290238117162437678L;

        public DTMException(short code, String message) {
            super(code, message);
        }

        public DTMException(short code) {
            super(code, "");
        }
    }
}

