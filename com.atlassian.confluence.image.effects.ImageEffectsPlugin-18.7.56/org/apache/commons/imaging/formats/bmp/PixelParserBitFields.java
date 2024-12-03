/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.bmp;

import java.io.IOException;
import java.nio.ByteOrder;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.BinaryFunctions;
import org.apache.commons.imaging.formats.bmp.BmpHeaderInfo;
import org.apache.commons.imaging.formats.bmp.PixelParserSimple;

class PixelParserBitFields
extends PixelParserSimple {
    private final int redShift;
    private final int greenShift;
    private final int blueShift;
    private final int alphaShift;
    private final int redMask;
    private final int greenMask;
    private final int blueMask;
    private final int alphaMask;
    private int bytecount;

    PixelParserBitFields(BmpHeaderInfo bhi, byte[] colorTable, byte[] imageData) {
        super(bhi, colorTable, imageData);
        this.redMask = bhi.redMask;
        this.greenMask = bhi.greenMask;
        this.blueMask = bhi.blueMask;
        this.alphaMask = bhi.alphaMask;
        this.redShift = this.getMaskShift(this.redMask);
        this.greenShift = this.getMaskShift(this.greenMask);
        this.blueShift = this.getMaskShift(this.blueMask);
        this.alphaShift = this.alphaMask != 0 ? this.getMaskShift(this.alphaMask) : 0;
    }

    private int getMaskShift(int mask) {
        if (mask == 0) {
            return 0;
        }
        int trailingZeroes = 0;
        while ((1 & mask) == 0) {
            mask = Integer.MAX_VALUE & mask >> 1;
            ++trailingZeroes;
        }
        int maskLength = 0;
        while ((1 & mask) == 1) {
            mask = Integer.MAX_VALUE & mask >> 1;
            ++maskLength;
        }
        return trailingZeroes - (8 - maskLength);
    }

    @Override
    public int getNextRGB() throws ImageReadException, IOException {
        int data;
        if (this.bhi.bitsPerPixel == 8) {
            data = 0xFF & this.imageData[this.bytecount + 0];
            ++this.bytecount;
        } else if (this.bhi.bitsPerPixel == 24) {
            data = BinaryFunctions.read3Bytes("Pixel", this.is, "BMP Image Data", ByteOrder.LITTLE_ENDIAN);
            this.bytecount += 3;
        } else if (this.bhi.bitsPerPixel == 32) {
            data = BinaryFunctions.read4Bytes("Pixel", this.is, "BMP Image Data", ByteOrder.LITTLE_ENDIAN);
            this.bytecount += 4;
        } else if (this.bhi.bitsPerPixel == 16) {
            data = BinaryFunctions.read2Bytes("Pixel", this.is, "BMP Image Data", ByteOrder.LITTLE_ENDIAN);
            this.bytecount += 2;
        } else {
            throw new ImageReadException("Unknown BitsPerPixel: " + this.bhi.bitsPerPixel);
        }
        int red = this.redMask & data;
        int green = this.greenMask & data;
        int blue = this.blueMask & data;
        int alpha = this.alphaMask != 0 ? this.alphaMask & data : 255;
        red = this.redShift >= 0 ? red >> this.redShift : red << -this.redShift;
        green = this.greenShift >= 0 ? green >> this.greenShift : green << -this.greenShift;
        blue = this.blueShift >= 0 ? blue >> this.blueShift : blue << -this.blueShift;
        alpha = this.alphaShift >= 0 ? alpha >> this.alphaShift : alpha << -this.alphaShift;
        return alpha << 24 | red << 16 | green << 8 | blue << 0;
    }

    @Override
    public void newline() throws ImageReadException, IOException {
        while (this.bytecount % 4 != 0) {
            BinaryFunctions.readByte("Pixel", this.is, "BMP Image Data");
            ++this.bytecount;
        }
    }
}

