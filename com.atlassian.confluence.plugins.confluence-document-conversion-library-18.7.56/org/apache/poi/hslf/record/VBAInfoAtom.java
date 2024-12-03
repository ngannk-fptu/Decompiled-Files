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

public final class VBAInfoAtom
extends RecordAtom {
    private static final long _type = RecordTypes.VBAInfoAtom.typeID;
    private byte[] _header;
    private long persistIdRef;
    private boolean hasMacros;
    private long version;

    private VBAInfoAtom() {
        this._header = new byte[8];
        LittleEndian.putUInt(this._header, 0, _type);
        this.persistIdRef = 0L;
        this.hasMacros = true;
        this.version = 2L;
    }

    public VBAInfoAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this.persistIdRef = LittleEndian.getUInt(source, start + 8);
        this.hasMacros = LittleEndian.getUInt(source, start + 12) == 1L;
        this.version = LittleEndian.getUInt(source, start + 16);
    }

    @Override
    public long getRecordType() {
        return _type;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        LittleEndian.putUInt(this.persistIdRef, out);
        LittleEndian.putUInt(this.hasMacros ? 1L : 0L, out);
        LittleEndian.putUInt(this.version, out);
    }

    public long getPersistIdRef() {
        return this.persistIdRef;
    }

    public void setPersistIdRef(long persistIdRef) {
        this.persistIdRef = persistIdRef;
    }

    public boolean isHasMacros() {
        return this.hasMacros;
    }

    public void setHasMacros(boolean hasMacros) {
        this.hasMacros = hasMacros;
    }

    public long getVersion() {
        return this.version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("persistIdRef", this::getPersistIdRef, "hasMacros", this::isHasMacros, "version", this::getVersion);
    }
}

