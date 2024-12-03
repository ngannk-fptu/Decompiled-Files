/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.bmp;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.imaging.common.BinaryOutputStream;
import org.apache.commons.imaging.formats.bmp.BmpWriter;

class BmpWriterRgb
implements BmpWriter {
    BmpWriterRgb() {
    }

    @Override
    public int getPaletteSize() {
        return 0;
    }

    @Override
    public int getBitsPerPixel() {
        return 24;
    }

    @Override
    public void writePalette(BinaryOutputStream bos) throws IOException {
    }

    @Override
    public byte[] getImageData(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bytecount = 0;
        for (int y = height - 1; y >= 0; --y) {
            for (int x = 0; x < width; ++x) {
                int argb = src.getRGB(x, y);
                int rgb = 0xFFFFFF & argb;
                int red = 0xFF & rgb >> 16;
                int green = 0xFF & rgb >> 8;
                int blue = 0xFF & rgb >> 0;
                baos.write(blue);
                baos.write(green);
                baos.write(red);
                bytecount += 3;
            }
            while (bytecount % 4 != 0) {
                baos.write(0);
                ++bytecount;
            }
        }
        return baos.toByteArray();
    }
}

