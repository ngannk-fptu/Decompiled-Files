/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Arrays;
import org.apache.poi.hwpf.model.LSTF;
import org.apache.poi.hwpf.model.ListLevel;
import org.apache.poi.util.Internal;

@Internal
public final class ListData {
    private ListLevel[] _levels;
    private LSTF _lstf;

    ListData(byte[] buf, int offset) {
        this._lstf = new LSTF(buf, offset);
        this._levels = this._lstf.isFSimpleList() ? new ListLevel[1] : new ListLevel[9];
    }

    public ListData(int listID, boolean numbered) {
        this._lstf = new LSTF();
        this._lstf.setLsid(listID);
        this._lstf.setRgistdPara(new short[9]);
        Arrays.fill(this._lstf.getRgistdPara(), (short)4095);
        this._levels = new ListLevel[9];
        for (int x = 0; x < this._levels.length; ++x) {
            this._levels[x] = new ListLevel(x, numbered);
        }
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
        ListData other = (ListData)obj;
        if (!Arrays.equals(this._levels, other._levels)) {
            return false;
        }
        return !(this._lstf == null ? other._lstf != null : !this._lstf.equals(other._lstf));
    }

    public ListLevel getLevel(int index) {
        return this._levels[index - 1];
    }

    public ListLevel[] getLevels() {
        return this._levels;
    }

    public int getLevelStyle(int index) {
        return this._lstf.getRgistdPara()[index];
    }

    public int getLsid() {
        return this._lstf.getLsid();
    }

    public int hashCode() {
        return Arrays.deepHashCode(new Object[]{this._levels, this._lstf});
    }

    public int numLevels() {
        return this._levels.length;
    }

    int resetListID() {
        this._lstf.setLsid((int)(Math.random() * (double)System.currentTimeMillis()));
        return this._lstf.getLsid();
    }

    public void setLevel(int index, ListLevel level) {
        this._levels[index] = level;
    }

    public void setLevelStyle(int index, int styleIndex) {
        this._lstf.getRgistdPara()[index] = (short)styleIndex;
    }

    public byte[] toByteArray() {
        return this._lstf.serialize();
    }
}

