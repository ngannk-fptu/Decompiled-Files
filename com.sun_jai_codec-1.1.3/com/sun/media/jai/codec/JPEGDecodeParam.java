/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.ImageDecodeParam;

public class JPEGDecodeParam
implements ImageDecodeParam {
    private boolean decodeToCSM = true;

    public void setDecodeToCSM(boolean decodeToCSM) {
        this.decodeToCSM = decodeToCSM;
    }

    public boolean getDecodeToCSM() {
        return this.decodeToCSM;
    }
}

