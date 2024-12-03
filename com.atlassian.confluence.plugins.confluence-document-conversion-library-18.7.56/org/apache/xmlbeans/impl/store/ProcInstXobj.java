/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.store;

import org.apache.xmlbeans.impl.store.DomImpl;
import org.apache.xmlbeans.impl.store.Locale;
import org.apache.xmlbeans.impl.store.NodeXobj;
import org.apache.xmlbeans.impl.store.Xobj;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;

class ProcInstXobj
extends NodeXobj
implements ProcessingInstruction {
    ProcInstXobj(Locale l, String target) {
        super(l, 5, 7);
        this._name = this._locale.makeQName(null, target);
    }

    @Override
    Xobj newNode(Locale l) {
        return new ProcInstXobj(l, this._name.getLocalPart());
    }

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public Node getFirstChild() {
        return null;
    }

    @Override
    public String getData() {
        return DomImpl._processingInstruction_getData(this);
    }

    @Override
    public String getTarget() {
        return DomImpl._processingInstruction_getTarget(this);
    }

    @Override
    public void setData(String data) {
        DomImpl._processingInstruction_setData(this, data);
    }
}

