/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.blocking;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

public interface UpdatingCacheEntryFactory
extends CacheEntryFactory {
    public void updateEntryValue(Object var1, Object var2) throws Exception;
}

