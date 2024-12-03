/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;

public final class PowerPtg
extends ValueOperatorPtg {
    public static final byte sid = 7;
    public static final PowerPtg instance = new PowerPtg();

    private PowerPtg() {
    }

    @Override
    public byte getSid() {
        return 7;
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + "^" + operands[1];
    }

    @Override
    public PowerPtg copy() {
        return instance;
    }
}

