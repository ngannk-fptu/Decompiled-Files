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

public final class AreaRecord
extends StandardRecord {
    public static final short sid = 4122;
    private static final BitField stacked = BitFieldFactory.getInstance(1);
    private static final BitField displayAsPercentage = BitFieldFactory.getInstance(2);
    private static final BitField shadow = BitFieldFactory.getInstance(4);
    private short field_1_formatFlags;

    public AreaRecord() {
    }

    public AreaRecord(AreaRecord other) {
        super(other);
        this.field_1_formatFlags = other.field_1_formatFlags;
    }

    public AreaRecord(RecordInputStream in) {
        this.field_1_formatFlags = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_formatFlags);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 4122;
    }

    public short getFormatFlags() {
        return this.field_1_formatFlags;
    }

    public void setFormatFlags(short field_1_formatFlags) {
        this.field_1_formatFlags = field_1_formatFlags;
    }

    public void setStacked(boolean value) {
        this.field_1_formatFlags = stacked.setShortBoolean(this.field_1_formatFlags, value);
    }

    public boolean isStacked() {
        return stacked.isSet(this.field_1_formatFlags);
    }

    public void setDisplayAsPercentage(boolean value) {
        this.field_1_formatFlags = displayAsPercentage.setShortBoolean(this.field_1_formatFlags, value);
    }

    public boolean isDisplayAsPercentage() {
        return displayAsPercentage.isSet(this.field_1_formatFlags);
    }

    public void setShadow(boolean value) {
        this.field_1_formatFlags = shadow.setShortBoolean(this.field_1_formatFlags, value);
    }

    public boolean isShadow() {
        return shadow.isSet(this.field_1_formatFlags);
    }

    @Override
    public AreaRecord copy() {
        return new AreaRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.AREA;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("formatFlags", this::getFormatFlags, "stacked", this::isStacked, "displayAsPercentage", this::isDisplayAsPercentage, "shadow", this::isShadow);
    }
}

