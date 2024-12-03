/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.cluster.ClusterNode;
import net.sf.ehcache.cluster.ClusterScheme;
import net.sf.ehcache.cluster.ClusterTopologyListener;
import net.sf.ehcache.statistics.StatisticBuilder;
import org.terracotta.statistics.Statistic;
import org.terracotta.statistics.StatisticsManager;
import org.terracotta.statistics.observer.OperationObserver;

public class CacheClusterStateStatisticsListener
implements ClusterTopologyListener {
    private final OperationObserver<CacheOperationOutcomes.ClusterEventOutcomes> clusterObserver = ((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)((StatisticBuilder.OperationStatisticBuilder)StatisticBuilder.operation(CacheOperationOutcomes.ClusterEventOutcomes.class).named("cluster")).of(this)).tag(new String[]{"cache"})).build();
    private volatile long mostRecentRejoinTimeStamp = 0L;
    private final Cache cache;

    CacheClusterStateStatisticsListener(Cache cache) {
        this.cache = cache;
        StatisticsManager.associate(this).withParent(cache);
    }

    @Override
    public void nodeLeft(ClusterNode node) {
    }

    @Override
    public void nodeJoined(ClusterNode node) {
    }

    @Override
    public void clusterRejoined(ClusterNode oldNode, ClusterNode newNode) {
        if (newNode.equals(this.cache.getCacheManager().getCluster(ClusterScheme.TERRACOTTA).getCurrentNode())) {
            this.mostRecentRejoinTimeStamp = System.currentTimeMillis();
            this.clusterObserver.end(CacheOperationOutcomes.ClusterEventOutcomes.REJOINED);
        }
    }

    @Override
    public void clusterOnline(ClusterNode node) {
        if (node.equals(this.cache.getCacheManager().getCluster(ClusterScheme.TERRACOTTA).getCurrentNode())) {
            this.clusterObserver.end(CacheOperationOutcomes.ClusterEventOutcomes.ONLINE);
        }
    }

    @Override
    public void clusterOffline(ClusterNode node) {
        if (node.equals(this.cache.getCacheManager().getCluster(ClusterScheme.TERRACOTTA).getCurrentNode())) {
            this.clusterObserver.end(CacheOperationOutcomes.ClusterEventOutcomes.OFFLINE);
        }
    }

    @Statistic(name="lastRejoinTime", tags={"cache"})
    public long getMostRecentRejoinTimeStampMS() {
        return this.mostRecentRejoinTimeStamp;
    }
}

