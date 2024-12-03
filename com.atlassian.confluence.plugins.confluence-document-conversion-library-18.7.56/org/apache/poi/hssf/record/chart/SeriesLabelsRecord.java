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

public final class SeriesLabelsRecord
extends StandardRecord {
    public static final short sid = 4108;
    private static final BitField showActual = BitFieldFactory.getInstance(1);
    private static final BitField showPercent = BitFieldFactory.getInstance(2);
    private static final BitField labelAsPercentage = BitFieldFactory.getInstance(4);
    private static final BitField smoothedLine = BitFieldFactory.getInstance(8);
    private static final BitField showLabel = BitFieldFactory.getInstance(16);
    private static final BitField showBubbleSizes = BitFieldFactory.getInstance(32);
    private short field_1_formatFlags;

    public SeriesLabelsRecord() {
    }

    public SeriesLabelsRecord(SeriesLabelsRecord other) {
        super(other);
        this.field_1_formatFlags = other.field_1_formatFlags;
    }

    public SeriesLabelsRecord(RecordInputStream in) {
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
        return 4108;
    }

    @Override
    public SeriesLabelsRecord copy() {
        return new SeriesLabelsRecord(this);
    }

    public short getFormatFlags() {
        return this.field_1_formatFlags;
    }

    public void setFormatFlags(short field_1_formatFlags) {
        this.field_1_formatFlags = field_1_formatFlags;
    }

    public void setShowActual(boolean value) {
        this.field_1_formatFlags = showActual.setShortBoolean(this.field_1_formatFlags, value);
    }

    public boolean isShowActual() {
        return showActual.isSet(this.field_1_formatFlags);
    }

    public void setShowPercent(boolean value) {
        this.field_1_formatFlags = showPercent.setShortBoolean(this.field_1_formatFlags, value);
    }

    public boolean isShowPercent() {
        return showPercent.isSet(this.field_1_formatFlags);
    }

    public void setLabelAsPercentage(boolean value) {
        this.field_1_formatFlags = labelAsPercentage.setShortBoolean(this.field_1_formatFlags, value);
    }

    public boolean isLabelAsPercentage() {
        return labelAsPercentage.isSet(this.field_1_formatFlags);
    }

    public void setSmoothedLine(boolean value) {
        this.field_1_formatFlags = smoothedLine.setShortBoolean(this.field_1_formatFlags, value);
    }

    public boolean isSmoothedLine() {
        return smoothedLine.isSet(this.field_1_formatFlags);
    }

    public void setShowLabel(boolean value) {
        this.field_1_formatFlags = showLabel.setShortBoolean(this.field_1_formatFlags, value);
    }

    public boolean isShowLabel() {
        return showLabel.isSet(this.field_1_formatFlags);
    }

    public void setShowBubbleSizes(boolean value) {
        this.field_1_formatFlags = showBubbleSizes.setShortBoolean(this.field_1_formatFlags, value);
    }

    public boolean isShowBubbleSizes() {
        return showBubbleSizes.isSet(this.field_1_formatFlags);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.SERIES_LABELS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("formatFlags", GenericRecordUtil.getBitsAsString(this::getFormatFlags, new BitField[]{showActual, showPercent, labelAsPercentage, smoothedLine, showLabel, showBubbleSizes}, new String[]{"SHOW_ACTUAL", "SHOW_PERCENT", "LABEL_AS_PERCENTAGE", "SMOOTHED_LINE", "SHOW_LABEL", "SHOW_BUBBLE_SIZES"}));
    }
}

