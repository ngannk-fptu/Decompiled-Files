/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.logging.ILogger;
import com.hazelcast.map.QueryResultSizeExceededException;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.spi.properties.HazelcastProperties;
import java.util.Collection;

public class QueryResultSizeLimiter {
    public static final int MINIMUM_MAX_RESULT_LIMIT = 100000;
    public static final float MAX_RESULT_LIMIT_FACTOR = 1.15f;
    public static final float MAX_RESULT_LIMIT_FACTOR_FOR_PRECHECK = 1.25f;
    static final int DISABLED = -1;
    private final MapServiceContext mapServiceContext;
    private final ILogger logger;
    private final int maxResultLimit;
    private final int maxLocalPartitionsLimitForPreCheck;
    private final float resultLimitPerPartition;
    private final boolean isQueryResultLimitEnabled;
    private final boolean isPreCheckEnabled;

    public QueryResultSizeLimiter(MapServiceContext mapServiceContext, ILogger logger) {
        NodeEngine nodeEngine = mapServiceContext.getNodeEngine();
        this.mapServiceContext = mapServiceContext;
        this.logger = logger;
        HazelcastProperties hazelcastProperties = nodeEngine.getProperties();
        this.maxResultLimit = this.getMaxResultLimit(hazelcastProperties);
        this.maxLocalPartitionsLimitForPreCheck = this.getMaxLocalPartitionsLimitForPreCheck(hazelcastProperties);
        this.resultLimitPerPartition = (float)this.maxResultLimit * 1.15f / (float)this.getPartitionCount(nodeEngine);
        this.isQueryResultLimitEnabled = this.maxResultLimit != -1;
        this.isPreCheckEnabled = this.isQueryResultLimitEnabled && this.maxLocalPartitionsLimitForPreCheck != -1;
    }

    public boolean isQueryResultLimitEnabled() {
        return this.isQueryResultLimitEnabled;
    }

    public boolean isPreCheckEnabled() {
        return this.isPreCheckEnabled;
    }

    long getNodeResultLimit(int ownedPartitions) {
        return this.isQueryResultLimitEnabled ? (long)Math.ceil(this.resultLimitPerPartition * (float)ownedPartitions) : Long.MAX_VALUE;
    }

    void precheckMaxResultLimitOnLocalPartitions(String mapName) {
        if (!this.isPreCheckEnabled) {
            return;
        }
        Collection<Integer> localPartitions = this.mapServiceContext.getOwnedPartitions();
        int partitionsToCheck = Math.min(localPartitions.size(), this.maxLocalPartitionsLimitForPreCheck);
        if (partitionsToCheck == 0) {
            return;
        }
        int localPartitionSize = this.getLocalPartitionSize(mapName, localPartitions, partitionsToCheck);
        if (localPartitionSize == 0) {
            return;
        }
        long localResultLimit = this.getNodeResultLimit(partitionsToCheck);
        if ((float)localPartitionSize > (float)localResultLimit * 1.25f) {
            throw new QueryResultSizeExceededException(this.maxResultLimit, " Result size exceeded in local pre-check.");
        }
    }

    private int getLocalPartitionSize(String mapName, Collection<Integer> localPartitions, int partitionsToCheck) {
        int localSize = 0;
        int partitionsChecked = 0;
        for (int partitionId : localPartitions) {
            localSize += this.mapServiceContext.getRecordStore(partitionId, mapName).size();
            if (++partitionsChecked != partitionsToCheck) continue;
            break;
        }
        return localSize;
    }

    private int getMaxResultLimit(HazelcastProperties hazelcastProperties) {
        int maxResultLimit = hazelcastProperties.getInteger(GroupProperty.QUERY_RESULT_SIZE_LIMIT);
        if (maxResultLimit == -1) {
            return -1;
        }
        if (maxResultLimit <= 0) {
            throw new IllegalArgumentException(GroupProperty.QUERY_RESULT_SIZE_LIMIT + " has to be -1 (disabled) or a positive number!");
        }
        if (maxResultLimit < 100000) {
            this.logger.finest("Max result limit was set to minimal value of 100000");
            return 100000;
        }
        return maxResultLimit;
    }

    private int getMaxLocalPartitionsLimitForPreCheck(HazelcastProperties hazelcastProperties) {
        int maxLocalPartitionLimitForPreCheck = hazelcastProperties.getInteger(GroupProperty.QUERY_MAX_LOCAL_PARTITION_LIMIT_FOR_PRE_CHECK);
        if (maxLocalPartitionLimitForPreCheck == -1) {
            return -1;
        }
        if (maxLocalPartitionLimitForPreCheck <= 0) {
            throw new IllegalArgumentException(GroupProperty.QUERY_MAX_LOCAL_PARTITION_LIMIT_FOR_PRE_CHECK + " has to be -1 (disabled) or a positive number!");
        }
        return maxLocalPartitionLimitForPreCheck;
    }

    private int getPartitionCount(NodeEngine nodeEngine) {
        return nodeEngine.getPartitionService().getPartitionCount();
    }
}

