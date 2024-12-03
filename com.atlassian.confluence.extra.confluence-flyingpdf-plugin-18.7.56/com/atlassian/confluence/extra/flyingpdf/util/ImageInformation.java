/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.flyingpdf.util;

public class ImageInformation {
    private final int height;
    private final int width;
    private final String tempFileName;
    private final boolean isSVGImage;

    public ImageInformation(int height, int width, String tempFileName, boolean isSVGImage) {
        this.height = height;
        this.width = width;
        this.tempFileName = tempFileName;
        this.isSVGImage = isSVGImage;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public String getTempFileName() {
        return this.tempFileName;
    }

    public boolean isSVGImage() {
        return this.isSVGImage;
    }
}

