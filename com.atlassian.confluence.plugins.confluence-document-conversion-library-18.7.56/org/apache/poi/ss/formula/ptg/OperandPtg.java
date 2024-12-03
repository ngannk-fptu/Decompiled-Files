/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.Ptg;

public abstract class OperandPtg
extends Ptg {
    protected OperandPtg() {
    }

    protected OperandPtg(OperandPtg other) {
        super(other);
    }

    @Override
    public final boolean isBaseToken() {
        return false;
    }

    @Override
    public abstract OperandPtg copy();
}

