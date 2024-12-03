/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.event;

import java.util.Properties;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;
import net.sf.ehcache.event.TerracottaCacheEventReplication;

public class TerracottaCacheEventReplicationFactory
extends CacheEventListenerFactory {
    @Override
    public CacheEventListener createCacheEventListener(Properties properties) {
        return new TerracottaCacheEventReplication();
    }
}

