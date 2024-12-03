/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.iterator;

import com.sun.media.jai.iterator.RandomIterCSM;
import java.awt.Rectangle;
import java.awt.image.RenderedImage;

public class RandomIterCSMUShort
extends RandomIterCSM {
    public RandomIterCSMUShort(RenderedImage im, Rectangle bounds) {
        super(im, bounds);
    }

    public final int getSample(int x, int y, int b) {
        return 0;
    }
}

