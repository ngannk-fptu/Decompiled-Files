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

public final class FontBasisRecord
extends StandardRecord {
    public static final short sid = 4192;
    private short field_1_xBasis;
    private short field_2_yBasis;
    private short field_3_heightBasis;
    private short field_4_scale;
    private short field_5_indexToFontTable;

    public FontBasisRecord() {
    }

    public FontBasisRecord(FontBasisRecord other) {
        super(other);
        this.field_1_xBasis = other.field_1_xBasis;
        this.field_2_yBasis = other.field_2_yBasis;
        this.field_3_heightBasis = other.field_3_heightBasis;
        this.field_4_scale = other.field_4_scale;
        this.field_5_indexToFontTable = other.field_5_indexToFontTable;
    }

    public FontBasisRecord(RecordInputStream in) {
        this.field_1_xBasis = in.readShort();
        this.field_2_yBasis = in.readShort();
        this.field_3_heightBasis = in.readShort();
        this.field_4_scale = in.readShort();
        this.field_5_indexToFontTable = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_xBasis);
        out.writeShort(this.field_2_yBasis);
        out.writeShort(this.field_3_heightBasis);
        out.writeShort(this.field_4_scale);
        out.writeShort(this.field_5_indexToFontTable);
    }

    @Override
    protected int getDataSize() {
        return 10;
    }

    @Override
    public short getSid() {
        return 4192;
    }

    @Override
    public FontBasisRecord copy() {
        return new FontBasisRecord(this);
    }

    public short getXBasis() {
        return this.field_1_xBasis;
    }

    public void setXBasis(short field_1_xBasis) {
        this.field_1_xBasis = field_1_xBasis;
    }

    public short getYBasis() {
        return this.field_2_yBasis;
    }

    public void setYBasis(short field_2_yBasis) {
        this.field_2_yBasis = field_2_yBasis;
    }

    public short getHeightBasis() {
        return this.field_3_heightBasis;
    }

    public void setHeightBasis(short field_3_heightBasis) {
        this.field_3_heightBasis = field_3_heightBasis;
    }

    public short getScale() {
        return this.field_4_scale;
    }

    public void setScale(short field_4_scale) {
        this.field_4_scale = field_4_scale;
    }

    public short getIndexToFontTable() {
        return this.field_5_indexToFontTable;
    }

    public void setIndexToFontTable(short field_5_indexToFontTable) {
        this.field_5_indexToFontTable = field_5_indexToFontTable;
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.FONT_BASIS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("xBasis", this::getXBasis, "yBasis", this::getYBasis, "heightBasis", this::getHeightBasis, "scale", this::getScale, "indexToFontTable", this::getIndexToFontTable);
    }
}

