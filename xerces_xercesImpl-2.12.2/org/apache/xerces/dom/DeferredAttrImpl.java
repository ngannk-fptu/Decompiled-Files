/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.DeferredNode;

public final class DeferredAttrImpl
extends AttrImpl
implements DeferredNode {
    static final long serialVersionUID = 6903232312469148636L;
    protected transient int fNodeIndex;

    DeferredAttrImpl(DeferredDocumentImpl deferredDocumentImpl, int n) {
        super(deferredDocumentImpl, null);
        this.fNodeIndex = n;
        this.needsSyncData(true);
        this.needsSyncChildren(true);
    }

    @Override
    public int getNodeIndex() {
        return this.fNodeIndex;
    }

    @Override
    protected void synchronizeData() {
        this.needsSyncData(false);
        DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument();
        this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
        int n = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
        this.isSpecified((n & 0x20) != 0);
        this.isIdAttribute((n & 0x200) != 0);
        int n2 = deferredDocumentImpl.getLastChild(this.fNodeIndex);
        this.type = deferredDocumentImpl.getTypeInfo(n2);
    }

    @Override
    protected void synchronizeChildren() {
        DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument();
        deferredDocumentImpl.synchronizeChildren(this, this.fNodeIndex);
    }
}

