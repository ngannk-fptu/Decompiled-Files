/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ReusableBufferedInputStream
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.pages.thumbnail.renderer;

import com.atlassian.confluence.content.render.image.MemoryAwareImageRenderPredicate;
import com.atlassian.confluence.impl.pages.thumbnail.renderer.ThumbnailRenderer;
import com.atlassian.confluence.pages.thumbnail.Dimensions;
import com.atlassian.core.util.ReusableBufferedInputStream;
import com.google.common.base.Predicate;
import java.io.InputStream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@Deprecated
public class AdaptiveThresholdPredicate
implements Predicate<Dimensions> {
    private final MemoryAwareImageRenderPredicate delegate = new MemoryAwareImageRenderPredicate();

    public boolean apply(@NonNull Dimensions input) {
        return this.delegate.test(input.getImageDimensions());
    }

    public static InputStreamPredicate createInputStreamPredicate() {
        return new AdaptiveThresholdPredicate().new InputStreamPredicate();
    }

    private class InputStreamPredicate
    implements Predicate<ReusableBufferedInputStream> {
        private InputStreamPredicate() {
        }

        public boolean apply(@Nullable ReusableBufferedInputStream input) {
            return AdaptiveThresholdPredicate.this.apply(ThumbnailRenderer.imageDimensions((InputStream)input));
        }
    }
}

