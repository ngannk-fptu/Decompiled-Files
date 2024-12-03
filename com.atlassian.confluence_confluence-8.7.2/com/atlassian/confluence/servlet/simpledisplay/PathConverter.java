/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.servlet.simpledisplay;

import com.atlassian.confluence.servlet.simpledisplay.ConvertedPath;

public interface PathConverter {
    @Deprecated
    default public boolean handles(String path) {
        return this.handles(path, "");
    }

    @Deprecated
    default public ConvertedPath getPath(String path) {
        return this.getPath(path, "");
    }

    default public boolean handles(String path, String queryString) {
        return this.handles(path);
    }

    default public ConvertedPath getPath(String path, String queryString) {
        return this.getPath(path);
    }
}

