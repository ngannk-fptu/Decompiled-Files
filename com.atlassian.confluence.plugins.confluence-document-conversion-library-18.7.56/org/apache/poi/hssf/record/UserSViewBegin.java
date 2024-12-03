/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.HSSFRecordTypes;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.StandardRecord;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class UserSViewBegin
extends StandardRecord {
    public static final short sid = 426;
    private byte[] _rawData;

    public UserSViewBegin(UserSViewBegin other) {
        super(other);
        this._rawData = other._rawData == null ? null : (byte[])other._rawData.clone();
    }

    public UserSViewBegin(byte[] data) {
        this._rawData = data;
    }

    public UserSViewBegin(RecordInputStream in) {
        this._rawData = in.readRemainder();
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.write(this._rawData);
    }

    @Override
    protected int getDataSize() {
        return this._rawData.length;
    }

    @Override
    public short getSid() {
        return 426;
    }

    public byte[] getGuid() {
        return Arrays.copyOf(this._rawData, 16);
    }

    @Override
    public UserSViewBegin copy() {
        return new UserSViewBegin(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.USER_SVIEW_BEGIN;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("guid", this::getGuid, "rawData", () -> this._rawData);
    }
}

