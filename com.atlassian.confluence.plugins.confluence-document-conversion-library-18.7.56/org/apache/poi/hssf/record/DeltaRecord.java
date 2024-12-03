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

public final class DeltaRecord
extends StandardRecord {
    public static final short sid = 16;
    public static final double DEFAULT_VALUE = 0.001;
    private double field_1_max_change;

    public DeltaRecord(double maxChange) {
        this.field_1_max_change = maxChange;
    }

    public DeltaRecord(DeltaRecord other) {
        super(other);
        this.field_1_max_change = other.field_1_max_change;
    }

    public DeltaRecord(RecordInputStream in) {
        this.field_1_max_change = in.readDouble();
    }

    public double getMaxChange() {
        return this.field_1_max_change;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeDouble(this.getMaxChange());
    }

    @Override
    protected int getDataSize() {
        return 8;
    }

    @Override
    public short getSid() {
        return 16;
    }

    @Override
    public DeltaRecord copy() {
        return this;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.DELTA;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("maxChange", this::getMaxChange);
    }
}

