/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Arrays;
import org.apache.poi.util.LittleEndian;

public class Xst {
    private int _cch;
    private char[] _rgtchar;

    public Xst() {
        this._cch = 0;
        this._rgtchar = new char[0];
    }

    public Xst(byte[] data, int startOffset) {
        int offset = startOffset;
        this._cch = LittleEndian.getUShort(data, offset);
        offset += 2;
        this._rgtchar = new char[this._cch];
        for (int x = 0; x < this._cch; ++x) {
            this._rgtchar[x] = (char)LittleEndian.getShort(data, offset);
            offset += 2;
        }
    }

    public Xst(String str) {
        this._cch = str.length();
        this._rgtchar = str.toCharArray();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        Xst other = (Xst)obj;
        if (this._cch != other._cch) {
            return false;
        }
        return Arrays.equals(this._rgtchar, other._rgtchar);
    }

    public String getAsJavaString() {
        return new String(this._rgtchar);
    }

    public int getCch() {
        return this._cch;
    }

    public char[] getRgtchar() {
        return this._rgtchar;
    }

    public int getSize() {
        return 2 + this._rgtchar.length * 2;
    }

    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{this._cch, this._rgtchar});
    }

    public void serialize(byte[] data, int startOffset) {
        int offset = startOffset;
        LittleEndian.putUShort(data, offset, this._cch);
        offset += 2;
        for (char c : this._rgtchar) {
            LittleEndian.putShort(data, offset, (short)c);
            offset += 2;
        }
    }

    public String toString() {
        return "Xst [" + this._cch + "; " + Arrays.toString(this._rgtchar) + "]";
    }
}

