/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.photometricinterpreters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreter;

public class PhotometricInterpreterPalette
extends PhotometricInterpreter {
    private final int[] indexColorMap;
    private final int bitsPerPixelMask;

    public PhotometricInterpreterPalette(int samplesPerPixel, int[] bitsPerSample, int predictor, int width, int height, int[] colorMap) {
        super(samplesPerPixel, bitsPerSample, predictor, width, height);
        int bitsPerPixel = this.getBitsPerSample(0);
        int colormapScale = 1 << bitsPerPixel;
        this.indexColorMap = new int[colormapScale];
        for (int index = 0; index < colormapScale; ++index) {
            int red = colorMap[index] >> 8 & 0xFF;
            int green = colorMap[index + colormapScale] >> 8 & 0xFF;
            int blue = colorMap[index + 2 * colormapScale] >> 8 & 0xFF;
            this.indexColorMap[index] = 0xFF000000 | red << 16 | green << 8 | blue;
        }
        int temp = 0;
        for (int i = 0; i < bitsPerPixel; ++i) {
            temp = temp << 1 | 1;
        }
        this.bitsPerPixelMask = temp;
    }

    @Override
    public void interpretPixel(ImageBuilder imageBuilder, int[] samples, int x, int y) throws ImageReadException, IOException {
        imageBuilder.setRGB(x, y, this.indexColorMap[samples[0] & this.bitsPerPixelMask]);
    }
}

