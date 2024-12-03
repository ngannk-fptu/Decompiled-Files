/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.event;

import java.util.Properties;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.event.CacheManagerEventListener;

public abstract class CacheManagerEventListenerFactory {
    public abstract CacheManagerEventListener createCacheManagerEventListener(CacheManager var1, Properties var2);
}

