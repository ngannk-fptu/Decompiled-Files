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

public final class DefaultColWidthRecord
extends StandardRecord {
    public static final short sid = 85;
    public static final int DEFAULT_COLUMN_WIDTH = 8;
    private int field_1_col_width;

    public DefaultColWidthRecord() {
        this.field_1_col_width = 8;
    }

    public DefaultColWidthRecord(DefaultColWidthRecord other) {
        super(other);
        this.field_1_col_width = other.field_1_col_width;
    }

    public DefaultColWidthRecord(RecordInputStream in) {
        this.field_1_col_width = in.readUShort();
    }

    public void setColWidth(int width) {
        this.field_1_col_width = width;
    }

    public int getColWidth() {
        return this.field_1_col_width;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getColWidth());
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 85;
    }

    @Override
    public DefaultColWidthRecord copy() {
        return new DefaultColWidthRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.DEFAULT_COL_WIDTH;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("colWidth", this::getColWidth);
    }
}

