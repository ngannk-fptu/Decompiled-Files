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

public final class DateWindow1904Record
extends StandardRecord {
    public static final short sid = 34;
    private short field_1_window;

    public DateWindow1904Record() {
    }

    public DateWindow1904Record(DateWindow1904Record other) {
        super(other);
        this.field_1_window = other.field_1_window;
    }

    public DateWindow1904Record(RecordInputStream in) {
        this.field_1_window = in.readShort();
    }

    public void setWindowing(short window) {
        this.field_1_window = window;
    }

    public short getWindowing() {
        return this.field_1_window;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getWindowing());
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 34;
    }

    @Override
    public DateWindow1904Record copy() {
        return new DateWindow1904Record(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.DATE_WINDOW_1904;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("is1904", this::getWindowing);
    }
}

