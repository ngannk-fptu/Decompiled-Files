/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.LittleEndian;

public final class LineSpacingDescriptor
implements Duplicatable {
    short _dyaLine;
    short _fMultiLinespace;

    public LineSpacingDescriptor() {
        this._dyaLine = (short)240;
        this._fMultiLinespace = 1;
    }

    public LineSpacingDescriptor(LineSpacingDescriptor other) {
        this._dyaLine = other._dyaLine;
        this._fMultiLinespace = other._fMultiLinespace;
    }

    public LineSpacingDescriptor(byte[] buf, int offset) {
        this._dyaLine = LittleEndian.getShort(buf, offset);
        this._fMultiLinespace = LittleEndian.getShort(buf, offset + 2);
    }

    @Override
    public LineSpacingDescriptor copy() {
        return new LineSpacingDescriptor(this);
    }

    public void setMultiLinespace(short fMultiLinespace) {
        this._fMultiLinespace = fMultiLinespace;
    }

    public int toInt() {
        byte[] intHolder = new byte[4];
        this.serialize(intHolder, 0);
        return LittleEndian.getInt(intHolder);
    }

    public void serialize(byte[] buf, int offset) {
        LittleEndian.putShort(buf, offset, this._dyaLine);
        LittleEndian.putShort(buf, offset + 2, this._fMultiLinespace);
    }

    public void setDyaLine(short dyaLine) {
        this._dyaLine = dyaLine;
    }

    public boolean equals(Object o) {
        if (!(o instanceof LineSpacingDescriptor)) {
            return false;
        }
        LineSpacingDescriptor lspd = (LineSpacingDescriptor)o;
        return this._dyaLine == lspd._dyaLine && this._fMultiLinespace == lspd._fMultiLinespace;
    }

    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    public boolean isEmpty() {
        return this._dyaLine == 0 && this._fMultiLinespace == 0;
    }

    public String toString() {
        if (this.isEmpty()) {
            return "[LSPD] EMPTY";
        }
        return "[LSPD] (dyaLine: " + this._dyaLine + "; fMultLinespace: " + this._fMultiLinespace + ")";
    }
}

