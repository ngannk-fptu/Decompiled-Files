/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hwpf.model.OldFfn;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class OldFontTable {
    private static final Logger LOG = LogManager.getLogger(OldFontTable.class);
    private final OldFfn[] _fontNames;

    public OldFontTable(byte[] buf, int offset, int length) {
        OldFfn oldFfn;
        ArrayList<OldFfn> ffns = new ArrayList<OldFfn>();
        short fontTableLength = LittleEndian.getShort(buf, offset);
        int endOfTableOffset = offset + length;
        int startOffset = offset + 2;
        while ((oldFfn = OldFfn.build(buf, startOffset, endOfTableOffset)) != null) {
            ffns.add(oldFfn);
            startOffset += oldFfn.getLength();
        }
        this._fontNames = ffns.toArray(new OldFfn[0]);
    }

    public OldFfn[] getFontNames() {
        return this._fontNames;
    }

    public String getMainFont(int chpFtc) {
        if (chpFtc >= this._fontNames.length) {
            LOG.atInfo().log("Mismatch in chpFtc with stringCount");
            return null;
        }
        return this._fontNames[chpFtc].getMainFontName();
    }

    public String toString() {
        return "OldFontTable{_fontNames=" + Arrays.toString(this._fontNames) + '}';
    }
}

