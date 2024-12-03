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

public final class OEPlaceholderAtom
extends RecordAtom {
    public static final int PLACEHOLDER_FULLSIZE = 0;
    public static final int PLACEHOLDER_HALFSIZE = 1;
    public static final int PLACEHOLDER_QUARTSIZE = 2;
    private final byte[] _header;
    private int placementId;
    private int placeholderId;
    private int placeholderSize;
    private short unusedShort;

    public OEPlaceholderAtom() {
        this._header = new byte[8];
        LittleEndian.putUShort(this._header, 0, 0);
        LittleEndian.putUShort(this._header, 2, (int)this.getRecordType());
        LittleEndian.putInt(this._header, 4, 8);
        this.placementId = 0;
        this.placeholderId = 0;
        this.placeholderSize = 0;
    }

    OEPlaceholderAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        int offset = start + 8;
        this.placementId = LittleEndian.getInt(source, offset);
        this.placeholderId = LittleEndian.getUByte(source, offset += 4);
        this.placeholderSize = LittleEndian.getUByte(source, ++offset);
        this.unusedShort = LittleEndian.getShort(source, ++offset);
    }

    @Override
    public long getRecordType() {
        return RecordTypes.OEPlaceholderAtom.typeID;
    }

    public int getPlacementId() {
        return this.placementId;
    }

    public void setPlacementId(int id) {
        this.placementId = id;
    }

    public int getPlaceholderId() {
        return this.placeholderId;
    }

    public void setPlaceholderId(byte id) {
        this.placeholderId = id;
    }

    public int getPlaceholderSize() {
        return this.placeholderSize;
    }

    public void setPlaceholderSize(byte size) {
        this.placeholderSize = size;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        byte[] recdata = new byte[8];
        LittleEndian.putInt(recdata, 0, this.placementId);
        recdata[4] = (byte)this.placeholderId;
        recdata[5] = (byte)this.placeholderSize;
        LittleEndian.putShort(recdata, 6, this.unusedShort);
        out.write(recdata);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("placementId", this::getPlacementId, "placeholderId", this::getPlaceholderId, "placeholderSize", this::getPlaceholderSize);
    }
}

