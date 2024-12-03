/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.imageeffects.core.exif;

public class ExifInfo {
    private Integer width;
    private Integer height;
    private final Integer orientation;

    public ExifInfo(Integer width, Integer height, Integer orientation) {
        this.width = width;
        this.height = height;
        this.orientation = orientation;
    }

    public Integer getWidth() {
        return this.width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return this.height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getOrientation() {
        return this.orientation;
    }
}

