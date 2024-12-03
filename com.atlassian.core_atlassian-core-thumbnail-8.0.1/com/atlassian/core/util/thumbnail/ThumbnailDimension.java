/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util.thumbnail;

public class ThumbnailDimension {
    private final int width;
    private final int height;

    public ThumbnailDimension() {
        this(0, 0);
    }

    public ThumbnailDimension(int width, int height) {
        this.height = height;
        this.width = width;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject == null || this.getClass() != anObject.getClass()) {
            return false;
        }
        ThumbnailDimension anotherThumbnailDimension = (ThumbnailDimension)anObject;
        if (this.height != anotherThumbnailDimension.height) {
            return false;
        }
        return this.width == anotherThumbnailDimension.width;
    }

    public int hashCode() {
        return 31 * this.width + this.height;
    }

    public String toString() {
        return this.getClass().getName() + " [width=" + this.width + ",height=" + this.height + "]";
    }
}

