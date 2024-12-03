/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ss.formula.ptg.OperandPtg;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class MemAreaPtg
extends OperandPtg {
    public static final short sid = 38;
    private static final int SIZE = 7;
    private final int field_1_reserved;
    private final int field_2_subex_len;

    public MemAreaPtg(int subexLen) {
        this.field_1_reserved = 0;
        this.field_2_subex_len = subexLen;
    }

    public MemAreaPtg(LittleEndianInput in) {
        this.field_1_reserved = in.readInt();
        this.field_2_subex_len = in.readShort();
    }

    public int getLenRefSubexpression() {
        return this.field_2_subex_len;
    }

    @Override
    public void write(LittleEndianOutput out) {
        out.writeByte(38 + this.getPtgClass());
        out.writeInt(this.field_1_reserved);
        out.writeShort(this.field_2_subex_len);
    }

    @Override
    public byte getSid() {
        return 38;
    }

    @Override
    public int getSize() {
        return 7;
    }

    @Override
    public String toFormulaString() {
        return "";
    }

    @Override
    public byte getDefaultOperandClass() {
        return 32;
    }

    @Override
    public MemAreaPtg copy() {
        return this;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("lenRefSubexpression", this::getLenRefSubexpression);
    }
}

