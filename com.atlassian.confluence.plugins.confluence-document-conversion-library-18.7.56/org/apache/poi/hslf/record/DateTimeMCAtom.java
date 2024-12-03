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

public class DateTimeMCAtom
extends RecordAtom {
    private final byte[] _header;
    private int position;
    private int index;
    private final byte[] unused = new byte[3];

    protected DateTimeMCAtom() {
        this._header = new byte[8];
        this.position = 0;
        this.index = 0;
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, 8);
    }

    protected DateTimeMCAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this.position = LittleEndian.getInt(source, start + 8);
        this.index = LittleEndian.getUByte(source, start + 12);
        System.arraycopy(source, start + 13, this.unused, 0, 3);
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        LittleEndian.putInt(this.position, out);
        out.write(this.index);
        out.write(this.unused);
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public long getRecordType() {
        return RecordTypes.DateTimeMCAtom.typeID;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("position", this::getPosition, "index", this::getIndex);
    }
}

