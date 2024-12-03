/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.LigatureSubstFormat1;
import org.apache.batik.svggen.font.table.LookupSubtable;

public abstract class LigatureSubst
extends LookupSubtable {
    public static LigatureSubst read(RandomAccessFile raf, int offset) throws IOException {
        LigatureSubstFormat1 ls = null;
        raf.seek(offset);
        int format = raf.readUnsignedShort();
        if (format == 1) {
            ls = new LigatureSubstFormat1(raf, offset);
        }
        return ls;
    }
}

