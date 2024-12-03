/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpSessionBindingEvent
 *  javax.servlet.http.HttpSessionBindingListener
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.web;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.CacheEntry;
import com.opensymphony.oscache.web.ServletCacheAdministrator;
import java.io.Serializable;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class ServletCache
extends Cache
implements HttpSessionBindingListener,
Serializable {
    private static final transient Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$web$ServletCache == null ? (class$com$opensymphony$oscache$web$ServletCache = ServletCache.class$("com.opensymphony.oscache.web.ServletCache")) : class$com$opensymphony$oscache$web$ServletCache));
    private ServletCacheAdministrator admin;
    private int scope;
    static /* synthetic */ Class class$com$opensymphony$oscache$web$ServletCache;

    public ServletCache(ServletCacheAdministrator admin, int scope) {
        super(admin.isMemoryCaching(), admin.isUnlimitedDiskCache(), admin.isOverflowPersistence());
        this.setScope(scope);
        this.admin = admin;
    }

    public ServletCache(ServletCacheAdministrator admin, String algorithmClass, int limit, int scope) {
        super(admin.isMemoryCaching(), admin.isUnlimitedDiskCache(), admin.isOverflowPersistence(), admin.isBlocking(), algorithmClass, limit);
        this.setScope(scope);
        this.admin = admin;
    }

    public int getScope() {
        return this.scope;
    }

    private void setScope(int scope) {
        this.scope = scope;
    }

    public void valueBound(HttpSessionBindingEvent event) {
    }

    public void valueUnbound(HttpSessionBindingEvent event) {
        if (log.isInfoEnabled()) {
            log.info((Object)("[Cache] Unbound from session " + event.getSession().getId() + " using name " + event.getName()));
        }
        this.admin.finalizeListeners(this);
        this.clear();
    }

    protected boolean isStale(CacheEntry cacheEntry, int refreshPeriod, String cronExpiry) {
        return super.isStale(cacheEntry, refreshPeriod, cronExpiry) || this.admin.isScopeFlushed(cacheEntry, this.scope);
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

