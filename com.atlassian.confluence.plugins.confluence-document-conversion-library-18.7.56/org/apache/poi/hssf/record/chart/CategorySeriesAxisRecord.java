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

public final class CategorySeriesAxisRecord
extends StandardRecord {
    public static final short sid = 4128;
    private static final BitField valueAxisCrossing = BitFieldFactory.getInstance(1);
    private static final BitField crossesFarRight = BitFieldFactory.getInstance(2);
    private static final BitField reversed = BitFieldFactory.getInstance(4);
    private short field_1_crossingPoint;
    private short field_2_labelFrequency;
    private short field_3_tickMarkFrequency;
    private short field_4_options;

    public CategorySeriesAxisRecord() {
    }

    public CategorySeriesAxisRecord(CategorySeriesAxisRecord other) {
        super(other);
        this.field_1_crossingPoint = other.field_1_crossingPoint;
        this.field_2_labelFrequency = other.field_2_labelFrequency;
        this.field_3_tickMarkFrequency = other.field_3_tickMarkFrequency;
        this.field_4_options = other.field_4_options;
    }

    public CategorySeriesAxisRecord(RecordInputStream in) {
        this.field_1_crossingPoint = in.readShort();
        this.field_2_labelFrequency = in.readShort();
        this.field_3_tickMarkFrequency = in.readShort();
        this.field_4_options = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_crossingPoint);
        out.writeShort(this.field_2_labelFrequency);
        out.writeShort(this.field_3_tickMarkFrequency);
        out.writeShort(this.field_4_options);
    }

    @Override
    protected int getDataSize() {
        return 8;
    }

    @Override
    public short getSid() {
        return 4128;
    }

    public short getCrossingPoint() {
        return this.field_1_crossingPoint;
    }

    public void setCrossingPoint(short field_1_crossingPoint) {
        this.field_1_crossingPoint = field_1_crossingPoint;
    }

    public short getLabelFrequency() {
        return this.field_2_labelFrequency;
    }

    public void setLabelFrequency(short field_2_labelFrequency) {
        this.field_2_labelFrequency = field_2_labelFrequency;
    }

    public short getTickMarkFrequency() {
        return this.field_3_tickMarkFrequency;
    }

    public void setTickMarkFrequency(short field_3_tickMarkFrequency) {
        this.field_3_tickMarkFrequency = field_3_tickMarkFrequency;
    }

    public short getOptions() {
        return this.field_4_options;
    }

    public void setOptions(short field_4_options) {
        this.field_4_options = field_4_options;
    }

    public void setValueAxisCrossing(boolean value) {
        this.field_4_options = valueAxisCrossing.setShortBoolean(this.field_4_options, value);
    }

    public boolean isValueAxisCrossing() {
        return valueAxisCrossing.isSet(this.field_4_options);
    }

    public void setCrossesFarRight(boolean value) {
        this.field_4_options = crossesFarRight.setShortBoolean(this.field_4_options, value);
    }

    public boolean isCrossesFarRight() {
        return crossesFarRight.isSet(this.field_4_options);
    }

    public void setReversed(boolean value) {
        this.field_4_options = reversed.setShortBoolean(this.field_4_options, value);
    }

    public boolean isReversed() {
        return reversed.isSet(this.field_4_options);
    }

    @Override
    public CategorySeriesAxisRecord copy() {
        return new CategorySeriesAxisRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CATEGORY_SERIES_AXIS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("crossingPoint", this::getCrossingPoint, "labelFrequency", this::getLabelFrequency, "tickMarkFrequency", this::getTickMarkFrequency, "options", this::getOptions, "valueAxisCrossing", this::isValueAxisCrossing, "crossesFarRight", this::isCrossesFarRight, "reversed", this::isReversed);
    }
}

