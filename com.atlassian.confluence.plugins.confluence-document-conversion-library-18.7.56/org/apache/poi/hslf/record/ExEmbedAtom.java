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
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LittleEndian;

public class ExEmbedAtom
extends RecordAtom {
    public static final int DOES_NOT_FOLLOW_COLOR_SCHEME = 0;
    public static final int FOLLOWS_ENTIRE_COLOR_SCHEME = 1;
    public static final int FOLLOWS_TEXT_AND_BACKGROUND_SCHEME = 2;
    private byte[] _header;
    private byte[] _data;

    protected ExEmbedAtom() {
        this._header = new byte[8];
        this._data = new byte[8];
        LittleEndian.putShort(this._header, 2, (short)this.getRecordType());
        LittleEndian.putInt(this._header, 4, this._data.length);
    }

    protected ExEmbedAtom(byte[] source, int start, int len) {
        this._header = Arrays.copyOfRange(source, start, start + 8);
        this._data = IOUtils.safelyClone(source, start + 8, len - 8, ExEmbedAtom.getMaxRecordLength());
        if (this._data.length < 8) {
            throw new IllegalArgumentException("The length of the data for a ExEmbedAtom must be at least 4 bytes, but was only " + this._data.length);
        }
    }

    public int getFollowColorScheme() {
        return LittleEndian.getInt(this._data, 0);
    }

    public boolean getCantLockServerB() {
        return this._data[4] != 0;
    }

    public void setCantLockServerB(boolean cantBeLocked) {
        this._data[4] = (byte)(cantBeLocked ? 1 : 0);
    }

    public boolean getNoSizeToServerB() {
        return this._data[5] != 0;
    }

    public boolean getIsTable() {
        return this._data[6] != 0;
    }

    @Override
    public long getRecordType() {
        return RecordTypes.ExEmbedAtom.typeID;
    }

    @Override
    public void writeOut(OutputStream out) throws IOException {
        out.write(this._header);
        out.write(this._data);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("followColorScheme", this::getFollowColorSchemeString, "cantLockServer", this::getCantLockServerB, "noSizeToServer", this::getNoSizeToServerB, "isTable", this::getIsTable);
    }

    private String getFollowColorSchemeString() {
        switch (this.getFollowColorScheme()) {
            default: {
                return "DOES_NOT_FOLLOW_COLOR_SCHEME";
            }
            case 1: {
                return "FOLLOWS_ENTIRE_COLOR_SCHEME";
            }
            case 2: 
        }
        return "FOLLOWS_TEXT_AND_BACKGROUND_SCHEME";
    }
}

