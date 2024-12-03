/*
 * Decompiled with CFR 0.152.
 */
package javax.cache.processor;

import javax.cache.CacheException;

public class EntryProcessorException
extends CacheException {
    private static final long serialVersionUID = 20130822110920L;

    public EntryProcessorException() {
    }

    public EntryProcessorException(String message) {
        super(message);
    }

    public EntryProcessorException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntryProcessorException(Throwable cause) {
        super(cause);
    }
}

