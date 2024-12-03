/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.util.DOMUtilities
 *  org.apache.batik.util.XBLConstants
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.util.XBLConstants;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public abstract class XBLOMElement
extends SVGOMElement
implements XBLConstants {
    protected String prefix;

    protected XBLOMElement() {
    }

    protected XBLOMElement(String prefix, AbstractDocument owner) {
        this.ownerDocument = owner;
        this.setPrefix(prefix);
    }

    @Override
    public String getNodeName() {
        if (this.prefix == null || this.prefix.equals("")) {
            return this.getLocalName();
        }
        return this.prefix + ':' + this.getLocalName();
    }

    @Override
    public String getNamespaceURI() {
        return "http://www.w3.org/2004/xbl";
    }

    @Override
    public void setPrefix(String prefix) throws DOMException {
        if (this.isReadonly()) {
            throw this.createDOMException((short)7, "readonly.node", new Object[]{(int)this.getNodeType(), this.getNodeName()});
        }
        if (prefix != null && !prefix.equals("") && !DOMUtilities.isValidName((String)prefix)) {
            throw this.createDOMException((short)5, "prefix", new Object[]{(int)this.getNodeType(), this.getNodeName(), prefix});
        }
        this.prefix = prefix;
    }

    @Override
    protected Node export(Node n, AbstractDocument d) {
        super.export(n, d);
        XBLOMElement e = (XBLOMElement)((Object)n);
        e.prefix = this.prefix;
        return n;
    }

    @Override
    protected Node deepExport(Node n, AbstractDocument d) {
        super.deepExport(n, d);
        XBLOMElement e = (XBLOMElement)((Object)n);
        e.prefix = this.prefix;
        return n;
    }

    @Override
    protected Node copyInto(Node n) {
        super.copyInto(n);
        XBLOMElement e = (XBLOMElement)((Object)n);
        e.prefix = this.prefix;
        return n;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        super.deepCopyInto(n);
        XBLOMElement e = (XBLOMElement)((Object)n);
        e.prefix = this.prefix;
        return n;
    }
}

