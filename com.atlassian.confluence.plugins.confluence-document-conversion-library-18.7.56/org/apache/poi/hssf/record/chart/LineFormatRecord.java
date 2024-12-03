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

public final class LineFormatRecord
extends StandardRecord {
    public static final short sid = 4103;
    private static final BitField auto = BitFieldFactory.getInstance(1);
    private static final BitField drawTicks = BitFieldFactory.getInstance(4);
    private static final BitField unknown = BitFieldFactory.getInstance(4);
    public static final short LINE_PATTERN_SOLID = 0;
    public static final short LINE_PATTERN_DASH = 1;
    public static final short LINE_PATTERN_DOT = 2;
    public static final short LINE_PATTERN_DASH_DOT = 3;
    public static final short LINE_PATTERN_DASH_DOT_DOT = 4;
    public static final short LINE_PATTERN_NONE = 5;
    public static final short LINE_PATTERN_DARK_GRAY_PATTERN = 6;
    public static final short LINE_PATTERN_MEDIUM_GRAY_PATTERN = 7;
    public static final short LINE_PATTERN_LIGHT_GRAY_PATTERN = 8;
    public static final short WEIGHT_HAIRLINE = -1;
    public static final short WEIGHT_NARROW = 0;
    public static final short WEIGHT_MEDIUM = 1;
    public static final short WEIGHT_WIDE = 2;
    private int field_1_lineColor;
    private short field_2_linePattern;
    private short field_3_weight;
    private short field_4_format;
    private short field_5_colourPaletteIndex;

    public LineFormatRecord() {
    }

    public LineFormatRecord(LineFormatRecord other) {
        super(other);
        this.field_1_lineColor = other.field_1_lineColor;
        this.field_2_linePattern = other.field_2_linePattern;
        this.field_3_weight = other.field_3_weight;
        this.field_4_format = other.field_4_format;
        this.field_5_colourPaletteIndex = other.field_5_colourPaletteIndex;
    }

    public LineFormatRecord(RecordInputStream in) {
        this.field_1_lineColor = in.readInt();
        this.field_2_linePattern = in.readShort();
        this.field_3_weight = in.readShort();
        this.field_4_format = in.readShort();
        this.field_5_colourPaletteIndex = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeInt(this.field_1_lineColor);
        out.writeShort(this.field_2_linePattern);
        out.writeShort(this.field_3_weight);
        out.writeShort(this.field_4_format);
        out.writeShort(this.field_5_colourPaletteIndex);
    }

    @Override
    protected int getDataSize() {
        return 12;
    }

    @Override
    public short getSid() {
        return 4103;
    }

    @Override
    public LineFormatRecord copy() {
        return new LineFormatRecord(this);
    }

    public int getLineColor() {
        return this.field_1_lineColor;
    }

    public void setLineColor(int field_1_lineColor) {
        this.field_1_lineColor = field_1_lineColor;
    }

    public short getLinePattern() {
        return this.field_2_linePattern;
    }

    public void setLinePattern(short field_2_linePattern) {
        this.field_2_linePattern = field_2_linePattern;
    }

    public short getWeight() {
        return this.field_3_weight;
    }

    public void setWeight(short field_3_weight) {
        this.field_3_weight = field_3_weight;
    }

    public short getFormat() {
        return this.field_4_format;
    }

    public void setFormat(short field_4_format) {
        this.field_4_format = field_4_format;
    }

    public short getColourPaletteIndex() {
        return this.field_5_colourPaletteIndex;
    }

    public void setColourPaletteIndex(short field_5_colourPaletteIndex) {
        this.field_5_colourPaletteIndex = field_5_colourPaletteIndex;
    }

    public void setAuto(boolean value) {
        this.field_4_format = auto.setShortBoolean(this.field_4_format, value);
    }

    public boolean isAuto() {
        return auto.isSet(this.field_4_format);
    }

    public void setDrawTicks(boolean value) {
        this.field_4_format = drawTicks.setShortBoolean(this.field_4_format, value);
    }

    public boolean isDrawTicks() {
        return drawTicks.isSet(this.field_4_format);
    }

    public void setUnknown(boolean value) {
        this.field_4_format = unknown.setShortBoolean(this.field_4_format, value);
    }

    public boolean isUnknown() {
        return unknown.isSet(this.field_4_format);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.LINE_FORMAT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("lineColor", this::getLineColor, "linePattern", GenericRecordUtil.getEnumBitsAsString(this::getLinePattern, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8}, new String[]{"SOLID", "DASH", "DOT", "DASH_DOT", "DASH_DOT_DOT", "NONE", "DARK_GRAY_PATTERN", "MEDIUM_GRAY_PATTERN", "LIGHT_GRAY_PATTERN"}), "weight", GenericRecordUtil.getEnumBitsAsString(this::getWeight, new int[]{-1, 0, 1, 2}, new String[]{"HAIRLINE", "NARROW", "MEDIUM", "WIDE"}), "format", GenericRecordUtil.getBitsAsString(this::getFormat, new BitField[]{auto, drawTicks, unknown}, new String[]{"AUTO", "DRAWTICKS", "UNKNOWN"}), "colourPaletteIndex", this::getColourPaletteIndex);
    }
}

