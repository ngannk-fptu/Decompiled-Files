/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class GreaterThanPtg
extends ValueOperatorPtg {
    public static final byte sid = 13;
    private static final String GREATERTHAN = ">";
    public static final GreaterThanPtg instance = new GreaterThanPtg();

    private GreaterThanPtg() {
    }

    @Override
    public byte getSid() {
        return 13;
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + GREATERTHAN + operands[1];
    }

    @Override
    public GreaterThanPtg copy() {
        return instance;
    }
}

