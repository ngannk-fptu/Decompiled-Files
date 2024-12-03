/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.DeferredNode;
import org.apache.xerces.dom.EntityReferenceImpl;

public class DeferredEntityReferenceImpl
extends EntityReferenceImpl
implements DeferredNode {
    static final long serialVersionUID = 390319091370032223L;
    protected transient int fNodeIndex;

    DeferredEntityReferenceImpl(DeferredDocumentImpl deferredDocumentImpl, int n) {
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
        DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
        this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
        this.baseURI = deferredDocumentImpl.getNodeValue(this.fNodeIndex);
    }

    @Override
    protected void synchronizeChildren() {
        this.needsSyncChildren(false);
        this.isReadOnly(false);
        DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument();
        deferredDocumentImpl.synchronizeChildren(this, this.fNodeIndex);
        this.setReadOnly(true, true);
    }
}

