/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class GreaterEqualPtg
extends ValueOperatorPtg {
    public static final int SIZE = 1;
    public static final byte sid = 12;
    public static final GreaterEqualPtg instance = new GreaterEqualPtg();

    private GreaterEqualPtg() {
    }

    @Override
    public byte getSid() {
        return 12;
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + ">=" + operands[1];
    }

    @Override
    public GreaterEqualPtg copy() {
        return instance;
    }
}

