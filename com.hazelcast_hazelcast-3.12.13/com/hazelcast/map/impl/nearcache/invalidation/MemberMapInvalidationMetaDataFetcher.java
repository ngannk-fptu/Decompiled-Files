/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.nearcache.invalidation;

import com.hazelcast.cluster.memberselector.MemberSelectors;
import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.nearcache.impl.invalidation.InvalidationMetaDataFetcher;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.operation.MapGetInvalidationMetaDataOperation;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.OperationService;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MemberMapInvalidationMetaDataFetcher
extends InvalidationMetaDataFetcher {
    private final ClusterService clusterService;
    private final OperationService operationService;

    public MemberMapInvalidationMetaDataFetcher(ClusterService clusterService, OperationService operationService, ILogger logger) {
        super(logger);
        this.clusterService = clusterService;
        this.operationService = operationService;
    }

    @Override
    protected Collection<Member> getDataMembers() {
        return this.clusterService.getMembers(MemberSelectors.DATA_MEMBER_SELECTOR);
    }

    @Override
    protected InternalCompletableFuture fetchMetadataOf(Address address, List<String> names) {
        MapGetInvalidationMetaDataOperation operation = new MapGetInvalidationMetaDataOperation(names);
        return this.operationService.invokeOnTarget("hz:impl:mapService", operation, address);
    }

    @Override
    protected void extractMemberMetadata(Member member, InternalCompletableFuture future, InvalidationMetaDataFetcher.MetadataHolder metadataHolder) throws Exception {
        MapGetInvalidationMetaDataOperation.MetaDataResponse response = (MapGetInvalidationMetaDataOperation.MetaDataResponse)future.get(1L, TimeUnit.MINUTES);
        metadataHolder.setMetadata(response.getPartitionUuidList().entrySet(), response.getNamePartitionSequenceList().entrySet());
    }
}

