/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.DeferredNode;
import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xs.XSTypeDefinition;
import org.w3c.dom.NamedNodeMap;

public class DeferredElementNSImpl
extends ElementNSImpl
implements DeferredNode {
    static final long serialVersionUID = -5001885145370927385L;
    protected transient int fNodeIndex;

    DeferredElementNSImpl(DeferredDocumentImpl deferredDocumentImpl, int n) {
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
        int n = this.name.indexOf(58);
        this.localName = n < 0 ? this.name : this.name.substring(n + 1);
        this.namespaceURI = deferredDocumentImpl.getNodeURI(this.fNodeIndex);
        this.type = (XSTypeDefinition)deferredDocumentImpl.getTypeInfo(this.fNodeIndex);
        this.setupDefaultAttributes();
        int n2 = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
        if (n2 != -1) {
            NamedNodeMap namedNodeMap = this.getAttributes();
            boolean bl2 = false;
            do {
                AttrImpl attrImpl;
                if (!(attrImpl = (AttrImpl)((Object)deferredDocumentImpl.getNodeObject(n2))).getSpecified() && (bl2 || attrImpl.getNamespaceURI() != null && attrImpl.getNamespaceURI() != NamespaceContext.XMLNS_URI && attrImpl.getName().indexOf(58) < 0)) {
                    bl2 = true;
                    namedNodeMap.setNamedItemNS(attrImpl);
                    continue;
                }
                namedNodeMap.setNamedItem(attrImpl);
            } while ((n2 = deferredDocumentImpl.getPrevSibling(n2)) != -1);
        }
        deferredDocumentImpl.mutationEvents = bl;
    }

    @Override
    protected final void synchronizeChildren() {
        DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument();
        deferredDocumentImpl.synchronizeChildren(this, this.fNodeIndex);
    }
}

