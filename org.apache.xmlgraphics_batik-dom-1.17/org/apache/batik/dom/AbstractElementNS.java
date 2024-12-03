/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractElement;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public abstract class AbstractElementNS
extends AbstractElement {
    protected String namespaceURI;

    protected AbstractElementNS() {
    }

    protected AbstractElementNS(String nsURI, String qname, AbstractDocument owner) throws DOMException {
        super(qname, owner);
        if (nsURI != null && nsURI.length() == 0) {
            nsURI = null;
        }
        this.namespaceURI = nsURI;
        String prefix = DOMUtilities.getPrefix(qname);
        if (prefix != null && (nsURI == null || "xml".equals(prefix) && !"http://www.w3.org/XML/1998/namespace".equals(nsURI))) {
            throw this.createDOMException((short)14, "namespace.uri", new Object[]{(int)this.getNodeType(), this.getNodeName(), nsURI});
        }
    }

    @Override
    public String getNamespaceURI() {
        return this.namespaceURI;
    }

    @Override
    protected Node export(Node n, AbstractDocument d) {
        super.export(n, d);
        AbstractElementNS ae = (AbstractElementNS)n;
        ae.namespaceURI = this.namespaceURI;
        return n;
    }

    @Override
    protected Node deepExport(Node n, AbstractDocument d) {
        super.deepExport(n, d);
        AbstractElementNS ae = (AbstractElementNS)n;
        ae.namespaceURI = this.namespaceURI;
        return n;
    }

    @Override
    protected Node copyInto(Node n) {
        super.copyInto(n);
        AbstractElementNS ae = (AbstractElementNS)n;
        ae.namespaceURI = this.namespaceURI;
        return n;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        super.deepCopyInto(n);
        AbstractElementNS ae = (AbstractElementNS)n;
        ae.namespaceURI = this.namespaceURI;
        return n;
    }
}

