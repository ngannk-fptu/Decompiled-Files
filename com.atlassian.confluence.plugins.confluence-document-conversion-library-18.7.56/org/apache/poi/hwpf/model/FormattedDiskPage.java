/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public abstract class FormattedDiskPage {
    protected byte[] _fkp;
    protected int _crun;
    protected int _offset;

    public FormattedDiskPage() {
    }

    public FormattedDiskPage(byte[] documentStream, int offset) {
        this._crun = LittleEndian.getUByte(documentStream, offset + 511);
        this._fkp = documentStream;
        this._offset = offset;
    }

    protected int getStart(int index) {
        return LittleEndian.getInt(this._fkp, this._offset + index * 4);
    }

    protected int getEnd(int index) {
        return LittleEndian.getInt(this._fkp, this._offset + (index + 1) * 4);
    }

    public int size() {
        return this._crun;
    }

    protected abstract byte[] getGrpprl(int var1);
}

