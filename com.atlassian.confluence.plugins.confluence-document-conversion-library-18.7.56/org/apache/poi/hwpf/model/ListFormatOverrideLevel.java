/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Objects;
import org.apache.poi.hwpf.model.LFOLVLBase;
import org.apache.poi.hwpf.model.ListLevel;
import org.apache.poi.hwpf.model.types.LFOLVLBaseAbstractType;
import org.apache.poi.util.Internal;

@Internal
public final class ListFormatOverrideLevel {
    private LFOLVLBase _base;
    private ListLevel _lvl;

    public ListFormatOverrideLevel(byte[] buf, int offset) {
        this._base = new LFOLVLBase(buf, offset);
        offset += LFOLVLBaseAbstractType.getSize();
        if (this._base.isFFormatting()) {
            this._lvl = new ListLevel(buf, offset);
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ListFormatOverrideLevel)) {
            return false;
        }
        ListFormatOverrideLevel lfolvl = (ListFormatOverrideLevel)obj;
        boolean lvlEquality = false;
        lvlEquality = this._lvl != null ? this._lvl.equals(lfolvl._lvl) : lfolvl._lvl == null;
        return lvlEquality && lfolvl._base.equals(this._base);
    }

    public int getIStartAt() {
        return this._base.getIStartAt();
    }

    public ListLevel getLevel() {
        return this._lvl;
    }

    public int getLevelNum() {
        return this._base.getILvl();
    }

    public int getSizeInBytes() {
        return this._lvl == null ? LFOLVLBaseAbstractType.getSize() : LFOLVLBaseAbstractType.getSize() + this._lvl.getSizeInBytes();
    }

    public int hashCode() {
        return Objects.hash(this._base, this._lvl);
    }

    public boolean isFormatting() {
        return this._base.isFFormatting();
    }

    public boolean isStartAt() {
        return this._base.isFStartAt();
    }

    public byte[] toByteArray() {
        int offset = 0;
        byte[] buf = new byte[this.getSizeInBytes()];
        this._base.serialize(buf, offset);
        offset += LFOLVLBaseAbstractType.getSize();
        if (this._lvl != null) {
            byte[] levelBuf = this._lvl.toByteArray();
            System.arraycopy(levelBuf, 0, buf, offset, levelBuf.length);
        }
        return buf;
    }
}

