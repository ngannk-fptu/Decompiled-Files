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

public final class TxInteractiveInfoAtom
extends RecordAtom {
    private byte[] _header;
    private byte[] _data;

    public TxInteractiveInfoAtom() {
        this._header = new byte[8];
        this._data = new byte[8];
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._data.length);
    }

    protected TxInteractiveInfoAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._data = IOUtils.safelyClone(source, start + 8, len - 8, TxInteractiveInfoAtom.getMaxRecordLength());
    }

    public int getStartIndex() {
        return LittleEndian.getInt(this._data, 0);
    }

    public void setStartIndex(int idx) {
        LittleEndian.putInt(this._data, 0, idx);
    }

    public int getEndIndex() {
        return LittleEndian.getInt(this._data, 4);
    }

    public void setEndIndex(int idx) {
        LittleEndian.putInt(this._data, 4, idx);
    }

    @Override
    public long getRecordType() {
        return RecordTypes.TxInteractiveInfoAtom.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._data);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("startIndex", this::getStartIndex, "endIndex", this::getEndIndex);
    }
}

