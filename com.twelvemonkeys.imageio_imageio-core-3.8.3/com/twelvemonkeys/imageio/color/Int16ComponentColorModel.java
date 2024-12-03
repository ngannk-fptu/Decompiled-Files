/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.color;

import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;

public final class Int16ComponentColorModel
extends ComponentColorModel {
    private final ComponentColorModel delegate;

    public Int16ComponentColorModel(ColorSpace colorSpace, boolean bl, boolean bl2) {
        super(colorSpace, bl, bl2, bl ? 3 : 1, 2);
        this.delegate = new ComponentColorModel(colorSpace, bl, bl2, bl ? 3 : 1, 1);
    }

    private void remap(short[] sArray, int n) {
        short s = sArray[n];
        sArray[n] = s < 0 ? (short)(s - Short.MIN_VALUE) : (short)(s + Short.MIN_VALUE);
    }

    @Override
    public int getRed(Object object) {
        this.remap((short[])object, 0);
        return this.delegate.getRed(object);
    }

    @Override
    public int getGreen(Object object) {
        this.remap((short[])object, 1);
        return this.delegate.getGreen(object);
    }

    @Override
    public int getBlue(Object object) {
        this.remap((short[])object, 2);
        return this.delegate.getBlue(object);
    }
}

