/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.chart;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndianOutput;

public final class BarRecord
extends StandardRecord {
    public static final short sid = 4119;
    private static final BitField horizontal = BitFieldFactory.getInstance(1);
    private static final BitField stacked = BitFieldFactory.getInstance(2);
    private static final BitField displayAsPercentage = BitFieldFactory.getInstance(4);
    private static final BitField shadow = BitFieldFactory.getInstance(8);
    private short field_1_barSpace;
    private short field_2_categorySpace;
    private short field_3_formatFlags;

    public BarRecord() {
    }

    public BarRecord(BarRecord other) {
        super(other);
        this.field_1_barSpace = other.field_1_barSpace;
        this.field_2_categorySpace = other.field_2_categorySpace;
        this.field_3_formatFlags = other.field_3_formatFlags;
    }

    public BarRecord(RecordInputStream in) {
        this.field_1_barSpace = in.readShort();
        this.field_2_categorySpace = in.readShort();
        this.field_3_formatFlags = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_barSpace);
        out.writeShort(this.field_2_categorySpace);
        out.writeShort(this.field_3_formatFlags);
    }

    @Override
    protected int getDataSize() {
        return 6;
    }

    @Override
    public short getSid() {
        return 4119;
    }

    public short getBarSpace() {
        return this.field_1_barSpace;
    }

    public void setBarSpace(short field_1_barSpace) {
        this.field_1_barSpace = field_1_barSpace;
    }

    public short getCategorySpace() {
        return this.field_2_categorySpace;
    }

    public void setCategorySpace(short field_2_categorySpace) {
        this.field_2_categorySpace = field_2_categorySpace;
    }

    public short getFormatFlags() {
        return this.field_3_formatFlags;
    }

    public void setFormatFlags(short field_3_formatFlags) {
        this.field_3_formatFlags = field_3_formatFlags;
    }

    public void setHorizontal(boolean value) {
        this.field_3_formatFlags = horizontal.setShortBoolean(this.field_3_formatFlags, value);
    }

    public boolean isHorizontal() {
        return horizontal.isSet(this.field_3_formatFlags);
    }

    public void setStacked(boolean value) {
        this.field_3_formatFlags = stacked.setShortBoolean(this.field_3_formatFlags, value);
    }

    public boolean isStacked() {
        return stacked.isSet(this.field_3_formatFlags);
    }

    public void setDisplayAsPercentage(boolean value) {
        this.field_3_formatFlags = displayAsPercentage.setShortBoolean(this.field_3_formatFlags, value);
    }

    public boolean isDisplayAsPercentage() {
        return displayAsPercentage.isSet(this.field_3_formatFlags);
    }

    public void setShadow(boolean value) {
        this.field_3_formatFlags = shadow.setShortBoolean(this.field_3_formatFlags, value);
    }

    public boolean isShadow() {
        return shadow.isSet(this.field_3_formatFlags);
    }

    @Override
    public BarRecord copy() {
        return new BarRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.BAR;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        LinkedHashMap<String, Supplier<Object>> m = new LinkedHashMap<String, Supplier<Object>>();
        m.put("barSpace", this::getBarSpace);
        m.put("categorySpace", this::getCategorySpace);
        m.put("formatFlags", this::getFormatFlags);
        m.put("horizontal", this::isHorizontal);
        m.put("stacked", this::isStacked);
        m.put("displayAsPercentage", this::isDisplayAsPercentage);
        m.put("shadow", this::isShadow);
        return Collections.unmodifiableMap(m);
    }
}

