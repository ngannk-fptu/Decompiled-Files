/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.DeferredDocumentImpl;
import org.apache.xerces.dom.DeferredNode;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xerces.dom.NamedNodeMapImpl;

public class DeferredDocumentTypeImpl
extends DocumentTypeImpl
implements DeferredNode {
    static final long serialVersionUID = -2172579663227313509L;
    protected transient int fNodeIndex;

    DeferredDocumentTypeImpl(DeferredDocumentImpl deferredDocumentImpl, int n) {
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
        this.publicID = deferredDocumentImpl.getNodeValue(this.fNodeIndex);
        this.systemID = deferredDocumentImpl.getNodeURI(this.fNodeIndex);
        int n = deferredDocumentImpl.getNodeExtra(this.fNodeIndex);
        this.internalSubset = deferredDocumentImpl.getNodeValue(n);
    }

    @Override
    protected void synchronizeChildren() {
        boolean bl = this.ownerDocument().getMutationEvents();
        this.ownerDocument().setMutationEvents(false);
        this.needsSyncChildren(false);
        DeferredDocumentImpl deferredDocumentImpl = (DeferredDocumentImpl)this.ownerDocument;
        this.entities = new NamedNodeMapImpl(this);
        this.notations = new NamedNodeMapImpl(this);
        this.elements = new NamedNodeMapImpl(this);
        DeferredNode deferredNode = null;
        int n = deferredDocumentImpl.getLastChild(this.fNodeIndex);
        while (n != -1) {
            DeferredNode deferredNode2 = deferredDocumentImpl.getNodeObject(n);
            short s = deferredNode2.getNodeType();
            switch (s) {
                case 6: {
                    this.entities.setNamedItem(deferredNode2);
                    break;
                }
                case 12: {
                    this.notations.setNamedItem(deferredNode2);
                    break;
                }
                case 21: {
                    this.elements.setNamedItem(deferredNode2);
                    break;
                }
                case 1: {
                    if (((DocumentImpl)this.getOwnerDocument()).allowGrammarAccess) {
                        this.insertBefore(deferredNode2, deferredNode);
                        deferredNode = deferredNode2;
                        break;
                    }
                }
                default: {
                    System.out.println("DeferredDocumentTypeImpl#synchronizeInfo: node.getNodeType() = " + deferredNode2.getNodeType() + ", class = " + deferredNode2.getClass().getName());
                }
            }
            n = deferredDocumentImpl.getPrevSibling(n);
        }
        this.ownerDocument().setMutationEvents(bl);
        this.setReadOnly(true, false);
    }
}

