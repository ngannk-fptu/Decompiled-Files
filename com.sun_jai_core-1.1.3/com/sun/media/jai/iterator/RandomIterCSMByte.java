/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.RandomIterCSM;
import java.awt.Rectangle;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;

public class RandomIterCSMByte
extends RandomIterCSM {
    byte[][] bankData;

    public RandomIterCSMByte(RenderedImage im, Rectangle bounds) {
        super(im, bounds);
    }

    protected final void dataBufferChanged() {
        this.bankData = ((DataBufferByte)this.dataBuffer).getBankData();
    }

    public final int getSample(int x, int y, int b) {
        this.makeCurrent(x - this.boundsX, y - this.boundsY);
        return this.bankData[b][(x - this.sampleModelTranslateX) * this.pixelStride + (y - this.sampleModelTranslateY) * this.scanlineStride + this.bandOffsets[b]] & 0xFF;
    }

    public final float getSampleFloat(int x, int y, int b) {
        this.makeCurrent(x - this.boundsX, y - this.boundsX);
        return this.bankData[b][(x - this.sampleModelTranslateX) * this.pixelStride + (y - this.sampleModelTranslateY) * this.scanlineStride + this.bandOffsets[b]] & 0xFF;
    }

    public final double getSampleDouble(int x, int y, int b) {
        this.makeCurrent(x - this.boundsX, y - this.boundsX);
        return this.bankData[b][(x - this.sampleModelTranslateX) * this.pixelStride + (y - this.sampleModelTranslateY) * this.scanlineStride + this.bandOffsets[b]] & 0xFF;
    }

    public int[] getPixel(int x, int y, int[] iArray) {
        if (iArray == null) {
            iArray = new int[this.numBands];
        }
        int offset = (x - this.sampleModelTranslateX) * this.pixelStride + (y - this.sampleModelTranslateY) * this.scanlineStride;
        for (int b = 0; b < this.numBands; ++b) {
            iArray[b] = this.bankData[b][offset + this.bandOffsets[b]] & 0xFF;
        }
        return iArray;
    }
}

