/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.KernSubtableFormat0;
import org.apache.batik.svggen.font.table.KernSubtableFormat2;
import org.apache.batik.svggen.font.table.KerningPair;

public abstract class KernSubtable {
    protected KernSubtable() {
    }

    public abstract int getKerningPairCount();

    public abstract KerningPair getKerningPair(int var1);

    public static KernSubtable read(RandomAccessFile raf) throws IOException {
        KernSubtable table = null;
        raf.readUnsignedShort();
        raf.readUnsignedShort();
        int coverage = raf.readUnsignedShort();
        int format = coverage >> 8;
        switch (format) {
            case 0: {
                table = new KernSubtableFormat0(raf);
                break;
            }
            case 2: {
                table = new KernSubtableFormat2(raf);
                break;
            }
        }
        return table;
    }
}

