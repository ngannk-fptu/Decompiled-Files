/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FeatureRecord {
    private int tag;
    private int offset;

    public FeatureRecord(RandomAccessFile raf) throws IOException {
        this.tag = raf.readInt();
        this.offset = raf.readUnsignedShort();
    }

    public int getTag() {
        return this.tag;
    }

    public int getOffset() {
        return this.offset;
    }
}

