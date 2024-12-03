/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.DeferredNode;
import org.apache.xerces.dom.TextImpl;

public class DeferredTextImpl
extends TextImpl
implements DeferredNode {
    static final long serialVersionUID = 2310613872100393425L;
    protected transient int fNodeIndex;

    DeferredTextImpl(DeferredDocumentImpl deferredDocumentImpl, int n) {
        super(deferredDocumentImpl, null);
        this.fNodeIndex = n;
        this.needsSyncData(true);
    }

    @Override
    public int getNodeIndex() {
        return this.fNodeIndex;
    }

    @Override
    protected void synchronizeData() {
        this.needsSyncData(false);
        DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument();
        this.data = deferredDocumentImpl.getNodeValueString(this.fNodeIndex);
        this.isIgnorableWhitespace(deferredDocumentImpl.getNodeExtra(this.fNodeIndex) == 1);
    }
}

