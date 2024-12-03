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

public final class RoundTripHFPlaceholder12
extends RecordAtom {
    private byte[] _header;
    private byte _placeholderId;

    public RoundTripHFPlaceholder12() {
        this._header = new byte[8];
        LittleEndian.putUShort(this._header, 0, 0);
        LittleEndian.putUShort(this._header, 2, (int)this.getRecordType());
        LittleEndian.putInt(this._header, 4, 8);
        this._placeholderId = 0;
    }

    protected RoundTripHFPlaceholder12(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._placeholderId = source[start + 8];
    }

    public int getPlaceholderId() {
        return this._placeholderId;
    }

    public void setPlaceholderId(int number) {
        this._placeholderId = (byte)number;
    }

    @Override
    public long getRecordType() {
        return RecordTypes.RoundTripHFPlaceholder12.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._placeholderId);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("placeholderId", this::getPlaceholderId);
    }
}

