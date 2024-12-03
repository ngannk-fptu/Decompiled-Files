/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.image;

import com.jhlabs.image.PointFilter;

public class InvertAlphaFilter
extends PointFilter {
    public InvertAlphaFilter() {
        this.canFilterIndexColorModel = true;
    }

    public int filterRGB(int x, int y, int rgb) {
        return rgb ^ 0xFF000000;
    }

    public String toString() {
        return "Alpha/Invert";
    }
}

