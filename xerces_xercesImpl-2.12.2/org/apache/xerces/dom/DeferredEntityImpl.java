/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.DeferredNode;
import org.apache.xerces.dom.EntityImpl;

public class DeferredEntityImpl
extends EntityImpl
implements DeferredNode {
    static final long serialVersionUID = 4760180431078941638L;
    protected transient int fNodeIndex;

    DeferredEntityImpl(DeferredDocumentImpl deferredDocumentImpl, int n) {
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
        DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
        this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
        this.publicId = deferredDocumentImpl.getNodeValue(this.fNodeIndex);
        this.systemId = deferredDocumentImpl.getNodeURI(this.fNodeIndex);
        int n = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
        deferredDocumentImpl.getNodeType(n);
        this.notationName = deferredDocumentImpl.getNodeName(n);
        this.version = deferredDocumentImpl.getNodeValue(n);
        this.encoding = deferredDocumentImpl.getNodeURI(n);
        int n2 = deferredDocumentImpl.getNodeExtra(n);
        this.baseURI = deferredDocumentImpl.getNodeName(n2);
        this.inputEncoding = deferredDocumentImpl.getNodeValue(n2);
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

