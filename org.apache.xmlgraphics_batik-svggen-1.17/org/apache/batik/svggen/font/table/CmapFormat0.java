/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.CmapFormat;

public class CmapFormat0
extends CmapFormat {
    private int[] glyphIdArray = new int[256];
    private int first;
    private int last;

    protected CmapFormat0(RandomAccessFile raf) throws IOException {
        super(raf);
        this.format = 0;
        this.first = -1;
        for (int i = 0; i < 256; ++i) {
            this.glyphIdArray[i] = raf.readUnsignedByte();
            if (this.glyphIdArray[i] <= 0) continue;
            if (this.first == -1) {
                this.first = i;
            }
            this.last = i;
        }
    }

    @Override
    public int getFirst() {
        return this.first;
    }

    @Override
    public int getLast() {
        return this.last;
    }

    @Override
    public int mapCharCode(int charCode) {
        if (0 <= charCode && charCode < 256) {
            return this.glyphIdArray[charCode];
        }
        return 0;
    }
}

