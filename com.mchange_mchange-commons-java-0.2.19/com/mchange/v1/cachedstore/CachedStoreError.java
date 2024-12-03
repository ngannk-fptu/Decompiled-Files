/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v1.cachedstore;

import com.mchange.lang.PotentiallySecondaryError;

public class CachedStoreError
extends PotentiallySecondaryError {
    public CachedStoreError(String string, Throwable throwable) {
        super(string, throwable);
    }

    public CachedStoreError(Throwable throwable) {
        super(throwable);
    }

    public CachedStoreError(String string) {
        super(string);
    }

    public CachedStoreError() {
    }
}

