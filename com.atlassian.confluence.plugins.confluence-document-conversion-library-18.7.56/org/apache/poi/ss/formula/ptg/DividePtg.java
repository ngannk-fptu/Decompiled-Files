/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class DividePtg
extends ValueOperatorPtg {
    public static final byte sid = 6;
    public static final DividePtg instance = new DividePtg();

    private DividePtg() {
    }

    @Override
    public byte getSid() {
        return 6;
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + "/" + operands[1];
    }

    @Override
    public DividePtg copy() {
        return instance;
    }
}

