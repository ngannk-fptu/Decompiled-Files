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
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public final class ExMediaAtom
extends RecordAtom {
    public static final int fLoop = 1;
    public static final int fRewind = 2;
    public static final int fNarration = 4;
    private static final int[] FLAG_MASKS = new int[]{1, 2, 4};
    private static final String[] FLAG_NAMES = new String[]{"LOOP", "REWIND", "NARRATION"};
    private byte[] _header;
    private byte[] _recdata;

    protected ExMediaAtom() {
        this._recdata = new byte[8];
        this._header = new byte[8];
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._recdata.length);
    }

    protected ExMediaAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._recdata = IOUtils.safelyClone(source, start + 8, len - 8, ExMediaAtom.getMaxRecordLength());
    }

    @Override
    public long getRecordType() {
        return RecordTypes.ExMediaAtom.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._recdata);
    }

    public int getObjectId() {
        return LittleEndian.getInt(this._recdata, 0);
    }

    public void setObjectId(int id) {
        LittleEndian.putInt(this._recdata, 0, id);
    }

    public int getMask() {
        return LittleEndian.getInt(this._recdata, 4);
    }

    public void setMask(int mask) {
        LittleEndian.putInt(this._recdata, 4, mask);
    }

    public boolean getFlag(int bit) {
        return (this.getMask() & bit) != 0;
    }

    public void setFlag(int bit, boolean value) {
        int mask = this.getMask();
        mask = value ? (mask |= bit) : (mask &= ~bit);
        this.setMask(mask);
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("objectId", this::getObjectId, "flags", GenericRecordUtil.getBitsAsString(this::getMask, FLAG_MASKS, FLAG_NAMES));
    }
}

