/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class UnaryPlusPtg
extends ValueOperatorPtg {
    public static final byte sid = 18;
    private static final String ADD = "+";
    public static final UnaryPlusPtg instance = new UnaryPlusPtg();

    private UnaryPlusPtg() {
    }

    @Override
    public byte getSid() {
        return 18;
    }

    @Override
    public int getNumberOfOperands() {
        return 1;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return ADD + operands[0];
    }

    @Override
    public UnaryPlusPtg copy() {
        return instance;
    }
}

