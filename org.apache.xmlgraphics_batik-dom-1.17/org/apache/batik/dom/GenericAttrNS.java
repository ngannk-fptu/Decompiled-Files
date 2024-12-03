/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractAttrNS;
import org.apache.batik.dom.AbstractDocument;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class GenericAttrNS
extends AbstractAttrNS {
    protected boolean readonly;

    protected GenericAttrNS() {
    }

    public GenericAttrNS(String nsURI, String qname, AbstractDocument owner) throws DOMException {
        super(nsURI, qname, owner);
        this.setNodeName(qname);
    }

    @Override
    public boolean isReadonly() {
        return this.readonly;
    }

    @Override
    public void setReadonly(boolean v) {
        this.readonly = v;
    }

    @Override
    protected Node newNode() {
        return new GenericAttrNS();
    }
}

