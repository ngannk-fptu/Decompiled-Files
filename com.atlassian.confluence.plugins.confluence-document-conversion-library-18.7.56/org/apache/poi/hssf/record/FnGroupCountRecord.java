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

public final class FnGroupCountRecord
extends StandardRecord {
    public static final short sid = 156;
    public static final short COUNT = 14;
    private short field_1_count;

    public FnGroupCountRecord() {
    }

    public FnGroupCountRecord(FnGroupCountRecord other) {
        super(other);
        this.field_1_count = other.field_1_count;
    }

    public FnGroupCountRecord(RecordInputStream in) {
        this.field_1_count = in.readShort();
    }

    public void setCount(short count) {
        this.field_1_count = count;
    }

    public short getCount() {
        return this.field_1_count;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this.getCount());
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 156;
    }

    @Override
    public FnGroupCountRecord copy() {
        return new FnGroupCountRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.FN_GROUP_COUNT;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("count", this::getCount);
    }
}

