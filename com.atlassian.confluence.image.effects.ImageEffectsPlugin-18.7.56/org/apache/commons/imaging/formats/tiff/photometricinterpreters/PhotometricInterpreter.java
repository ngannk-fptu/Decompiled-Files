/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.formats.tiff.photometricinterpreters;

import java.io.IOException;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.common.ImageBuilder;

public abstract class PhotometricInterpreter {
    protected final int samplesPerPixel;
    private final int[] bitsPerSample;
    protected final int predictor;
    protected final int width;
    protected final int height;

    public PhotometricInterpreter(int samplesPerPixel, int[] bitsPerSample, int predictor, int width, int height) {
        this.samplesPerPixel = samplesPerPixel;
        this.bitsPerSample = bitsPerSample;
        this.predictor = predictor;
        this.width = width;
        this.height = height;
    }

    public abstract void interpretPixel(ImageBuilder var1, int[] var2, int var3, int var4) throws ImageReadException, IOException;

    protected int getBitsPerSample(int offset) {
        return this.bitsPerSample[offset];
    }
}

