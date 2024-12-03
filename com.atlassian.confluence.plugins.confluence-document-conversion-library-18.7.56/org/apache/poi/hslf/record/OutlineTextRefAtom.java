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

public final class OutlineTextRefAtom
extends RecordAtom {
    private byte[] _header;
    private int _index;

    protected OutlineTextRefAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._index = LittleEndian.getInt(source, start + 8);
    }

    protected OutlineTextRefAtom() {
        this._index = 0;
        this._header = new byte[8];
        LittleEndian.putUShort(this._header, 0, 0);
        LittleEndian.putUShort(this._header, 2, (int)this.getRecordType());
        LittleEndian.putInt(this._header, 4, 4);
    }

    @Override
    public long getRecordType() {
        return RecordTypes.OutlineTextRefAtom.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        byte[] recdata = new byte[4];
        LittleEndian.putInt(recdata, 0, this._index);
        out.write(recdata);
    }

    public void setTextIndex(int idx) {
        this._index = idx;
    }

    public int getTextIndex() {
        return this._index;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("textIndex", this::getTextIndex);
    }
}

