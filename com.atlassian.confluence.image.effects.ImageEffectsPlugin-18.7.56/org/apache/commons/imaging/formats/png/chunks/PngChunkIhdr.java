/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.chunks;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.png.InterlaceMethod;
import org.apache.commons.imaging.formats.png.PngColorType;
import org.apache.commons.imaging.formats.png.chunks.PngChunk;

public class PngChunkIhdr
extends PngChunk {
    public final int width;
    public final int height;
    public final int bitDepth;
    public final PngColorType pngColorType;
    public final int compressionMethod;
    public final int filterMethod;
    public final InterlaceMethod interlaceMethod;

    public PngChunkIhdr(int length, int chunkType, int crc, byte[] bytes) throws ImageReadException, IOException {
        super(length, chunkType, crc, bytes);
        ByteArrayInputStream is = new ByteArrayInputStream(bytes);
        this.width = BinaryFunctions.read4Bytes("Width", is, "Not a Valid Png File: IHDR Corrupt", this.getByteOrder());
        this.height = BinaryFunctions.read4Bytes("Height", is, "Not a Valid Png File: IHDR Corrupt", this.getByteOrder());
        this.bitDepth = BinaryFunctions.readByte("BitDepth", is, "Not a Valid Png File: IHDR Corrupt");
        byte type = BinaryFunctions.readByte("ColorType", is, "Not a Valid Png File: IHDR Corrupt");
        this.pngColorType = PngColorType.getColorType(type);
        if (this.pngColorType == null) {
            throw new ImageReadException("PNG: unknown color type: " + type);
        }
        this.compressionMethod = BinaryFunctions.readByte("CompressionMethod", is, "Not a Valid Png File: IHDR Corrupt");
        this.filterMethod = BinaryFunctions.readByte("FilterMethod", is, "Not a Valid Png File: IHDR Corrupt");
        byte method = BinaryFunctions.readByte("InterlaceMethod", is, "Not a Valid Png File: IHDR Corrupt");
        if (method < 0 || method >= InterlaceMethod.values().length) {
            throw new ImageReadException("PNG: unknown interlace method: " + method);
        }
        this.interlaceMethod = InterlaceMethod.values()[method];
    }
}

