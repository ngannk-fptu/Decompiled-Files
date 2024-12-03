/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.thumbnail;

public enum ThumbnailSize {
    SMALL(64.0, 64.0);

    private double width;
    private double height;

    public double getWidth() {
        return this.width;
    }

    public double getHeight() {
        return this.height;
    }

    public String getSize() {
        return (int)this.getWidth() + "x" + (int)this.getHeight();
    }

    private ThumbnailSize(double width, double height) {
        this.width = width;
        this.height = height;
    }
}

