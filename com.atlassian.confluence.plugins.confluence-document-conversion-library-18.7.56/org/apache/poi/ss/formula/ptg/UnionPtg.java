/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.formula.ptg.OperationPtg;
import org.apache.poi.util.LittleEndianOutput;

public final class UnionPtg
extends OperationPtg {
    public static final byte sid = 16;
    public static final UnionPtg instance = new UnionPtg();

    private UnionPtg() {
    }

    @Override
    public final boolean isBaseToken() {
        return true;
    }

    @Override
    public byte getSid() {
        return 16;
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public void write(LittleEndianOutput out) {
        out.writeByte(16 + this.getPtgClass());
    }

    @Override
    public String toFormulaString() {
        return ",";
    }

    @Override
    public String toFormulaString(String[] operands) {
        return operands[0] + "," + operands[1];
    }

    @Override
    public int getNumberOfOperands() {
        return 2;
    }

    @Override
    public UnionPtg copy() {
        return instance;
    }
}

