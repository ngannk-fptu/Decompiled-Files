/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.poi.hwpf.model.ListFormatOverrideLevel;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public class LFOData {
    private int _cp;
    private ListFormatOverrideLevel[] _rgLfoLvl;

    public LFOData() {
        this._cp = 0;
        this._rgLfoLvl = new ListFormatOverrideLevel[0];
    }

    LFOData(byte[] buf, int startOffset, int cLfolvl) {
        if (cLfolvl < 0) {
            throw new IllegalArgumentException("Cannot create LFOData with negative count");
        }
        int offset = startOffset;
        this._cp = LittleEndian.getInt(buf, offset);
        offset += 4;
        this._rgLfoLvl = new ListFormatOverrideLevel[cLfolvl];
        for (int x = 0; x < cLfolvl; ++x) {
            this._rgLfoLvl[x] = new ListFormatOverrideLevel(buf, offset);
            offset += this._rgLfoLvl[x].getSizeInBytes();
        }
    }

    public int getCp() {
        return this._cp;
    }

    public ListFormatOverrideLevel[] getRgLfoLvl() {
        return this._rgLfoLvl;
    }

    public int getSizeInBytes() {
        int result = 0;
        result += 4;
        for (ListFormatOverrideLevel lfolvl : this._rgLfoLvl) {
            result += lfolvl.getSizeInBytes();
        }
        return result;
    }

    void writeTo(ByteArrayOutputStream tableStream) throws IOException {
        LittleEndian.putInt(this._cp, tableStream);
        for (ListFormatOverrideLevel lfolvl : this._rgLfoLvl) {
            tableStream.write(lfolvl.toByteArray());
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        LFOData lfoData = (LFOData)o;
        if (this._cp != lfoData._cp) {
            return false;
        }
        return Arrays.equals(this._rgLfoLvl, lfoData._rgLfoLvl);
    }

    public int hashCode() {
        return Arrays.deepHashCode(this._rgLfoLvl);
    }
}

