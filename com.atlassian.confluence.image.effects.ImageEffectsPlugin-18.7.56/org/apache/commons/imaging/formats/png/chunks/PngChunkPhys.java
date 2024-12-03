/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.chunks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.png.chunks.PngChunk;

public class PngChunkPhys
extends PngChunk {
    public final int pixelsPerUnitXAxis;
    public final int pixelsPerUnitYAxis;
    public final int unitSpecifier;

    public PngChunkPhys(int length, int chunkType, int crc, byte[] bytes) throws IOException {
        super(length, chunkType, crc, bytes);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        this.pixelsPerUnitXAxis = BinaryFunctions.read4Bytes("PixelsPerUnitXAxis", is, "Not a Valid Png File: pHYs Corrupt", this.getByteOrder());
        this.pixelsPerUnitYAxis = BinaryFunctions.read4Bytes("PixelsPerUnitYAxis", is, "Not a Valid Png File: pHYs Corrupt", this.getByteOrder());
        this.unitSpecifier = BinaryFunctions.readByte("Unit specifier", is, "Not a Valid Png File: pHYs Corrupt");
    }
}

