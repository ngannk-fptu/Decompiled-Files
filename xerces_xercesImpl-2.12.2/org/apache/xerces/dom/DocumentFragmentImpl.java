/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import org.apache.xerces.dom.ChildNode;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ParentNode;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Text;

public class DocumentFragmentImpl
extends ParentNode
implements DocumentFragment {
    static final long serialVersionUID = -7596449967279236746L;

    public DocumentFragmentImpl(CoreDocumentImpl coreDocumentImpl) {
        super(coreDocumentImpl);
    }

    public DocumentFragmentImpl() {
    }

    @Override
    public short getNodeType() {
        return 11;
    }

    @Override
    public String getNodeName() {
        return "#document-fragment";
    }

    @Override
    public void normalize() {
        if (this.isNormalized()) {
            return;
        }
        if (this.needsSyncChildren()) {
            this.synchronizeChildren();
        }
        ChildNode childNode = this.firstChild;
        while (childNode != null) {
            ChildNode childNode2 = childNode.nextSibling;
            if (childNode.getNodeType() == 3) {
                if (childNode2 != null && childNode2.getNodeType() == 3) {
                    ((Text)((Object)childNode)).appendData(childNode2.getNodeValue());
                    this.removeChild(childNode2);
                    childNode2 = childNode;
                } else if (childNode.getNodeValue() == null || childNode.getNodeValue().length() == 0) {
                    this.removeChild(childNode);
                }
            }
            childNode.normalize();
            childNode = childNode2;
        }
        this.isNormalized(true);
    }
}

