/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Arrays;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class Ffn {
    private int _cbFfnM1;
    private byte _info;
    private static BitField _prq = BitFieldFactory.getInstance(3);
    private static BitField _fTrueType = BitFieldFactory.getInstance(4);
    private static BitField _ff = BitFieldFactory.getInstance(112);
    private short _wWeight;
    private byte _chs;
    private byte _ixchSzAlt;
    private byte[] _panose = new byte[10];
    private byte[] _fontSig = new byte[24];
    private char[] _xszFfn;
    private int _xszFfnLength;

    public Ffn(byte[] buf, int offset) {
        int offsetTmp = offset;
        this._cbFfnM1 = LittleEndian.getUByte(buf, offset);
        this._info = buf[++offset];
        this._wWeight = LittleEndian.getShort(buf, ++offset);
        this._chs = buf[offset += 2];
        this._ixchSzAlt = buf[++offset];
        System.arraycopy(buf, ++offset, this._panose, 0, this._panose.length);
        System.arraycopy(buf, offset += this._panose.length, this._fontSig, 0, this._fontSig.length);
        offsetTmp = (offset += this._fontSig.length) - offsetTmp;
        this._xszFfnLength = (this.getSize() - offsetTmp) / 2;
        if (this._xszFfnLength < 0) {
            throw new IllegalArgumentException("Had invalid computed size: " + this._xszFfnLength + " with size " + this.getSize() + " and offsetTmp: " + offsetTmp);
        }
        this._xszFfn = new char[this._xszFfnLength];
        for (int i = 0; i < this._xszFfnLength; ++i) {
            this._xszFfn[i] = (char)LittleEndian.getShort(buf, offset);
            offset += 2;
        }
    }

    public int get_cbFfnM1() {
        return this._cbFfnM1;
    }

    public short getWeight() {
        return this._wWeight;
    }

    public byte getChs() {
        return this._chs;
    }

    public byte[] getPanose() {
        return this._panose;
    }

    public byte[] getFontSig() {
        return this._fontSig;
    }

    public int getSize() {
        return this._cbFfnM1 + 1;
    }

    public String getMainFontName() {
        int index;
        for (index = 0; index < this._xszFfnLength && this._xszFfn[index] != '\u0000'; ++index) {
        }
        return new String(this._xszFfn, 0, index);
    }

    public String getAltFontName() {
        int index;
        for (index = this._ixchSzAlt; index < this._xszFfnLength && this._xszFfn[index] != '\u0000'; ++index) {
        }
        return new String(this._xszFfn, (int)this._ixchSzAlt, index);
    }

    public void set_cbFfnM1(int _cbFfnM1) {
        this._cbFfnM1 = _cbFfnM1;
    }

    public byte[] toByteArray() {
        int offset = 0;
        byte[] buf = IOUtils.safelyAllocate(this.getSize(), HWPFDocument.getMaxRecordLength());
        buf[offset] = (byte)this._cbFfnM1;
        buf[++offset] = this._info;
        LittleEndian.putShort(buf, ++offset, this._wWeight);
        buf[offset += 2] = this._chs;
        buf[++offset] = this._ixchSzAlt;
        System.arraycopy(this._panose, 0, buf, ++offset, this._panose.length);
        System.arraycopy(this._fontSig, 0, buf, offset += this._panose.length, this._fontSig.length);
        offset += this._fontSig.length;
        for (char c : this._xszFfn) {
            LittleEndian.putShort(buf, offset, (short)c);
            offset += 2;
        }
        return buf;
    }

    public boolean equals(Object other) {
        if (!(other instanceof Ffn)) {
            return false;
        }
        Ffn o = (Ffn)other;
        return o._cbFfnM1 == this._cbFfnM1 && o._info == this._info && o._wWeight == this._wWeight && o._chs == this._chs && o._ixchSzAlt == this._ixchSzAlt && Arrays.equals(o._panose, this._panose) && Arrays.equals(o._fontSig, this._fontSig) && Arrays.equals(o._xszFfn, this._xszFfn);
    }

    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }
}

