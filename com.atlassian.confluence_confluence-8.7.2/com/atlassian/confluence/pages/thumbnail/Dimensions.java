/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.errorprone.annotations.Immutable
 */
package com.atlassian.confluence.pages.thumbnail;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.Immutable;

@Immutable
@Deprecated
public final class Dimensions {
    private final ImageDimensions imageDimensions;

    public Dimensions(int width, int height) {
        this.imageDimensions = new ImageDimensions(width, height);
    }

    public Dimensions(ImageDimensions imageDimensions) {
        this.imageDimensions = (ImageDimensions)Preconditions.checkNotNull((Object)imageDimensions);
    }

    public ImageDimensions getImageDimensions() {
        return this.imageDimensions;
    }

    public int getWidth() {
        return this.imageDimensions.getWidth();
    }

    public int getHeight() {
        return this.imageDimensions.getHeight();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Dimensions that = (Dimensions)o;
        return this.imageDimensions.equals(that.imageDimensions);
    }

    public int hashCode() {
        return this.imageDimensions.hashCode();
    }

    public String toString() {
        return this.imageDimensions.toString();
    }
}

