/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.bmp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.formats.bmp.BmpWriter;
import org.apache.commons.imaging.palette.SimplePalette;

class BmpWriterPalette
implements BmpWriter {
    private final SimplePalette palette;
    private final int bitsPerSample;

    BmpWriterPalette(SimplePalette palette) {
        this.palette = palette;
        this.bitsPerSample = palette.length() <= 2 ? 1 : (palette.length() <= 16 ? 4 : 8);
    }

    @Override
    public int getPaletteSize() {
        return this.palette.length();
    }

    @Override
    public int getBitsPerPixel() {
        return this.bitsPerSample;
    }

    @Override
    public void writePalette(BinaryOutputStream bos) throws IOException {
        for (int i = 0; i < this.palette.length(); ++i) {
            int rgb = this.palette.getEntry(i);
            int red = 0xFF & rgb >> 16;
            int green = 0xFF & rgb >> 8;
            int blue = 0xFF & rgb >> 0;
            bos.write(blue);
            bos.write(green);
            bos.write(red);
            bos.write(0);
        }
    }

    @Override
    public byte[] getImageData(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bitCache = 0;
        int bitsInCache = 0;
        int bytecount = 0;
        for (int y = height - 1; y >= 0; --y) {
            for (int x = 0; x < width; ++x) {
                int argb = src.getRGB(x, y);
                int rgb = 0xFFFFFF & argb;
                int index = this.palette.getPaletteIndex(rgb);
                if (this.bitsPerSample == 8) {
                    baos.write(0xFF & index);
                    ++bytecount;
                    continue;
                }
                bitCache = bitCache << this.bitsPerSample | index;
                if ((bitsInCache += this.bitsPerSample) < 8) continue;
                baos.write(0xFF & bitCache);
                ++bytecount;
                bitCache = 0;
                bitsInCache = 0;
            }
            if (bitsInCache > 0) {
                baos.write(0xFF & (bitCache <<= 8 - bitsInCache));
                ++bytecount;
                bitCache = 0;
                bitsInCache = 0;
            }
            while (bytecount % 4 != 0) {
                baos.write(0);
                ++bytecount;
            }
        }
        return baos.toByteArray();
    }
}

