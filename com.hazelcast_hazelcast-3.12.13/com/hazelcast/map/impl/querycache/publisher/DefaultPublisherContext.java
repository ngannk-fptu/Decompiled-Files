/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.publisher;

import com.hazelcast.core.IFunction;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipAdapter;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.QueryCacheScheduler;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorScannerTask;
import com.hazelcast.map.impl.querycache.accumulator.DefaultAccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.publisher.AccumulatorSweeper;
import com.hazelcast.map.impl.querycache.publisher.MapListenerRegistry;
import com.hazelcast.map.impl.querycache.publisher.MapPublisherRegistry;
import com.hazelcast.map.impl.querycache.publisher.PartitionAccumulatorRegistry;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.querycache.publisher.PublisherRegistry;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.CollectionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DefaultPublisherContext
implements PublisherContext {
    private static final long SCAN_PERIOD_SECONDS = 5L;
    private static final long ORPHANED_QUERY_CACHE_REMOVAL_DELAY_SECONDS = TimeUnit.MINUTES.toSeconds(10L);
    private final QueryCacheContext context;
    private final NodeEngine nodeEngine;
    private final MapListenerRegistry mapListenerRegistry;
    private final MapPublisherRegistry mapPublisherRegistry;
    private final AccumulatorInfoSupplier accumulatorInfoSupplier;
    private final IFunction<String, String> listenerRegistrator;
    private final ConcurrentMap<String, ScheduledFuture> removalCandidateFutures;

    public DefaultPublisherContext(QueryCacheContext context, NodeEngine nodeEngine, IFunction<String, String> listenerRegistrator) {
        this.context = context;
        this.nodeEngine = nodeEngine;
        this.mapListenerRegistry = new MapListenerRegistry(context);
        this.mapPublisherRegistry = new MapPublisherRegistry(context);
        this.accumulatorInfoSupplier = new DefaultAccumulatorInfoSupplier();
        this.listenerRegistrator = listenerRegistrator;
        this.removalCandidateFutures = new ConcurrentHashMap<String, ScheduledFuture>();
        this.startBackgroundAccumulatorScanner();
        this.handleSubscriberAddRemove();
    }

    @Override
    public AccumulatorInfoSupplier getAccumulatorInfoSupplier() {
        return this.accumulatorInfoSupplier;
    }

    @Override
    public MapPublisherRegistry getMapPublisherRegistry() {
        return this.mapPublisherRegistry;
    }

    @Override
    public MapListenerRegistry getMapListenerRegistry() {
        return this.mapListenerRegistry;
    }

    @Override
    public QueryCacheContext getContext() {
        return this.context;
    }

    @Override
    public NodeEngine getNodeEngine() {
        return this.nodeEngine;
    }

    @Override
    public IFunction<String, String> getListenerRegistrator() {
        return this.listenerRegistrator;
    }

    @Override
    public void handleDisconnectedSubscriber(String uuid) {
        Collection<PartitionAccumulatorRegistry> removalCandidates = this.getRemovalCandidates(uuid);
        if (CollectionUtil.isEmpty(removalCandidates)) {
            return;
        }
        this.startRemovalTask(removalCandidates, uuid);
    }

    @Override
    public void handleConnectedSubscriber(String uuid) {
        this.cancelRemovalTask(uuid);
    }

    @Override
    public void flush() {
        AccumulatorSweeper.flushAllAccumulators(this);
    }

    private Collection<PartitionAccumulatorRegistry> getRemovalCandidates(String uuid) {
        ArrayList<PartitionAccumulatorRegistry> candidates = new ArrayList<PartitionAccumulatorRegistry>();
        MapPublisherRegistry mapPublisherRegistry = this.getMapPublisherRegistry();
        Map<String, PublisherRegistry> all = mapPublisherRegistry.getAll();
        for (PublisherRegistry publisherRegistry : all.values()) {
            Map<String, PartitionAccumulatorRegistry> partitionAccumulators = publisherRegistry.getAll();
            Set<Map.Entry<String, PartitionAccumulatorRegistry>> entries = partitionAccumulators.entrySet();
            for (Map.Entry<String, PartitionAccumulatorRegistry> entry : entries) {
                PartitionAccumulatorRegistry accumulatorRegistry = entry.getValue();
                if (!uuid.equals(accumulatorRegistry.getUuid())) continue;
                candidates.add(accumulatorRegistry);
            }
        }
        return candidates;
    }

    private PartitionAccumulatorRegistry removePartitionAccumulatorRegistry(PartitionAccumulatorRegistry registry) {
        AccumulatorInfo info = registry.getInfo();
        String mapName = info.getMapName();
        String cacheId = info.getCacheId();
        MapPublisherRegistry mapPublisherRegistry = this.getMapPublisherRegistry();
        PublisherRegistry publisherRegistry = mapPublisherRegistry.getOrNull(mapName);
        if (publisherRegistry == null) {
            return null;
        }
        return publisherRegistry.remove(cacheId);
    }

    private void startRemovalTask(final Collection<PartitionAccumulatorRegistry> removalCandidates, String uuid) {
        QueryCacheScheduler queryCacheScheduler = this.context.getQueryCacheScheduler();
        ScheduledFuture<?> scheduledFuture = queryCacheScheduler.scheduleWithRepetition(new Runnable(){

            @Override
            public void run() {
                for (PartitionAccumulatorRegistry registry : removalCandidates) {
                    DefaultPublisherContext.this.removePartitionAccumulatorRegistry(registry);
                }
            }
        }, ORPHANED_QUERY_CACHE_REMOVAL_DELAY_SECONDS);
        ScheduledFuture<?> prevFuture = this.removalCandidateFutures.put(uuid, scheduledFuture);
        if (prevFuture != null) {
            prevFuture.cancel(false);
        }
    }

    private void cancelRemovalTask(String uuid) {
        this.removalCandidateFutures.remove(uuid);
    }

    private void startBackgroundAccumulatorScanner() {
        QueryCacheScheduler scheduler = this.context.getQueryCacheScheduler();
        scheduler.scheduleWithRepetition(new AccumulatorScannerTask(this.context), 5L);
    }

    private void handleSubscriberAddRemove() {
        ClusterService clusterService = this.nodeEngine.getClusterService();
        clusterService.addMembershipListener(new MembershipAdapter(){

            @Override
            public void memberRemoved(MembershipEvent membershipEvent) {
                Member member = membershipEvent.getMember();
                String uuid = member.getUuid();
                DefaultPublisherContext.this.handleDisconnectedSubscriber(uuid);
            }

            @Override
            public void memberAdded(MembershipEvent membershipEvent) {
                Member member = membershipEvent.getMember();
                String uuid = member.getUuid();
                DefaultPublisherContext.this.handleConnectedSubscriber(uuid);
            }
        });
    }
}

