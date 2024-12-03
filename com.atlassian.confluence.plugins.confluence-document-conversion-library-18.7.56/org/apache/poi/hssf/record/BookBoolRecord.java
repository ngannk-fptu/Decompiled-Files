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

public final class BookBoolRecord
extends StandardRecord {
    public static final short sid = 218;
    private short field_1_save_link_values;

    public BookBoolRecord() {
    }

    public BookBoolRecord(BookBoolRecord other) {
        super(other);
        this.field_1_save_link_values = other.field_1_save_link_values;
    }

    public BookBoolRecord(RecordInputStream in) {
        this.field_1_save_link_values = in.readShort();
    }

    public void setSaveLinkValues(short flag) {
        this.field_1_save_link_values = flag;
    }

    public short getSaveLinkValues() {
        return this.field_1_save_link_values;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.field_1_save_link_values);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 218;
    }

    @Override
    public BookBoolRecord copy() {
        return new BookBoolRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.BOOK_BOOL;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("saveLinkValues", this::getSaveLinkValues);
    }
}

