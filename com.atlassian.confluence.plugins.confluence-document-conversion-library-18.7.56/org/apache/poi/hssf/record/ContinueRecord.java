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

public final class ContinueRecord
extends StandardRecord {
    public static final short sid = 60;
    private byte[] _data;

    public ContinueRecord(byte[] data) {
        this._data = (byte[])data.clone();
    }

    public ContinueRecord(ContinueRecord other) {
        super(other);
        this._data = other._data == null ? null : (byte[])other._data.clone();
    }

    @Override
    protected int getDataSize() {
        return this._data.length;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.write(this._data);
    }

    public byte[] getData() {
        return this._data;
    }

    @Override
    public short getSid() {
        return 60;
    }

    public ContinueRecord(RecordInputStream in) {
        this._data = in.readRemainder();
    }

    @Override
    public ContinueRecord copy() {
        return new ContinueRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.CONTINUE;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("data", this::getData);
    }
}

