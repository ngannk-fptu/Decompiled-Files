/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class CalcCountRecord
extends StandardRecord {
    public static final short sid = 12;
    private short field_1_iterations;

    public CalcCountRecord() {
    }

    public CalcCountRecord(CalcCountRecord other) {
        super(other);
        this.field_1_iterations = other.field_1_iterations;
    }

    public CalcCountRecord(RecordInputStream in) {
        this.field_1_iterations = in.readShort();
    }

    public void setIterations(short iterations) {
        this.field_1_iterations = iterations;
    }

    public short getIterations() {
        return this.field_1_iterations;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getIterations());
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 12;
    }

    @Override
    public CalcCountRecord copy() {
        return new CalcCountRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CALC_COUNT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("iterations", this::getIterations);
    }
}

