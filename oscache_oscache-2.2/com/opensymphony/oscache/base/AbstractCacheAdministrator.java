/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.base;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.FinalizationException;
import com.opensymphony.oscache.base.InitializationException;
import com.opensymphony.oscache.base.LifecycleAware;
import com.opensymphony.oscache.base.events.CacheEntryEventListener;
import com.opensymphony.oscache.base.events.CacheEventListener;
import com.opensymphony.oscache.base.events.CacheMapAccessEventListener;
import com.opensymphony.oscache.base.persistence.PersistenceListener;
import com.opensymphony.oscache.util.StringUtil;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;
import javax.swing.event.EventListenerList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractCacheAdministrator
implements Serializable {
    private static final transient Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$base$AbstractCacheAdministrator == null ? (class$com$opensymphony$oscache$base$AbstractCacheAdministrator = AbstractCacheAdministrator.class$("com.opensymphony.oscache.base.AbstractCacheAdministrator")) : class$com$opensymphony$oscache$base$AbstractCacheAdministrator));
    public static final String CACHE_MEMORY_KEY = "cache.memory";
    public static final String CACHE_CAPACITY_KEY = "cache.capacity";
    public static final String CACHE_ALGORITHM_KEY = "cache.algorithm";
    public static final String CACHE_DISK_UNLIMITED_KEY = "cache.unlimited.disk";
    public static final String CACHE_BLOCKING_KEY = "cache.blocking";
    public static final String PERSISTENCE_CLASS_KEY = "cache.persistence.class";
    public static final String CACHE_PERSISTENCE_OVERFLOW_KEY = "cache.persistence.overflow.only";
    public static final String CACHE_ENTRY_EVENT_LISTENERS_KEY = "cache.event.listeners";
    protected Config config = null;
    protected EventListenerList listenerList = new EventListenerList();
    protected String algorithmClass = null;
    protected int cacheCapacity = -1;
    private boolean blocking = false;
    private boolean memoryCaching = true;
    private boolean overflowPersistence;
    private boolean unlimitedDiskCache;
    static /* synthetic */ Class class$com$opensymphony$oscache$base$AbstractCacheAdministrator;
    static /* synthetic */ Class class$com$opensymphony$oscache$base$events$CacheEventListener;
    static /* synthetic */ Class class$com$opensymphony$oscache$base$events$CacheEntryEventListener;
    static /* synthetic */ Class class$com$opensymphony$oscache$base$events$CacheMapAccessEventListener;

    protected AbstractCacheAdministrator() {
        this(null);
    }

    protected AbstractCacheAdministrator(Properties p) {
        this.loadProps(p);
        this.initCacheParameters();
        if (log.isDebugEnabled()) {
            log.debug((Object)"Constructed AbstractCacheAdministrator()");
        }
    }

    public void setAlgorithmClass(String newAlgorithmClass) {
        this.algorithmClass = newAlgorithmClass;
    }

    public boolean isBlocking() {
        return this.blocking;
    }

    protected void setCacheCapacity(int newCacheCapacity) {
        this.cacheCapacity = newCacheCapacity;
    }

    public boolean isMemoryCaching() {
        return this.memoryCaching;
    }

    public String getProperty(String key) {
        return this.config.getProperty(key);
    }

    public boolean isUnlimitedDiskCache() {
        return this.unlimitedDiskCache;
    }

    public boolean isOverflowPersistence() {
        return this.overflowPersistence;
    }

    public void setOverflowPersistence(boolean overflowPersistence) {
        this.overflowPersistence = overflowPersistence;
    }

    protected CacheEventListener[] getCacheEventListeners() {
        CacheEventListener[] listeners = null;
        List classes = StringUtil.split(this.config.getProperty(CACHE_ENTRY_EVENT_LISTENERS_KEY), ',');
        listeners = new CacheEventListener[classes.size()];
        for (int i = 0; i < classes.size(); ++i) {
            String className = (String)classes.get(i);
            try {
                Class<?> clazz = Class.forName(className);
                if (!(class$com$opensymphony$oscache$base$events$CacheEventListener == null ? AbstractCacheAdministrator.class$("com.opensymphony.oscache.base.events.CacheEventListener") : class$com$opensymphony$oscache$base$events$CacheEventListener).isAssignableFrom(clazz)) {
                    log.error((Object)("Specified listener class '" + className + "' does not implement CacheEventListener. Ignoring this listener."));
                    continue;
                }
                listeners[i] = (CacheEventListener)clazz.newInstance();
                continue;
            }
            catch (ClassNotFoundException e) {
                log.error((Object)("CacheEventListener class '" + className + "' not found. Ignoring this listener."), (Throwable)e);
                continue;
            }
            catch (InstantiationException e) {
                log.error((Object)("CacheEventListener class '" + className + "' could not be instantiated because it is not a concrete class. Ignoring this listener."), (Throwable)e);
                continue;
            }
            catch (IllegalAccessException e) {
                log.error((Object)("CacheEventListener class '" + className + "' could not be instantiated because it is not public. Ignoring this listener."), (Throwable)e);
            }
        }
        return listeners;
    }

    protected Cache setPersistenceListener(Cache cache) {
        String persistenceClassname = this.config.getProperty(PERSISTENCE_CLASS_KEY);
        try {
            Class<?> clazz = Class.forName(persistenceClassname);
            PersistenceListener persistenceListener = (PersistenceListener)clazz.newInstance();
            cache.setPersistenceListener(persistenceListener.configure(this.config));
        }
        catch (ClassNotFoundException e) {
            log.error((Object)("PersistenceListener class '" + persistenceClassname + "' not found. Check your configuration."), (Throwable)e);
        }
        catch (Exception e) {
            log.error((Object)("Error instantiating class '" + persistenceClassname + "'"), (Throwable)e);
        }
        return cache;
    }

    protected Cache configureStandardListeners(Cache cache) {
        if (this.config.getProperty(PERSISTENCE_CLASS_KEY) != null) {
            cache = this.setPersistenceListener(cache);
        }
        if (this.config.getProperty(CACHE_ENTRY_EVENT_LISTENERS_KEY) != null) {
            CacheEventListener[] listeners = this.getCacheEventListeners();
            for (int i = 0; i < listeners.length; ++i) {
                if (listeners[i] instanceof LifecycleAware) {
                    try {
                        ((LifecycleAware)((Object)listeners[i])).initialize(cache, this.config);
                    }
                    catch (InitializationException e) {
                        log.error((Object)("Could not initialize listener '" + listeners[i].getClass().getName() + "'. Listener ignored."), (Throwable)e);
                        continue;
                    }
                }
                if (listeners[i] instanceof CacheEntryEventListener) {
                    cache.addCacheEventListener(listeners[i], class$com$opensymphony$oscache$base$events$CacheEntryEventListener == null ? AbstractCacheAdministrator.class$("com.opensymphony.oscache.base.events.CacheEntryEventListener") : class$com$opensymphony$oscache$base$events$CacheEntryEventListener);
                }
                if (!(listeners[i] instanceof CacheMapAccessEventListener)) continue;
                cache.addCacheEventListener(listeners[i], class$com$opensymphony$oscache$base$events$CacheMapAccessEventListener == null ? AbstractCacheAdministrator.class$("com.opensymphony.oscache.base.events.CacheMapAccessEventListener") : class$com$opensymphony$oscache$base$events$CacheMapAccessEventListener);
            }
        }
        return cache;
    }

    protected void finalizeListeners(Cache cache) {
        if (cache == null) {
            return;
        }
        Object[] listeners = cache.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (!(listeners[i + 1] instanceof LifecycleAware)) continue;
            try {
                ((LifecycleAware)listeners[i + 1]).finialize();
                continue;
            }
            catch (FinalizationException e) {
                log.error((Object)"Listener could not be finalized", (Throwable)e);
            }
        }
    }

    private void initCacheParameters() {
        this.algorithmClass = this.getProperty(CACHE_ALGORITHM_KEY);
        this.blocking = "true".equalsIgnoreCase(this.getProperty(CACHE_BLOCKING_KEY));
        String cacheMemoryStr = this.getProperty(CACHE_MEMORY_KEY);
        if (cacheMemoryStr != null && cacheMemoryStr.equalsIgnoreCase("false")) {
            this.memoryCaching = false;
        }
        this.unlimitedDiskCache = Boolean.valueOf(this.config.getProperty(CACHE_DISK_UNLIMITED_KEY));
        this.overflowPersistence = Boolean.valueOf(this.config.getProperty(CACHE_PERSISTENCE_OVERFLOW_KEY));
        String cacheSize = this.getProperty(CACHE_CAPACITY_KEY);
        try {
            if (cacheSize != null && cacheSize.length() > 0) {
                this.cacheCapacity = Integer.parseInt(cacheSize);
            }
        }
        catch (NumberFormatException e) {
            log.error((Object)("The value supplied for the cache capacity, '" + cacheSize + "', is not a valid number. The cache capacity setting is being ignored."));
        }
    }

    private void loadProps(Properties p) {
        this.config = new Config(p);
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

