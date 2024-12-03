/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.photometricinterpreters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.color.ColorConversions;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreter;

public class PhotometricInterpreterCmyk
extends PhotometricInterpreter {
    public PhotometricInterpreterCmyk(int samplesPerPixel, int[] bitsPerSample, int predictor, int width, int height) {
        super(samplesPerPixel, bitsPerSample, predictor, width, height);
    }

    @Override
    public void interpretPixel(ImageBuilder imageBuilder, int[] samples, int x, int y) throws ImageReadException, IOException {
        int sc = samples[0];
        int sm = samples[1];
        int sy = samples[2];
        int sk = samples[3];
        int rgb = ColorConversions.convertCMYKtoRGB(sc, sm, sy, sk);
        imageBuilder.setRGB(x, y, rgb);
    }
}

