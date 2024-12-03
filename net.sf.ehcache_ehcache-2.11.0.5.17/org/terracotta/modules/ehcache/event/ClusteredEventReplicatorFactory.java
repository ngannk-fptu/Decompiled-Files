/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.event;

import java.util.HashMap;
import java.util.Map;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.NonstopConfiguration;
import net.sf.ehcache.event.CacheEventListener;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.event.ClusteredEventReplicator;
import org.terracotta.modules.ehcache.event.NonStopEventReplicator;

public class ClusteredEventReplicatorFactory {
    private final Map<String, CacheEventListener> eventReplicators = new HashMap<String, CacheEventListener>();
    private final ToolkitInstanceFactory toolkitInstanceFactory;

    public ClusteredEventReplicatorFactory(ToolkitInstanceFactory toolkitInstanceFactory) {
        this.toolkitInstanceFactory = toolkitInstanceFactory;
    }

    public synchronized CacheEventListener getOrCreateClusteredEventReplicator(Ehcache cache) {
        String fullyQualifiedCacheName = this.toolkitInstanceFactory.getFullyQualifiedCacheName(cache);
        CacheEventListener replicator = this.eventReplicators.get(fullyQualifiedCacheName);
        if (replicator == null) {
            NonstopConfiguration nonStopConfiguration = cache.getCacheConfiguration().getTerracottaConfiguration().getNonstopConfiguration();
            ClusteredEventReplicator clusteredEventReplicator = new ClusteredEventReplicator(cache, fullyQualifiedCacheName, this.toolkitInstanceFactory.getOrCreateCacheEventNotifier(cache), this);
            replicator = new NonStopEventReplicator(clusteredEventReplicator, this.toolkitInstanceFactory, nonStopConfiguration);
            this.eventReplicators.put(fullyQualifiedCacheName, replicator);
        }
        return replicator;
    }

    public synchronized void disposeClusteredEventReplicator(String fullyQualifiedCacheName) {
        this.eventReplicators.remove(fullyQualifiedCacheName);
    }
}

