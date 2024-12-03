/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.bmp;

import org.apache.commons.imaging.formats.bmp.BmpHeaderInfo;
import org.apache.commons.imaging.formats.bmp.PixelParser;

class BmpImageContents {
    final BmpHeaderInfo bhi;
    final byte[] colorTable;
    final byte[] imageData;
    final PixelParser pixelParser;

    BmpImageContents(BmpHeaderInfo bhi, byte[] colorTable, byte[] imageData, PixelParser pixelParser) {
        this.bhi = bhi;
        this.colorTable = colorTable;
        this.imageData = imageData;
        this.pixelParser = pixelParser;
    }
}

