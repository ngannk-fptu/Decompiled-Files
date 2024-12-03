/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.image;

import com.jhlabs.image.ConvolveFilter;

public class BlurFilter
extends ConvolveFilter {
    static final long serialVersionUID = -4753886159026796838L;
    protected static float[] blurMatrix = new float[]{0.071428575f, 0.14285715f, 0.071428575f, 0.14285715f, 0.14285715f, 0.14285715f, 0.071428575f, 0.14285715f, 0.071428575f};

    public BlurFilter() {
        super(blurMatrix);
    }

    public String toString() {
        return "Blur/Simple Blur";
    }
}

