/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.photometricinterpreters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreter;

public class PhotometricInterpreterBiLevel
extends PhotometricInterpreter {
    private final boolean invert;

    public PhotometricInterpreterBiLevel(int samplesPerPixel, int[] bitsPerSample, int predictor, int width, int height, boolean invert) {
        super(samplesPerPixel, bitsPerSample, predictor, width, height);
        this.invert = invert;
    }

    @Override
    public void interpretPixel(ImageBuilder imageBuilder, int[] samples, int x, int y) throws ImageReadException, IOException {
        int sample = samples[0];
        if (this.invert) {
            sample = 255 - sample;
        }
        int red = sample;
        int green = sample;
        int blue = sample;
        int alpha = 255;
        int rgb = 0xFF000000 | red << 16 | green << 8 | blue << 0;
        imageBuilder.setRGB(x, y, rgb);
    }
}

