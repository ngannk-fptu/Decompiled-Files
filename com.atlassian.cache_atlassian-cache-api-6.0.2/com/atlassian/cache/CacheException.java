/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.cache;

import com.atlassian.annotations.PublicApi;

@PublicApi
public class CacheException
extends RuntimeException {
    private static final long serialVersionUID = 4803271026261445311L;

    public CacheException() {
    }

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }
}

