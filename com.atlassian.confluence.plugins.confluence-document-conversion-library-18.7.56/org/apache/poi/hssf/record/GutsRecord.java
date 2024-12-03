/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class GutsRecord
extends StandardRecord {
    public static final short sid = 128;
    private short field_1_left_row_gutter;
    private short field_2_top_col_gutter;
    private short field_3_row_level_max;
    private short field_4_col_level_max;

    public GutsRecord() {
    }

    public GutsRecord(GutsRecord other) {
        super(other);
        this.field_1_left_row_gutter = other.field_1_left_row_gutter;
        this.field_2_top_col_gutter = other.field_2_top_col_gutter;
        this.field_3_row_level_max = other.field_3_row_level_max;
        this.field_4_col_level_max = other.field_4_col_level_max;
    }

    public GutsRecord(RecordInputStream in) {
        this.field_1_left_row_gutter = in.readShort();
        this.field_2_top_col_gutter = in.readShort();
        this.field_3_row_level_max = in.readShort();
        this.field_4_col_level_max = in.readShort();
    }

    public void setLeftRowGutter(short gut) {
        this.field_1_left_row_gutter = gut;
    }

    public void setTopColGutter(short gut) {
        this.field_2_top_col_gutter = gut;
    }

    public void setRowLevelMax(short max) {
        this.field_3_row_level_max = max;
    }

    public void setColLevelMax(short max) {
        this.field_4_col_level_max = max;
    }

    public short getLeftRowGutter() {
        return this.field_1_left_row_gutter;
    }

    public short getTopColGutter() {
        return this.field_2_top_col_gutter;
    }

    public short getRowLevelMax() {
        return this.field_3_row_level_max;
    }

    public short getColLevelMax() {
        return this.field_4_col_level_max;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getLeftRowGutter());
        out.writeShort(this.getTopColGutter());
        out.writeShort(this.getRowLevelMax());
        out.writeShort(this.getColLevelMax());
    }

    @Override
    protected int getDataSize() {
        return 8;
    }

    @Override
    public short getSid() {
        return 128;
    }

    @Override
    public GutsRecord copy() {
        return new GutsRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.GUTS;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("leftGutter", this::getLeftRowGutter, "topGutter", this::getTopColGutter, "rowLevelMax", this::getRowLevelMax, "colLevelMax", this::getColLevelMax);
    }
}

