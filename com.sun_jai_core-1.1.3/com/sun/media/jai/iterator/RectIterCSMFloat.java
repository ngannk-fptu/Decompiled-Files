/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.RectIterCSM;
import com.sun.media.jai.util.DataBufferUtils;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;

public class RectIterCSMFloat
extends RectIterCSM {
    float[][] bankData;
    float[] bank;

    public RectIterCSMFloat(RenderedImage im, Rectangle bounds) {
        super(im, bounds);
        this.bankData = new float[this.numBands + 1][];
        this.dataBufferChanged();
    }

    protected final void dataBufferChanged() {
        if (this.bankData == null) {
            return;
        }
        float[][] bd = DataBufferUtils.getBankDataFloat(this.dataBuffer);
        for (int i = 0; i < this.numBands; ++i) {
            this.bankData[i] = bd[this.bankIndices[i]];
        }
        this.bank = this.bankData[this.b];
        this.adjustBandOffsets();
    }

    public void startBands() {
        super.startBands();
        this.bank = this.bankData[0];
    }

    public void nextBand() {
        super.nextBand();
        this.bank = this.bankData[this.b];
    }

    public final int getSample() {
        return (int)this.bank[this.offset + this.bandOffset];
    }

    public final int getSample(int b) {
        return (int)this.bankData[b][this.offset + this.bandOffsets[b]];
    }

    public final float getSampleFloat() {
        return this.bank[this.offset + this.bandOffset];
    }

    public final float getSampleFloat(int b) {
        return this.bankData[b][this.offset + this.bandOffsets[b]];
    }

    public final double getSampleDouble() {
        return this.bank[this.offset + this.bandOffset];
    }

    public final double getSampleDouble(int b) {
        return this.bankData[b][this.offset + this.bandOffsets[b]];
    }

    public int[] getPixel(int[] iArray) {
        if (iArray == null) {
            iArray = new int[this.numBands];
        }
        for (int b = 0; b < this.numBands; ++b) {
            iArray[b] = (int)this.bankData[b][this.offset + this.bandOffsets[b]];
        }
        return iArray;
    }

    public float[] getPixel(float[] fArray) {
        if (fArray == null) {
            fArray = new float[this.numBands];
        }
        for (int b = 0; b < this.numBands; ++b) {
            fArray[b] = this.bankData[b][this.offset + this.bandOffsets[b]];
        }
        return fArray;
    }

    public double[] getPixel(double[] dArray) {
        if (dArray == null) {
            dArray = new double[this.numBands];
        }
        for (int b = 0; b < this.numBands; ++b) {
            dArray[b] = this.bankData[b][this.offset + this.bandOffsets[b]];
        }
        return dArray;
    }
}

