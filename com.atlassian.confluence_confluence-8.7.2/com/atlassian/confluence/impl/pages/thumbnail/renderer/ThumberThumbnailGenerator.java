/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.thumbnail.Thumber
 *  com.atlassian.core.util.thumbnail.Thumbnail
 */
package com.atlassian.confluence.impl.pages.thumbnail.renderer;

import com.atlassian.confluence.impl.pages.thumbnail.renderer.ThumbnailGenerator;
import com.atlassian.core.util.thumbnail.Thumber;
import com.atlassian.core.util.thumbnail.Thumbnail;
import java.io.File;
import java.io.InputStream;

class ThumberThumbnailGenerator
implements ThumbnailGenerator {
    private final Thumber thumber;

    public ThumberThumbnailGenerator(Thumber thumber) {
        this.thumber = thumber;
    }

    @Override
    public Thumbnail generate(InputStream inputStream, File outputFile, int maxWidth, int maxHeight) {
        return this.thumber.retrieveOrCreateThumbNail(inputStream, null, outputFile, maxWidth, maxHeight, 0L);
    }
}

