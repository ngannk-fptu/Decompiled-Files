/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class ConcatPtg
extends ValueOperatorPtg {
    public static final byte sid = 8;
    private static final String CONCAT = "&";
    public static final ConcatPtg instance = new ConcatPtg();

    private ConcatPtg() {
    }

    @Override
    public byte getSid() {
        return 8;
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + CONCAT + operands[1];
    }

    @Override
    public ConcatPtg copy() {
        return instance;
    }
}

