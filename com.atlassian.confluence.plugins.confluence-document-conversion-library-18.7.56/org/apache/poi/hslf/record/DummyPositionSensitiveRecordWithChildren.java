/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.record;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.hslf.record.PositionDependentRecordContainer;
import org.apache.poi.hslf.record.Record;
import org.apache.poi.util.LittleEndian;

public final class DummyPositionSensitiveRecordWithChildren
extends PositionDependentRecordContainer {
    private byte[] _header;
    private long _type;

    protected DummyPositionSensitiveRecordWithChildren(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._type = LittleEndian.getUShort(this._header, 2);
        this._children = Record.findChildRecords(source, start + 8, len - 8);
    }

    @Override
    public long getRecordType() {
        return this._type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        this.writeOut(this._header[0], this._header[1], this._type, this._children, out);
    }
}

