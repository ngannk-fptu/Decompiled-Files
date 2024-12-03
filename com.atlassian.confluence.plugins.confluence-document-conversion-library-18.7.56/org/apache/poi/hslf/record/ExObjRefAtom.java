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

public final class ExObjRefAtom
extends RecordAtom {
    private byte[] _header;
    private int exObjIdRef;

    public ExObjRefAtom() {
        this._header = new byte[8];
        LittleEndian.putUShort(this._header, 0, 0);
        LittleEndian.putUShort(this._header, 2, (int)this.getRecordType());
        LittleEndian.putInt(this._header, 4, 4);
        this.exObjIdRef = 0;
    }

    protected ExObjRefAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this.exObjIdRef = (int)LittleEndian.getUInt(source, start + 8);
    }

    @Override
    public long getRecordType() {
        return RecordTypes.ExObjRefAtom.typeID;
    }

    public int getExObjIdRef() {
        return this.exObjIdRef;
    }

    public void setExObjIdRef(int id) {
        this.exObjIdRef = id;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        byte[] recdata = new byte[4];
        LittleEndian.putUInt(recdata, 0, this.exObjIdRef);
        out.write(recdata);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("exObjIdRef", this::getExObjIdRef);
    }
}

