/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.rest.common.multipart;

public interface MultipartConfig {
    default public long getMaxFileSize() {
        return -1L;
    }

    default public long getMaxSize() {
        return -1L;
    }

    default public long getMaxFileCount() {
        return 1000L;
    }
}

