/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.ManagedCache
 *  com.hazelcast.core.HazelcastInstance
 *  io.atlassian.fugue.Pair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.cache.hazelcast.asyncinvalidation;

import com.atlassian.cache.Cache;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cache.hazelcast.asyncinvalidation.CacheInvalidationReceiver;
import com.atlassian.cache.hazelcast.asyncinvalidation.CacheInvalidationSequenceSnapshotVerifier;
import com.atlassian.cache.hazelcast.asyncinvalidation.CacheInvalidator;
import com.atlassian.cache.hazelcast.asyncinvalidation.CacheReplicator;
import com.atlassian.cache.hazelcast.asyncinvalidation.ClusterNode;
import com.atlassian.cache.hazelcast.asyncinvalidation.InvalidationTopicSender;
import com.atlassian.cache.hazelcast.asyncinvalidation.Observability;
import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceSnapshotReceiver;
import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceSnapshotSender;
import com.atlassian.cache.hazelcast.asyncinvalidation.SequenceTracker;
import com.atlassian.cache.hazelcast.asyncinvalidation.Topic;
import com.atlassian.cache.hazelcast.asyncinvalidation.Topics;
import com.hazelcast.core.HazelcastInstance;
import io.atlassian.fugue.Pair;
import java.io.Serializable;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CacheInvalidatorFactory
implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(CacheInvalidatorFactory.class);
    private final Topics topics;
    private final Observability observability;
    private final SequenceSnapshotReceiver<String> sequenceSnapshotReceiver;
    private final SequenceSnapshotSender<String> sequenceSnapshotSender;

    public static CacheInvalidatorFactory create(HazelcastInstance hazelcast, Function<String, String> topicNamePrefixer, Observability observability) {
        Topics topics = Topics.from(hazelcast, topicNamePrefixer);
        return new CacheInvalidatorFactory(topics, observability).registerSequenceSnapshotReceiver();
    }

    private CacheInvalidatorFactory(Topics topics, Observability observability) {
        this.topics = topics;
        this.observability = observability;
        this.sequenceSnapshotReceiver = new SequenceSnapshotReceiver();
        this.sequenceSnapshotSender = new SequenceSnapshotSender(topics.sequenceSnapshot());
    }

    private CacheInvalidatorFactory registerSequenceSnapshotReceiver() {
        this.topics.sequenceSnapshot().addListener(this.sequenceSnapshotReceiver::processSequenceSnapshot, this.topics::addRegistration);
        return this;
    }

    @Override
    public void close() {
        this.topics.close();
        this.sequenceSnapshotSender.close();
        this.sequenceSnapshotReceiver.close();
    }

    public void publishSequenceSnapshot() {
        this.sequenceSnapshotSender.publishSequenceSnapshot();
    }

    public <K extends Serializable> CacheInvalidator<K> createCacheInvalidator(Cache<K, ?> cache, ManagedCache managedCache) {
        log.debug("Creating invalidator for cache '{}'", (Object)cache.getName());
        Topic invalidationTopic = this.topics.cacheInvalidation(cache);
        SequenceTracker<ClusterNode> sequenceTracker = new SequenceTracker<ClusterNode>();
        CacheInvalidationReceiver<K> invalidationReceiver = this.createCacheInvalidationReceiver(cache, managedCache, sequenceTracker);
        invalidationTopic.addListener(invalidationReceiver::processInvalidation, this.topics::addRegistration);
        InvalidationTopicSender invalidationSender = new InvalidationTopicSender();
        this.sequenceSnapshotSender.registerSequenceNumberSource(cache.getName(), invalidationSender::getCurrentSequenceNumber);
        CacheInvalidationSequenceSnapshotVerifier snapshotVerifier = this.createSnapshotVerifier(managedCache, sequenceTracker);
        this.sequenceSnapshotReceiver.registerSequenceSnapshotConsumer(cache.getName(), snapshotVerifier::verifyMinimumSequenceNumber);
        return invalidationSender.createInvalidator(invalidationTopic);
    }

    public <K extends Serializable, V> CacheReplicator<K, V> createCacheReplicator(Cache<K, V> localCache) {
        String cacheName = localCache.getName();
        log.debug("Creating replicator for cache '{}'", (Object)cacheName);
        Topic topic = this.topics.cacheReplication(localCache);
        Topic.MessageConsumer consumer = (sentBy, pair) -> {
            log.debug("Processing replication sent by {} for cache '{}'", sentBy, (Object)cacheName);
            localCache.put((Object)((Serializable)pair.left()), pair.right());
        };
        topic.addListener(consumer, this.topics::addRegistration);
        return (key, value) -> {
            log.debug("Publishing key/value pair for cache [{}]", (Object)cacheName);
            topic.publish(Pair.pair((Object)key, (Object)value));
        };
    }

    private <K extends Serializable> CacheInvalidationReceiver<K> createCacheInvalidationReceiver(Cache<K, ?> cache, ManagedCache managedCache, SequenceTracker<ClusterNode> sequenceTracker) {
        return new CacheInvalidationReceiver<K>(cache, sequenceTracker, () -> this.observability.cacheInvalidationOutOfSequence(managedCache));
    }

    private CacheInvalidationSequenceSnapshotVerifier createSnapshotVerifier(ManagedCache cache, SequenceTracker<ClusterNode> sequenceTracker) {
        return new CacheInvalidationSequenceSnapshotVerifier(cache, () -> this.observability.sequenceSnapshotInconsistent(cache), sequenceTracker);
    }
}

