/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.hslf.record.RecordContainer;
import org.apache.poi.hslf.record.RecordTypes;
import org.apache.poi.util.LittleEndian;

public final class DocInfoListContainer
extends RecordContainer {
    private byte[] _header;
    private static final long _type = RecordTypes.List.typeID;

    protected DocInfoListContainer(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
        this.findInterestingChildren();
    }

    private void findInterestingChildren() {
    }

    private DocInfoListContainer() {
        this._header = new byte[8];
        this._children = new Record[0];
        this._header[0] = 15;
        LittleEndian.putShort(this._header, 2, (short)_type);
        this.findInterestingChildren();
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.writeOut(this._header[0], this._header[1], _type, this._children, out);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return null;
    }
}

