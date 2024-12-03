/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.DeferredNode;
import org.apache.xerces.dom.ElementDefinitionImpl;
import org.apache.xerces.dom.NamedNodeMapImpl;

public class DeferredElementDefinitionImpl
extends ElementDefinitionImpl
implements DeferredNode {
    static final long serialVersionUID = 6703238199538041591L;
    protected transient int fNodeIndex;

    DeferredElementDefinitionImpl(DeferredDocumentImpl deferredDocumentImpl, int n) {
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
    }

    @Override
    protected void synchronizeChildren() {
        boolean bl = this.ownerDocument.getMutationEvents();
        this.ownerDocument.setMutationEvents(false);
        this.needsSyncChildren(false);
        DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
        this.attributes = new NamedNodeMapImpl(deferredDocumentImpl);
        int n = deferredDocumentImpl.getLastChild(this.fNodeIndex);
        while (n != -1) {
            DeferredNode deferredNode = deferredDocumentImpl.getNodeObject(n);
            this.attributes.setNamedItem(deferredNode);
            n = deferredDocumentImpl.getPrevSibling(n);
        }
        deferredDocumentImpl.setMutationEvents(bl);
    }
}

