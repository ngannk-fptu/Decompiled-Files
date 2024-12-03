/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.twelvemonkeys.lang.Validate
 */
package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.lang.Validate;
import java.awt.Point;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public final class DiscreteAlphaIndexColorModel
extends ColorModel {
    private final IndexColorModel icm;
    private final int extraSamples;
    private final int samples;

    public DiscreteAlphaIndexColorModel(IndexColorModel indexColorModel) {
        this(indexColorModel, 1, true);
    }

    public DiscreteAlphaIndexColorModel(IndexColorModel indexColorModel, int n, boolean bl) {
        super(((IndexColorModel)Validate.notNull((Object)indexColorModel, (String)"IndexColorModel")).getPixelSize() * (1 + n), new int[]{indexColorModel.getPixelSize(), indexColorModel.getPixelSize(), indexColorModel.getPixelSize(), indexColorModel.getPixelSize()}, indexColorModel.getColorSpace(), bl, false, bl ? 3 : 1, indexColorModel.getTransferType());
        this.icm = indexColorModel;
        this.extraSamples = n;
        this.samples = 1 + n;
    }

    @Override
    public int getNumComponents() {
        return this.getNumColorComponents() + this.extraSamples;
    }

    @Override
    public int getRed(int n) {
        return this.icm.getRed(n);
    }

    @Override
    public int getGreen(int n) {
        return this.icm.getGreen(n);
    }

    @Override
    public int getBlue(int n) {
        return this.icm.getBlue(n);
    }

    @Override
    public int getAlpha(int n) {
        return this.hasAlpha() ? (int)((float)n / (float)((1 << this.getComponentSize(3)) - 1) * 255.0f + 0.5f) : 255;
    }

    private int getSample(Object object, int n) {
        int n2;
        switch (this.transferType) {
            case 0: {
                byte[] byArray = (byte[])object;
                n2 = byArray[n] & 0xFF;
                break;
            }
            case 1: {
                short[] sArray = (short[])object;
                n2 = sArray[n] & 0xFFFF;
                break;
            }
            case 3: {
                int[] nArray = (int[])object;
                n2 = nArray[n];
                break;
            }
            default: {
                throw new UnsupportedOperationException("This method has not been implemented for transferType " + this.transferType);
            }
        }
        return n2;
    }

    @Override
    public int getRed(Object object) {
        return this.getRed(this.getSample(object, 0));
    }

    @Override
    public int getGreen(Object object) {
        return this.getGreen(this.getSample(object, 0));
    }

    @Override
    public int getBlue(Object object) {
        return this.getBlue(this.getSample(object, 0));
    }

    @Override
    public int getAlpha(Object object) {
        return this.hasAlpha() ? this.getAlpha(this.getSample(object, 1)) : 255;
    }

    @Override
    public SampleModel createCompatibleSampleModel(int n, int n2) {
        return new PixelInterleavedSampleModel(this.transferType, n, n2, this.samples, n * this.samples, this.createOffsets(this.samples));
    }

    private int[] createOffsets(int n) {
        int[] nArray = new int[n];
        for (int i = 0; i < n; ++i) {
            nArray[i] = i;
        }
        return nArray;
    }

    @Override
    public boolean isCompatibleSampleModel(SampleModel sampleModel) {
        return sampleModel instanceof PixelInterleavedSampleModel && sampleModel.getNumBands() == this.samples;
    }

    @Override
    public WritableRaster createCompatibleWritableRaster(int n, int n2) {
        return Raster.createWritableRaster(this.createCompatibleSampleModel(n, n2), new Point(0, 0));
    }

    @Override
    public boolean isCompatibleRaster(Raster raster) {
        int n = raster.getSampleModel().getSampleSize(0);
        return raster.getTransferType() == this.transferType && raster.getNumBands() == this.samples && 1 << n >= this.icm.getMapSize();
    }

    @Override
    public boolean equals(Object object) {
        return this == object || object != null && this.getClass() == object.getClass() && this.icm.equals(((DiscreteAlphaIndexColorModel)object).icm);
    }

    @Override
    public String toString() {
        return "DiscreteAlphaIndexColorModel: #pixelBits = " + this.pixel_bits + " numComponents = " + this.getNumComponents() + " color space = " + this.getColorSpace() + " transparency = " + this.getTransparency() + " has alpha = " + this.hasAlpha() + " isAlphaPre = " + this.isAlphaPremultiplied();
    }
}

