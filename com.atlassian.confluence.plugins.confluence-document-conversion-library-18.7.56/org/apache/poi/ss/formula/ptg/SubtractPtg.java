/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class SubtractPtg
extends ValueOperatorPtg {
    public static final byte sid = 4;
    public static final SubtractPtg instance = new SubtractPtg();

    private SubtractPtg() {
    }

    @Override
    public byte getSid() {
        return 4;
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + "-" + operands[1];
    }

    @Override
    public SubtractPtg copy() {
        return instance;
    }
}

