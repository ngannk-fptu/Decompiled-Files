/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.confluence.dom;

import java.awt.Dimension;

public class ImageSizeException
extends RuntimeException {
    private final Dimension imgSize;
    private final Dimension maxSize;

    public ImageSizeException(Dimension imgSize, Dimension maxSize) {
        this.imgSize = imgSize;
        this.maxSize = maxSize;
    }

    public Dimension getImgSize() {
        return this.imgSize;
    }

    public Dimension getMaxSize() {
        return this.maxSize;
    }
}

