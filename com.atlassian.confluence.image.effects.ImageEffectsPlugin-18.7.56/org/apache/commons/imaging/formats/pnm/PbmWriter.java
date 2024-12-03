/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.pnm;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.pnm.PnmWriter;

class PbmWriter
implements PnmWriter {
    private final boolean rawbits;

    PbmWriter(boolean rawbits) {
        this.rawbits = rawbits;
    }

    @Override
    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        os.write(80);
        os.write(this.rawbits ? 52 : 49);
        os.write(32);
        int width = src.getWidth();
        int height = src.getHeight();
        os.write(Integer.toString(width).getBytes(StandardCharsets.US_ASCII));
        os.write(32);
        os.write(Integer.toString(height).getBytes(StandardCharsets.US_ASCII));
        os.write(10);
        int bitcache = 0;
        int bitsInCache = 0;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int blue;
                int green;
                int argb = src.getRGB(x, y);
                int red = 0xFF & argb >> 16;
                int sample = (red + (green = 0xFF & argb >> 8) + (blue = 0xFF & argb >> 0)) / 3;
                sample = sample > 127 ? 0 : 1;
                if (this.rawbits) {
                    bitcache = bitcache << 1 | 1 & sample;
                    if (++bitsInCache < 8) continue;
                    os.write((byte)bitcache);
                    bitcache = 0;
                    bitsInCache = 0;
                    continue;
                }
                os.write(Integer.toString(sample).getBytes(StandardCharsets.US_ASCII));
                os.write(32);
            }
            if (!this.rawbits || bitsInCache <= 0) continue;
            os.write((byte)(bitcache <<= 8 - bitsInCache));
            bitcache = 0;
            bitsInCache = 0;
        }
    }
}

