/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.util.concurrent.ListenableFuture
 *  com.google.common.util.concurrent.MoreExecutors
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.annotation.concurrent.ThreadSafe
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.cluster.event;

import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.impl.cluster.event.ClusterEventService;
import com.atlassian.confluence.impl.cluster.event.PendingAcks;
import com.atlassian.confluence.impl.cluster.event.TopicEvent;
import com.atlassian.confluence.impl.cluster.event.TopicEventCluster;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.event.api.EventPublisher;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.concurrent.ThreadSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ThreadSafe
public final class TopicEventPublisher<E extends TopicEvent, N>
implements ClusterEventService {
    private static final Logger log = LoggerFactory.getLogger(TopicEventPublisher.class);
    private static final String DARK_FEATURE_KEY = "topicEventPublisher";
    private final BooleanSupplier isClustered;
    private final BooleanSupplier isDarkFeatureEnabled;
    private final Consumer<Object> localEventConsumer;
    private final TopicEventCluster<E, N> cluster;
    private final PendingAcks<N> pendingAcks;

    public static <E extends TopicEvent, N> TopicEventPublisher<E, N> create(ClusterManager clusterManager, EventPublisher eventPublisher, TopicEventCluster<E, N> cluster, DarkFeaturesManager darkFeaturesManager) {
        return new TopicEventPublisher<E, N>(clusterManager::isClustered, () -> darkFeaturesManager.getSiteDarkFeatures().isFeatureEnabled(DARK_FEATURE_KEY), arg_0 -> ((EventPublisher)eventPublisher).publish(arg_0), cluster, new PendingAcks<N>(cluster));
    }

    TopicEventPublisher(BooleanSupplier isClustered, BooleanSupplier isDarkFeatureEnabled, Consumer<Object> localEventConsumer, TopicEventCluster<E, N> cluster, PendingAcks<N> pendingAcks) {
        this.isClustered = Objects.requireNonNull(isClustered);
        this.isDarkFeatureEnabled = Objects.requireNonNull(isDarkFeatureEnabled);
        this.localEventConsumer = Objects.requireNonNull(localEventConsumer);
        this.cluster = Objects.requireNonNull(cluster);
        this.pendingAcks = Objects.requireNonNull(pendingAcks);
    }

    @Override
    public ListenableFuture<?> publishEventToCluster(Object event) {
        TopicEvent topicEvent = (TopicEvent)this.cluster.wrapEvent(event);
        ListenableFuture<?> future = this.pendingAcks.addPendingAcks(topicEvent.getId());
        future.addListener(() -> log.debug("Cluster processing of {} has completed", (Object)topicEvent), MoreExecutors.directExecutor());
        log.debug("Publishing {} to Hazelcast topic", (Object)topicEvent);
        this.cluster.publishEvent(topicEvent);
        return future;
    }

    @PreDestroy
    void clearAllPendingAcks() {
        log.debug("Clearing all pending acks");
        this.pendingAcks.removeAll();
    }

    @PostConstruct
    void registerListeners() {
        if (this.isClustered()) {
            this.cluster.initialise(this::onEvent, this::onAck, this::onClusterMemberRemoved);
        }
    }

    @Override
    public boolean isAvailable() {
        return this.isClustered() && this.isDarkFeatureEnabled() && this.cluster.allNodesInitialised();
    }

    private boolean isDarkFeatureEnabled() {
        return this.isDarkFeatureEnabled.getAsBoolean();
    }

    private boolean isClustered() {
        return this.isClustered.getAsBoolean();
    }

    void onClusterMemberRemoved(N node) {
        log.debug("{} removed from cluster, clearing all corresponding pending acks", node);
        this.pendingAcks.clearPendingAcks(node);
    }

    void onEvent(N clusterNode, E incomingEvent) {
        log.debug("Locally forwarding {} from {}", incomingEvent, clusterNode);
        this.localEventConsumer.accept(incomingEvent.getPayload());
        log.debug("Sending ack for {} to Hazelcast topic", incomingEvent);
        this.cluster.publishAck(incomingEvent.getId());
    }

    void onAck(N clusterNode, UUID eventId) {
        log.debug("{} acked event {}", clusterNode, (Object)eventId);
        this.pendingAcks.ackReceived(eventId, clusterNode);
    }
}

