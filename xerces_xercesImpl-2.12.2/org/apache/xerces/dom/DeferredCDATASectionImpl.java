/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.CDATASectionImpl;
import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.DeferredNode;

public class DeferredCDATASectionImpl
extends CDATASectionImpl
implements DeferredNode {
    static final long serialVersionUID = 1983580632355645726L;
    protected transient int fNodeIndex;

    DeferredCDATASectionImpl(DeferredDocumentImpl deferredDocumentImpl, int n) {
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
    }
}

