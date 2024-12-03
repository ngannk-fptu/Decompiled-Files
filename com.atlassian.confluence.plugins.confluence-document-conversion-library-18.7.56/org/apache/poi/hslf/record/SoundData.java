/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class SoundData
extends RecordAtom {
    private static final int DEFAULT_MAX_RECORD_LENGTH = 100000000;
    private static int MAX_RECORD_LENGTH = 100000000;
    private byte[] _header;
    private byte[] _data;

    public static void setMaxRecordLength(int length) {
        MAX_RECORD_LENGTH = length;
    }

    public static int getMaxRecordLength() {
        return MAX_RECORD_LENGTH;
    }

    protected SoundData() {
        this._header = new byte[8];
        this._data = new byte[0];
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._data.length);
    }

    protected SoundData(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._data = IOUtils.safelyClone(source, start + 8, len - 8, MAX_RECORD_LENGTH);
    }

    public byte[] getData() {
        return this._data;
    }

    @Override
    public long getRecordType() {
        return RecordTypes.SoundData.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._data);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("data", this::getData);
    }
}

