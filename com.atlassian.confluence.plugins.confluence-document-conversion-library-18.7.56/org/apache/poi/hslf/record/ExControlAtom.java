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
import org.apache.poi.util.LittleEndian;

public final class ExControlAtom
extends RecordAtom {
    private byte[] _header;
    private int _id;

    protected ExControlAtom() {
        this._header = new byte[8];
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, 4);
    }

    protected ExControlAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._id = LittleEndian.getInt(source, start + 8);
    }

    public int getSlideId() {
        return this._id;
    }

    public void setSlideId(int id) {
        this._id = id;
    }

    @Override
    public long getRecordType() {
        return RecordTypes.ExControlAtom.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        byte[] data = new byte[4];
        LittleEndian.putInt(data, 0, this._id);
        out.write(data);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("slideId", this::getSlideId);
    }
}

