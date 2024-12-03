/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.llom.OMNodeImpl;

public abstract class OMLeafNode
extends OMNodeImpl {
    public OMLeafNode(OMContainer parent, OMFactory factory, boolean fromBuilder) {
        super(factory);
        if (parent != null) {
            ((OMContainerEx)parent).addChild(this, fromBuilder);
        }
    }

    public OMLeafNode(OMFactory factory) {
        super(factory);
    }

    public final OMXMLParserWrapper getBuilder() {
        return null;
    }

    public final boolean isComplete() {
        return true;
    }

    public final void setComplete(boolean state) {
        if (!state) {
            throw new IllegalStateException();
        }
    }

    public final void discard() throws OMException {
        this.detach();
    }

    public void build() {
    }
}

