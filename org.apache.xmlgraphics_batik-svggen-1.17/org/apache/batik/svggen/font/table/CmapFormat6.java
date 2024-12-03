/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.batik.svggen.font.table.CmapFormat;

public class CmapFormat6
extends CmapFormat {
    private short format = (short)6;
    private short length;
    private short version;
    private short firstCode;
    private short entryCount;
    private short[] glyphIdArray;

    protected CmapFormat6(RandomAccessFile raf) throws IOException {
        super(raf);
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

