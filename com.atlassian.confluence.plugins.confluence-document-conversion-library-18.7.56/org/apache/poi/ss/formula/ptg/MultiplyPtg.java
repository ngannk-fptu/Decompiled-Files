/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class MultiplyPtg
extends ValueOperatorPtg {
    public static final byte sid = 5;
    public static final MultiplyPtg instance = new MultiplyPtg();

    private MultiplyPtg() {
    }

    @Override
    public byte getSid() {
        return 5;
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + "*" + operands[1];
    }

    @Override
    public MultiplyPtg copy() {
        return instance;
    }
}

