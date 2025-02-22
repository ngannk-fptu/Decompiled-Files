/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen.font.table;

import java.io.IOException;
import java.io.RandomAccessFile;

public class KerningPair {
    private int left;
    private int right;
    private short value;

    protected KerningPair(RandomAccessFile raf) throws IOException {
        this.left = raf.readUnsignedShort();
        this.right = raf.readUnsignedShort();
        this.value = raf.readShort();
    }

    public int getLeft() {
        return this.left;
    }

    public int getRight() {
        return this.right;
    }

    public short getValue() {
        return this.value;
    }
}

