/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.RandomIterCSMShort;
import java.awt.Rectangle;
import java.awt.image.WritableRenderedImage;
import javax.media.jai.iterator.WritableRandomIter;

public final class WritableRandomIterCSMShort
extends RandomIterCSMShort
implements WritableRandomIter {
    public WritableRandomIterCSMShort(WritableRenderedImage im, Rectangle bounds) {
        super(im, bounds);
    }

    public void setSample(int x, int y, int b, int val) {
    }

    public void setSample(int x, int y, int b, float val) {
    }

    public void setSample(int x, int y, int b, double val) {
    }

    public void setPixel(int x, int y, int[] iArray) {
    }

    public void setPixel(int x, int y, float[] fArray) {
    }

    public void setPixel(int x, int y, double[] dArray) {
    }
}

