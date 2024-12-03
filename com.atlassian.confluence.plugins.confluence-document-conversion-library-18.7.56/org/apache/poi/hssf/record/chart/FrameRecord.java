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

public final class FrameRecord
extends StandardRecord {
    public static final short sid = 4146;
    public static final short BORDER_TYPE_REGULAR = 0;
    public static final short BORDER_TYPE_SHADOW = 1;
    private static final BitField autoSize = BitFieldFactory.getInstance(1);
    private static final BitField autoPosition = BitFieldFactory.getInstance(2);
    private short field_1_borderType;
    private short field_2_options;

    public FrameRecord() {
    }

    public FrameRecord(FrameRecord other) {
        super(other);
        this.field_1_borderType = other.field_1_borderType;
        this.field_2_options = other.field_2_options;
    }

    public FrameRecord(RecordInputStream in) {
        this.field_1_borderType = in.readShort();
        this.field_2_options = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_borderType);
        out.writeShort(this.field_2_options);
    }

    @Override
    protected int getDataSize() {
        return 4;
    }

    @Override
    public short getSid() {
        return 4146;
    }

    @Override
    public FrameRecord copy() {
        return new FrameRecord(this);
    }

    public short getBorderType() {
        return this.field_1_borderType;
    }

    public void setBorderType(short field_1_borderType) {
        this.field_1_borderType = field_1_borderType;
    }

    public short getOptions() {
        return this.field_2_options;
    }

    public void setOptions(short field_2_options) {
        this.field_2_options = field_2_options;
    }

    public void setAutoSize(boolean value) {
        this.field_2_options = autoSize.setShortBoolean(this.field_2_options, value);
    }

    public boolean isAutoSize() {
        return autoSize.isSet(this.field_2_options);
    }

    public void setAutoPosition(boolean value) {
        this.field_2_options = autoPosition.setShortBoolean(this.field_2_options, value);
    }

    public boolean isAutoPosition() {
        return autoPosition.isSet(this.field_2_options);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.FRAME;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("borderType", this::getBorderType, "options", this::getOptions, "autoSize", this::isAutoSize, "autoPosition", this::isAutoPosition);
    }
}

