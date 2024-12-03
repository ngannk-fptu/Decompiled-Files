/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.Ligature;

public class LigatureSet {
    private int ligatureCount;
    private int[] ligatureOffsets;
    private Ligature[] ligatures;

    public LigatureSet(RandomAccessFile raf, int offset) throws IOException {
        int i;
        raf.seek(offset);
        this.ligatureCount = raf.readUnsignedShort();
        this.ligatureOffsets = new int[this.ligatureCount];
        this.ligatures = new Ligature[this.ligatureCount];
        for (i = 0; i < this.ligatureCount; ++i) {
            this.ligatureOffsets[i] = raf.readUnsignedShort();
        }
        for (i = 0; i < this.ligatureCount; ++i) {
            raf.seek(offset + this.ligatureOffsets[i]);
            this.ligatures[i] = new Ligature(raf);
        }
    }
}

