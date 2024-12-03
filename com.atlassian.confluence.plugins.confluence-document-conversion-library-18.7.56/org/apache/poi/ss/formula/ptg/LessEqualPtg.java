/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class LessEqualPtg
extends ValueOperatorPtg {
    public static final byte sid = 10;
    public static final LessEqualPtg instance = new LessEqualPtg();

    private LessEqualPtg() {
    }

    @Override
    public byte getSid() {
        return 10;
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + "<=" + operands[1];
    }

    @Override
    public LessEqualPtg copy() {
        return instance;
    }
}

