/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png.chunks;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.png.chunks.PngChunk;

public class PngChunkScal
extends PngChunk {
    public final double unitsPerPixelXAxis;
    public final double unitsPerPixelYAxis;
    public final int unitSpecifier;

    public PngChunkScal(int length, int chunkType, int crc, byte[] bytes) throws ImageReadException, IOException {
        super(length, chunkType, crc, bytes);
        this.unitSpecifier = bytes[0];
        if (this.unitSpecifier != 1 && this.unitSpecifier != 2) {
            throw new ImageReadException("PNG sCAL invalid unit specifier: " + this.unitSpecifier);
        }
        int separator = BinaryFunctions.findNull(bytes);
        if (separator < 0) {
            throw new ImageReadException("PNG sCAL x and y axis value separator not found.");
        }
        boolean xIndex = true;
        String xStr = new String(bytes, 1, separator - 1, StandardCharsets.ISO_8859_1);
        this.unitsPerPixelXAxis = this.toDouble(xStr);
        int yIndex = separator + 1;
        if (yIndex >= length) {
            throw new ImageReadException("PNG sCAL chunk missing the y axis value.");
        }
        String yStr = new String(bytes, yIndex, length - yIndex, StandardCharsets.ISO_8859_1);
        this.unitsPerPixelYAxis = this.toDouble(yStr);
    }

    private double toDouble(String str) throws ImageReadException {
        try {
            return Double.valueOf(str);
        }
        catch (NumberFormatException e) {
            throw new ImageReadException("PNG sCAL error reading axis value - " + str);
        }
    }
}

