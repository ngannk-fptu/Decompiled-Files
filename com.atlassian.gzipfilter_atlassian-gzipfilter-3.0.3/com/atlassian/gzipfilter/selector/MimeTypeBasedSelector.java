/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gzipfilter.selector;

import com.atlassian.gzipfilter.selector.GzipCompatibilitySelector;
import java.util.Set;

public class MimeTypeBasedSelector
implements GzipCompatibilitySelector {
    private final Set<String> compressableMimeTypes;

    public MimeTypeBasedSelector(Set<String> compressableMimeTypes) {
        this.compressableMimeTypes = compressableMimeTypes;
    }

    @Override
    public boolean shouldGzip(String contentType) {
        return this.compressableMimeTypes.contains(contentType);
    }

    @Override
    public boolean shouldGzip() {
        return true;
    }
}

