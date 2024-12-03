/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractDocument;
import org.apache.batik.dom.AbstractParentChildNode;
import org.apache.batik.dom.util.DOMUtilities;
import org.w3c.dom.DOMException;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;

public abstract class AbstractEntityReference
extends AbstractParentChildNode
implements EntityReference {
    protected String nodeName;

    protected AbstractEntityReference() {
    }

    protected AbstractEntityReference(String name, AbstractDocument owner) throws DOMException {
        this.ownerDocument = owner;
        if (owner.getStrictErrorChecking() && !DOMUtilities.isValidName(name)) {
            throw this.createDOMException((short)5, "xml.name", new Object[]{name});
        }
        this.nodeName = name;
    }

    @Override
    public short getNodeType() {
        return 5;
    }

    @Override
    public void setNodeName(String v) {
        this.nodeName = v;
    }

    @Override
    public String getNodeName() {
        return this.nodeName;
    }

    @Override
    protected Node export(Node n, AbstractDocument d) {
        super.export(n, d);
        AbstractEntityReference ae = (AbstractEntityReference)n;
        ae.nodeName = this.nodeName;
        return n;
    }

    @Override
    protected Node deepExport(Node n, AbstractDocument d) {
        super.deepExport(n, d);
        AbstractEntityReference ae = (AbstractEntityReference)n;
        ae.nodeName = this.nodeName;
        return n;
    }

    @Override
    protected Node copyInto(Node n) {
        super.copyInto(n);
        AbstractEntityReference ae = (AbstractEntityReference)n;
        ae.nodeName = this.nodeName;
        return n;
    }

    @Override
    protected Node deepCopyInto(Node n) {
        super.deepCopyInto(n);
        AbstractEntityReference ae = (AbstractEntityReference)n;
        ae.nodeName = this.nodeName;
        return n;
    }

    @Override
    protected void checkChildType(Node n, boolean replace) {
        switch (n.getNodeType()) {
            case 1: 
            case 3: 
            case 4: 
            case 5: 
            case 7: 
            case 8: 
            case 11: {
                break;
            }
            default: {
                throw this.createDOMException((short)3, "child.type", new Object[]{(int)this.getNodeType(), this.getNodeName(), (int)n.getNodeType(), n.getNodeName()});
            }
        }
    }
}

