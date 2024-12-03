/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 */
package com.atlassian.confluence.content.render.image;

import com.google.errorprone.annotations.Immutable;

@Immutable
public class ImageDimensions {
    public static final ImageDimensions EMPTY = new ImageDimensions(-1, -1);
    private final int width;
    private final int height;

    public ImageDimensions(int width, int height) {
        this.height = height;
        this.width = width;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ImageDimensions)) {
            return false;
        }
        ImageDimensions that = (ImageDimensions)o;
        if (this.height != that.height) {
            return false;
        }
        return this.width == that.width;
    }

    public int hashCode() {
        int result = this.width;
        result = 31 * result + this.height;
        return result;
    }

    public String toString() {
        return this.width + "x" + this.height;
    }
}

