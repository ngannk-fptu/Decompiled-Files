/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.image;

import com.jhlabs.image.PixelUtils;
import com.jhlabs.image.TransferFilter;

public class RescaleFilter
extends TransferFilter {
    static final long serialVersionUID = -2724874183243154495L;
    private float scale = 1.0f;

    protected float transferFunction(float v) {
        return PixelUtils.clamp((int)(v * this.scale));
    }

    public void setScale(float scale) {
        this.scale = scale;
        this.initialized = false;
    }

    public float getScale() {
        return this.scale;
    }

    public String toString() {
        return "Colors/Rescale...";
    }
}

