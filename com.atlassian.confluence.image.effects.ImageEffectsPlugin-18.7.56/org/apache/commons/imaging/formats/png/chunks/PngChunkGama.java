/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.chunks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.png.chunks.PngChunk;

public class PngChunkGama
extends PngChunk {
    public final int gamma;

    public PngChunkGama(int length, int chunkType, int crc, byte[] bytes) throws IOException {
        super(length, chunkType, crc, bytes);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        this.gamma = BinaryFunctions.read4Bytes("Gamma", is, "Not a Valid Png File: gAMA Corrupt", this.getByteOrder());
    }

    public double getGamma() {
        return 1.0 / ((double)this.gamma / 100000.0);
    }
}

