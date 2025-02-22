/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.ClassDef;

public class ClassDefFormat1
extends ClassDef {
    private int startGlyph;
    private int glyphCount;
    private int[] classValues;

    public ClassDefFormat1(RandomAccessFile raf) throws IOException {
        this.startGlyph = raf.readUnsignedShort();
        this.glyphCount = raf.readUnsignedShort();
        this.classValues = new int[this.glyphCount];
        for (int i = 0; i < this.glyphCount; ++i) {
            this.classValues[i] = raf.readUnsignedShort();
        }
    }

    @Override
    public int getFormat() {
        return 1;
    }
}

