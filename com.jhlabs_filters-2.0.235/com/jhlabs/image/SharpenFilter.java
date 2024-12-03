/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.image;

import com.jhlabs.image.ConvolveFilter;

public class SharpenFilter
extends ConvolveFilter {
    static final long serialVersionUID = -4883137561307845895L;
    protected static float[] sharpenMatrix = new float[]{0.0f, -0.2f, 0.0f, -0.2f, 1.8f, -0.2f, 0.0f, -0.2f, 0.0f};

    public SharpenFilter() {
        super(sharpenMatrix);
    }

    public String toString() {
        return "Blur/Sharpen";
    }
}

