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
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.util.Map;

public interface ConfigurationService {
    public void broadcastConfig(IdentifiedDataSerializable var1);

    public MultiMapConfig findMultiMapConfig(String var1);

    public MapConfig findMapConfig(String var1);

    public TopicConfig findTopicConfig(String var1);

    public CardinalityEstimatorConfig findCardinalityEstimatorConfig(String var1);

    public PNCounterConfig findPNCounterConfig(String var1);

    public ExecutorConfig findExecutorConfig(String var1);

    public ScheduledExecutorConfig findScheduledExecutorConfig(String var1);

    public DurableExecutorConfig findDurableExecutorConfig(String var1);

    public SemaphoreConfig findSemaphoreConfig(String var1);

    public RingbufferConfig findRingbufferConfig(String var1);

    public AtomicLongConfig findAtomicLongConfig(String var1);

    public AtomicReferenceConfig findAtomicReferenceConfig(String var1);

    public CountDownLatchConfig findCountDownLatchConfig(String var1);

    public LockConfig findLockConfig(String var1);

    public ListConfig findListConfig(String var1);

    public QueueConfig findQueueConfig(String var1);

    public SetConfig findSetConfig(String var1);

    public ReplicatedMapConfig findReplicatedMapConfig(String var1);

    public ReliableTopicConfig findReliableTopicConfig(String var1);

    public CacheSimpleConfig findCacheSimpleConfig(String var1);

    public EventJournalConfig findCacheEventJournalConfig(String var1);

    public EventJournalConfig findMapEventJournalConfig(String var1);

    public MerkleTreeConfig findMapMerkleTreeConfig(String var1);

    public FlakeIdGeneratorConfig findFlakeIdGeneratorConfig(String var1);

    public Map<String, MapConfig> getMapConfigs();

    public Map<String, LockConfig> getLockConfigs();

    public Map<String, QueueConfig> getQueueConfigs();

    public Map<String, ListConfig> getListConfigs();

    public Map<String, SetConfig> getSetConfigs();

    public Map<String, MultiMapConfig> getMultiMapConfigs();

    public Map<String, ReplicatedMapConfig> getReplicatedMapConfigs();

    public Map<String, RingbufferConfig> getRingbufferConfigs();

    public Map<String, AtomicLongConfig> getAtomicLongConfigs();

    public Map<String, AtomicReferenceConfig> getAtomicReferenceConfigs();

    public Map<String, CountDownLatchConfig> getCountDownLatchConfigs();

    public Map<String, TopicConfig> getTopicConfigs();

    public Map<String, ReliableTopicConfig> getReliableTopicConfigs();

    public Map<String, ExecutorConfig> getExecutorConfigs();

    public Map<String, DurableExecutorConfig> getDurableExecutorConfigs();

    public Map<String, ScheduledExecutorConfig> getScheduledExecutorConfigs();

    public Map<String, CardinalityEstimatorConfig> getCardinalityEstimatorConfigs();

    public Map<String, PNCounterConfig> getPNCounterConfigs();

    public Map<String, SemaphoreConfig> getSemaphoreConfigs();

    public Map<String, CacheSimpleConfig> getCacheSimpleConfigs();

    public Map<String, EventJournalConfig> getCacheEventJournalConfigs();

    public Map<String, EventJournalConfig> getMapEventJournalConfigs();

    public Map<String, MerkleTreeConfig> getMapMerkleTreeConfigs();

    public Map<String, FlakeIdGeneratorConfig> getFlakeIdGeneratorConfigs();
}

