/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.cluster.ClusterInfo
 *  org.terracotta.toolkit.cluster.ClusterListener
 *  org.terracotta.toolkit.cluster.ClusterNode
 */
package org.terracotta.modules.ehcache.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.cluster.ClusterScheme;
import net.sf.ehcache.cluster.ClusterTopologyListener;
import org.terracotta.modules.ehcache.event.ClusterListenerAdapter;
import org.terracotta.modules.ehcache.event.TerracottaNodeImpl;
import org.terracotta.toolkit.cluster.ClusterInfo;
import org.terracotta.toolkit.cluster.ClusterListener;
import org.terracotta.toolkit.cluster.ClusterNode;

public class TerracottaTopologyImpl
implements CacheCluster {
    private final ClusterInfo cluster;
    private final CopyOnWriteArrayList<ClusterTopologyListener> listeners = new CopyOnWriteArrayList();
    private final ReentrantReadWriteLock.WriteLock writeLock = new ReentrantReadWriteLock().writeLock();

    public TerracottaTopologyImpl(ClusterInfo clusterInfo) {
        this.cluster = clusterInfo;
    }

    @Override
    public ClusterScheme getScheme() {
        return ClusterScheme.TERRACOTTA;
    }

    @Override
    public net.sf.ehcache.cluster.ClusterNode getCurrentNode() {
        return new TerracottaNodeImpl(this.cluster.getCurrentNode());
    }

    @Override
    public net.sf.ehcache.cluster.ClusterNode waitUntilNodeJoinsCluster() {
        return new TerracottaNodeImpl(this.cluster.getCurrentNode());
    }

    @Override
    public Collection<net.sf.ehcache.cluster.ClusterNode> getNodes() {
        Set toolkitNodes = this.cluster.getNodes();
        ArrayList<net.sf.ehcache.cluster.ClusterNode> nodes = new ArrayList<net.sf.ehcache.cluster.ClusterNode>();
        for (ClusterNode node : toolkitNodes) {
            nodes.add(new TerracottaNodeImpl(node));
        }
        return nodes;
    }

    @Override
    public boolean isClusterOnline() {
        return this.cluster.areOperationsEnabled();
    }

    @Override
    public boolean addTopologyListener(ClusterTopologyListener listener) {
        boolean rv;
        this.writeLock.lock();
        try {
            rv = this.listeners.add(listener);
            if (rv) {
                this.addInternal(listener);
            }
        }
        finally {
            this.writeLock.unlock();
        }
        return rv;
    }

    @Override
    public boolean removeTopologyListener(ClusterTopologyListener listener) {
        boolean rv;
        this.writeLock.lock();
        try {
            rv = this.listeners.remove(listener);
            if (rv) {
                this.removeInternal(listener);
            }
        }
        finally {
            this.writeLock.unlock();
        }
        return rv;
    }

    private void addInternal(ClusterTopologyListener listener) {
        this.cluster.addClusterListener((ClusterListener)new ClusterListenerAdapter(listener, this.cluster));
    }

    private void removeInternal(ClusterTopologyListener listener) {
        this.cluster.removeClusterListener((ClusterListener)new ClusterListenerAdapter(listener, this.cluster));
    }

    @Override
    public List<ClusterTopologyListener> getTopologyListeners() {
        this.writeLock.lock();
        try {
            List<ClusterTopologyListener> list = Collections.unmodifiableList(this.listeners);
            return list;
        }
        finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public void removeAllListeners() {
        this.writeLock.lock();
        try {
            for (ClusterTopologyListener listener : this.listeners) {
                this.removeInternal(listener);
            }
            this.listeners.clear();
        }
        finally {
            this.writeLock.unlock();
        }
    }
}

