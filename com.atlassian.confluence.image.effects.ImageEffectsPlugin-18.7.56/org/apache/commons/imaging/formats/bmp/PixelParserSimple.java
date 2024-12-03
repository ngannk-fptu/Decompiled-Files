/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.bmp;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.formats.bmp.BmpHeaderInfo;
import org.apache.commons.imaging.formats.bmp.PixelParser;

abstract class PixelParserSimple
extends PixelParser {
    PixelParserSimple(BmpHeaderInfo bhi, byte[] colorTable, byte[] imageData) {
        super(bhi, colorTable, imageData);
    }

    public abstract int getNextRGB() throws ImageReadException, IOException;

    public abstract void newline() throws ImageReadException, IOException;

    @Override
    public void processImage(ImageBuilder imageBuilder) throws ImageReadException, IOException {
        for (int y = this.bhi.height - 1; y >= 0; --y) {
            for (int x = 0; x < this.bhi.width; ++x) {
                int rgb = this.getNextRGB();
                imageBuilder.setRGB(x, y, rgb);
            }
            this.newline();
        }
    }
}

