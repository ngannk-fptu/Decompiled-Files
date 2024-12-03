/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.png;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.png.BitParser;
import org.apache.commons.imaging.formats.png.GammaCorrection;
import org.apache.commons.imaging.formats.png.PngColorType;
import org.apache.commons.imaging.formats.png.ScanExpediter;
import org.apache.commons.imaging.formats.png.chunks.PngChunkPlte;
import org.apache.commons.imaging.formats.png.transparencyfilters.TransparencyFilter;

class ScanExpediterInterlaced
extends ScanExpediter {
    private static final int[] STARTING_ROW = new int[]{0, 0, 4, 0, 2, 0, 1};
    private static final int[] STARTING_COL = new int[]{0, 4, 0, 2, 0, 1, 0};
    private static final int[] ROW_INCREMENT = new int[]{8, 8, 8, 4, 4, 2, 2};
    private static final int[] COL_INCREMENT = new int[]{8, 8, 4, 4, 2, 2, 1};

    ScanExpediterInterlaced(int width, int height, InputStream is, BufferedImage bi, PngColorType pngColorType, int bitDepth, int bitsPerPixel, PngChunkPlte fPNGChunkPLTE, GammaCorrection gammaCorrection, TransparencyFilter transparencyFilter) {
        super(width, height, is, bi, pngColorType, bitDepth, bitsPerPixel, fPNGChunkPLTE, gammaCorrection, transparencyFilter);
    }

    private void visit(int x, int y, BufferedImage bi, BitParser fBitParser, int pixelIndexInScanline) throws ImageReadException, IOException {
        int rgb = this.getRGB(fBitParser, pixelIndexInScanline);
        bi.setRGB(x, y, rgb);
    }

    @Override
    public void drive() throws ImageReadException, IOException {
        for (int pass = 1; pass <= 7; ++pass) {
            byte[] prev = null;
            for (int y = STARTING_ROW[pass - 1]; y < this.height; y += ROW_INCREMENT[pass - 1]) {
                byte[] unfiltered;
                int x = STARTING_COL[pass - 1];
                int pixelIndexInScanline = 0;
                if (x >= this.width) continue;
                int columnsInRow = 1 + (this.width - STARTING_COL[pass - 1] - 1) / COL_INCREMENT[pass - 1];
                int bitsPerScanLine = this.bitsPerPixel * columnsInRow;
                int pixelBytesPerScanLine = this.getBitsToBytesRoundingUp(bitsPerScanLine);
                prev = unfiltered = this.getNextScanline(this.is, pixelBytesPerScanLine, prev, this.bytesPerPixel);
                BitParser fBitParser = new BitParser(unfiltered, this.bitsPerPixel, this.bitDepth);
                while (x < this.width) {
                    this.visit(x, y, this.bi, fBitParser, pixelIndexInScanline);
                    x += COL_INCREMENT[pass - 1];
                    ++pixelIndexInScanline;
                }
            }
        }
    }
}

