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

public final class AxisRecord
extends StandardRecord {
    public static final short sid = 4125;
    public static final short AXIS_TYPE_CATEGORY_OR_X_AXIS = 0;
    public static final short AXIS_TYPE_VALUE_AXIS = 1;
    public static final short AXIS_TYPE_SERIES_AXIS = 2;
    private short field_1_axisType;
    private int field_2_reserved1;
    private int field_3_reserved2;
    private int field_4_reserved3;
    private int field_5_reserved4;

    public AxisRecord() {
    }

    public AxisRecord(AxisRecord other) {
        super(other);
        this.field_1_axisType = other.field_1_axisType;
        this.field_2_reserved1 = other.field_2_reserved1;
        this.field_3_reserved2 = other.field_3_reserved2;
        this.field_4_reserved3 = other.field_4_reserved3;
        this.field_5_reserved4 = other.field_5_reserved4;
    }

    public AxisRecord(RecordInputStream in) {
        this.field_1_axisType = in.readShort();
        this.field_2_reserved1 = in.readInt();
        this.field_3_reserved2 = in.readInt();
        this.field_4_reserved3 = in.readInt();
        this.field_5_reserved4 = in.readInt();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_axisType);
        out.writeInt(this.field_2_reserved1);
        out.writeInt(this.field_3_reserved2);
        out.writeInt(this.field_4_reserved3);
        out.writeInt(this.field_5_reserved4);
    }

    @Override
    protected int getDataSize() {
        return 18;
    }

    @Override
    public short getSid() {
        return 4125;
    }

    public short getAxisType() {
        return this.field_1_axisType;
    }

    public void setAxisType(short field_1_axisType) {
        this.field_1_axisType = field_1_axisType;
    }

    public int getReserved1() {
        return this.field_2_reserved1;
    }

    public void setReserved1(int field_2_reserved1) {
        this.field_2_reserved1 = field_2_reserved1;
    }

    public int getReserved2() {
        return this.field_3_reserved2;
    }

    public void setReserved2(int field_3_reserved2) {
        this.field_3_reserved2 = field_3_reserved2;
    }

    public int getReserved3() {
        return this.field_4_reserved3;
    }

    public void setReserved3(int field_4_reserved3) {
        this.field_4_reserved3 = field_4_reserved3;
    }

    public int getReserved4() {
        return this.field_5_reserved4;
    }

    public void setReserved4(int field_5_reserved4) {
        this.field_5_reserved4 = field_5_reserved4;
    }

    @Override
    public AxisRecord copy() {
        return new AxisRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.AXIS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("axisType", this::getAxisType, "reserved1", this::getReserved1, "reserved2", this::getReserved2, "reserved3", this::getReserved3, "reserved4", this::getReserved4);
    }
}

