/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RangeRecord {
    private int start;
    private int end;
    private int startCoverageIndex;

    public RangeRecord(RandomAccessFile raf) throws IOException {
        this.start = raf.readUnsignedShort();
        this.end = raf.readUnsignedShort();
        this.startCoverageIndex = raf.readUnsignedShort();
    }

    public boolean isInRange(int glyphId) {
        return this.start <= glyphId && glyphId <= this.end;
    }

    public int getCoverageIndex(int glyphId) {
        if (this.isInRange(glyphId)) {
            return this.startCoverageIndex + glyphId - this.start;
        }
        return -1;
    }
}

