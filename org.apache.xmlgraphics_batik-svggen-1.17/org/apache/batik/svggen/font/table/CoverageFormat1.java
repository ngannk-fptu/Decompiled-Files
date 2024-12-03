/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.Coverage;

public class CoverageFormat1
extends Coverage {
    private int glyphCount;
    private int[] glyphIds;

    protected CoverageFormat1(RandomAccessFile raf) throws IOException {
        this.glyphCount = raf.readUnsignedShort();
        this.glyphIds = new int[this.glyphCount];
        for (int i = 0; i < this.glyphCount; ++i) {
            this.glyphIds[i] = raf.readUnsignedShort();
        }
    }

    @Override
    public int getFormat() {
        return 1;
    }

    @Override
    public int findGlyph(int glyphId) {
        for (int i = 0; i < this.glyphCount; ++i) {
            if (this.glyphIds[i] != glyphId) continue;
            return i;
        }
        return -1;
    }
}

