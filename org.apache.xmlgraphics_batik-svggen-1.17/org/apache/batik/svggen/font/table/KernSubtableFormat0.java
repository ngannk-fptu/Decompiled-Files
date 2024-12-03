/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.KernSubtable;
import org.apache.batik.svggen.font.table.KerningPair;

public class KernSubtableFormat0
extends KernSubtable {
    private int nPairs;
    private int searchRange;
    private int entrySelector;
    private int rangeShift;
    private KerningPair[] kerningPairs;

    protected KernSubtableFormat0(RandomAccessFile raf) throws IOException {
        this.nPairs = raf.readUnsignedShort();
        this.searchRange = raf.readUnsignedShort();
        this.entrySelector = raf.readUnsignedShort();
        this.rangeShift = raf.readUnsignedShort();
        this.kerningPairs = new KerningPair[this.nPairs];
        for (int i = 0; i < this.nPairs; ++i) {
            this.kerningPairs[i] = new KerningPair(raf);
        }
    }

    @Override
    public int getKerningPairCount() {
        return this.nPairs;
    }

    @Override
    public KerningPair getKerningPair(int i) {
        return this.kerningPairs[i];
    }
}

