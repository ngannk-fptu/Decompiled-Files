/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class NotEqualPtg
extends ValueOperatorPtg {
    public static final byte sid = 14;
    public static final NotEqualPtg instance = new NotEqualPtg();

    private NotEqualPtg() {
    }

    @Override
    public byte getSid() {
        return 14;
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + "<>" + operands[1];
    }

    @Override
    public NotEqualPtg copy() {
        return instance;
    }
}

