/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.event;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import net.sf.ehcache.Status;
import net.sf.ehcache.event.CacheManagerEventListener;

public class CacheManagerEventListenerRegistry
implements CacheManagerEventListener {
    private volatile Status status = Status.STATUS_UNINITIALISED;
    private Set listeners = new CopyOnWriteArraySet();

    public final boolean registerListener(CacheManagerEventListener cacheManagerEventListener) {
        if (cacheManagerEventListener == null) {
            return false;
        }
        return this.listeners.add(cacheManagerEventListener);
    }

    public final boolean unregisterListener(CacheManagerEventListener cacheManagerEventListener) {
        return this.listeners.remove(cacheManagerEventListener);
    }

    public boolean hasRegisteredListeners() {
        return this.listeners.size() > 0;
    }

    public Set getRegisteredListeners() {
        return this.listeners;
    }

    @Override
    public void init() {
        for (CacheManagerEventListener cacheManagerEventListener : this.listeners) {
            cacheManagerEventListener.init();
        }
        this.status = Status.STATUS_ALIVE;
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public void dispose() {
        for (CacheManagerEventListener cacheManagerEventListener : this.listeners) {
            cacheManagerEventListener.dispose();
        }
        this.listeners.clear();
        this.status = Status.STATUS_SHUTDOWN;
    }

    @Override
    public void notifyCacheAdded(String cacheName) {
        for (CacheManagerEventListener cacheManagerEventListener : this.listeners) {
            cacheManagerEventListener.notifyCacheAdded(cacheName);
        }
    }

    @Override
    public void notifyCacheRemoved(String cacheName) {
        for (CacheManagerEventListener cacheManagerEventListener : this.listeners) {
            cacheManagerEventListener.notifyCacheRemoved(cacheName);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(" cacheManagerEventListeners: ");
        for (CacheManagerEventListener cacheManagerEventListener : this.listeners) {
            sb.append(cacheManagerEventListener.getClass().getName()).append(" ");
        }
        return sb.toString();
    }
}

