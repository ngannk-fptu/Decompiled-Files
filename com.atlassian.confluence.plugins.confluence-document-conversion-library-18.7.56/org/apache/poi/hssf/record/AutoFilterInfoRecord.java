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

public final class AutoFilterInfoRecord
extends StandardRecord {
    public static final short sid = 157;
    private short _cEntries;

    public AutoFilterInfoRecord() {
    }

    public AutoFilterInfoRecord(AutoFilterInfoRecord other) {
        super(other);
        this._cEntries = other._cEntries;
    }

    public AutoFilterInfoRecord(RecordInputStream in) {
        this._cEntries = in.readShort();
    }

    public void setNumEntries(short num) {
        this._cEntries = num;
    }

    public short getNumEntries() {
        return this._cEntries;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.writeShort(this._cEntries);
    }

    @Override
    protected int getDataSize() {
        return 2;
    }

    @Override
    public short getSid() {
        return 157;
    }

    @Override
    public AutoFilterInfoRecord copy() {
        return new AutoFilterInfoRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.AUTO_FILTER_INFO;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("numEntries", this::getNumEntries);
    }
}

