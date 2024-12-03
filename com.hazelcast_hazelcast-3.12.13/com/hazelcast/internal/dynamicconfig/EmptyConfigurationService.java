/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.dynamicconfig;

import com.hazelcast.config.AtomicLongConfig;
import com.hazelcast.config.AtomicReferenceConfig;
import com.hazelcast.config.CacheSimpleConfig;
import com.hazelcast.config.CardinalityEstimatorConfig;
import com.hazelcast.config.CountDownLatchConfig;
import com.hazelcast.config.DurableExecutorConfig;
import com.hazelcast.config.EventJournalConfig;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.config.FlakeIdGeneratorConfig;
import com.hazelcast.config.ListConfig;
import com.hazelcast.config.LockConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MerkleTreeConfig;
import com.hazelcast.config.MultiMapConfig;
import com.hazelcast.config.PNCounterConfig;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.config.ReliableTopicConfig;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.config.RingbufferConfig;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.config.SemaphoreConfig;
import com.hazelcast.config.SetConfig;
import com.hazelcast.config.TopicConfig;
import com.hazelcast.internal.dynamicconfig.ConfigurationService;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.Collections;
import java.util.Map;

class EmptyConfigurationService
implements ConfigurationService {
    EmptyConfigurationService() {
    }

    @Override
    public MultiMapConfig findMultiMapConfig(String name) {
        return null;
    }

    @Override
    public MapConfig findMapConfig(String name) {
        return null;
    }

    @Override
    public TopicConfig findTopicConfig(String name) {
        return null;
    }

    @Override
    public CardinalityEstimatorConfig findCardinalityEstimatorConfig(String name) {
        return null;
    }

    @Override
    public PNCounterConfig findPNCounterConfig(String name) {
        return null;
    }

    @Override
    public ExecutorConfig findExecutorConfig(String name) {
        return null;
    }

    @Override
    public ScheduledExecutorConfig findScheduledExecutorConfig(String name) {
        return null;
    }

    @Override
    public DurableExecutorConfig findDurableExecutorConfig(String name) {
        return null;
    }

    @Override
    public SemaphoreConfig findSemaphoreConfig(String name) {
        return null;
    }

    @Override
    public RingbufferConfig findRingbufferConfig(String name) {
        return null;
    }

    @Override
    public AtomicLongConfig findAtomicLongConfig(String name) {
        return null;
    }

    @Override
    public AtomicReferenceConfig findAtomicReferenceConfig(String name) {
        return null;
    }

    @Override
    public CountDownLatchConfig findCountDownLatchConfig(String name) {
        return null;
    }

    @Override
    public LockConfig findLockConfig(String name) {
        return null;
    }

    @Override
    public ListConfig findListConfig(String name) {
        return null;
    }

    @Override
    public QueueConfig findQueueConfig(String name) {
        return null;
    }

    @Override
    public SetConfig findSetConfig(String name) {
        return null;
    }

    @Override
    public ReplicatedMapConfig findReplicatedMapConfig(String name) {
        return null;
    }

    @Override
    public ReliableTopicConfig findReliableTopicConfig(String name) {
        return null;
    }

    @Override
    public CacheSimpleConfig findCacheSimpleConfig(String name) {
        return null;
    }

    @Override
    public Map<String, CacheSimpleConfig> getCacheSimpleConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public EventJournalConfig findCacheEventJournalConfig(String baseName) {
        return null;
    }

    @Override
    public Map<String, EventJournalConfig> getCacheEventJournalConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public EventJournalConfig findMapEventJournalConfig(String baseName) {
        return null;
    }

    @Override
    public MerkleTreeConfig findMapMerkleTreeConfig(String baseName) {
        return null;
    }

    @Override
    public Map<String, EventJournalConfig> getMapEventJournalConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, MerkleTreeConfig> getMapMerkleTreeConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, LockConfig> getLockConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, MapConfig> getMapConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, QueueConfig> getQueueConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, ListConfig> getListConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, SetConfig> getSetConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, MultiMapConfig> getMultiMapConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, ReplicatedMapConfig> getReplicatedMapConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, RingbufferConfig> getRingbufferConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, AtomicLongConfig> getAtomicLongConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, AtomicReferenceConfig> getAtomicReferenceConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, CountDownLatchConfig> getCountDownLatchConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, TopicConfig> getTopicConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, ReliableTopicConfig> getReliableTopicConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, ExecutorConfig> getExecutorConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, DurableExecutorConfig> getDurableExecutorConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, ScheduledExecutorConfig> getScheduledExecutorConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, CardinalityEstimatorConfig> getCardinalityEstimatorConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, PNCounterConfig> getPNCounterConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public Map<String, SemaphoreConfig> getSemaphoreConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public FlakeIdGeneratorConfig findFlakeIdGeneratorConfig(String baseName) {
        return null;
    }

    @Override
    public Map<String, FlakeIdGeneratorConfig> getFlakeIdGeneratorConfigs() {
        return Collections.emptyMap();
    }

    @Override
    public void broadcastConfig(IdentifiedDataSerializable config) {
        throw new IllegalStateException("Cannot add a new config while Hazelcast is starting.");
    }
}

