/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.ptg;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.ss.formula.constant.ConstantValueParser;
import org.apache.poi.ss.formula.ptg.ArrayPtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

final class ArrayInitialPtg
extends Ptg {
    private final int _reserved0;
    private final int _reserved1;
    private final int _reserved2;

    public ArrayInitialPtg(LittleEndianInput in) {
        this._reserved0 = in.readInt();
        this._reserved1 = in.readUShort();
        this._reserved2 = in.readUByte();
    }

    private static RuntimeException invalid() {
        throw new IllegalStateException("This object is a partially initialised tArray, and cannot be used as a Ptg");
    }

    @Override
    public byte getDefaultOperandClass() {
        throw ArrayInitialPtg.invalid();
    }

    @Override
    public int getSize() {
        return 8;
    }

    @Override
    public boolean isBaseToken() {
        return false;
    }

    @Override
    public String toFormulaString() {
        throw ArrayInitialPtg.invalid();
    }

    @Override
    public void write(LittleEndianOutput out) {
        throw ArrayInitialPtg.invalid();
    }

    public ArrayPtg finishReading(LittleEndianInput in) {
        int nColumns = in.readUByte();
        short nRows = in.readShort();
        nRows = (short)(nRows + 1);
        int totalCount = nRows * ++nColumns;
        Object[] arrayValues = ConstantValueParser.parse(in, totalCount);
        ArrayPtg result = new ArrayPtg(this._reserved0, this._reserved1, this._reserved2, nColumns, nRows, arrayValues);
        result.setClass(this.getPtgClass());
        return result;
    }

    @Override
    public ArrayInitialPtg copy() {
        return this;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("reserved0", () -> this._reserved0, "reserved1", () -> this._reserved1, "reserved2", () -> this._reserved2);
    }

    @Override
    public byte getSid() {
        return -1;
    }
}

