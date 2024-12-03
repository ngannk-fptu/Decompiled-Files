/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd.models;

import org.apache.xerces.impl.dtd.models.CMStateSet;

public abstract class CMNode {
    private final int fType;
    private CMStateSet fFirstPos = null;
    private CMStateSet fFollowPos = null;
    private CMStateSet fLastPos = null;
    private int fMaxStates = -1;
    private boolean fCompactedForUPA = false;

    public CMNode(int n) {
        this.fType = n;
    }

    public abstract boolean isNullable();

    public final int type() {
        return this.fType;
    }

    public final CMStateSet firstPos() {
        if (this.fFirstPos == null) {
            this.fFirstPos = new CMStateSet(this.fMaxStates);
            this.calcFirstPos(this.fFirstPos);
        }
        return this.fFirstPos;
    }

    public final CMStateSet lastPos() {
        if (this.fLastPos == null) {
            this.fLastPos = new CMStateSet(this.fMaxStates);
            this.calcLastPos(this.fLastPos);
        }
        return this.fLastPos;
    }

    final void setFollowPos(CMStateSet cMStateSet) {
        this.fFollowPos = cMStateSet;
    }

    public final void setMaxStates(int n) {
        this.fMaxStates = n;
    }

    public boolean isCompactedForUPA() {
        return this.fCompactedForUPA;
    }

    public void setIsCompactUPAModel(boolean bl) {
        this.fCompactedForUPA = bl;
    }

    protected abstract void calcFirstPos(CMStateSet var1);

    protected abstract void calcLastPos(CMStateSet var1);
}

