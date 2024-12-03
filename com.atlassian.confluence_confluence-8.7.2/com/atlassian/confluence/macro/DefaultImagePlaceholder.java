/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.atlassian.confluence.macro.ImagePlaceholder;
import com.atlassian.confluence.pages.thumbnail.Dimensions;

public class DefaultImagePlaceholder
implements ImagePlaceholder {
    private final String url;
    private final ImageDimensions dimensions;
    private final boolean applyPlaceholderChrome;

    @Deprecated
    public DefaultImagePlaceholder(String url, Dimensions dimensions, boolean applyPlaceholderChrome) {
        this.url = url;
        this.dimensions = dimensions != null ? dimensions.getImageDimensions() : null;
        this.applyPlaceholderChrome = applyPlaceholderChrome;
    }

    public DefaultImagePlaceholder(String url, boolean applyPlaceholderChrome, ImageDimensions imageDimensions) {
        this.url = url;
        this.dimensions = imageDimensions;
        this.applyPlaceholderChrome = applyPlaceholderChrome;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    @Deprecated
    public Dimensions getDimensions() {
        if (this.dimensions == null) {
            return null;
        }
        return new Dimensions(this.dimensions);
    }

    @Override
    public ImageDimensions getImageDimensions() {
        return this.dimensions;
    }

    @Override
    public boolean applyPlaceholderChrome() {
        return this.applyPlaceholderChrome;
    }
}

