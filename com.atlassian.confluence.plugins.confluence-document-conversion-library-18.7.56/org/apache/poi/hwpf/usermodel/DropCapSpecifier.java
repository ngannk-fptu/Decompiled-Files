/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.LittleEndian;

public final class DropCapSpecifier
implements Duplicatable {
    private static final BitField _lines = BitFieldFactory.getInstance(248);
    private static final BitField _type = BitFieldFactory.getInstance(7);
    private short _fdct;

    public DropCapSpecifier() {
        this._fdct = 0;
    }

    public DropCapSpecifier(DropCapSpecifier other) {
        this._fdct = other._fdct;
    }

    public DropCapSpecifier(byte[] buf, int offset) {
        this(LittleEndian.getShort(buf, offset));
    }

    public DropCapSpecifier(short fdct) {
        this._fdct = fdct;
    }

    @Override
    public DropCapSpecifier copy() {
        return new DropCapSpecifier(this);
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
        DropCapSpecifier other = (DropCapSpecifier)obj;
        return this._fdct == other._fdct;
    }

    public byte getCountOfLinesToDrop() {
        return (byte)_lines.getValue(this._fdct);
    }

    public byte getDropCapType() {
        return (byte)_type.getValue(this._fdct);
    }

    public int hashCode() {
        return this._fdct;
    }

    public boolean isEmpty() {
        return this._fdct == 0;
    }

    public void setCountOfLinesToDrop(byte value) {
        this._fdct = (short)_lines.setValue(this._fdct, value);
    }

    public void setDropCapType(byte value) {
        this._fdct = (short)_type.setValue(this._fdct, value);
    }

    public short toShort() {
        return this._fdct;
    }

    public String toString() {
        if (this.isEmpty()) {
            return "[DCS] EMPTY";
        }
        return "[DCS] (type: " + this.getDropCapType() + "; count: " + this.getCountOfLinesToDrop() + ")";
    }
}

