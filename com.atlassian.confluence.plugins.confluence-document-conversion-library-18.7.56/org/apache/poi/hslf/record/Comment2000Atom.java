/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.record.RecordAtom;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.hslf.util.SystemTimeUtils;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class Comment2000Atom
extends RecordAtom {
    private byte[] _header;
    private byte[] _data;

    protected Comment2000Atom() {
        this._header = new byte[8];
        this._data = new byte[28];
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._data.length);
    }

    protected Comment2000Atom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._data = IOUtils.safelyClone(source, start + 8, len - 8, Comment2000Atom.getMaxRecordLength());
    }

    public int getNumber() {
        return LittleEndian.getInt(this._data, 0);
    }

    public void setNumber(int number) {
        LittleEndian.putInt(this._data, 0, number);
    }

    public Date getDate() {
        return SystemTimeUtils.getDate(this._data, 4);
    }

    public void setDate(Date date) {
        SystemTimeUtils.storeDate(date, this._data, 4);
    }

    public int getXOffset() {
        return LittleEndian.getInt(this._data, 20);
    }

    public void setXOffset(int xOffset) {
        LittleEndian.putInt(this._data, 20, xOffset);
    }

    public int getYOffset() {
        return LittleEndian.getInt(this._data, 24);
    }

    public void setYOffset(int yOffset) {
        LittleEndian.putInt(this._data, 24, yOffset);
    }

    @Override
    public long getRecordType() {
        return RecordTypes.Comment2000Atom.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._data);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("number", this::getNumber, "date", this::getDate, "xOffset", this::getXOffset, "yOffset", this::getYOffset);
    }
}

