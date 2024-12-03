/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import net.sf.ehcache.CacheException;

public final class ObjectExistsException
extends CacheException {
    public ObjectExistsException() {
    }

    public ObjectExistsException(String message) {
        super(message);
    }
}

