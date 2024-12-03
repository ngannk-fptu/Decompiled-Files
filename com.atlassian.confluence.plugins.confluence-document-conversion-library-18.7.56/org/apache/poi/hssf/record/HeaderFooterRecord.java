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

public final class HeaderFooterRecord
extends StandardRecord {
    public static final short sid = 2204;
    private static final byte[] BLANK_GUID = new byte[16];
    private byte[] _rawData;

    public HeaderFooterRecord(byte[] data) {
        this._rawData = data;
    }

    public HeaderFooterRecord(HeaderFooterRecord other) {
        super(other);
        this._rawData = other._rawData == null ? null : (byte[])other._rawData.clone();
    }

    public HeaderFooterRecord(RecordInputStream in) {
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
        return 2204;
    }

    public byte[] getGuid() {
        return Arrays.copyOfRange(this._rawData, 12, 28);
    }

    public boolean isCurrentSheet() {
        return Arrays.equals(this.getGuid(), BLANK_GUID);
    }

    @Override
    public HeaderFooterRecord copy() {
        return new HeaderFooterRecord(this);
    }

    @Override
    public HSSFRecordTypes getGenericRecordType() {
        return HSSFRecordTypes.HEADER_FOOTER;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("rawData", () -> this._rawData);
    }
}

