/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.LookupSubtable;
import org.apache.batik.svggen.font.table.SingleSubstFormat1;
import org.apache.batik.svggen.font.table.SingleSubstFormat2;

public abstract class SingleSubst
extends LookupSubtable {
    public abstract int getFormat();

    public abstract int substitute(int var1);

    public static SingleSubst read(RandomAccessFile raf, int offset) throws IOException {
        SingleSubst s = null;
        raf.seek(offset);
        int format = raf.readUnsignedShort();
        if (format == 1) {
            s = new SingleSubstFormat1(raf, offset);
        } else if (format == 2) {
            s = new SingleSubstFormat2(raf, offset);
        }
        return s;
    }
}

