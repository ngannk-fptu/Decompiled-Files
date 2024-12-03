/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.util.concurrent.Futures
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.MoreExecutors
 *  com.google.common.util.concurrent.SettableFuture
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.confluence.impl.cluster.event;

import com.atlassian.confluence.impl.cluster.event.TopicEventCluster;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
final class PendingAcks<M> {
    private final TopicEventCluster<?, M> cluster;
    private final Map<UUID, Map<M, SettableFuture<AckResult>>> pending = new ConcurrentHashMap<UUID, Map<M, SettableFuture<AckResult>>>();

    PendingAcks(TopicEventCluster<?, M> cluster) {
        this.cluster = cluster;
    }

    private void clearAllPendingAcks(UUID eventId) {
        this.pending.remove(eventId);
    }

    private Map<M, SettableFuture<AckResult>> createFutures() {
        return this.cluster.getOtherClusterMembers().stream().collect(Collectors.toMap(member -> member, member -> SettableFuture.create()));
    }

    public ListenableFuture<?> addPendingAcks(UUID eventId) {
        Map<M, SettableFuture<AckResult>> acks = this.createFutures();
        this.pending.put(eventId, acks);
        ListenableFuture combinedFuture = Futures.allAsList(acks.values());
        combinedFuture.addListener(() -> this.clearAllPendingAcks(eventId), MoreExecutors.directExecutor());
        return combinedFuture;
    }

    @VisibleForTesting
    Map<M, SettableFuture<AckResult>> get(UUID id) {
        return this.pending.get(id);
    }

    public void removeAll() {
        this.getAllPendingAcks().forEach(f -> f.set((Object)AckResult.ERROR));
        this.pending.clear();
    }

    private Stream<SettableFuture<AckResult>> getAllPendingAcks() {
        return this.pending.values().stream().flatMap(map -> map.values().stream());
    }

    @VisibleForTesting
    boolean isEmpty() {
        return this.pending.isEmpty();
    }

    public void clearPendingAcks(M member) {
        this.pending.values().forEach(ackMap -> ackMap.getOrDefault(member, SettableFuture.create()).set((Object)AckResult.ERROR));
    }

    public void ackReceived(UUID eventId, M member) {
        this.pending.getOrDefault(eventId, Collections.emptyMap()).getOrDefault(member, SettableFuture.create()).set((Object)AckResult.OK);
    }

    static enum AckResult {
        OK,
        ERROR;

    }
}

