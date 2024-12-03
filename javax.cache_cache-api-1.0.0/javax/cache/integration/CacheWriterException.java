/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.integration;

import javax.cache.CacheException;

public class CacheWriterException
extends CacheException {
    private static final long serialVersionUID = 20130822161612L;

    public CacheWriterException() {
    }

    public CacheWriterException(String message) {
        super(message);
    }

    public CacheWriterException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheWriterException(Throwable cause) {
        super(cause);
    }
}

