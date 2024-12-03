/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.RandomIterFallback;
import java.awt.Rectangle;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;

public abstract class RandomIterCSM
extends RandomIterFallback {
    protected ComponentSampleModel sampleModel;
    protected int pixelStride;
    protected int scanlineStride;
    protected int[] bandOffsets;
    protected int numBands;

    public RandomIterCSM(RenderedImage im, Rectangle bounds) {
        super(im, bounds);
        this.sampleModel = (ComponentSampleModel)im.getSampleModel();
        this.numBands = this.sampleModel.getNumBands();
        this.pixelStride = this.sampleModel.getPixelStride();
        this.scanlineStride = this.sampleModel.getScanlineStride();
    }

    protected void dataBufferChanged() {
    }

    protected final void makeCurrent(int xLocal, int yLocal) {
        int xIDNew = this.xTiles[xLocal];
        int yIDNew = this.yTiles[yLocal];
        if (xIDNew != this.xID || yIDNew != this.yID || this.dataBuffer == null) {
            this.xID = xIDNew;
            this.yID = yIDNew;
            Raster tile = this.im.getTile(this.xID, this.yID);
            this.dataBuffer = tile.getDataBuffer();
            this.dataBufferChanged();
            this.bandOffsets = this.dataBuffer.getOffsets();
        }
    }

    public float getSampleFloat(int x, int y, int b) {
        return this.getSample(x, y, b);
    }

    public double getSampleDouble(int x, int y, int b) {
        return this.getSample(x, y, b);
    }

    public int[] getPixel(int x, int y, int[] iArray) {
        if (iArray == null) {
            iArray = new int[this.numBands];
        }
        for (int b = 0; b < this.numBands; ++b) {
            iArray[b] = this.getSample(x, y, b);
        }
        return iArray;
    }

    public float[] getPixel(int x, int y, float[] fArray) {
        if (fArray == null) {
            fArray = new float[this.numBands];
        }
        for (int b = 0; b < this.numBands; ++b) {
            fArray[b] = this.getSampleFloat(x, y, b);
        }
        return fArray;
    }

    public double[] getPixel(int x, int y, double[] dArray) {
        if (dArray == null) {
            dArray = new double[this.numBands];
        }
        for (int b = 0; b < this.numBands; ++b) {
            dArray[b] = this.getSampleDouble(x, y, b);
        }
        return dArray;
    }
}

