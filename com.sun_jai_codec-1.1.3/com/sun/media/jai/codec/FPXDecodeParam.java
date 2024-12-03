/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.ImageDecodeParam;

public class FPXDecodeParam
implements ImageDecodeParam {
    private int resolution = -1;

    public FPXDecodeParam() {
    }

    public FPXDecodeParam(int resolution) {
        this.resolution = resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public int getResolution() {
        return this.resolution;
    }
}

