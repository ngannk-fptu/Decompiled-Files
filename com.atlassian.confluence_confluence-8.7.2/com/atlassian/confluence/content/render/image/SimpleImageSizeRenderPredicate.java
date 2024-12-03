/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.Immutable
 */
package com.atlassian.confluence.content.render.image;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import com.google.errorprone.annotations.Immutable;
import java.util.function.Predicate;

@Immutable
public class SimpleImageSizeRenderPredicate
implements Predicate<ImageDimensions> {
    private final int rasterSizeThresholdPx;

    public SimpleImageSizeRenderPredicate(int rasterSizeThresholdPx) {
        this.rasterSizeThresholdPx = rasterSizeThresholdPx;
    }

    @Override
    public boolean test(ImageDimensions input) {
        return input != null && input.getWidth() < this.rasterSizeThresholdPx && input.getHeight() < this.rasterSizeThresholdPx;
    }
}

