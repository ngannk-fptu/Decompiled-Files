/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.DeferredNode;
import org.apache.xerces.dom.ProcessingInstructionImpl;

public class DeferredProcessingInstructionImpl
extends ProcessingInstructionImpl
implements DeferredNode {
    static final long serialVersionUID = -4643577954293565388L;
    protected transient int fNodeIndex;

    DeferredProcessingInstructionImpl(DeferredDocumentImpl deferredDocumentImpl, int n) {
        super(deferredDocumentImpl, null, null);
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
        this.target = deferredDocumentImpl.getNodeName(this.fNodeIndex);
        this.data = deferredDocumentImpl.getNodeValueString(this.fNodeIndex);
    }
}

