/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.terracotta;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.cluster.ClusterNode;
import net.sf.ehcache.cluster.ClusterScheme;
import net.sf.ehcache.cluster.ClusterTopologyListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerracottaCacheCluster
implements CacheCluster {
    private static final Logger LOGGER = LoggerFactory.getLogger(TerracottaCacheCluster.class);
    private final List<ClusterTopologyListener> listeners = new CopyOnWriteArrayList<ClusterTopologyListener>();
    private volatile CacheCluster realCacheCluster;

    public void setUnderlyingCacheCluster(CacheCluster newCacheCluster) {
        if (newCacheCluster == null) {
            throw new IllegalArgumentException("CacheCluster can't be null");
        }
        CacheCluster oldRealCacheCluster = this.realCacheCluster;
        this.realCacheCluster = newCacheCluster;
        for (ClusterTopologyListener listener : this.listeners) {
            this.realCacheCluster.addTopologyListener(listener);
        }
        if (oldRealCacheCluster != null) {
            for (ClusterTopologyListener listener : this.listeners) {
                oldRealCacheCluster.removeTopologyListener(listener);
            }
        }
    }

    void fireNodeRejoinedEvent(ClusterNode oldNode, ClusterNode newNode) {
        HashSet<ClusterTopologyListener> firedToListeners = new HashSet<ClusterTopologyListener>();
        for (ClusterTopologyListener listener : this.realCacheCluster.getTopologyListeners()) {
            firedToListeners.add(listener);
            this.fireRejoinEvent(oldNode, newNode, listener);
        }
        for (ClusterTopologyListener listener : this.listeners) {
            if (firedToListeners.contains(listener)) continue;
            this.fireRejoinEvent(oldNode, newNode, listener);
        }
    }

    private void fireRejoinEvent(ClusterNode oldNode, ClusterNode newNode, ClusterTopologyListener listener) {
        try {
            listener.clusterRejoined(oldNode, newNode);
        }
        catch (Throwable e) {
            LOGGER.error("Caught exception while firing rejoin event", e);
        }
    }

    @Override
    public boolean addTopologyListener(ClusterTopologyListener listener) {
        this.checkIfInitialized();
        boolean added = this.realCacheCluster.addTopologyListener(listener);
        if (added) {
            this.listeners.add(listener);
        }
        return added;
    }

    @Override
    public boolean removeTopologyListener(ClusterTopologyListener listener) {
        this.checkIfInitialized();
        boolean removed = this.realCacheCluster.removeTopologyListener(listener);
        if (removed) {
            this.listeners.remove(listener);
        }
        return removed;
    }

    @Override
    public ClusterNode getCurrentNode() {
        this.checkIfInitialized();
        return this.realCacheCluster.getCurrentNode();
    }

    @Override
    public Collection<ClusterNode> getNodes() {
        this.checkIfInitialized();
        return this.realCacheCluster.getNodes();
    }

    @Override
    public ClusterScheme getScheme() {
        this.checkIfInitialized();
        return this.realCacheCluster.getScheme();
    }

    @Override
    public boolean isClusterOnline() {
        this.checkIfInitialized();
        return this.realCacheCluster.isClusterOnline();
    }

    @Override
    public ClusterNode waitUntilNodeJoinsCluster() {
        this.checkIfInitialized();
        return this.realCacheCluster.waitUntilNodeJoinsCluster();
    }

    private void checkIfInitialized() {
        if (this.realCacheCluster == null) {
            throw new CacheException("The underlying cache cluster has not been initialized. Probably the terracotta client has not been configured yet.");
        }
    }

    @Override
    public List<ClusterTopologyListener> getTopologyListeners() {
        return this.listeners;
    }

    @Override
    public void removeAllListeners() {
        this.checkIfInitialized();
        this.realCacheCluster.removeAllListeners();
        this.listeners.clear();
    }
}

