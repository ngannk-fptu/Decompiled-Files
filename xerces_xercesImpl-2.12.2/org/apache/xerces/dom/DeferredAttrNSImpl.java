/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.AttrNSImpl;
import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.DeferredNode;

public final class DeferredAttrNSImpl
extends AttrNSImpl
implements DeferredNode {
    static final long serialVersionUID = 6074924934945957154L;
    protected transient int fNodeIndex;

    DeferredAttrNSImpl(DeferredDocumentImpl deferredDocumentImpl, int n) {
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
        int n = this.name.indexOf(58);
        this.localName = n < 0 ? this.name : this.name.substring(n + 1);
        int n2 = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
        this.isSpecified((n2 & 0x20) != 0);
        this.isIdAttribute((n2 & 0x200) != 0);
        this.namespaceURI = deferredDocumentImpl.getNodeURI(this.fNodeIndex);
        int n3 = deferredDocumentImpl.getLastChild(this.fNodeIndex);
        this.type = deferredDocumentImpl.getTypeInfo(n3);
    }

    @Override
    protected void synchronizeChildren() {
        DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument();
        deferredDocumentImpl.synchronizeChildren(this, this.fNodeIndex);
    }
}

