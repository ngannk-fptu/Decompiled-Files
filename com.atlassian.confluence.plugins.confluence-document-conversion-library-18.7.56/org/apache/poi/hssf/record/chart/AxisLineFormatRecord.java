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

public final class AxisLineFormatRecord
extends StandardRecord {
    public static final short sid = 4129;
    public static final short AXIS_TYPE_AXIS_LINE = 0;
    public static final short AXIS_TYPE_MAJOR_GRID_LINE = 1;
    public static final short AXIS_TYPE_MINOR_GRID_LINE = 2;
    public static final short AXIS_TYPE_WALLS_OR_FLOOR = 3;
    private short field_1_axisType;

    public AxisLineFormatRecord() {
    }

    public AxisLineFormatRecord(AxisLineFormatRecord other) {
        super(other);
        this.field_1_axisType = other.field_1_axisType;
    }

    public AxisLineFormatRecord(RecordInputStream in) {
        this.field_1_axisType = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_axisType);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 4129;
    }

    public short getAxisType() {
        return this.field_1_axisType;
    }

    public void setAxisType(short field_1_axisType) {
        this.field_1_axisType = field_1_axisType;
    }

    @Override
    public AxisLineFormatRecord copy() {
        return new AxisLineFormatRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.AXIS_LINE_FORMAT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("axisType", this::getAxisType);
    }
}

