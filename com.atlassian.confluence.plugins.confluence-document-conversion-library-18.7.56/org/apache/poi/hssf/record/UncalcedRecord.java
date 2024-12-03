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

public final class UncalcedRecord
extends StandardRecord {
    public static final short sid = 94;
    private short _reserved;

    public UncalcedRecord() {
        this._reserved = 0;
    }

    public UncalcedRecord(UncalcedRecord other) {
        super(other);
        this._reserved = other._reserved;
    }

    @Override
    public short getSid() {
        return 94;
    }

    public UncalcedRecord(RecordInputStream in) {
        this._reserved = in.readShort();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._reserved);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    public static int getStaticRecordSize() {
        return 6;
    }

    @Override
    public UncalcedRecord copy() {
        return new UncalcedRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.UNCALCED;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("reserved", () -> this._reserved);
    }
}

