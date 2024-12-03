/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.event;

import java.util.Properties;
import net.sf.ehcache.event.CacheEventListener;

public abstract class CacheEventListenerFactory {
    public abstract CacheEventListener createCacheEventListener(Properties var1);
}

