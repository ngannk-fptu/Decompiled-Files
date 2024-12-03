/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.photometricinterpreters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreter;

public class PhotometricInterpreterRgb
extends PhotometricInterpreter {
    public PhotometricInterpreterRgb(int samplesPerPixel, int[] bitsPerSample, int predictor, int width, int height) {
        super(samplesPerPixel, bitsPerSample, predictor, width, height);
    }

    @Override
    public void interpretPixel(ImageBuilder imageBuilder, int[] samples, int x, int y) throws ImageReadException, IOException {
        int red = samples[0];
        int green = samples[1];
        int blue = samples[2];
        int alpha = 255;
        int rgb = 0xFF000000 | red << 16 | green << 8 | blue << 0;
        imageBuilder.setRGB(x, y, rgb);
    }
}

