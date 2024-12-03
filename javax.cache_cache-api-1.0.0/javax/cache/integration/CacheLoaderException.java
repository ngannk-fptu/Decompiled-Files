/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.integration;

import javax.cache.CacheException;

public class CacheLoaderException
extends CacheException {
    private static final long serialVersionUID = 20130822163231L;

    public CacheLoaderException() {
    }

    public CacheLoaderException(String message) {
        super(message);
    }

    public CacheLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheLoaderException(Throwable cause) {
        super(cause);
    }
}

