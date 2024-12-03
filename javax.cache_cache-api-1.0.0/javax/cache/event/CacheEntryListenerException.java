/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.event;

import javax.cache.CacheException;

public class CacheEntryListenerException
extends CacheException {
    private static final long serialVersionUID = 20130621110150L;

    public CacheEntryListenerException() {
    }

    public CacheEntryListenerException(String message) {
        super(message);
    }

    public CacheEntryListenerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheEntryListenerException(Throwable cause) {
        super(cause);
    }
}

