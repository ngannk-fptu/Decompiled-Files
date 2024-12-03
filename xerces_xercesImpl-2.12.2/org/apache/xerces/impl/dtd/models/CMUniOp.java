/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd.models;

import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.impl.dtd.models.CMStateSet;

public class CMUniOp
extends CMNode {
    private final CMNode fChild;

    public CMUniOp(int n, CMNode cMNode) {
        super(n);
        if (this.type() != 1 && this.type() != 2 && this.type() != 3) {
            throw new RuntimeException("ImplementationMessages.VAL_UST");
        }
        this.fChild = cMNode;
    }

    final CMNode getChild() {
        return this.fChild;
    }

    @Override
    public boolean isNullable() {
        if (this.type() == 3) {
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

