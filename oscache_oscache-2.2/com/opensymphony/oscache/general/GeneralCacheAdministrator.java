/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.general;

import com.opensymphony.oscache.base.AbstractCacheAdministrator;
import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.EntryRefreshPolicy;
import com.opensymphony.oscache.base.NeedsRefreshException;
import java.util.Date;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GeneralCacheAdministrator
extends AbstractCacheAdministrator {
    private static final transient Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$general$GeneralCacheAdministrator == null ? (class$com$opensymphony$oscache$general$GeneralCacheAdministrator = GeneralCacheAdministrator.class$("com.opensymphony.oscache.general.GeneralCacheAdministrator")) : class$com$opensymphony$oscache$general$GeneralCacheAdministrator));
    private Cache applicationCache = null;
    static /* synthetic */ Class class$com$opensymphony$oscache$general$GeneralCacheAdministrator;

    public GeneralCacheAdministrator() {
        this(null);
    }

    public GeneralCacheAdministrator(Properties p) {
        super(p);
        log.info((Object)"Constructed GeneralCacheAdministrator()");
        this.createCache();
    }

    public Cache getCache() {
        return this.applicationCache;
    }

    public void removeEntry(String key) {
        this.getCache().removeEntry(key);
    }

    public Object getFromCache(String key) throws NeedsRefreshException {
        return this.getCache().getFromCache(key);
    }

    public Object getFromCache(String key, int refreshPeriod) throws NeedsRefreshException {
        return this.getCache().getFromCache(key, refreshPeriod);
    }

    public Object getFromCache(String key, int refreshPeriod, String cronExpression) throws NeedsRefreshException {
        return this.getCache().getFromCache(key, refreshPeriod, cronExpression);
    }

    public void cancelUpdate(String key) {
        this.getCache().cancelUpdate(key);
    }

    public void destroy() {
        this.finalizeListeners(this.applicationCache);
    }

    public void flushAll() {
        this.getCache().flushAll(new Date());
    }

    public void flushAll(Date date) {
        this.getCache().flushAll(date);
    }

    public void flushEntry(String key) {
        this.getCache().flushEntry(key);
    }

    public void flushGroup(String group) {
        this.getCache().flushGroup(group);
    }

    public void flushPattern(String pattern) {
        this.getCache().flushPattern(pattern);
    }

    public void putInCache(String key, Object content, EntryRefreshPolicy policy) {
        Cache cache = this.getCache();
        cache.putInCache(key, content, policy);
    }

    public void putInCache(String key, Object content) {
        this.putInCache(key, content, (EntryRefreshPolicy)null);
    }

    public void putInCache(String key, Object content, String[] groups) {
        this.getCache().putInCache(key, content, groups);
    }

    public void putInCache(String key, Object content, String[] groups, EntryRefreshPolicy policy) {
        this.getCache().putInCache(key, content, groups, policy, null);
    }

    public void setCacheCapacity(int capacity) {
        super.setCacheCapacity(capacity);
        this.getCache().setCapacity(capacity);
    }

    private void createCache() {
        log.info((Object)"Creating new cache");
        this.applicationCache = new Cache(this.isMemoryCaching(), this.isUnlimitedDiskCache(), this.isOverflowPersistence(), this.isBlocking(), this.algorithmClass, this.cacheCapacity);
        this.configureStandardListeners(this.applicationCache);
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

