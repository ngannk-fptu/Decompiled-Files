/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class AddPtg
extends ValueOperatorPtg {
    public static final byte sid = 3;
    private static final String ADD = "+";
    public static final AddPtg instance = new AddPtg();

    private AddPtg() {
    }

    @Override
    public byte getSid() {
        return 3;
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + ADD + operands[1];
    }

    @Override
    public AddPtg copy() {
        return instance;
    }
}

