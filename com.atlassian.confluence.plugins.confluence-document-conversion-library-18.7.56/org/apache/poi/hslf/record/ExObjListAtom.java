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

public class ExObjListAtom
extends RecordAtom {
    private byte[] _header;
    private byte[] _data;

    protected ExObjListAtom() {
        this._header = new byte[8];
        this._data = new byte[4];
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._data.length);
    }

    protected ExObjListAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._data = IOUtils.safelyClone(source, start + 8, len - 8, ExObjListAtom.getMaxRecordLength());
        if (this._data.length < 4) {
            throw new IllegalArgumentException("The length of the data for a ExObjListAtom must be at least 4 bytes, but was only " + this._data.length);
        }
    }

    public long getObjectIDSeed() {
        return LittleEndian.getUInt(this._data, 0);
    }

    public void setObjectIDSeed(int seed) {
        LittleEndian.putInt(this._data, 0, seed);
    }

    @Override
    public long getRecordType() {
        return RecordTypes.ExObjListAtom.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._data);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("objectIDSeed", this::getObjectIDSeed);
    }
}

