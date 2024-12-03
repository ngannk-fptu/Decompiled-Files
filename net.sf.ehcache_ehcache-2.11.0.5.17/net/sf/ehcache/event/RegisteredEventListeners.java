/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.event;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.CacheStoreHelper;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.InternalCacheEventListener;
import net.sf.ehcache.event.NotificationScope;
import net.sf.ehcache.event.TerracottaCacheEventReplication;
import net.sf.ehcache.statistics.StatisticBuilder;
import net.sf.ehcache.store.TerracottaStore;
import org.terracotta.statistics.StatisticsManager;
import org.terracotta.statistics.observer.OperationObserver;

public class RegisteredEventListeners {
    private final Set<ListenerWrapper> cacheEventListeners = new CopyOnWriteArraySet<ListenerWrapper>();
    private final Set<InternalCacheEventListener> orderedListeners = new CopyOnWriteArraySet<InternalCacheEventListener>();
    private final Ehcache cache;
    private final CacheStoreHelper helper;
    private final OperationObserver<CacheOperationOutcomes.ExpiredOutcome> expiryObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.ExpiredOutcome.class).named("expiry")).of(this)).tag(new String[]{"cache"})).build();

    public RegisteredEventListeners(Cache cache) {
        this(cache, new CacheStoreHelper(cache));
    }

    public RegisteredEventListeners(Ehcache cache, CacheStoreHelper helper) {
        StatisticsManager.associate(this).withParent(cache);
        this.cache = cache;
        this.helper = helper;
    }

    public final void notifyElementUpdatedOrdered(Element oldElement, Element newElement) {
        if (!this.orderedListeners.isEmpty()) {
            for (InternalCacheEventListener listener : this.orderedListeners) {
                listener.notifyElementRemoved(this.cache, oldElement);
                listener.notifyElementPut(this.cache, newElement);
            }
        }
    }

    public final void notifyElementRemovedOrdered(Element element) {
        if (!this.orderedListeners.isEmpty()) {
            for (InternalCacheEventListener listener : this.orderedListeners) {
                listener.notifyElementRemoved(this.cache, element);
            }
        }
    }

    public final void notifyElementPutOrdered(Element element) {
        if (!this.orderedListeners.isEmpty()) {
            for (InternalCacheEventListener listener : this.orderedListeners) {
                listener.notifyElementPut(this.cache, element);
            }
        }
    }

    public final void notifyElementRemoved(Element element, boolean remoteEvent) throws CacheException {
        this.internalNotifyElementRemoved(element, null, remoteEvent);
    }

    public final void notifyElementRemoved(ElementCreationCallback callback, boolean remoteEvent) throws CacheException {
        this.internalNotifyElementRemoved(null, callback, remoteEvent);
    }

    void internalNotifyElementRemoved(Element element, ElementCreationCallback callback, boolean remoteEvent) {
        if (this.hasCacheEventListeners()) {
            for (ListenerWrapper listenerWrapper : this.cacheEventListeners) {
                if (!listenerWrapper.getScope().shouldDeliver(remoteEvent) || RegisteredEventListeners.isCircularNotification(remoteEvent, listenerWrapper.getListener())) continue;
                CacheEventListener listener = listenerWrapper.getListener();
                listener.notifyElementRemoved(this.cache, this.resolveElement(listener, element, callback));
            }
        }
    }

    public final void notifyElementPut(Element element, boolean remoteEvent) throws CacheException {
        this.internalNotifyElementPut(element, null, remoteEvent);
    }

    public final void notifyElementPut(ElementCreationCallback callback, boolean remoteEvent) throws CacheException {
        this.internalNotifyElementPut(null, callback, remoteEvent);
    }

    void internalNotifyElementPut(Element element, ElementCreationCallback callback, boolean remoteEvent) {
        if (this.hasCacheEventListeners()) {
            for (ListenerWrapper listenerWrapper : this.cacheEventListeners) {
                if (!listenerWrapper.getScope().shouldDeliver(remoteEvent) || RegisteredEventListeners.isCircularNotification(remoteEvent, listenerWrapper.getListener())) continue;
                CacheEventListener listener = listenerWrapper.getListener();
                listener.notifyElementPut(this.cache, this.resolveElement(listener, element, callback));
            }
        }
    }

    public final void notifyElementUpdated(Element element, boolean remoteEvent) {
        this.internalNotifyElementUpdated(element, null, remoteEvent);
    }

    public final void notifyElementUpdated(ElementCreationCallback callback, boolean remoteEvent) {
        this.internalNotifyElementUpdated(null, callback, remoteEvent);
    }

    void internalNotifyElementUpdated(Element element, ElementCreationCallback callback, boolean remoteEvent) {
        if (this.hasCacheEventListeners()) {
            for (ListenerWrapper listenerWrapper : this.cacheEventListeners) {
                if (!listenerWrapper.getScope().shouldDeliver(remoteEvent) || RegisteredEventListeners.isCircularNotification(remoteEvent, listenerWrapper.getListener())) continue;
                CacheEventListener listener = listenerWrapper.getListener();
                listener.notifyElementUpdated(this.cache, this.resolveElement(listener, element, callback));
            }
        }
    }

    public final void notifyElementExpiry(Element element, boolean remoteEvent) {
        this.internalNotifyElementExpiry(element, null, remoteEvent);
    }

    public final void notifyElementExpiry(ElementCreationCallback callback, boolean remoteEvent) {
        this.internalNotifyElementExpiry(null, callback, remoteEvent);
    }

    void internalNotifyElementExpiry(Element element, ElementCreationCallback callback, boolean remoteEvent) {
        if (!remoteEvent) {
            this.expiryObserver.begin();
            this.expiryObserver.end(CacheOperationOutcomes.ExpiredOutcome.SUCCESS);
        }
        if (this.hasCacheEventListeners()) {
            for (ListenerWrapper listenerWrapper : this.cacheEventListeners) {
                if (!listenerWrapper.getScope().shouldDeliver(remoteEvent) || RegisteredEventListeners.isCircularNotification(remoteEvent, listenerWrapper.getListener())) continue;
                CacheEventListener listener = listenerWrapper.getListener();
                listener.notifyElementExpired(this.cache, this.resolveElement(listener, element, callback));
            }
        }
    }

    public final boolean hasCacheEventListeners() {
        return !this.cacheEventListeners.isEmpty();
    }

    public final void notifyElementEvicted(Element element, boolean remoteEvent) {
        this.internalNotifyElementEvicted(element, null, remoteEvent);
    }

    public final void notifyElementEvicted(ElementCreationCallback callback, boolean remoteEvent) {
        this.internalNotifyElementEvicted(null, callback, remoteEvent);
    }

    void internalNotifyElementEvicted(Element element, ElementCreationCallback callback, boolean remoteEvent) {
        if (this.hasCacheEventListeners()) {
            for (ListenerWrapper listenerWrapper : this.cacheEventListeners) {
                if (!listenerWrapper.getScope().shouldDeliver(remoteEvent) || RegisteredEventListeners.isCircularNotification(remoteEvent, listenerWrapper.getListener())) continue;
                CacheEventListener listener = listenerWrapper.getListener();
                listener.notifyElementEvicted(this.cache, this.resolveElement(listener, element, callback));
            }
        }
    }

    private Element resolveElement(CacheEventListener listener, Element element, ElementCreationCallback callback) {
        if (callback != null) {
            return callback.createElement(listener.getClass().getClassLoader());
        }
        return element;
    }

    public final void notifyRemoveAll(boolean remoteEvent) {
        if (this.hasCacheEventListeners()) {
            for (ListenerWrapper listenerWrapper : this.cacheEventListeners) {
                if (!listenerWrapper.getScope().shouldDeliver(remoteEvent) || RegisteredEventListeners.isCircularNotification(remoteEvent, listenerWrapper.getListener())) continue;
                listenerWrapper.getListener().notifyRemoveAll(this.cache);
            }
        }
    }

    private static boolean isCircularNotification(boolean remoteEvent, CacheEventListener cacheEventListener) {
        return remoteEvent && cacheEventListener instanceof TerracottaCacheEventReplication;
    }

    public final boolean registerListener(CacheEventListener cacheEventListener) {
        return this.registerListener(cacheEventListener, NotificationScope.ALL);
    }

    public final boolean registerListener(CacheEventListener cacheEventListener, NotificationScope scope) {
        if (cacheEventListener == null) {
            return false;
        }
        boolean result = this.cacheEventListeners.add(new ListenerWrapper(cacheEventListener, scope));
        if (result) {
            this.notifyEventListenersChangedIfNecessary();
        }
        return result;
    }

    final boolean registerOrderedListener(InternalCacheEventListener cacheEventListener) {
        if (cacheEventListener == null) {
            return false;
        }
        return this.orderedListeners.add(cacheEventListener);
    }

    public final boolean unregisterListener(CacheEventListener cacheEventListener) {
        boolean result = false;
        boolean cacheReplicators = false;
        for (ListenerWrapper listenerWrapper : this.cacheEventListeners) {
            if (!listenerWrapper.getListener().equals(cacheEventListener)) continue;
            this.cacheEventListeners.remove(listenerWrapper);
            result = true;
        }
        if (result) {
            this.notifyEventListenersChangedIfNecessary();
        }
        return result;
    }

    final boolean unregisterOrderedListener(InternalCacheEventListener cacheEventListener) {
        return this.orderedListeners.remove(cacheEventListener);
    }

    public final Set<CacheEventListener> getCacheEventListeners() {
        HashSet<CacheEventListener> listenerSet = new HashSet<CacheEventListener>();
        for (ListenerWrapper listenerWrapper : this.cacheEventListeners) {
            listenerSet.add(listenerWrapper.getListener());
        }
        return listenerSet;
    }

    public final void dispose() {
        for (ListenerWrapper listenerWrapper : this.cacheEventListeners) {
            listenerWrapper.getListener().dispose();
        }
        this.cacheEventListeners.clear();
        this.notifyEventListenersChangedIfNecessary();
        for (InternalCacheEventListener orderedListener : this.orderedListeners) {
            orderedListener.dispose();
        }
        this.orderedListeners.clear();
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(" cacheEventListeners: ");
        for (ListenerWrapper listenerWrapper : this.cacheEventListeners) {
            sb.append(listenerWrapper.getListener().getClass().getName()).append(" ");
        }
        sb.append("; orderedCacheEventListeners: ");
        for (InternalCacheEventListener orderedListener : this.orderedListeners) {
            sb.append(orderedListener.getClass().getName()).append(" ");
        }
        return sb.toString();
    }

    private void notifyEventListenersChangedIfNecessary() {
        if (this.cache.getStatus() == Status.STATUS_ALIVE && this.helper.getStore() instanceof TerracottaStore) {
            ((TerracottaStore)this.helper.getStore()).notifyCacheEventListenersChanged();
        }
    }

    private static enum Event {
        EVICTED,
        PUT,
        EXPIRY,
        UPDATED,
        REMOVED;

    }

    public static interface ElementCreationCallback {
        public Element createElement(ClassLoader var1);
    }

    private static final class ListenerWrapper {
        private final CacheEventListener listener;
        private final NotificationScope scope;

        private ListenerWrapper(CacheEventListener listener, NotificationScope scope) {
            this.listener = listener;
            this.scope = scope;
        }

        private CacheEventListener getListener() {
            return this.listener;
        }

        private NotificationScope getScope() {
            return this.scope;
        }

        public int hashCode() {
            return this.listener.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            ListenerWrapper other = (ListenerWrapper)obj;
            return !(this.listener == null ? other.listener != null : !this.listener.equals(other.listener));
        }

        public String toString() {
            return this.listener.toString();
        }
    }
}

