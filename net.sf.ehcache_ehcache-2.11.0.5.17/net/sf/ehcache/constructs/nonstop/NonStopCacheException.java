/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.nonstop;

import net.sf.ehcache.CacheException;

public class NonStopCacheException
extends CacheException {
    public NonStopCacheException() {
    }

    public NonStopCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonStopCacheException(String message) {
        super(message);
    }

    public NonStopCacheException(Throwable cause) {
        super(cause);
    }
}

