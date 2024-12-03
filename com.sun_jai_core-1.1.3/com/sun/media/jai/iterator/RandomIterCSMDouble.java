/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.RandomIterCSM;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;

public class RandomIterCSMDouble
extends RandomIterCSM {
    public RandomIterCSMDouble(RenderedImage im, Rectangle bounds) {
        super(im, bounds);
    }

    public final int getSample(int x, int y, int b) {
        return (int)this.getSampleDouble(x, y, b);
    }

    public final float getSampleFloat(int x, int y, int b) {
        return (float)this.getSampleDouble(x, y, b);
    }

    public final double getSampleDouble(int x, int y, int b) {
        return 0.0;
    }
}

