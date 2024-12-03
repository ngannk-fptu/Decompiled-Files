/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class LessThanPtg
extends ValueOperatorPtg {
    public static final byte sid = 9;
    private static final String LESSTHAN = "<";
    public static final LessThanPtg instance = new LessThanPtg();

    private LessThanPtg() {
    }

    @Override
    public byte getSid() {
        return 9;
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + LESSTHAN + operands[1];
    }

    @Override
    public LessThanPtg copy() {
        return instance;
    }
}

