/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.OperationPtg;
import org.apache.poi.util.LittleEndianOutput;

public final class RangePtg
extends OperationPtg {
    public static final int SIZE = 1;
    public static final byte sid = 17;
    public static final RangePtg instance = new RangePtg();

    private RangePtg() {
    }

    @Override
    public final boolean isBaseToken() {
        return true;
    }

    @Override
    public byte getSid() {
        return 17;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public void write(LittleEndianOutput out) {
        out.writeByte(17 + this.getPtgClass());
    }

    @Override
    public String toFormulaString() {
        return ":";
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + ":" + operands[1];
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public RangePtg copy() {
        return instance;
    }
}

