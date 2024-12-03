/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.thumbnail.Thumbnail
 */
package com.atlassian.confluence.impl.pages.thumbnail.renderer;

import com.atlassian.confluence.impl.pages.thumbnail.renderer.ThumbnailGenerator;
import com.atlassian.confluence.impl.pages.thumbnail.renderer.ThumbnailRenderer;
import com.atlassian.confluence.util.io.InputStreamConsumer;
import com.atlassian.core.util.thumbnail.Thumbnail;
import java.io.File;
import java.io.InputStream;

abstract class AbstractStreamedThumbnailGenerator
implements ThumbnailGenerator {
    AbstractStreamedThumbnailGenerator() {
    }

    @Override
    public Thumbnail generate(InputStream inputStream, File outputFile, int maxWidth, int maxHeight) {
        return ThumbnailRenderer.withStreamConsumer(inputStream, this.getInputStreamConsumer(outputFile, maxWidth, maxHeight));
    }

    protected abstract InputStreamConsumer<Thumbnail> getInputStreamConsumer(File var1, int var2, int var3);
}

