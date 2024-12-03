/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class EqualPtg
extends ValueOperatorPtg {
    public static final byte sid = 11;
    public static final EqualPtg instance = new EqualPtg();

    private EqualPtg() {
    }

    @Override
    public byte getSid() {
        return 11;
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + "=" + operands[1];
    }

    @Override
    public EqualPtg copy() {
        return instance;
    }
}

