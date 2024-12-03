/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class UnaryMinusPtg
extends ValueOperatorPtg {
    public static final byte sid = 19;
    private static final String MINUS = "-";
    public static final UnaryMinusPtg instance = new UnaryMinusPtg();

    private UnaryMinusPtg() {
    }

    @Override
    public byte getSid() {
        return 19;
    }

    @Override
    public int getNumberOfOperands() {
        return 1;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return MINUS + operands[0];
    }

    @Override
    public UnaryMinusPtg copy() {
        return instance;
    }
}

