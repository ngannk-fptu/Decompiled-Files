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

class PixelParserRgb
extends PixelParserSimple {
    private int bytecount;
    private int cachedBitCount;
    private int cachedByte;

    PixelParserRgb(BmpHeaderInfo bhi, byte[] colorTable, byte[] imageData) {
        super(bhi, colorTable, imageData);
    }

    @Override
    public int getNextRGB() throws ImageReadException, IOException {
        if (this.bhi.bitsPerPixel == 1 || this.bhi.bitsPerPixel == 4) {
            if (this.cachedBitCount < this.bhi.bitsPerPixel) {
                if (this.cachedBitCount != 0) {
                    throw new ImageReadException("Unexpected leftover bits: " + this.cachedBitCount + "/" + this.bhi.bitsPerPixel);
                }
                this.cachedBitCount += 8;
                this.cachedByte = 0xFF & this.imageData[this.bytecount];
                ++this.bytecount;
            }
            int cacheMask = (1 << this.bhi.bitsPerPixel) - 1;
            int sample = cacheMask & this.cachedByte >> 8 - this.bhi.bitsPerPixel;
            this.cachedByte = 0xFF & this.cachedByte << this.bhi.bitsPerPixel;
            this.cachedBitCount -= this.bhi.bitsPerPixel;
            return this.getColorTableRGB(sample);
        }
        if (this.bhi.bitsPerPixel == 8) {
            int sample = 0xFF & this.imageData[this.bytecount + 0];
            int rgb = this.getColorTableRGB(sample);
            ++this.bytecount;
            return rgb;
        }
        if (this.bhi.bitsPerPixel == 16) {
            int data = BinaryFunctions.read2Bytes("Pixel", this.is, "BMP Image Data", ByteOrder.LITTLE_ENDIAN);
            int blue = (0x1F & data >> 0) << 3;
            int green = (0x1F & data >> 5) << 3;
            int red = (0x1F & data >> 10) << 3;
            int alpha = 255;
            int rgb = 0xFF000000 | red << 16 | green << 8 | blue << 0;
            this.bytecount += 2;
            return rgb;
        }
        if (this.bhi.bitsPerPixel == 24) {
            int blue = 0xFF & this.imageData[this.bytecount + 0];
            int green = 0xFF & this.imageData[this.bytecount + 1];
            int red = 0xFF & this.imageData[this.bytecount + 2];
            int alpha = 255;
            int rgb = 0xFF000000 | red << 16 | green << 8 | blue << 0;
            this.bytecount += 3;
            return rgb;
        }
        if (this.bhi.bitsPerPixel == 32) {
            int blue = 0xFF & this.imageData[this.bytecount + 0];
            int green = 0xFF & this.imageData[this.bytecount + 1];
            int red = 0xFF & this.imageData[this.bytecount + 2];
            int alpha = 255;
            int rgb = 0xFF000000 | red << 16 | green << 8 | blue << 0;
            this.bytecount += 4;
            return rgb;
        }
        throw new ImageReadException("Unknown BitsPerPixel: " + this.bhi.bitsPerPixel);
    }

    @Override
    public void newline() throws ImageReadException, IOException {
        this.cachedBitCount = 0;
        while (this.bytecount % 4 != 0) {
            BinaryFunctions.readByte("Pixel", this.is, "BMP Image Data");
            ++this.bytecount;
        }
    }
}

