/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.opensymphony.oscache.plugins.clustersupport;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.base.Config;
import com.opensymphony.oscache.base.InitializationException;
import com.opensymphony.oscache.base.LifecycleAware;
import com.opensymphony.oscache.base.events.CacheEntryEvent;
import com.opensymphony.oscache.base.events.CacheEntryEventListener;
import com.opensymphony.oscache.base.events.CacheGroupEvent;
import com.opensymphony.oscache.base.events.CachePatternEvent;
import com.opensymphony.oscache.base.events.CachewideEvent;
import com.opensymphony.oscache.plugins.clustersupport.ClusterNotification;
import java.io.Serializable;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractBroadcastingListener
implements CacheEntryEventListener,
LifecycleAware {
    private static final Log log = LogFactory.getLog((Class)(class$com$opensymphony$oscache$plugins$clustersupport$AbstractBroadcastingListener == null ? (class$com$opensymphony$oscache$plugins$clustersupport$AbstractBroadcastingListener = AbstractBroadcastingListener.class$("com.opensymphony.oscache.plugins.clustersupport.AbstractBroadcastingListener")) : class$com$opensymphony$oscache$plugins$clustersupport$AbstractBroadcastingListener));
    protected static final String CLUSTER_ORIGIN = "CLUSTER";
    protected Cache cache = null;
    static /* synthetic */ Class class$com$opensymphony$oscache$plugins$clustersupport$AbstractBroadcastingListener;

    public AbstractBroadcastingListener() {
        if (log.isInfoEnabled()) {
            log.info((Object)"AbstractBroadcastingListener registered");
        }
    }

    public void cacheEntryFlushed(CacheEntryEvent event) {
        if (!"NESTED".equals(event.getOrigin()) && !CLUSTER_ORIGIN.equals(event.getOrigin())) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("cacheEntryFlushed called (" + event + ")"));
            }
            this.sendNotification(new ClusterNotification(1, (Serializable)((Object)event.getKey())));
        }
    }

    public void cacheGroupFlushed(CacheGroupEvent event) {
        if (!"NESTED".equals(event.getOrigin()) && !CLUSTER_ORIGIN.equals(event.getOrigin())) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("cacheGroupFushed called (" + event + ")"));
            }
            this.sendNotification(new ClusterNotification(2, (Serializable)((Object)event.getGroup())));
        }
    }

    public void cachePatternFlushed(CachePatternEvent event) {
        if (!"NESTED".equals(event.getOrigin()) && !CLUSTER_ORIGIN.equals(event.getOrigin())) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("cachePatternFushed called (" + event + ")"));
            }
            this.sendNotification(new ClusterNotification(3, (Serializable)((Object)event.getPattern())));
        }
    }

    public void cacheFlushed(CachewideEvent event) {
        if (!"NESTED".equals(event.getOrigin()) && !CLUSTER_ORIGIN.equals(event.getOrigin())) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("cacheFushed called (" + event + ")"));
            }
            this.sendNotification(new ClusterNotification(4, event.getDate()));
        }
    }

    public void cacheEntryAdded(CacheEntryEvent event) {
    }

    public void cacheEntryRemoved(CacheEntryEvent event) {
    }

    public void cacheEntryUpdated(CacheEntryEvent event) {
    }

    public void cacheGroupAdded(CacheGroupEvent event) {
    }

    public void cacheGroupEntryAdded(CacheGroupEvent event) {
    }

    public void cacheGroupEntryRemoved(CacheGroupEvent event) {
    }

    public void cacheGroupRemoved(CacheGroupEvent event) {
    }

    public void cacheGroupUpdated(CacheGroupEvent event) {
    }

    public void initialize(Cache cache, Config config) throws InitializationException {
        this.cache = cache;
    }

    public void handleClusterNotification(ClusterNotification message) {
        if (this.cache == null) {
            log.warn((Object)("A cluster notification (" + message + ") was received, but no cache is registered on this machine. Notification ignored."));
            return;
        }
        if (log.isInfoEnabled()) {
            log.info((Object)("Cluster notification (" + message + ") was received."));
        }
        switch (message.getType()) {
            case 1: {
                this.cache.flushEntry((String)((Object)message.getData()), CLUSTER_ORIGIN);
                break;
            }
            case 2: {
                this.cache.flushGroup((String)((Object)message.getData()), CLUSTER_ORIGIN);
                break;
            }
            case 3: {
                this.cache.flushPattern((String)((Object)message.getData()), CLUSTER_ORIGIN);
                break;
            }
            case 4: {
                this.cache.flushAll((Date)message.getData(), CLUSTER_ORIGIN);
                break;
            }
            default: {
                log.error((Object)("The cluster notification (" + message + ") is of an unknown type. Notification ignored."));
            }
        }
    }

    protected abstract void sendNotification(ClusterNotification var1);

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

