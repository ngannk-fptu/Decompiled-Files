/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.DeferredNode;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.NamedNodeMap;

public class DeferredElementImpl
extends ElementImpl
implements DeferredNode {
    static final long serialVersionUID = -7670981133940934842L;
    protected transient int fNodeIndex;

    DeferredElementImpl(DeferredDocumentImpl deferredDocumentImpl, int n) {
        super(deferredDocumentImpl, null);
        this.fNodeIndex = n;
        this.needsSyncChildren(true);
    }

    @Override
    public final int getNodeIndex() {
        return this.fNodeIndex;
    }

    @Override
    protected final void synchronizeData() {
        this.needsSyncData(false);
        DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
        boolean bl = deferredDocumentImpl.mutationEvents;
        deferredDocumentImpl.mutationEvents = false;
        this.name = deferredDocumentImpl.getNodeName(this.fNodeIndex);
        this.setupDefaultAttributes();
        int n = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
        if (n != -1) {
            NamedNodeMap namedNodeMap = this.getAttributes();
            do {
                NodeImpl nodeImpl = (NodeImpl)((Object)deferredDocumentImpl.getNodeObject(n));
                namedNodeMap.setNamedItem(nodeImpl);
            } while ((n = deferredDocumentImpl.getPrevSibling(n)) != -1);
        }
        deferredDocumentImpl.mutationEvents = bl;
    }

    @Override
    protected final void synchronizeChildren() {
        DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument();
        deferredDocumentImpl.synchronizeChildren(this, this.fNodeIndex);
    }
}

