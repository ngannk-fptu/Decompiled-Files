/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.models;

import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.impl.dtd.models.CMStateSet;

public class XSCMLeaf
extends CMNode {
    private final Object fLeaf;
    private int fParticleId = -1;
    private int fPosition = -1;

    public XSCMLeaf(int n, Object object, int n2, int n3) {
        super(n);
        this.fLeaf = object;
        this.fParticleId = n2;
        this.fPosition = n3;
    }

    final Object getLeaf() {
        return this.fLeaf;
    }

    final int getParticleId() {
        return this.fParticleId;
    }

    final int getPosition() {
        return this.fPosition;
    }

    final void setPosition(int n) {
        this.fPosition = n;
    }

    @Override
    public boolean isNullable() {
        return this.fPosition == -1;
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer(this.fLeaf.toString());
        if (this.fPosition >= 0) {
            stringBuffer.append(" (Pos:").append(Integer.toString(this.fPosition)).append(')');
        }
        return stringBuffer.toString();
    }

    @Override
    protected void calcFirstPos(CMStateSet cMStateSet) {
        if (this.fPosition == -1) {
            cMStateSet.zeroBits();
        } else {
            cMStateSet.setBit(this.fPosition);
        }
    }

    @Override
    protected void calcLastPos(CMStateSet cMStateSet) {
        if (this.fPosition == -1) {
            cMStateSet.zeroBits();
        } else {
            cMStateSet.setBit(this.fPosition);
        }
    }
}

