/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.chunks;

import org.apache.commons.imaging.formats.png.PngText;
import org.apache.commons.imaging.formats.png.chunks.PngChunk;

public abstract class PngTextChunk
extends PngChunk {
    public PngTextChunk(int length, int chunkType, int crc, byte[] bytes) {
        super(length, chunkType, crc, bytes);
    }

    public abstract String getKeyword();

    public abstract String getText();

    public abstract PngText getContents();
}

