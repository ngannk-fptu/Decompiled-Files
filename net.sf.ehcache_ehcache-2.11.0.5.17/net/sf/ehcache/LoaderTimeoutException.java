/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import net.sf.ehcache.CacheException;

public class LoaderTimeoutException
extends CacheException {
    public LoaderTimeoutException() {
    }

    public LoaderTimeoutException(String message) {
        super(message);
    }

    public LoaderTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoaderTimeoutException(Throwable cause) {
        super(cause);
    }
}

