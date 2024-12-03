/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hdgf.chunks;

import java.util.Arrays;

public final class ChunkTrailer {
    private final byte[] trailerData;

    public ChunkTrailer(byte[] data, int offset) {
        this.trailerData = Arrays.copyOfRange(data, offset, offset + 8);
    }

    public String toString() {
        return "<ChunkTrailer of length " + this.trailerData.length + ">";
    }

    byte[] getTrailerData() {
        return this.trailerData;
    }
}

