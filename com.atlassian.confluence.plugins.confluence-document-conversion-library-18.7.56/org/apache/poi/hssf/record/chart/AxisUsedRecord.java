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

public final class AxisUsedRecord
extends StandardRecord {
    public static final short sid = 4166;
    private short field_1_numAxis;

    public AxisUsedRecord() {
    }

    public AxisUsedRecord(AxisUsedRecord other) {
        super(other);
        this.field_1_numAxis = other.field_1_numAxis;
    }

    public AxisUsedRecord(RecordInputStream in) {
        this.field_1_numAxis = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_numAxis);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 4166;
    }

    public short getNumAxis() {
        return this.field_1_numAxis;
    }

    public void setNumAxis(short field_1_numAxis) {
        this.field_1_numAxis = field_1_numAxis;
    }

    @Override
    public AxisUsedRecord copy() {
        return new AxisUsedRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.AXIS_USED;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("numAxis", this::getNumAxis);
    }
}

