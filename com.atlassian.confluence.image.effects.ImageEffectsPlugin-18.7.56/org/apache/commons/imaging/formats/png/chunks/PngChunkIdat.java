/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.chunks;

import org.apache.commons.imaging.formats.png.chunks.PngChunk;

public class PngChunkIdat
extends PngChunk {
    public PngChunkIdat(int length, int chunkType, int crc, byte[] bytes) {
        super(length, chunkType, crc, bytes);
    }
}

