/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.image;

import com.jhlabs.image.ConvolveFilter;

public class BumpFilter
extends ConvolveFilter {
    static final long serialVersionUID = 2528502820741699111L;
    protected static float[] embossMatrix = new float[]{-1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f};

    public BumpFilter() {
        super(embossMatrix);
    }

    public String toString() {
        return "Blur/Emboss Edges";
    }
}

