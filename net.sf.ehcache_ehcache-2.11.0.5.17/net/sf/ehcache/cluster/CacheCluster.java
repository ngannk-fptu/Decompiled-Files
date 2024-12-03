/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.cluster;

import java.util.Collection;
import java.util.List;
import net.sf.ehcache.cluster.ClusterNode;
import net.sf.ehcache.cluster.ClusterScheme;
import net.sf.ehcache.cluster.ClusterTopologyListener;

public interface CacheCluster {
    public ClusterScheme getScheme();

    public ClusterNode getCurrentNode();

    public ClusterNode waitUntilNodeJoinsCluster();

    public Collection<ClusterNode> getNodes();

    public boolean isClusterOnline();

    public boolean addTopologyListener(ClusterTopologyListener var1);

    public boolean removeTopologyListener(ClusterTopologyListener var1);

    public void removeAllListeners();

    public List<ClusterTopologyListener> getTopologyListeners();
}

