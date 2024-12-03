/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.distribution;

import net.sf.ehcache.CacheException;

public final class RemoteCacheException
extends CacheException {
    public RemoteCacheException() {
    }

    public RemoteCacheException(String message) {
        super(message);
    }

    public RemoteCacheException(String message, Throwable cause) {
        super(message, cause);
    }
}

