/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.nonstop;

import net.sf.ehcache.CacheException;

public class RejoinCacheException
extends CacheException {
    public RejoinCacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public RejoinCacheException(String message) {
        super(message);
    }
}

