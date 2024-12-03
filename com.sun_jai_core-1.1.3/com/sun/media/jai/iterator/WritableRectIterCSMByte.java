/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.RectIterCSMByte;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;
import javax.media.jai.iterator.WritableRectIter;

public class WritableRectIterCSMByte
extends RectIterCSMByte
implements WritableRectIter {
    public WritableRectIterCSMByte(RenderedImage im, Rectangle bounds) {
        super(im, bounds);
    }

    public void setSample(int s) {
        this.bank[this.offset + this.bandOffset] = (byte)s;
    }

    public void setSample(int b, int s) {
        this.bankData[b][this.offset + this.bandOffsets[b]] = (byte)s;
    }

    public void setSample(float s) {
        this.bank[this.offset + this.bandOffset] = (byte)s;
    }

    public void setSample(int b, float s) {
        this.bankData[b][this.offset + this.bandOffsets[b]] = (byte)s;
    }

    public void setSample(double s) {
        this.bank[this.offset + this.bandOffset] = (byte)s;
    }

    public void setSample(int b, double s) {
        this.bankData[b][this.offset + this.bandOffsets[b]] = (byte)s;
    }

    public void setPixel(int[] iArray) {
        for (int b = 0; b < this.numBands; ++b) {
            this.bankData[b][this.offset + this.bandOffsets[b]] = (byte)iArray[b];
        }
    }

    public void setPixel(float[] fArray) {
        for (int b = 0; b < this.numBands; ++b) {
            this.bankData[b][this.offset + this.bandOffsets[b]] = (byte)fArray[b];
        }
    }

    public void setPixel(double[] dArray) {
        for (int b = 0; b < this.numBands; ++b) {
            this.bankData[b][this.offset + this.bandOffsets[b]] = (byte)dArray[b];
        }
    }
}

