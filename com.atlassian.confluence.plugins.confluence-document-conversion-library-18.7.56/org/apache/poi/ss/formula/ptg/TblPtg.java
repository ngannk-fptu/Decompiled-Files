/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ss.formula.ptg.ControlPtg;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class TblPtg
extends ControlPtg {
    private static final int SIZE = 5;
    public static final short sid = 2;
    private final int field_1_first_row;
    private final int field_2_first_col;

    public TblPtg(LittleEndianInput in) {
        this.field_1_first_row = in.readUShort();
        this.field_2_first_col = in.readUShort();
    }

    @Override
    public void write(LittleEndianOutput out) {
        out.writeByte(2 + this.getPtgClass());
        out.writeShort(this.field_1_first_row);
        out.writeShort(this.field_2_first_col);
    }

    @Override
    public byte getSid() {
        return 2;
    }

    @Override
    public int getSize() {
        return 5;
    }

    public int getRow() {
        return this.field_1_first_row;
    }

    public int getColumn() {
        return this.field_2_first_col;
    }

    @Override
    public String toFormulaString() {
        throw new RuntimeException("Table and Arrays are not yet supported");
    }

    @Override
    public TblPtg copy() {
        return this;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("row", this::getRow, "column", this::getColumn);
    }
}

