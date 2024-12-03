/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.collections.SequencedHashMap
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.base.algorithm;

import com.opensymphony.oscache.base.algorithm.AbstractConcurrentReadCache;
import com.opensymphony.oscache.util.ClassLoaderUtil;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.SequencedHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LRUCache
extends AbstractConcurrentReadCache {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$base$algorithm$LRUCache == null ? (class$com$opensymphony$oscache$base$algorithm$LRUCache = LRUCache.class$("com.opensymphony.oscache.base.algorithm.LRUCache")) : class$com$opensymphony$oscache$base$algorithm$LRUCache));
    private Collection list;
    private Map map;
    private boolean isList = false;
    private boolean isMap = false;
    private boolean isSet = false;
    private volatile boolean removeInProgress = false;
    static /* synthetic */ Class class$com$opensymphony$oscache$base$algorithm$LRUCache;

    public LRUCache() {
        try {
            ClassLoaderUtil.loadClass("java.util.LinkedHashSet", this.getClass());
            this.list = new LinkedHashSet();
            this.isSet = true;
        }
        catch (ClassNotFoundException e) {
            try {
                ClassLoaderUtil.loadClass("org.apache.commons.collections.SequencedHashMap", this.getClass());
                this.map = new SequencedHashMap();
                this.isMap = true;
            }
            catch (ClassNotFoundException e1) {
                log.warn((Object)"When using the LRUCache under JRE 1.3.x, commons-collections.jar should be added to your classpath to increase OSCache's performance.");
                this.list = new LinkedList();
                this.isList = true;
            }
        }
    }

    public LRUCache(int capacity) {
        this();
        this.maxEntries = capacity;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void itemRetrieved(Object key) {
        while (this.removeInProgress) {
            try {
                Thread.sleep(5L);
            }
            catch (InterruptedException interruptedException) {}
        }
        if (this.isMap) {
            Map map = this.map;
            synchronized (map) {
                this.map.remove(key);
                this.map.put(key, Boolean.TRUE);
            }
        }
        Collection collection = this.list;
        synchronized (collection) {
            this.list.remove(key);
            this.list.add(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void itemPut(Object key) {
        if (this.isMap) {
            Map map = this.map;
            synchronized (map) {
                this.map.remove(key);
                this.map.put(key, Boolean.TRUE);
            }
        }
        Collection collection = this.list;
        synchronized (collection) {
            this.list.remove(key);
            this.list.add(key);
        }
    }

    protected Object removeItem() {
        Object toRemove;
        this.removeInProgress = true;
        try {
            toRemove = this.removeFirst();
        }
        catch (Exception e) {
            do {
                try {
                    Thread.sleep(5L);
                }
                catch (InterruptedException interruptedException) {
                    // empty catch block
                }
            } while (!this.isMap ? this.list.size() == 0 : this.map.size() == 0);
            toRemove = this.removeFirst();
        }
        this.removeInProgress = false;
        return toRemove;
    }

    protected void itemRemoved(Object key) {
        if (this.isMap) {
            this.map.remove(key);
        } else {
            this.list.remove(key);
        }
    }

    private Object removeFirst() {
        Object toRemove;
        if (this.isSet) {
            Iterator it = this.list.iterator();
            toRemove = it.next();
            it.remove();
        } else if (this.isMap) {
            toRemove = ((SequencedHashMap)this.map).getFirstKey();
            this.map.remove(toRemove);
        } else {
            toRemove = ((List)this.list).remove(0);
        }
        return toRemove;
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

