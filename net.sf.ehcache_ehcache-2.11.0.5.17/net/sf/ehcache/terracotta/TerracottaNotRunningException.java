/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.terracotta;

import net.sf.ehcache.CacheException;

public class TerracottaNotRunningException
extends CacheException {
    public TerracottaNotRunningException() {
    }

    public TerracottaNotRunningException(String message, Throwable cause) {
        super(message, cause);
    }

    public TerracottaNotRunningException(String message) {
        super(message);
    }

    public TerracottaNotRunningException(Throwable cause) {
        super(cause);
    }
}

