/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.gzipfilter.selector;

import com.atlassian.gzipfilter.selector.GzipCompatibilitySelector;

public class NoGzipCompatibilitySelector
implements GzipCompatibilitySelector {
    @Override
    public boolean shouldGzip(String contentType) {
        return false;
    }

    @Override
    public boolean shouldGzip() {
        return false;
    }
}

