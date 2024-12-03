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

public final class MemErrPtg
extends OperandPtg {
    public static final short sid = 39;
    private static final int SIZE = 7;
    private int field_1_reserved;
    private short field_2_subex_len;

    public MemErrPtg(MemErrPtg other) {
        super(other);
        this.field_1_reserved = other.field_1_reserved;
        this.field_2_subex_len = other.field_2_subex_len;
    }

    public MemErrPtg(LittleEndianInput in) {
        this.field_1_reserved = in.readInt();
        this.field_2_subex_len = in.readShort();
    }

    @Override
    public void write(LittleEndianOutput out) {
        out.writeByte(39 + this.getPtgClass());
        out.writeInt(this.field_1_reserved);
        out.writeShort(this.field_2_subex_len);
    }

    @Override
    public byte getSid() {
        return 39;
    }

    @Override
    public int getSize() {
        return 7;
    }

    @Override
    public String toFormulaString() {
        return "ERR#";
    }

    @Override
    public byte getDefaultOperandClass() {
        return 32;
    }

    public int getLenRefSubexpression() {
        return this.field_2_subex_len;
    }

    @Override
    public MemErrPtg copy() {
        return new MemErrPtg(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("lenRefSubexpression", this::getLenRefSubexpression);
    }
}

