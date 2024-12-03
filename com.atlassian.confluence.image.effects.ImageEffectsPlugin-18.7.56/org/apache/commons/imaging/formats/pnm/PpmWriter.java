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

class PpmWriter
implements PnmWriter {
    private final boolean rawbits;

    PpmWriter(boolean rawbits) {
        this.rawbits = rawbits;
    }

    @Override
    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        os.write(80);
        os.write(this.rawbits ? 54 : 51);
        os.write(32);
        int width = src.getWidth();
        int height = src.getHeight();
        os.write(Integer.toString(width).getBytes(StandardCharsets.US_ASCII));
        os.write(32);
        os.write(Integer.toString(height).getBytes(StandardCharsets.US_ASCII));
        os.write(32);
        os.write(Integer.toString(255).getBytes(StandardCharsets.US_ASCII));
        os.write(10);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int argb = src.getRGB(x, y);
                int red = 0xFF & argb >> 16;
                int green = 0xFF & argb >> 8;
                int blue = 0xFF & argb >> 0;
                if (this.rawbits) {
                    os.write((byte)red);
                    os.write((byte)green);
                    os.write((byte)blue);
                    continue;
                }
                os.write(Integer.toString(red).getBytes(StandardCharsets.US_ASCII));
                os.write(32);
                os.write(Integer.toString(green).getBytes(StandardCharsets.US_ASCII));
                os.write(32);
                os.write(Integer.toString(blue).getBytes(StandardCharsets.US_ASCII));
                os.write(32);
            }
        }
    }
}

