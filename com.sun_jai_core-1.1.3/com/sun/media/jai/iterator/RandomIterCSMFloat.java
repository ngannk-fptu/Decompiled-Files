/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.RandomIterCSM;
import com.sun.media.jai.util.DataBufferUtils;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;

public class RandomIterCSMFloat
extends RandomIterCSM {
    float[][] bankData;

    public RandomIterCSMFloat(RenderedImage im, Rectangle bounds) {
        super(im, bounds);
    }

    protected final void dataBufferChanged() {
        this.bankData = DataBufferUtils.getBankDataFloat(this.dataBuffer);
    }

    public final int getSample(int x, int y, int b) {
        this.makeCurrent(x - this.boundsX, y - this.boundsX);
        return (int)this.bankData[b][(x - this.sampleModelTranslateX) * this.pixelStride + (y - this.sampleModelTranslateY) * this.scanlineStride + this.bandOffsets[b]];
    }

    public final float getSampleFloat(int x, int y, int b) {
        this.makeCurrent(x - this.boundsX, y - this.boundsX);
        return this.bankData[b][(x - this.sampleModelTranslateX) * this.pixelStride + (y - this.sampleModelTranslateY) * this.scanlineStride + this.bandOffsets[b]];
    }

    public final double getSampleDouble(int x, int y, int b) {
        this.makeCurrent(x - this.boundsX, y - this.boundsX);
        return this.bankData[b][(x - this.sampleModelTranslateX) * this.pixelStride + (y - this.sampleModelTranslateY) * this.scanlineStride + this.bandOffsets[b]];
    }

    public float[] getPixel(int x, int y, float[] fArray) {
        if (fArray == null) {
            fArray = new float[this.numBands];
        }
        int offset = (x - this.sampleModelTranslateX) * this.pixelStride + (y - this.sampleModelTranslateY) * this.scanlineStride;
        for (int b = 0; b < this.numBands; ++b) {
            fArray[b] = this.bankData[b][offset + this.bandOffsets[b]];
        }
        return fArray;
    }
}

