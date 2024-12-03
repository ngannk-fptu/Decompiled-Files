/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.cluster;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.cluster.ClusterNode;
import net.sf.ehcache.cluster.ClusterScheme;
import net.sf.ehcache.cluster.ClusterTopologyListener;

public class NoopCacheCluster
implements CacheCluster {
    public static final CacheCluster INSTANCE = new NoopCacheCluster();

    @Override
    public Collection<ClusterNode> getNodes() {
        return Collections.emptyList();
    }

    @Override
    public ClusterScheme getScheme() {
        return ClusterScheme.NONE;
    }

    @Override
    public boolean addTopologyListener(ClusterTopologyListener listener) {
        return false;
    }

    @Override
    public boolean removeTopologyListener(ClusterTopologyListener listener) {
        return false;
    }

    @Override
    public boolean isClusterOnline() {
        return true;
    }

    @Override
    public ClusterNode getCurrentNode() {
        return null;
    }

    @Override
    public ClusterNode waitUntilNodeJoinsCluster() {
        return null;
    }

    @Override
    public List<ClusterTopologyListener> getTopologyListeners() {
        return Collections.emptyList();
    }

    @Override
    public void removeAllListeners() {
    }
}

