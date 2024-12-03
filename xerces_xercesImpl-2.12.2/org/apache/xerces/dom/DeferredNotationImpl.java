/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.DeferredNode;
import org.apache.xerces.dom.NotationImpl;

public class DeferredNotationImpl
extends NotationImpl
implements DeferredNode {
    static final long serialVersionUID = 5705337172887990848L;
    protected transient int fNodeIndex;

    DeferredNotationImpl(DeferredDocumentImpl deferredDocumentImpl, int n) {
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
        this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
        deferredDocumentImpl.getNodeType(this.fNodeIndex);
        this.publicId = deferredDocumentImpl.getNodeValue(this.fNodeIndex);
        this.systemId = deferredDocumentImpl.getNodeURI(this.fNodeIndex);
        int n = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
        deferredDocumentImpl.getNodeType(n);
        this.baseURI = deferredDocumentImpl.getNodeName(n);
    }
}

