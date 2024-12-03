/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.v1.cachedstore.CachedStoreException;

public class CacheFlushException
extends CachedStoreException {
    public CacheFlushException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public CacheFlushException(Throwable throwable) {
        super(throwable);
    }

    public CacheFlushException(String string) {
        super(string);
    }

    public CacheFlushException() {
    }
}

