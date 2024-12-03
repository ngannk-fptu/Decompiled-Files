/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.RookIterFallback;
import java.awt.Rectangle;
import java.awt.image.WritableRenderedImage;
import javax.media.jai.iterator.WritableRookIter;

public class WritableRookIterFallback
extends RookIterFallback
implements WritableRookIter {
    public WritableRookIterFallback(WritableRenderedImage im, Rectangle bounds) {
        super(im, bounds);
    }

    public void setSample(int s) {
        this.sampleModel.setSample(this.localX, this.localY, this.b, s, this.dataBuffer);
    }

    public void setSample(int b, int s) {
        this.sampleModel.setSample(this.localX, this.localY, b, s, this.dataBuffer);
    }

    public void setSample(float s) {
        this.sampleModel.setSample(this.localX, this.localY, this.b, s, this.dataBuffer);
    }

    public void setSample(int b, float s) {
        this.sampleModel.setSample(this.localX, this.localY, b, s, this.dataBuffer);
    }

    public void setSample(double s) {
        this.sampleModel.setSample(this.localX, this.localY, this.b, s, this.dataBuffer);
    }

    public void setSample(int b, double s) {
        this.sampleModel.setSample(this.localX, this.localY, b, s, this.dataBuffer);
    }

    public void setPixel(int[] iArray) {
        this.sampleModel.setPixel(this.localX, this.localY, iArray, this.dataBuffer);
    }

    public void setPixel(float[] fArray) {
        this.sampleModel.setPixel(this.localX, this.localY, fArray, this.dataBuffer);
    }

    public void setPixel(double[] dArray) {
        this.sampleModel.setPixel(this.localX, this.localY, dArray, this.dataBuffer);
    }
}

