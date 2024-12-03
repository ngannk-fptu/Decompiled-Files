/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.IFunction;
import com.hazelcast.map.impl.MapEntrySimple;
import com.hazelcast.map.impl.MapKeyLoader;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.UnmodifiableIterator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public final class MapKeyLoaderUtil {
    private MapKeyLoaderUtil() {
    }

    static MapKeyLoader.Role assignRole(boolean isPartitionOwner, boolean isMapNamePartition, boolean isMapNamePartitionFirstReplica) {
        if (isMapNamePartition) {
            if (isPartitionOwner) {
                return MapKeyLoader.Role.SENDER;
            }
            if (isMapNamePartitionFirstReplica) {
                return MapKeyLoader.Role.SENDER_BACKUP;
            }
            return MapKeyLoader.Role.NONE;
        }
        return isPartitionOwner ? MapKeyLoader.Role.RECEIVER : MapKeyLoader.Role.NONE;
    }

    static Iterator<Map<Integer, List<Data>>> toBatches(final Iterator<Map.Entry<Integer, Data>> entries, final int maxBatch) {
        return new UnmodifiableIterator<Map<Integer, List<Data>>>(){

            @Override
            public boolean hasNext() {
                return entries.hasNext();
            }

            @Override
            public Map<Integer, List<Data>> next() {
                if (!entries.hasNext()) {
                    throw new NoSuchElementException();
                }
                return MapKeyLoaderUtil.nextBatch(entries, maxBatch);
            }
        };
    }

    private static Map<Integer, List<Data>> nextBatch(Iterator<Map.Entry<Integer, Data>> entries, int maxBatch) {
        Map.Entry<Integer, Data> e;
        List<Data> partitionKeys;
        Map<Integer, List<Data>> batch = MapUtil.createHashMap(maxBatch);
        while (entries.hasNext() && (partitionKeys = CollectionUtil.addToValueList(batch, (e = entries.next()).getKey(), e.getValue())).size() < maxBatch) {
        }
        return batch;
    }

    public static int getMaxSizePerNode(MaxSizeConfig maxSizeConfig) {
        double maxSizePerNode;
        double d = maxSizePerNode = maxSizeConfig.getMaxSizePolicy() == MaxSizeConfig.MaxSizePolicy.PER_NODE ? (double)maxSizeConfig.getSize() : -1.0;
        if (maxSizePerNode == 2.147483647E9) {
            return -1;
        }
        return (int)maxSizePerNode;
    }

    static IFunction<Data, Map.Entry<Integer, Data>> toPartition(IPartitionService partitionService) {
        return new DataToEntry(partitionService);
    }

    @SerializableByConvention
    private static class DataToEntry
    implements IFunction<Data, Map.Entry<Integer, Data>> {
        private final IPartitionService partitionService;

        public DataToEntry(IPartitionService partitionService) {
            this.partitionService = partitionService;
        }

        @Override
        public Map.Entry<Integer, Data> apply(Data input) {
            Preconditions.checkNotNull(input, "Key loaded by a MapLoader cannot be null.");
            Integer partition = this.partitionService.getPartitionId(input);
            return new MapEntrySimple<Integer, Data>(partition, input);
        }
    }
}

