/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.photometricinterpreters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreter;

public class PhotometricInterpreterYCbCr
extends PhotometricInterpreter {
    public PhotometricInterpreterYCbCr(int samplesPerPixel, int[] bitsPerSample, int predictor, int width, int height) {
        super(samplesPerPixel, bitsPerSample, predictor, width, height);
    }

    public static int limit(int value, int min, int max) {
        return Math.min(max, Math.max(min, value));
    }

    public static int convertYCbCrtoRGB(int Y, int Cb, int Cr) {
        double r1 = 1.164 * ((double)Y - 16.0) + 1.596 * ((double)Cr - 128.0);
        double g1 = 1.164 * ((double)Y - 16.0) - 0.813 * ((double)Cr - 128.0) - 0.392 * ((double)Cb - 128.0);
        double b1 = 1.164 * ((double)Y - 16.0) + 2.017 * ((double)Cb - 128.0);
        int r = PhotometricInterpreterYCbCr.limit((int)r1, 0, 255);
        int g = PhotometricInterpreterYCbCr.limit((int)g1, 0, 255);
        int b = PhotometricInterpreterYCbCr.limit((int)b1, 0, 255);
        int alpha = 255;
        int rgb = 0xFF000000 | r << 16 | g << 8 | b << 0;
        return rgb;
    }

    @Override
    public void interpretPixel(ImageBuilder imageBuilder, int[] samples, int x, int y) throws ImageReadException, IOException {
        int Y = samples[0];
        int Cb = samples[1];
        int Cr = samples[2];
        double R = (double)Y + 1.402 * ((double)Cr - 128.0);
        double G = (double)Y - 0.34414 * ((double)Cb - 128.0) - 0.71414 * ((double)Cr - 128.0);
        double B = (double)Y + 1.772 * ((double)Cb - 128.0);
        int red = PhotometricInterpreterYCbCr.limit((int)R, 0, 255);
        int green = PhotometricInterpreterYCbCr.limit((int)G, 0, 255);
        int blue = PhotometricInterpreterYCbCr.limit((int)B, 0, 255);
        int alpha = 255;
        int rgb = 0xFF000000 | red << 16 | green << 8 | blue << 0;
        imageBuilder.setRGB(x, y, rgb);
    }
}

