/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.chart;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class ChartFormatRecord
extends StandardRecord {
    public static final short sid = 4116;
    private static final BitField varyDisplayPattern = BitFieldFactory.getInstance(1);
    private int field1_x_position;
    private int field2_y_position;
    private int field3_width;
    private int field4_height;
    private int field5_grbit;
    private int field6_unknown;

    public ChartFormatRecord() {
    }

    public ChartFormatRecord(ChartFormatRecord other) {
        super(other);
        this.field1_x_position = other.field1_x_position;
        this.field2_y_position = other.field2_y_position;
        this.field3_width = other.field3_width;
        this.field4_height = other.field4_height;
        this.field5_grbit = other.field5_grbit;
        this.field6_unknown = other.field6_unknown;
    }

    public ChartFormatRecord(RecordInputStream in) {
        this.field1_x_position = in.readInt();
        this.field2_y_position = in.readInt();
        this.field3_width = in.readInt();
        this.field4_height = in.readInt();
        this.field5_grbit = in.readUShort();
        this.field6_unknown = in.readUShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeInt(this.getXPosition());
        out.writeInt(this.getYPosition());
        out.writeInt(this.getWidth());
        out.writeInt(this.getHeight());
        out.writeShort(this.field5_grbit);
        out.writeShort(this.field6_unknown);
    }

    @Override
    protected int getDataSize() {
        return 20;
    }

    @Override
    public short getSid() {
        return 4116;
    }

    public int getXPosition() {
        return this.field1_x_position;
    }

    public void setXPosition(int xPosition) {
        this.field1_x_position = xPosition;
    }

    public int getYPosition() {
        return this.field2_y_position;
    }

    public void setYPosition(int yPosition) {
        this.field2_y_position = yPosition;
    }

    public int getWidth() {
        return this.field3_width;
    }

    public void setWidth(int width) {
        this.field3_width = width;
    }

    public int getHeight() {
        return this.field4_height;
    }

    public void setHeight(int height) {
        this.field4_height = height;
    }

    public boolean getVaryDisplayPattern() {
        return varyDisplayPattern.isSet(this.field5_grbit);
    }

    public void setVaryDisplayPattern(boolean value) {
        this.field5_grbit = varyDisplayPattern.setBoolean(this.field5_grbit, value);
    }

    @Override
    public ChartFormatRecord copy() {
        return new ChartFormatRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CHART_FORMAT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("x", this::getXPosition, "y", this::getYPosition, "width", this::getWidth, "height", this::getHeight, "grbit", () -> this.field5_grbit, "varyDisplayPattern", this::getVaryDisplayPattern, "unknown", () -> this.field6_unknown);
    }
}

