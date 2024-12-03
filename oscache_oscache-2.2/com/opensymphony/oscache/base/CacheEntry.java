/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.oscache.base;

import com.opensymphony.oscache.base.EntryRefreshPolicy;
import com.opensymphony.oscache.web.filter.ResponseContent;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class CacheEntry
implements Serializable {
    private static final byte NOT_YET = -1;
    public static final int INDEFINITE_EXPIRY = -1;
    private EntryRefreshPolicy policy = null;
    private Object content = null;
    private Set groups = null;
    private String key;
    private boolean wasFlushed = false;
    private long created = -1L;
    private long lastUpdate = -1L;
    static /* synthetic */ Class class$java$lang$String;

    public CacheEntry(String key) {
        this(key, null);
    }

    public CacheEntry(String key, EntryRefreshPolicy policy) {
        this(key, policy, null);
    }

    public CacheEntry(String key, EntryRefreshPolicy policy, String[] groups) {
        this.key = key;
        if (groups != null) {
            this.groups = new HashSet(groups.length);
            for (int i = 0; i < groups.length; ++i) {
                this.groups.add(groups[i]);
            }
        }
        this.policy = policy;
        this.created = System.currentTimeMillis();
    }

    public synchronized void setContent(Object value) {
        this.content = value;
        this.lastUpdate = System.currentTimeMillis();
        this.wasFlushed = false;
    }

    public Object getContent() {
        return this.content;
    }

    public long getCreated() {
        return this.created;
    }

    public synchronized void setGroups(String[] groups) {
        if (groups != null) {
            this.groups = new HashSet(groups.length);
            for (int i = 0; i < groups.length; ++i) {
                this.groups.add(groups[i]);
            }
        } else {
            this.groups = null;
        }
        this.lastUpdate = System.currentTimeMillis();
    }

    public void setGroups(Collection groups) {
        this.groups = groups != null ? new HashSet(groups) : null;
        this.lastUpdate = System.currentTimeMillis();
    }

    public Set getGroups() {
        return this.groups;
    }

    public String getKey() {
        return this.key;
    }

    public void setLastUpdate(long update) {
        this.lastUpdate = update;
    }

    public long getLastUpdate() {
        return this.lastUpdate;
    }

    public boolean isNew() {
        return this.lastUpdate == -1L;
    }

    public int getSize() {
        int size = this.key.length() * 2 + 4;
        if (this.content.getClass() == (class$java$lang$String == null ? (class$java$lang$String = CacheEntry.class$("java.lang.String")) : class$java$lang$String)) {
            size += this.content.toString().length() * 2 + 4;
        } else if (this.content instanceof ResponseContent) {
            size += ((ResponseContent)this.content).getSize();
        } else {
            return -1;
        }
        return size + 17;
    }

    public void flush() {
        this.wasFlushed = true;
    }

    public boolean needsRefresh(int refreshPeriod) {
        boolean needsRefresh = this.lastUpdate == -1L ? true : (this.wasFlushed ? true : (refreshPeriod == 0 ? true : (this.policy != null ? this.policy.needsRefresh(this) : refreshPeriod >= 0 && System.currentTimeMillis() >= this.lastUpdate + (long)refreshPeriod * 1000L)));
        return needsRefresh;
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

