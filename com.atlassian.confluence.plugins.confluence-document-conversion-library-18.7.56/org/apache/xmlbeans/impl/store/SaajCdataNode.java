/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.impl.soap.SOAPElement;
import org.apache.xmlbeans.impl.soap.Text;
import org.apache.xmlbeans.impl.store.CdataNode;
import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;

class SaajCdataNode
extends CdataNode
implements Text {
    public SaajCdataNode(Locale l) {
        super(l);
    }

    @Override
    public boolean isComment() {
        return DomImpl._soapText_isComment(this);
    }

    @Override
    public void detachNode() {
        DomImpl._soapNode_detachNode(this);
    }

    @Override
    public void recycleNode() {
        DomImpl._soapNode_recycleNode(this);
    }

    @Override
    public String getValue() {
        return DomImpl._soapNode_getValue(this);
    }

    @Override
    public void setValue(String value) {
        DomImpl._soapNode_setValue(this, value);
    }

    @Override
    public SOAPElement getParentElement() {
        return DomImpl._soapNode_getParentElement(this);
    }

    @Override
    public void setParentElement(SOAPElement p) {
        DomImpl._soapNode_setParentElement(this, p);
    }
}

