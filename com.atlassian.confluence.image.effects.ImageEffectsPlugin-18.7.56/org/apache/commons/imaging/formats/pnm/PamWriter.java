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

class PamWriter
implements PnmWriter {
    PamWriter() {
    }

    @Override
    public void writeImage(BufferedImage src, OutputStream os, Map<String, Object> params) throws ImageWriteException, IOException {
        os.write(80);
        os.write(55);
        os.write(10);
        int width = src.getWidth();
        int height = src.getHeight();
        os.write(("WIDTH " + width).getBytes(StandardCharsets.US_ASCII));
        os.write(10);
        os.write(("HEIGHT " + height).getBytes(StandardCharsets.US_ASCII));
        os.write(10);
        os.write("DEPTH 4".getBytes(StandardCharsets.US_ASCII));
        os.write(10);
        os.write("MAXVAL 255".getBytes(StandardCharsets.US_ASCII));
        os.write(10);
        os.write("TUPLTYPE RGB_ALPHA".getBytes(StandardCharsets.US_ASCII));
        os.write(10);
        os.write("ENDHDR".getBytes(StandardCharsets.US_ASCII));
        os.write(10);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int argb = src.getRGB(x, y);
                int alpha = 0xFF & argb >> 24;
                int red = 0xFF & argb >> 16;
                int green = 0xFF & argb >> 8;
                int blue = 0xFF & argb >> 0;
                os.write((byte)red);
                os.write((byte)green);
                os.write((byte)blue);
                os.write((byte)alpha);
            }
        }
    }
}

