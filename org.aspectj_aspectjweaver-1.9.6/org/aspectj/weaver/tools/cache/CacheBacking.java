/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import org.aspectj.weaver.tools.cache.CachedClassEntry;
import org.aspectj.weaver.tools.cache.CachedClassReference;

public interface CacheBacking {
    public String[] getKeys(String var1);

    public void remove(CachedClassReference var1);

    public void clear();

    public CachedClassEntry get(CachedClassReference var1, byte[] var2);

    public void put(CachedClassEntry var1, byte[] var2);
}

