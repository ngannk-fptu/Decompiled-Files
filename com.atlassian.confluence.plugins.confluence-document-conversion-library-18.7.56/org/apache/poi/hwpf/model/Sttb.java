/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;

@Internal
public class Sttb {
    private int _cbExtra;
    private final int _cDataLength;
    private String[] _data;
    private byte[][] _extraData;
    private final boolean _fExtend = true;

    public Sttb(byte[] buffer, int startOffset) {
        this(2, buffer, startOffset);
    }

    public Sttb(int cDataLength, byte[] buffer, int startOffset) {
        this._cDataLength = cDataLength;
        this.fillFields(buffer, startOffset);
    }

    public Sttb(int cDataLength, String[] data) {
        this._cDataLength = cDataLength;
        this._data = Arrays.copyOf(data, data.length);
        this._cbExtra = 0;
        this._extraData = null;
    }

    public void fillFields(byte[] buffer, int startOffset) {
        short ffff = LittleEndian.getShort(buffer, startOffset);
        int offset = startOffset + 2;
        if (ffff != -1) {
            LogManager.getLogger(Sttb.class).atWarn().log("Non-extended character Pascal strings are not supported right now. Creating empty values in the RevisionMarkAuthorTable for now.  Please, contact POI developers for update.");
            this._data = new String[0];
            this._extraData = new byte[0][];
            return;
        }
        int cData = this._cDataLength == 2 ? LittleEndian.getUShort(buffer, offset) : LittleEndian.getInt(buffer, offset);
        this._cbExtra = LittleEndian.getUShort(buffer, offset += this._cDataLength);
        offset += 2;
        this._data = new String[cData];
        this._extraData = new byte[cData][];
        for (int i = 0; i < cData; ++i) {
            short cchData = LittleEndian.getShort(buffer, offset);
            offset += 2;
            if (cchData < 0) continue;
            this._data[i] = StringUtil.getFromUnicodeLE(buffer, offset, cchData);
            this._extraData[i] = Arrays.copyOfRange(buffer, offset += cchData * 2, offset + this._cbExtra);
            offset += this._cbExtra;
        }
    }

    public String[] getData() {
        return this._data;
    }

    public int getSize() {
        int size = 2;
        size += this._cDataLength;
        size += 2;
        this.getClass();
        for (String data : this._data) {
            size += 2;
            size += 2 * data.length();
        }
        if (this._extraData != null) {
            size += this._cbExtra * this._data.length;
        }
        return size;
    }

    public byte[] serialize() {
        int offset;
        byte[] buffer = new byte[this.getSize()];
        LittleEndian.putShort(buffer, 0, (short)-1);
        if (this._data == null || this._data.length == 0) {
            if (this._cDataLength == 4) {
                LittleEndian.putInt(buffer, 2, 0);
                LittleEndian.putUShort(buffer, 6, this._cbExtra);
                return buffer;
            }
            LittleEndian.putUShort(buffer, 2, 0);
            LittleEndian.putUShort(buffer, 4, this._cbExtra);
            return buffer;
        }
        if (this._cDataLength == 4) {
            LittleEndian.putInt(buffer, 2, this._data.length);
            LittleEndian.putUShort(buffer, 6, this._cbExtra);
            offset = 8;
        } else {
            LittleEndian.putUShort(buffer, 2, this._data.length);
            LittleEndian.putUShort(buffer, 4, this._cbExtra);
            offset = 6;
        }
        for (int i = 0; i < this._data.length; ++i) {
            String entry = this._data[i];
            if (entry == null) {
                buffer[offset] = -1;
                buffer[offset + 1] = 0;
                offset += 2;
                continue;
            }
            LittleEndian.putUShort(buffer, offset, entry.length());
            StringUtil.putUnicodeLE(entry, buffer, offset += 2);
            offset += 2 * entry.length();
            if (this._cbExtra == 0) continue;
            if (this._extraData[i] != null && this._extraData[i].length != 0) {
                System.arraycopy(this._extraData[i], 0, buffer, offset, Math.min(this._extraData[i].length, this._cbExtra));
            }
            offset += this._cbExtra;
        }
        return buffer;
    }

    public int serialize(byte[] buffer, int offset) {
        byte[] bs = this.serialize();
        System.arraycopy(bs, 0, buffer, offset, bs.length);
        return bs.length;
    }
}

