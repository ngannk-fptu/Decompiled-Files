/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.Coverage;
import org.apache.batik.svggen.font.table.SingleSubst;

public class SingleSubstFormat1
extends SingleSubst {
    private int coverageOffset;
    private short deltaGlyphID;
    private Coverage coverage;

    protected SingleSubstFormat1(RandomAccessFile raf, int offset) throws IOException {
        this.coverageOffset = raf.readUnsignedShort();
        this.deltaGlyphID = raf.readShort();
        raf.seek(offset + this.coverageOffset);
        this.coverage = Coverage.read(raf);
    }

    @Override
    public int getFormat() {
        return 1;
    }

    @Override
    public int substitute(int glyphId) {
        int i = this.coverage.findGlyph(glyphId);
        if (i > -1) {
            return glyphId + this.deltaGlyphID;
        }
        return glyphId;
    }
}

