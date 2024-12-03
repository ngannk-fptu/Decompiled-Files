/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class PercentPtg
extends ValueOperatorPtg {
    public static final int SIZE = 1;
    public static final byte sid = 20;
    private static final String PERCENT = "%";
    public static final PercentPtg instance = new PercentPtg();

    private PercentPtg() {
    }

    @Override
    public byte getSid() {
        return 20;
    }

    @Override
    public int getNumberOfOperands() {
        return 1;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + PERCENT;
    }

    @Override
    public PercentPtg copy() {
        return instance;
    }
}

