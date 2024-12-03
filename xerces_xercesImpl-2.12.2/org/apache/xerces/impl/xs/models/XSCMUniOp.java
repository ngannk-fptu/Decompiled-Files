/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.models;

import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.impl.dtd.models.CMStateSet;

public class XSCMUniOp
extends CMNode {
    private CMNode fChild;

    public XSCMUniOp(int n, CMNode cMNode) {
        super(n);
        if (this.type() != 5 && this.type() != 4 && this.type() != 6) {
            throw new RuntimeException("ImplementationMessages.VAL_UST");
        }
        this.fChild = cMNode;
    }

    final CMNode getChild() {
        return this.fChild;
    }

    @Override
    public boolean isNullable() {
        if (this.type() == 6) {
            return this.fChild.isNullable();
        }
        return true;
    }

    @Override
    protected void calcFirstPos(CMStateSet cMStateSet) {
        cMStateSet.setTo(this.fChild.firstPos());
    }

    @Override
    protected void calcLastPos(CMStateSet cMStateSet) {
        cMStateSet.setTo(this.fChild.lastPos());
    }
}

