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

public final class LegendRecord
extends StandardRecord {
    public static final short sid = 4117;
    private static final BitField autoPosition = BitFieldFactory.getInstance(1);
    private static final BitField autoSeries = BitFieldFactory.getInstance(2);
    private static final BitField autoXPositioning = BitFieldFactory.getInstance(4);
    private static final BitField autoYPositioning = BitFieldFactory.getInstance(8);
    private static final BitField vertical = BitFieldFactory.getInstance(16);
    private static final BitField dataTable = BitFieldFactory.getInstance(32);
    public static final byte TYPE_BOTTOM = 0;
    public static final byte TYPE_CORNER = 1;
    public static final byte TYPE_TOP = 2;
    public static final byte TYPE_RIGHT = 3;
    public static final byte TYPE_LEFT = 4;
    public static final byte TYPE_UNDOCKED = 7;
    public static final byte SPACING_CLOSE = 0;
    public static final byte SPACING_MEDIUM = 1;
    public static final byte SPACING_OPEN = 2;
    private int field_1_xAxisUpperLeft;
    private int field_2_yAxisUpperLeft;
    private int field_3_xSize;
    private int field_4_ySize;
    private byte field_5_type;
    private byte field_6_spacing;
    private short field_7_options;

    public LegendRecord() {
    }

    public LegendRecord(LegendRecord other) {
        super(other);
        this.field_1_xAxisUpperLeft = other.field_1_xAxisUpperLeft;
        this.field_2_yAxisUpperLeft = other.field_2_yAxisUpperLeft;
        this.field_3_xSize = other.field_3_xSize;
        this.field_4_ySize = other.field_4_ySize;
        this.field_5_type = other.field_5_type;
        this.field_6_spacing = other.field_6_spacing;
        this.field_7_options = other.field_7_options;
    }

    public LegendRecord(RecordInputStream in) {
        this.field_1_xAxisUpperLeft = in.readInt();
        this.field_2_yAxisUpperLeft = in.readInt();
        this.field_3_xSize = in.readInt();
        this.field_4_ySize = in.readInt();
        this.field_5_type = in.readByte();
        this.field_6_spacing = in.readByte();
        this.field_7_options = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeInt(this.field_1_xAxisUpperLeft);
        out.writeInt(this.field_2_yAxisUpperLeft);
        out.writeInt(this.field_3_xSize);
        out.writeInt(this.field_4_ySize);
        out.writeByte(this.field_5_type);
        out.writeByte(this.field_6_spacing);
        out.writeShort(this.field_7_options);
    }

    @Override
    protected int getDataSize() {
        return 20;
    }

    @Override
    public short getSid() {
        return 4117;
    }

    @Override
    public LegendRecord copy() {
        return new LegendRecord(this);
    }

    public int getXAxisUpperLeft() {
        return this.field_1_xAxisUpperLeft;
    }

    public void setXAxisUpperLeft(int field_1_xAxisUpperLeft) {
        this.field_1_xAxisUpperLeft = field_1_xAxisUpperLeft;
    }

    public int getYAxisUpperLeft() {
        return this.field_2_yAxisUpperLeft;
    }

    public void setYAxisUpperLeft(int field_2_yAxisUpperLeft) {
        this.field_2_yAxisUpperLeft = field_2_yAxisUpperLeft;
    }

    public int getXSize() {
        return this.field_3_xSize;
    }

    public void setXSize(int field_3_xSize) {
        this.field_3_xSize = field_3_xSize;
    }

    public int getYSize() {
        return this.field_4_ySize;
    }

    public void setYSize(int field_4_ySize) {
        this.field_4_ySize = field_4_ySize;
    }

    public byte getType() {
        return this.field_5_type;
    }

    public void setType(byte field_5_type) {
        this.field_5_type = field_5_type;
    }

    public byte getSpacing() {
        return this.field_6_spacing;
    }

    public void setSpacing(byte field_6_spacing) {
        this.field_6_spacing = field_6_spacing;
    }

    public short getOptions() {
        return this.field_7_options;
    }

    public void setOptions(short field_7_options) {
        this.field_7_options = field_7_options;
    }

    public void setAutoPosition(boolean value) {
        this.field_7_options = autoPosition.setShortBoolean(this.field_7_options, value);
    }

    public boolean isAutoPosition() {
        return autoPosition.isSet(this.field_7_options);
    }

    public void setAutoSeries(boolean value) {
        this.field_7_options = autoSeries.setShortBoolean(this.field_7_options, value);
    }

    public boolean isAutoSeries() {
        return autoSeries.isSet(this.field_7_options);
    }

    public void setAutoXPositioning(boolean value) {
        this.field_7_options = autoXPositioning.setShortBoolean(this.field_7_options, value);
    }

    public boolean isAutoXPositioning() {
        return autoXPositioning.isSet(this.field_7_options);
    }

    public void setAutoYPositioning(boolean value) {
        this.field_7_options = autoYPositioning.setShortBoolean(this.field_7_options, value);
    }

    public boolean isAutoYPositioning() {
        return autoYPositioning.isSet(this.field_7_options);
    }

    public void setVertical(boolean value) {
        this.field_7_options = vertical.setShortBoolean(this.field_7_options, value);
    }

    public boolean isVertical() {
        return vertical.isSet(this.field_7_options);
    }

    public void setDataTable(boolean value) {
        this.field_7_options = dataTable.setShortBoolean(this.field_7_options, value);
    }

    public boolean isDataTable() {
        return dataTable.isSet(this.field_7_options);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.LEGEND;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("xAxisUpperLeft", this::getXAxisUpperLeft, "yAxisUpperLeft", this::getYAxisUpperLeft, "xSize", this::getXSize, "ySize", this::getYSize, "type", GenericRecordUtil.getEnumBitsAsString(this::getType, new int[]{0, 1, 2, 3, 4, 7}, new String[]{"BOTTOM", "CORNER", "TOP", "RIGHT", "LEFT", "UNDOCKED"}), "spacing", GenericRecordUtil.getEnumBitsAsString(this::getSpacing, new int[]{0, 1, 2}, new String[]{"CLOSE", "MEDIUM", "OPEN"}), "options", GenericRecordUtil.getBitsAsString(this::getOptions, new BitField[]{autoPosition, autoSeries, autoXPositioning, autoYPositioning, vertical, dataTable}, new String[]{"AUTO_POSITION", "AUTO_SERIES", "AUTO_X_POSITIONING", "AUTO_Y_POSITIONING", "VERTICAL", "DATA_TABLE"}));
    }
}

