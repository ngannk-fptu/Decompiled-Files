/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.ImageEncodeParam;

public class PNMEncodeParam
implements ImageEncodeParam {
    private boolean raw = true;

    public void setRaw(boolean raw) {
        this.raw = raw;
    }

    public boolean getRaw() {
        return this.raw;
    }
}

