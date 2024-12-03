/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.pages.thumbnail.Dimensions;

public interface ImagePlaceholder {
    public String getUrl();

    @Deprecated
    public Dimensions getDimensions();

    default public ImageDimensions getImageDimensions() {
        Dimensions dimensions = this.getDimensions();
        if (dimensions == null) {
            return null;
        }
        return dimensions.getImageDimensions();
    }

    public boolean applyPlaceholderChrome();
}

