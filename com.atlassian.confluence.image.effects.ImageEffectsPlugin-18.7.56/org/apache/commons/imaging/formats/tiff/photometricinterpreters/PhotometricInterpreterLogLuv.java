/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.photometricinterpreters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageBuilder;
import org.apache.commons.imaging.formats.tiff.photometricinterpreters.PhotometricInterpreter;

public class PhotometricInterpreterLogLuv
extends PhotometricInterpreter {
    public PhotometricInterpreterLogLuv(int samplesPerPixel, int[] bitsPerSample, int predictor, int width, int height) {
        super(samplesPerPixel, bitsPerSample, predictor, width, height);
    }

    @Override
    public void interpretPixel(ImageBuilder imageBuilder, int[] samples, int x, int y) throws ImageReadException, IOException {
        if (samples == null || samples.length != 3) {
            throw new ImageReadException("Invalid length of bits per sample (expected 3).");
        }
        int cieL = samples[0];
        byte cieA = (byte)samples[1];
        byte cieB = (byte)samples[2];
        TristimulusValues tristimulusValues = this.getTristimulusValues(cieL, cieA, cieB);
        RgbValues rgbValues = this.getRgbValues(tristimulusValues);
        int red = Math.min(255, Math.max(0, rgbValues.r));
        int green = Math.min(255, Math.max(0, rgbValues.g));
        int blue = Math.min(255, Math.max(0, rgbValues.b));
        int alpha = 255;
        int rgb = 0xFF000000 | red << 16 | green << 8 | blue << 0;
        imageBuilder.setRGB(x, y, rgb);
    }

    TristimulusValues getTristimulusValues(int cieL, int cieA, int cieB) {
        float var_Y = ((float)cieL * 100.0f / 255.0f + 16.0f) / 116.0f;
        float var_X = (float)cieA / 500.0f + var_Y;
        float var_Z = var_Y - (float)cieB / 200.0f;
        float var_x_cube = (float)Math.pow(var_X, 3.0);
        float var_y_cube = (float)Math.pow(var_Y, 3.0);
        float var_z_cube = (float)Math.pow(var_Z, 3.0);
        var_Y = var_y_cube > 0.008856f ? var_y_cube : (var_Y - 0.13793103f) / 7.787f;
        var_X = var_x_cube > 0.008856f ? var_x_cube : (var_X - 0.13793103f) / 7.787f;
        var_Z = var_z_cube > 0.008856f ? var_z_cube : (var_Z - 0.13793103f) / 7.787f;
        float ref_X = 95.047f;
        float ref_Y = 100.0f;
        float ref_Z = 108.883f;
        TristimulusValues values = new TristimulusValues();
        values.x = 95.047f * var_X;
        values.y = 100.0f * var_Y;
        values.z = 108.883f * var_Z;
        return values;
    }

    RgbValues getRgbValues(TristimulusValues tristimulusValues) {
        float var_X = tristimulusValues.x / 100.0f;
        float var_Y = tristimulusValues.y / 100.0f;
        float var_Z = tristimulusValues.z / 100.0f;
        float var_R = var_X * 3.2406f + var_Y * -1.5372f + var_Z * -0.4986f;
        float var_G = var_X * -0.9689f + var_Y * 1.8758f + var_Z * 0.0415f;
        float var_B = var_X * 0.0557f + var_Y * -0.204f + var_Z * 1.057f;
        var_R = (double)var_R > 0.0031308 ? 1.055f * (float)Math.pow(var_R, 0.4166666666666667) - 0.055f : 12.92f * var_R;
        var_G = (double)var_G > 0.0031308 ? 1.055f * (float)Math.pow(var_G, 0.4166666666666667) - 0.055f : 12.92f * var_G;
        var_B = (double)var_B > 0.0031308 ? 1.055f * (float)Math.pow(var_B, 0.4166666666666667) - 0.055f : 12.92f * var_B;
        RgbValues values = new RgbValues();
        values.r = (int)(var_R * 255.0f);
        values.g = (int)(var_G * 255.0f);
        values.b = (int)(var_B * 255.0f);
        return values;
    }

    static class RgbValues {
        public int r;
        public int g;
        public int b;

        RgbValues() {
        }
    }

    static class TristimulusValues {
        public float x;
        public float y;
        public float z;

        TristimulusValues() {
        }
    }
}

