/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.Coverage;
import org.apache.batik.svggen.font.table.RangeRecord;

public class CoverageFormat2
extends Coverage {
    private int rangeCount;
    private RangeRecord[] rangeRecords;

    protected CoverageFormat2(RandomAccessFile raf) throws IOException {
        this.rangeCount = raf.readUnsignedShort();
        this.rangeRecords = new RangeRecord[this.rangeCount];
        for (int i = 0; i < this.rangeCount; ++i) {
            this.rangeRecords[i] = new RangeRecord(raf);
        }
    }

    @Override
    public int getFormat() {
        return 2;
    }

    @Override
    public int findGlyph(int glyphId) {
        for (int i = 0; i < this.rangeCount; ++i) {
            int n = this.rangeRecords[i].getCoverageIndex(glyphId);
            if (n <= -1) continue;
            return n;
        }
        return -1;
    }
}

