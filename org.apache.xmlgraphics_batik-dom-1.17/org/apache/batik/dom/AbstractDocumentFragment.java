/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom;

import org.apache.batik.dom.AbstractParentNode;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;

public abstract class AbstractDocumentFragment
extends AbstractParentNode
implements DocumentFragment {
    @Override
    public String getNodeName() {
        return "#document-fragment";
    }

    @Override
    public short getNodeType() {
        return 11;
    }

    @Override
    protected void checkChildType(Node n, boolean replace) {
        switch (n.getNodeType()) {
            case 1: 
            case 3: 
            case 4: 
            case 5: 
            case 7: 
            case 8: 
            case 11: {
                break;
            }
            default: {
                throw this.createDOMException((short)3, "child.type", new Object[]{(int)this.getNodeType(), this.getNodeName(), (int)n.getNodeType(), n.getNodeName()});
            }
        }
    }
}

