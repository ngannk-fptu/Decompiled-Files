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

public final class ChartRecord
extends StandardRecord {
    public static final short sid = 4098;
    private int field_1_x;
    private int field_2_y;
    private int field_3_width;
    private int field_4_height;

    public ChartRecord() {
    }

    public ChartRecord(ChartRecord other) {
        super(other);
        this.field_1_x = other.field_1_x;
        this.field_2_y = other.field_2_y;
        this.field_3_width = other.field_3_width;
        this.field_4_height = other.field_4_height;
    }

    public ChartRecord(RecordInputStream in) {
        this.field_1_x = in.readInt();
        this.field_2_y = in.readInt();
        this.field_3_width = in.readInt();
        this.field_4_height = in.readInt();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeInt(this.field_1_x);
        out.writeInt(this.field_2_y);
        out.writeInt(this.field_3_width);
        out.writeInt(this.field_4_height);
    }

    @Override
    protected int getDataSize() {
        return 16;
    }

    @Override
    public short getSid() {
        return 4098;
    }

    @Override
    public ChartRecord copy() {
        return new ChartRecord(this);
    }

    public int getX() {
        return this.field_1_x;
    }

    public void setX(int x) {
        this.field_1_x = x;
    }

    public int getY() {
        return this.field_2_y;
    }

    public void setY(int y) {
        this.field_2_y = y;
    }

    public int getWidth() {
        return this.field_3_width;
    }

    public void setWidth(int width) {
        this.field_3_width = width;
    }

    public int getHeight() {
        return this.field_4_height;
    }

    public void setHeight(int height) {
        this.field_4_height = height;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CHART;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("x", this::getX, "y", this::getY, "width", this::getWidth, "height", this::getHeight);
    }
}

