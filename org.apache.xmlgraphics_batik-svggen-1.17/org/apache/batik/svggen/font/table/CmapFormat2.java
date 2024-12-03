/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.CmapFormat;

public class CmapFormat2
extends CmapFormat {
    private short[] subHeaderKeys = new short[256];
    private int[] subHeaders1;
    private int[] subHeaders2;
    private short[] glyphIndexArray;

    protected CmapFormat2(RandomAccessFile raf) throws IOException {
        super(raf);
        this.format = 2;
    }

    @Override
    public int getFirst() {
        return 0;
    }

    @Override
    public int getLast() {
        return 0;
    }

    @Override
    public int mapCharCode(int charCode) {
        return 0;
    }
}

