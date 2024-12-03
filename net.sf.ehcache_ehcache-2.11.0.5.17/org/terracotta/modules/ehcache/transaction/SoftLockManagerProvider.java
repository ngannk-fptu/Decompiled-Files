/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.modules.ehcache.transaction;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.transaction.SoftLockManager;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.transaction.ReadCommittedClusteredSoftLockFactory;

public class SoftLockManagerProvider {
    private final ConcurrentMap<String, SoftLockManager> softLockFactories = new ConcurrentHashMap<String, SoftLockManager>();
    private final ToolkitInstanceFactory toolkitInstanceFactory;

    public SoftLockManagerProvider(ToolkitInstanceFactory toolkitInstanceFactory) {
        this.toolkitInstanceFactory = toolkitInstanceFactory;
    }

    public SoftLockManager getOrCreateClusteredSoftLockFactory(Ehcache cache) {
        String name = this.toolkitInstanceFactory.getFullyQualifiedCacheName(cache);
        SoftLockManager softLockFactory = (SoftLockManager)this.softLockFactories.get(name);
        if (softLockFactory == null) {
            softLockFactory = new ReadCommittedClusteredSoftLockFactory(this.toolkitInstanceFactory, cache.getCacheManager().getName(), cache.getName());
            SoftLockManager old = this.softLockFactories.putIfAbsent(name, softLockFactory);
            if (old == null) {
                cache.getCacheEventNotificationService().registerListener(new EventListener(name));
            } else {
                softLockFactory = old;
            }
        }
        return softLockFactory;
    }

    private void disposeSoftLockFactory(String fullyQualifiedCacheName) {
        this.softLockFactories.remove(fullyQualifiedCacheName);
    }

    private class EventListener
    implements CacheEventListener {
        private final String fullyQualifiedCacheName;

        private EventListener(String fullyQualifiedCacheName) {
            this.fullyQualifiedCacheName = fullyQualifiedCacheName;
        }

        @Override
        public void dispose() {
            SoftLockManagerProvider.this.disposeSoftLockFactory(this.fullyQualifiedCacheName);
        }

        @Override
        public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
        }

        @Override
        public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
        }

        @Override
        public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
        }

        @Override
        public void notifyElementExpired(Ehcache cache, Element element) {
        }

        @Override
        public void notifyElementEvicted(Ehcache cache, Element element) {
        }

        @Override
        public void notifyRemoveAll(Ehcache cache) {
        }

        @Override
        public EventListener clone() throws CloneNotSupportedException {
            return (EventListener)super.clone();
        }
    }
}

