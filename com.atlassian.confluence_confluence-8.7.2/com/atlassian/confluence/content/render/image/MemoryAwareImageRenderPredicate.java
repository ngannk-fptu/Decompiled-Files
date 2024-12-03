/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.content.render.image;

import com.atlassian.confluence.content.render.image.ImageDimensions;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemoryAwareImageRenderPredicate
implements Predicate<ImageDimensions> {
    private static final Logger log = LoggerFactory.getLogger(MemoryAwareImageRenderPredicate.class);
    private static final int BYTES_PER_PIXEL = 4;
    private static final float PROCESSING_HEADROOM = 1.2f;

    @Override
    public boolean test(ImageDimensions imageDimensions) {
        return imageDimensions != null && this.sufficientMemory(imageDimensions);
    }

    private boolean sufficientMemory(ImageDimensions dimensions) {
        long freeMemory;
        boolean result;
        long requiredMemory = dimensions.getHeight() * dimensions.getWidth() * 4;
        boolean bl = result = (float)requiredMemory * 1.2f < (float)(freeMemory = this.freeMemory());
        if (log.isDebugEnabled()) {
            log.debug("Expected memory required for this image conversion: {}. Free memory: {}. Image dimension ({}) renderable: {}", new Object[]{requiredMemory, freeMemory, dimensions, result});
        }
        return result;
    }

    long freeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
}

