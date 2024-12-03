/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.chart;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class UnitsRecord
extends StandardRecord {
    public static final short sid = 4097;
    private short field_1_units;

    public UnitsRecord() {
    }

    public UnitsRecord(UnitsRecord other) {
        super(other);
        this.field_1_units = other.field_1_units;
    }

    public UnitsRecord(RecordInputStream in) {
        this.field_1_units = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_units);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 4097;
    }

    @Override
    public UnitsRecord copy() {
        return new UnitsRecord(this);
    }

    public short getUnits() {
        return this.field_1_units;
    }

    public void setUnits(short field_1_units) {
        this.field_1_units = field_1_units;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.UNITS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("units", this::getUnits);
    }
}

