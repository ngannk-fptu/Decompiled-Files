/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.AbstractDocument
 *  org.apache.batik.dom.util.DOMUtilities
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.XBLOMElement;
import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.Node;

public class XBLOMDefinitionElement
extends XBLOMElement {
    protected XBLOMDefinitionElement() {
    }

    public XBLOMDefinitionElement(String prefix, AbstractDocument owner) {
        super(prefix, owner);
    }

    public String getLocalName() {
        return "definition";
    }

    protected Node newNode() {
        return new XBLOMDefinitionElement();
    }

    public String getElementNamespaceURI() {
        String qname = this.getAttributeNS(null, "element");
        String prefix = DOMUtilities.getPrefix((String)qname);
        String ns = this.lookupNamespaceURI(prefix);
        if (ns == null) {
            throw this.createDOMException((short)14, "prefix", new Object[]{(int)this.getNodeType(), this.getNodeName(), prefix});
        }
        return ns;
    }

    public String getElementLocalName() {
        String qname = this.getAttributeNS(null, "element");
        return DOMUtilities.getLocalName((String)qname);
    }
}

