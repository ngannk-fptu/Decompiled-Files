/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ss.formula.ptg.OperandPtg;
import org.apache.poi.ss.usermodel.FormulaError;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class AreaErrPtg
extends OperandPtg {
    public static final byte sid = 43;
    private final int unused1;
    private final int unused2;

    public AreaErrPtg() {
        this.unused1 = 0;
        this.unused2 = 0;
    }

    public AreaErrPtg(LittleEndianInput in) {
        this.unused1 = in.readInt();
        this.unused2 = in.readInt();
    }

    @Override
    public void write(LittleEndianOutput out) {
        out.writeByte(43 + this.getPtgClass());
        out.writeInt(this.unused1);
        out.writeInt(this.unused2);
    }

    @Override
    public String toFormulaString() {
        return FormulaError.REF.getString();
    }

    @Override
    public byte getDefaultOperandClass() {
        return 0;
    }

    @Override
    public byte getSid() {
        return 43;
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public AreaErrPtg copy() {
        return this;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("unused1", () -> this.unused1, "unused2", () -> this.unused2);
    }
}

