/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.blocking;

import net.sf.ehcache.CacheException;

public class LockTimeoutException
extends CacheException {
    public LockTimeoutException() {
    }

    public LockTimeoutException(String message) {
        super(message);
    }

    public LockTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}

