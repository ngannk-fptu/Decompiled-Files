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

public final class DefaultRowHeightRecord
extends StandardRecord {
    public static final short sid = 549;
    public static final short DEFAULT_ROW_HEIGHT = 255;
    private short field_1_option_flags;
    private short field_2_row_height;

    public DefaultRowHeightRecord() {
        this.field_1_option_flags = 0;
        this.field_2_row_height = (short)255;
    }

    public DefaultRowHeightRecord(DefaultRowHeightRecord other) {
        super(other);
        this.field_1_option_flags = other.field_1_option_flags;
        this.field_2_row_height = other.field_2_row_height;
    }

    public DefaultRowHeightRecord(RecordInputStream in) {
        this.field_1_option_flags = in.readShort();
        this.field_2_row_height = in.readShort();
    }

    public void setOptionFlags(short flags) {
        this.field_1_option_flags = flags;
    }

    public void setRowHeight(short height) {
        this.field_2_row_height = height;
    }

    public short getOptionFlags() {
        return this.field_1_option_flags;
    }

    public short getRowHeight() {
        return this.field_2_row_height;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getOptionFlags());
        out.writeShort(this.getRowHeight());
    }

    @Override
    protected int getDataSize() {
        return 4;
    }

    @Override
    public short getSid() {
        return 549;
    }

    @Override
    public DefaultRowHeightRecord copy() {
        return new DefaultRowHeightRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.DEFAULT_ROW_HEIGHT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("optionFlags", this::getOptionFlags, "rowHeight", this::getRowHeight);
    }
}

