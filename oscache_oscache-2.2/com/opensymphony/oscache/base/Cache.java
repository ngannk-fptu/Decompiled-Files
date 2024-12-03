/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.base;

import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.base.EntryRefreshPolicy;
import com.opensymphony.oscache.base.EntryUpdateState;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.base.algorithm.AbstractConcurrentReadCache;
import com.opensymphony.oscache.base.algorithm.LRUCache;
import com.opensymphony.oscache.base.algorithm.UnlimitedCache;
import com.opensymphony.oscache.base.events.CacheEntryEvent;
import com.opensymphony.oscache.base.events.CacheEntryEventListener;
import com.opensymphony.oscache.base.events.CacheEntryEventType;
import com.opensymphony.oscache.base.events.CacheEventListener;
import com.opensymphony.oscache.base.events.CacheGroupEvent;
import com.opensymphony.oscache.base.events.CacheMapAccessEvent;
import com.opensymphony.oscache.base.events.CacheMapAccessEventListener;
import com.opensymphony.oscache.base.events.CacheMapAccessEventType;
import com.opensymphony.oscache.base.events.CachePatternEvent;
import com.opensymphony.oscache.base.events.CachewideEvent;
import com.opensymphony.oscache.base.events.CachewideEventType;
import com.opensymphony.oscache.base.persistence.PersistenceListener;
import com.opensymphony.oscache.util.FastCronParser;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.event.EventListenerList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Cache
implements Serializable {
    public static final String NESTED_EVENT = "NESTED";
    private static final transient Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$base$Cache == null ? (class$com$opensymphony$oscache$base$Cache = Cache.class$("com.opensymphony.oscache.base.Cache")) : class$com$opensymphony$oscache$base$Cache));
    protected EventListenerList listenerList = new EventListenerList();
    private AbstractConcurrentReadCache cacheMap = null;
    private Date flushDateTime = null;
    private Map updateStates = new HashMap();
    private boolean blocking = false;
    static /* synthetic */ Class class$com$opensymphony$oscache$base$Cache;
    static /* synthetic */ Class class$com$opensymphony$oscache$base$events$CacheEventListener;
    static /* synthetic */ Class class$com$opensymphony$oscache$base$events$CacheEntryEventListener;
    static /* synthetic */ Class class$com$opensymphony$oscache$base$events$CacheMapAccessEventListener;

    public Cache(boolean useMemoryCaching, boolean unlimitedDiskCache, boolean overflowPersistence) {
        this(useMemoryCaching, unlimitedDiskCache, overflowPersistence, false, null, 0);
    }

    public Cache(boolean useMemoryCaching, boolean unlimitedDiskCache, boolean overflowPersistence, boolean blocking, String algorithmClass, int capacity) {
        if (algorithmClass != null && algorithmClass.length() > 0 && capacity > 0) {
            try {
                this.cacheMap = (AbstractConcurrentReadCache)Class.forName(algorithmClass).newInstance();
                this.cacheMap.setMaxEntries(capacity);
            }
            catch (Exception e) {
                log.error((Object)("Invalid class name for cache algorithm class. " + e.toString()));
            }
        }
        if (this.cacheMap == null) {
            this.cacheMap = capacity > 0 ? new LRUCache(capacity) : new UnlimitedCache();
        }
        this.cacheMap.setUnlimitedDiskCache(unlimitedDiskCache);
        this.cacheMap.setOverflowPersistence(overflowPersistence);
        this.cacheMap.setMemoryCaching(useMemoryCaching);
        this.blocking = blocking;
    }

    public void setCapacity(int capacity) {
        this.cacheMap.setMaxEntries(capacity);
    }

    public boolean isFlushed(CacheEntry cacheEntry) {
        if (this.flushDateTime != null) {
            long lastUpdate = cacheEntry.getLastUpdate();
            return this.flushDateTime.getTime() >= lastUpdate;
        }
        return false;
    }

    public Object getFromCache(String key) throws NeedsRefreshException {
        return this.getFromCache(key, -1, null);
    }

    public Object getFromCache(String key, int refreshPeriod) throws NeedsRefreshException {
        return this.getFromCache(key, refreshPeriod, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object getFromCache(String key, int refreshPeriod, String cronExpiry) throws NeedsRefreshException {
        CacheEntry cacheEntry = this.getCacheEntry(key, null, null);
        Object content = cacheEntry.getContent();
        CacheMapAccessEventType accessEventType = CacheMapAccessEventType.HIT;
        boolean reload = false;
        if (this.isStale(cacheEntry, refreshPeriod, cronExpiry)) {
            EntryUpdateState updateState = this.getUpdateState(key);
            try {
                EntryUpdateState entryUpdateState = updateState;
                synchronized (entryUpdateState) {
                    if (updateState.isAwaitingUpdate() || updateState.isCancelled()) {
                        updateState.startUpdate();
                        accessEventType = cacheEntry.isNew() ? CacheMapAccessEventType.MISS : CacheMapAccessEventType.STALE_HIT;
                    } else if (updateState.isUpdating()) {
                        if (cacheEntry.isNew() || this.blocking) {
                            do {
                                try {
                                    updateState.wait();
                                }
                                catch (InterruptedException e) {
                                    // empty catch block
                                }
                            } while (updateState.isUpdating());
                            if (updateState.isCancelled()) {
                                updateState.startUpdate();
                                accessEventType = cacheEntry.isNew() ? CacheMapAccessEventType.MISS : CacheMapAccessEventType.STALE_HIT;
                            } else if (updateState.isComplete()) {
                                reload = true;
                            } else {
                                log.error((Object)("Invalid update state for cache entry " + key));
                            }
                        }
                    } else {
                        reload = true;
                    }
                }
            }
            finally {
                this.releaseUpdateState(updateState, key);
            }
        }
        if (reload) {
            cacheEntry = (CacheEntry)this.cacheMap.get(key);
            if (cacheEntry != null) {
                content = cacheEntry.getContent();
            } else {
                log.error((Object)"Could not reload cache entry after waiting for it to be rebuilt");
            }
        }
        this.dispatchCacheMapAccessEvent(accessEventType, cacheEntry, null);
        if (accessEventType != CacheMapAccessEventType.HIT) {
            throw new NeedsRefreshException(content);
        }
        return content;
    }

    public void setPersistenceListener(PersistenceListener listener) {
        this.cacheMap.setPersistenceListener(listener);
    }

    public PersistenceListener getPersistenceListener() {
        return this.cacheMap.getPersistenceListener();
    }

    public void addCacheEventListener(CacheEventListener listener, Class clazz) {
        if ((class$com$opensymphony$oscache$base$events$CacheEventListener == null ? (class$com$opensymphony$oscache$base$events$CacheEventListener = Cache.class$("com.opensymphony.oscache.base.events.CacheEventListener")) : class$com$opensymphony$oscache$base$events$CacheEventListener).isAssignableFrom(clazz)) {
            this.listenerList.add(clazz, listener);
        } else {
            log.error((Object)("The class '" + clazz.getName() + "' is not a CacheEventListener. Ignoring this listener."));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cancelUpdate(String key) {
        if (key != null) {
            Map map = this.updateStates;
            synchronized (map) {
                EntryUpdateState state = (EntryUpdateState)this.updateStates.get(key);
                if (state != null) {
                    EntryUpdateState entryUpdateState = state;
                    synchronized (entryUpdateState) {
                        int usageCounter = state.cancelUpdate();
                        state.notify();
                        this.checkEntryStateUpdateUsage(key, state, usageCounter);
                    }
                } else if (log.isErrorEnabled()) {
                    log.error((Object)("internal error: expected to get a state from key [" + key + "]"));
                }
            }
        }
    }

    private void checkEntryStateUpdateUsage(String key, EntryUpdateState state, int usageCounter) {
        EntryUpdateState removedState;
        if (usageCounter == 0 && state != (removedState = (EntryUpdateState)this.updateStates.remove(key)) && log.isErrorEnabled()) {
            log.error((Object)("internal error: removed state [" + removedState + "] from key [" + key + "] whereas we expected [" + state + "]"));
            try {
                throw new Exception("states not equal");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void flushAll(Date date) {
        this.flushAll(date, null);
    }

    public void flushAll(Date date, String origin) {
        this.flushDateTime = date;
        if (this.listenerList.getListenerCount() > 0) {
            this.dispatchCachewideEvent(CachewideEventType.CACHE_FLUSHED, date, origin);
        }
    }

    public void flushEntry(String key) {
        this.flushEntry(key, null);
    }

    public void flushEntry(String key, String origin) {
        this.flushEntry(this.getCacheEntry(key, null, origin), origin);
    }

    public void flushGroup(String group) {
        this.flushGroup(group, null);
    }

    public void flushGroup(String group, String origin) {
        Set groupEntries = this.cacheMap.getGroup(group);
        if (groupEntries != null) {
            Iterator itr = groupEntries.iterator();
            while (itr.hasNext()) {
                String key = (String)itr.next();
                CacheEntry entry = (CacheEntry)this.cacheMap.get(key);
                if (entry == null || entry.needsRefresh(-1)) continue;
                this.flushEntry(entry, NESTED_EVENT);
            }
        }
        if (this.listenerList.getListenerCount() > 0) {
            this.dispatchCacheGroupEvent(CacheEntryEventType.GROUP_FLUSHED, group, origin);
        }
    }

    public void flushPattern(String pattern) {
        this.flushPattern(pattern, null);
    }

    public void flushPattern(String pattern, String origin) {
        if (pattern != null && pattern.length() > 0) {
            String key = null;
            CacheEntry entry = null;
            Iterator itr = this.cacheMap.keySet().iterator();
            while (itr.hasNext()) {
                key = (String)itr.next();
                if (key.indexOf(pattern) < 0 || (entry = (CacheEntry)this.cacheMap.get(key)) == null) continue;
                this.flushEntry(entry, origin);
            }
            if (this.listenerList.getListenerCount() > 0) {
                this.dispatchCachePatternEvent(CacheEntryEventType.PATTERN_FLUSHED, pattern, origin);
            }
        }
    }

    public void putInCache(String key, Object content) {
        this.putInCache(key, content, null, null, null);
    }

    public void putInCache(String key, Object content, EntryRefreshPolicy policy) {
        this.putInCache(key, content, null, policy, null);
    }

    public void putInCache(String key, Object content, String[] groups) {
        this.putInCache(key, content, groups, null, null);
    }

    public void putInCache(String key, Object content, String[] groups, EntryRefreshPolicy policy, String origin) {
        CacheEntry cacheEntry = this.getCacheEntry(key, policy, origin);
        boolean isNewEntry = cacheEntry.isNew();
        if (!isNewEntry) {
            cacheEntry = new CacheEntry(key, policy);
        }
        cacheEntry.setContent(content);
        cacheEntry.setGroups(groups);
        this.cacheMap.put(key, cacheEntry);
        this.completeUpdate(key);
        if (this.listenerList.getListenerCount() > 0) {
            CacheEntryEvent event = new CacheEntryEvent(this, cacheEntry, origin);
            if (isNewEntry) {
                this.dispatchCacheEntryEvent(CacheEntryEventType.ENTRY_ADDED, event);
            } else {
                this.dispatchCacheEntryEvent(CacheEntryEventType.ENTRY_UPDATED, event);
            }
        }
    }

    public void removeCacheEventListener(CacheEventListener listener, Class clazz) {
        this.listenerList.remove(clazz, listener);
    }

    protected CacheEntry getCacheEntry(String key, EntryRefreshPolicy policy, String origin) {
        CacheEntry cacheEntry = null;
        if (key == null || key.length() == 0) {
            throw new IllegalArgumentException("getCacheEntry called with an empty or null key");
        }
        cacheEntry = (CacheEntry)this.cacheMap.get(key);
        if (cacheEntry == null) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("No cache entry exists for key='" + key + "', creating"));
            }
            cacheEntry = new CacheEntry(key, policy);
        }
        return cacheEntry;
    }

    protected boolean isStale(CacheEntry cacheEntry, int refreshPeriod, String cronExpiry) {
        boolean result;
        boolean bl = result = cacheEntry.needsRefresh(refreshPeriod) || this.isFlushed(cacheEntry);
        if (cronExpiry != null && cronExpiry.length() > 0) {
            try {
                FastCronParser parser = new FastCronParser(cronExpiry);
                result = result || parser.hasMoreRecentMatch(cacheEntry.getLastUpdate());
            }
            catch (ParseException e) {
                log.warn((Object)e);
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected EntryUpdateState getUpdateState(String key) {
        EntryUpdateState updateState;
        Map map = this.updateStates;
        synchronized (map) {
            updateState = (EntryUpdateState)this.updateStates.get(key);
            if (updateState == null) {
                updateState = new EntryUpdateState();
                this.updateStates.put(key, updateState);
            } else {
                updateState.incrementUsageCounter();
            }
        }
        return updateState;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void releaseUpdateState(EntryUpdateState state, String key) {
        Map map = this.updateStates;
        synchronized (map) {
            int usageCounter = state.decrementUsageCounter();
            this.checkEntryStateUpdateUsage(key, state, usageCounter);
        }
    }

    protected void clear() {
        this.cacheMap.clear();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void completeUpdate(String key) {
        Map map = this.updateStates;
        synchronized (map) {
            EntryUpdateState state = (EntryUpdateState)this.updateStates.get(key);
            if (state != null) {
                EntryUpdateState entryUpdateState = state;
                synchronized (entryUpdateState) {
                    int usageCounter = state.completeUpdate();
                    state.notifyAll();
                    this.checkEntryStateUpdateUsage(key, state, usageCounter);
                }
            }
        }
    }

    public void removeEntry(String key) {
        this.removeEntry(key, null);
    }

    protected void removeEntry(String key, String origin) {
        CacheEntry cacheEntry = (CacheEntry)this.cacheMap.get(key);
        this.cacheMap.remove(key);
        if (this.listenerList.getListenerCount() > 0) {
            CacheEntryEvent event = new CacheEntryEvent(this, cacheEntry, origin);
            this.dispatchCacheEntryEvent(CacheEntryEventType.ENTRY_REMOVED, event);
        }
    }

    private void dispatchCacheEntryEvent(CacheEntryEventType eventType, CacheEntryEvent event) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != (class$com$opensymphony$oscache$base$events$CacheEntryEventListener == null ? Cache.class$("com.opensymphony.oscache.base.events.CacheEntryEventListener") : class$com$opensymphony$oscache$base$events$CacheEntryEventListener)) continue;
            if (eventType.equals(CacheEntryEventType.ENTRY_ADDED)) {
                ((CacheEntryEventListener)listeners[i + 1]).cacheEntryAdded(event);
                continue;
            }
            if (eventType.equals(CacheEntryEventType.ENTRY_UPDATED)) {
                ((CacheEntryEventListener)listeners[i + 1]).cacheEntryUpdated(event);
                continue;
            }
            if (eventType.equals(CacheEntryEventType.ENTRY_FLUSHED)) {
                ((CacheEntryEventListener)listeners[i + 1]).cacheEntryFlushed(event);
                continue;
            }
            if (!eventType.equals(CacheEntryEventType.ENTRY_REMOVED)) continue;
            ((CacheEntryEventListener)listeners[i + 1]).cacheEntryRemoved(event);
        }
    }

    private void dispatchCacheGroupEvent(CacheEntryEventType eventType, String group, String origin) {
        CacheGroupEvent event = new CacheGroupEvent(this, group, origin);
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != (class$com$opensymphony$oscache$base$events$CacheEntryEventListener == null ? Cache.class$("com.opensymphony.oscache.base.events.CacheEntryEventListener") : class$com$opensymphony$oscache$base$events$CacheEntryEventListener) || !eventType.equals(CacheEntryEventType.GROUP_FLUSHED)) continue;
            ((CacheEntryEventListener)listeners[i + 1]).cacheGroupFlushed(event);
        }
    }

    private void dispatchCacheMapAccessEvent(CacheMapAccessEventType eventType, CacheEntry entry, String origin) {
        CacheMapAccessEvent event = new CacheMapAccessEvent(eventType, entry, origin);
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != (class$com$opensymphony$oscache$base$events$CacheMapAccessEventListener == null ? Cache.class$("com.opensymphony.oscache.base.events.CacheMapAccessEventListener") : class$com$opensymphony$oscache$base$events$CacheMapAccessEventListener)) continue;
            ((CacheMapAccessEventListener)listeners[i + 1]).accessed(event);
        }
    }

    private void dispatchCachePatternEvent(CacheEntryEventType eventType, String pattern, String origin) {
        CachePatternEvent event = new CachePatternEvent(this, pattern, origin);
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != (class$com$opensymphony$oscache$base$events$CacheEntryEventListener == null ? Cache.class$("com.opensymphony.oscache.base.events.CacheEntryEventListener") : class$com$opensymphony$oscache$base$events$CacheEntryEventListener) || !eventType.equals(CacheEntryEventType.PATTERN_FLUSHED)) continue;
            ((CacheEntryEventListener)listeners[i + 1]).cachePatternFlushed(event);
        }
    }

    private void dispatchCachewideEvent(CachewideEventType eventType, Date date, String origin) {
        CachewideEvent event = new CachewideEvent(this, date, origin);
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] != (class$com$opensymphony$oscache$base$events$CacheEntryEventListener == null ? Cache.class$("com.opensymphony.oscache.base.events.CacheEntryEventListener") : class$com$opensymphony$oscache$base$events$CacheEntryEventListener) || !eventType.equals(CachewideEventType.CACHE_FLUSHED)) continue;
            ((CacheEntryEventListener)listeners[i + 1]).cacheFlushed(event);
        }
    }

    private void flushEntry(CacheEntry entry, String origin) {
        String key = entry.getKey();
        entry.flush();
        if (!entry.isNew()) {
            this.cacheMap.put(key, entry);
        }
        if (this.listenerList.getListenerCount() > 0) {
            CacheEntryEvent event = new CacheEntryEvent(this, entry, origin);
            this.dispatchCacheEntryEvent(CacheEntryEventType.ENTRY_FLUSHED, event);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected int getNbUpdateState() {
        Map map = this.updateStates;
        synchronized (map) {
            return this.updateStates.size();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNbEntries() {
        AbstractConcurrentReadCache abstractConcurrentReadCache = this.cacheMap;
        synchronized (abstractConcurrentReadCache) {
            return this.cacheMap.size();
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

