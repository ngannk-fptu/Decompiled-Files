/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.photometricinterpreters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.color.ColorConversions;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreter;

public class PhotometricInterpreterCieLab
extends PhotometricInterpreter {
    public PhotometricInterpreterCieLab(int samplesPerPixel, int[] bitsPerSample, int predictor, int width, int height) {
        super(samplesPerPixel, bitsPerSample, predictor, width, height);
    }

    @Override
    public void interpretPixel(ImageBuilder imageBuilder, int[] samples, int x, int y) throws ImageReadException, IOException {
        int cieL = samples[0];
        byte cieA = (byte)samples[1];
        byte cieB = (byte)samples[2];
        int rgb = ColorConversions.convertCIELabtoARGBTest(cieL, cieA, cieB);
        imageBuilder.setRGB(x, y, rgb);
    }
}

