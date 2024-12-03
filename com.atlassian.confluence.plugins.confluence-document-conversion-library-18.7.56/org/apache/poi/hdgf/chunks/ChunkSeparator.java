/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.chunks;

import java.util.Arrays;

public final class ChunkSeparator {
    final byte[] separatorData;

    public ChunkSeparator(byte[] data, int offset) {
        this.separatorData = Arrays.copyOfRange(data, offset, offset + 4);
    }

    public String toString() {
        return "<ChunkSeparator of length " + this.separatorData.length + ">";
    }
}

