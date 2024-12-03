/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.Coverage;
import org.apache.batik.svggen.font.table.SingleSubst;

public class SingleSubstFormat2
extends SingleSubst {
    private int coverageOffset;
    private int glyphCount;
    private int[] substitutes;
    private Coverage coverage;

    protected SingleSubstFormat2(RandomAccessFile raf, int offset) throws IOException {
        this.coverageOffset = raf.readUnsignedShort();
        this.glyphCount = raf.readUnsignedShort();
        this.substitutes = new int[this.glyphCount];
        for (int i = 0; i < this.glyphCount; ++i) {
            this.substitutes[i] = raf.readUnsignedShort();
        }
        raf.seek(offset + this.coverageOffset);
        this.coverage = Coverage.read(raf);
    }

    @Override
    public int getFormat() {
        return 2;
    }

    @Override
    public int substitute(int glyphId) {
        int i = this.coverage.findGlyph(glyphId);
        if (i > -1) {
            return this.substitutes[i];
        }
        return glyphId;
    }
}

