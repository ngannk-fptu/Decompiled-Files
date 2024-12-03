/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd.models;

import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.impl.dtd.models.CMStateSet;

public class CMBinOp
extends CMNode {
    private final CMNode fLeftChild;
    private final CMNode fRightChild;

    public CMBinOp(int n, CMNode cMNode, CMNode cMNode2) {
        super(n);
        if (this.type() != 4 && this.type() != 5) {
            throw new RuntimeException("ImplementationMessages.VAL_BST");
        }
        this.fLeftChild = cMNode;
        this.fRightChild = cMNode2;
    }

    final CMNode getLeft() {
        return this.fLeftChild;
    }

    final CMNode getRight() {
        return this.fRightChild;
    }

    @Override
    public boolean isNullable() {
        if (this.type() == 4) {
            return this.fLeftChild.isNullable() || this.fRightChild.isNullable();
        }
        if (this.type() == 5) {
            return this.fLeftChild.isNullable() && this.fRightChild.isNullable();
        }
        throw new RuntimeException("ImplementationMessages.VAL_BST");
    }

    @Override
    protected void calcFirstPos(CMStateSet cMStateSet) {
        if (this.type() == 4) {
            cMStateSet.setTo(this.fLeftChild.firstPos());
            cMStateSet.union(this.fRightChild.firstPos());
        } else if (this.type() == 5) {
            cMStateSet.setTo(this.fLeftChild.firstPos());
            if (this.fLeftChild.isNullable()) {
                cMStateSet.union(this.fRightChild.firstPos());
            }
        } else {
            throw new RuntimeException("ImplementationMessages.VAL_BST");
        }
    }

    @Override
    protected void calcLastPos(CMStateSet cMStateSet) {
        if (this.type() == 4) {
            cMStateSet.setTo(this.fLeftChild.lastPos());
            cMStateSet.union(this.fRightChild.lastPos());
        } else if (this.type() == 5) {
            cMStateSet.setTo(this.fRightChild.lastPos());
            if (this.fRightChild.isNullable()) {
                cMStateSet.union(this.fLeftChild.lastPos());
            }
        } else {
            throw new RuntimeException("ImplementationMessages.VAL_BST");
        }
    }
}

