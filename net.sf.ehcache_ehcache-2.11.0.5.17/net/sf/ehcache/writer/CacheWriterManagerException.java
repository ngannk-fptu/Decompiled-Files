/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.writer;

import net.sf.ehcache.CacheException;

public class CacheWriterManagerException
extends CacheException {
    public CacheWriterManagerException(RuntimeException cause) {
        super(cause);
    }

    @Override
    public RuntimeException getCause() {
        return (RuntimeException)super.getCause();
    }
}

