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

public final class DataFormatRecord
extends StandardRecord {
    public static final short sid = 4102;
    private static final BitField useExcel4Colors = BitFieldFactory.getInstance(1);
    private short field_1_pointNumber;
    private short field_2_seriesIndex;
    private short field_3_seriesNumber;
    private short field_4_formatFlags;

    public DataFormatRecord() {
    }

    public DataFormatRecord(DataFormatRecord other) {
        super(other);
        this.field_1_pointNumber = other.field_1_pointNumber;
        this.field_2_seriesIndex = other.field_2_seriesIndex;
        this.field_3_seriesNumber = other.field_3_seriesNumber;
        this.field_4_formatFlags = other.field_4_formatFlags;
    }

    public DataFormatRecord(RecordInputStream in) {
        this.field_1_pointNumber = in.readShort();
        this.field_2_seriesIndex = in.readShort();
        this.field_3_seriesNumber = in.readShort();
        this.field_4_formatFlags = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_pointNumber);
        out.writeShort(this.field_2_seriesIndex);
        out.writeShort(this.field_3_seriesNumber);
        out.writeShort(this.field_4_formatFlags);
    }

    @Override
    protected int getDataSize() {
        return 8;
    }

    @Override
    public short getSid() {
        return 4102;
    }

    @Override
    public DataFormatRecord copy() {
        return new DataFormatRecord(this);
    }

    public short getPointNumber() {
        return this.field_1_pointNumber;
    }

    public void setPointNumber(short field_1_pointNumber) {
        this.field_1_pointNumber = field_1_pointNumber;
    }

    public short getSeriesIndex() {
        return this.field_2_seriesIndex;
    }

    public void setSeriesIndex(short field_2_seriesIndex) {
        this.field_2_seriesIndex = field_2_seriesIndex;
    }

    public short getSeriesNumber() {
        return this.field_3_seriesNumber;
    }

    public void setSeriesNumber(short field_3_seriesNumber) {
        this.field_3_seriesNumber = field_3_seriesNumber;
    }

    public short getFormatFlags() {
        return this.field_4_formatFlags;
    }

    public void setFormatFlags(short field_4_formatFlags) {
        this.field_4_formatFlags = field_4_formatFlags;
    }

    public void setUseExcel4Colors(boolean value) {
        this.field_4_formatFlags = useExcel4Colors.setShortBoolean(this.field_4_formatFlags, value);
    }

    public boolean isUseExcel4Colors() {
        return useExcel4Colors.isSet(this.field_4_formatFlags);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.DATA_FORMAT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("pointNumber", this::getPointNumber, "seriesIndex", this::getSeriesIndex, "seriesNumber", this::getSeriesNumber, "formatFlags", this::getFormatFlags, "useExcel4Colors", this::isUseExcel4Colors);
    }
}

