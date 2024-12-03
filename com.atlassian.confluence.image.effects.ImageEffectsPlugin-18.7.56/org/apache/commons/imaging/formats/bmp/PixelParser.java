/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.bmp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.formats.bmp.BmpHeaderInfo;

abstract class PixelParser {
    final BmpHeaderInfo bhi;
    final byte[] colorTable;
    final byte[] imageData;
    final InputStream is;

    PixelParser(BmpHeaderInfo bhi, byte[] colorTable, byte[] imageData) {
        this.bhi = bhi;
        this.colorTable = colorTable;
        this.imageData = imageData;
        this.is = new ByteArrayInputStream(imageData);
    }

    public abstract void processImage(ImageBuilder var1) throws ImageReadException, IOException;

    int getColorTableRGB(int index) {
        int blue = 0xFF & this.colorTable[(index *= 4) + 0];
        int green = 0xFF & this.colorTable[index + 1];
        int red = 0xFF & this.colorTable[index + 2];
        int alpha = 255;
        return 0xFF000000 | red << 16 | green << 8 | blue << 0;
    }
}

