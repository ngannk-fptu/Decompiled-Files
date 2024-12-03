/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.merge;

import com.hazelcast.cardinality.impl.hyperloglog.HyperLogLog;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.scheduledexecutor.impl.ScheduledTaskDescriptor;
import com.hazelcast.spi.merge.MergingCosts;
import com.hazelcast.spi.merge.MergingCreationTime;
import com.hazelcast.spi.merge.MergingEntry;
import com.hazelcast.spi.merge.MergingExpirationTime;
import com.hazelcast.spi.merge.MergingHits;
import com.hazelcast.spi.merge.MergingLastAccessTime;
import com.hazelcast.spi.merge.MergingLastStoredTime;
import com.hazelcast.spi.merge.MergingLastUpdateTime;
import com.hazelcast.spi.merge.MergingMaxIdle;
import com.hazelcast.spi.merge.MergingTTL;
import com.hazelcast.spi.merge.MergingValue;
import com.hazelcast.spi.merge.MergingVersion;
import com.hazelcast.spi.merge.RingbufferMergeData;
import java.util.Collection;

public class SplitBrainMergeTypes {

    public static interface CardinalityEstimatorMergeTypes
    extends MergingEntry<String, HyperLogLog> {
    }

    public static interface ScheduledExecutorMergeTypes
    extends MergingEntry<String, ScheduledTaskDescriptor> {
    }

    public static interface AtomicReferenceMergeTypes
    extends MergingValue<Object> {
    }

    public static interface AtomicLongMergeTypes
    extends MergingValue<Long> {
    }

    public static interface RingbufferMergeTypes
    extends MergingValue<RingbufferMergeData> {
    }

    public static interface QueueMergeTypes
    extends MergingValue<Collection<Object>> {
    }

    public static interface CollectionMergeTypes
    extends MergingValue<Collection<Object>> {
    }

    public static interface MultiMapMergeTypes
    extends MergingEntry<Data, Collection<Object>>,
    MergingCreationTime<Collection<Object>>,
    MergingHits<Collection<Object>>,
    MergingLastAccessTime<Collection<Object>>,
    MergingLastUpdateTime<Collection<Object>> {
    }

    public static interface ReplicatedMapMergeTypes
    extends MergingEntry<Object, Object>,
    MergingCreationTime<Object>,
    MergingHits<Object>,
    MergingLastAccessTime<Object>,
    MergingLastUpdateTime<Object>,
    MergingTTL<Object> {
    }

    public static interface CacheMergeTypes
    extends MergingEntry<Data, Data>,
    MergingCreationTime<Data>,
    MergingHits<Data>,
    MergingLastAccessTime<Data>,
    MergingExpirationTime<Data> {
    }

    public static interface MapMergeTypes
    extends MergingEntry<Data, Data>,
    MergingCreationTime<Data>,
    MergingHits<Data>,
    MergingLastAccessTime<Data>,
    MergingLastUpdateTime<Data>,
    MergingTTL<Data>,
    MergingMaxIdle<Data>,
    MergingCosts<Data>,
    MergingVersion<Data>,
    MergingExpirationTime<Data>,
    MergingLastStoredTime<Data> {
    }
}

