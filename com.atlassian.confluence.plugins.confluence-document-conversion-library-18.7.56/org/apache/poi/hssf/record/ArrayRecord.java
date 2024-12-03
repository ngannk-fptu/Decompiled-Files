/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.SharedValueRecordBase;
import org.apache.poi.hssf.util.CellRangeAddress8Bit;
import org.apache.poi.ss.formula.Formula;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class ArrayRecord
extends SharedValueRecordBase {
    public static final short sid = 545;
    private static final int OPT_ALWAYS_RECALCULATE = 1;
    private static final int OPT_CALCULATE_ON_OPEN = 2;
    private int _options;
    private int _field3notUsed;
    private Formula _formula;

    public ArrayRecord(ArrayRecord other) {
        super(other);
        this._options = other._options;
        this._field3notUsed = other._field3notUsed;
        this._formula = other._formula == null ? null : other._formula.copy();
    }

    public ArrayRecord(RecordInputStream in) {
        super(in);
        this._options = in.readUShort();
        this._field3notUsed = in.readInt();
        int formulaTokenLen = in.readUShort();
        int totalFormulaLen = in.available();
        this._formula = Formula.read(formulaTokenLen, in, totalFormulaLen);
    }

    public ArrayRecord(Formula formula, CellRangeAddress8Bit range) {
        super(range);
        this._options = 0;
        this._field3notUsed = 0;
        this._formula = formula;
    }

    public boolean isAlwaysRecalculate() {
        return (this._options & 1) != 0;
    }

    public boolean isCalculateOnOpen() {
        return (this._options & 2) != 0;
    }

    public Ptg[] getFormulaTokens() {
        return this._formula.getTokens();
    }

    @Override
    protected int getExtraDataSize() {
        return 6 + this._formula.getEncodedSize();
    }

    @Override
    protected void serializeExtraData(LittleEndianOutput out) {
        out.writeShort(this._options);
        out.writeInt(this._field3notUsed);
        this._formula.serialize(out);
    }

    @Override
    public short getSid() {
        return 545;
    }

    @Override
    public ArrayRecord copy() {
        return new ArrayRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.ARRAY;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("range", this::getRange, "options", () -> this._options, "notUsed", () -> this._field3notUsed, "formula", () -> this._formula);
    }
}

