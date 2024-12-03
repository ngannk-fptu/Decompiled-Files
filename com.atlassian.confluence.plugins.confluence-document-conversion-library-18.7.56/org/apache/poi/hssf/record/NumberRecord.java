/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.CellRecord;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class NumberRecord
extends CellRecord {
    public static final short sid = 515;
    private double field_4_value;

    public NumberRecord() {
    }

    public NumberRecord(NumberRecord other) {
        super(other);
        this.field_4_value = other.field_4_value;
    }

    public NumberRecord(RecordInputStream in) {
        super(in);
        this.field_4_value = in.readDouble();
    }

    public void setValue(double value) {
        this.field_4_value = value;
    }

    public double getValue() {
        return this.field_4_value;
    }

    @Override
    protected String getRecordName() {
        return "NUMBER";
    }

    @Override
    protected void serializeValue(LittleEndianOutput out) {
        out.writeDouble(this.getValue());
    }

    @Override
    protected int getValueDataSize() {
        return 8;
    }

    @Override
    public short getSid() {
        return 515;
    }

    @Override
    public NumberRecord copy() {
        return new NumberRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.NUMBER;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "value", this::getValue);
    }
}

