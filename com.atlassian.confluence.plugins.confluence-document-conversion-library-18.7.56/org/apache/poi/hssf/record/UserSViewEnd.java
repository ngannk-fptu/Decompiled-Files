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

public final class UserSViewEnd
extends StandardRecord {
    public static final short sid = 427;
    private byte[] _rawData;

    public UserSViewEnd(UserSViewEnd other) {
        super(other);
        this._rawData = other._rawData == null ? null : (byte[])other._rawData.clone();
    }

    public UserSViewEnd(byte[] data) {
        this._rawData = data;
    }

    public UserSViewEnd(RecordInputStream in) {
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
        return 427;
    }

    @Override
    public UserSViewEnd copy() {
        return new UserSViewEnd(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.USER_SVIEW_END;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("rawData", () -> this._rawData);
    }
}

