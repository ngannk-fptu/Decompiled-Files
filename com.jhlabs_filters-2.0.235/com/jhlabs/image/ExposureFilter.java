/*
 * Decompiled with CFR 0.152.
 */
package com.jhlabs.image;

import com.jhlabs.image.TransferFilter;

public class ExposureFilter
extends TransferFilter {
    private float exposure = 1.0f;

    protected float transferFunction(float f) {
        return 1.0f - (float)Math.exp(-f * this.exposure);
    }

    public void setExposure(float exposure) {
        this.exposure = exposure;
        this.initialized = false;
    }

    public float getExposure() {
        return this.exposure;
    }

    public String toString() {
        return "Colors/Exposure...";
    }
}

