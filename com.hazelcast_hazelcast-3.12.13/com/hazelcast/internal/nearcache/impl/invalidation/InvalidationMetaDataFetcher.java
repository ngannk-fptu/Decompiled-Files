/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.core.Member;
import com.hazelcast.internal.nearcache.impl.invalidation.RepairingHandler;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.util.MapUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public abstract class InvalidationMetaDataFetcher {
    protected static final int ASYNC_RESULT_WAIT_TIMEOUT_MINUTES = 1;
    protected final ILogger logger;

    public InvalidationMetaDataFetcher(ILogger logger) {
        this.logger = logger;
    }

    public final void init(RepairingHandler handler) throws Exception {
        MetadataHolder resultHolder = new MetadataHolder();
        List<String> dataStructureNames = Collections.singletonList(handler.getName());
        Map<Member, InternalCompletableFuture> futureByMember = this.fetchMembersMetadataFor(dataStructureNames);
        for (Map.Entry<Member, InternalCompletableFuture> entry : futureByMember.entrySet()) {
            Member member = entry.getKey();
            InternalCompletableFuture future = entry.getValue();
            this.extractMemberMetadata(member, future, resultHolder);
            this.initUuid(resultHolder.partitionUuidList, handler);
            this.initSequence(resultHolder.namePartitionSequenceList, handler);
        }
    }

    public final void fetchMetadata(ConcurrentMap<String, RepairingHandler> handlers) {
        if (handlers.isEmpty()) {
            return;
        }
        List<String> dataStructureNames = this.getDataStructureNames(handlers);
        Map<Member, InternalCompletableFuture> futureByMember = this.fetchMembersMetadataFor(dataStructureNames);
        for (Map.Entry<Member, InternalCompletableFuture> entry : futureByMember.entrySet()) {
            Member member = entry.getKey();
            InternalCompletableFuture future = entry.getValue();
            this.processMemberMetadata(member, future, handlers);
        }
    }

    protected abstract Collection<Member> getDataMembers();

    protected abstract void extractMemberMetadata(Member var1, InternalCompletableFuture var2, MetadataHolder var3) throws Exception;

    protected abstract InternalCompletableFuture fetchMetadataOf(Address var1, List<String> var2);

    private Map<Member, InternalCompletableFuture> fetchMembersMetadataFor(List<String> names) {
        Collection<Member> members = this.getDataMembers();
        if (members.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Member, InternalCompletableFuture> futureByMember = MapUtil.createHashMap(members.size());
        for (Member member : members) {
            Address address = member.getAddress();
            try {
                futureByMember.put(member, this.fetchMetadataOf(address, names));
            }
            catch (Exception e) {
                this.handleExceptionWhileProcessingMetadata(member, e);
            }
        }
        return futureByMember;
    }

    private void processMemberMetadata(Member member, InternalCompletableFuture future, ConcurrentMap<String, RepairingHandler> handlers) {
        MetadataHolder resultHolder = new MetadataHolder();
        try {
            this.extractMemberMetadata(member, future, resultHolder);
        }
        catch (Exception e) {
            this.handleExceptionWhileProcessingMetadata(member, e);
            return;
        }
        this.repairUuids(resultHolder.partitionUuidList, handlers);
        this.repairSequences(resultHolder.namePartitionSequenceList, handlers);
    }

    protected void handleExceptionWhileProcessingMetadata(Member member, Exception e) {
        if (e instanceof IllegalStateException) {
            this.logger.finest(e);
        } else if (this.logger.isWarningEnabled()) {
            this.logger.warning(String.format("Can't fetch or extract invalidation meta-data of %s", member), e);
        }
    }

    private List<String> getDataStructureNames(ConcurrentMap<String, RepairingHandler> handlers) {
        ArrayList<String> names = new ArrayList<String>(handlers.size());
        for (RepairingHandler handler : handlers.values()) {
            names.add(handler.getName());
        }
        return names;
    }

    private void repairUuids(Collection<Map.Entry<Integer, UUID>> uuids, ConcurrentMap<String, RepairingHandler> handlers) {
        for (Map.Entry<Integer, UUID> entry : uuids) {
            for (RepairingHandler handler : handlers.values()) {
                handler.checkOrRepairUuid(entry.getKey(), entry.getValue());
            }
        }
    }

    private void initUuid(Collection<Map.Entry<Integer, UUID>> uuids, RepairingHandler handler) {
        for (Map.Entry<Integer, UUID> entry : uuids) {
            int partitionID = entry.getKey();
            UUID partitionUuid = entry.getValue();
            handler.initUuid(partitionID, partitionUuid);
        }
    }

    private void repairSequences(Collection<Map.Entry<String, List<Map.Entry<Integer, Long>>>> namePartitionSequenceList, ConcurrentMap<String, RepairingHandler> handlers) {
        for (Map.Entry<String, List<Map.Entry<Integer, Long>>> entry : namePartitionSequenceList) {
            for (Map.Entry<Integer, Long> subEntry : entry.getValue()) {
                RepairingHandler repairingHandler = (RepairingHandler)handlers.get(entry.getKey());
                repairingHandler.checkOrRepairSequence(subEntry.getKey(), subEntry.getValue(), true);
            }
        }
    }

    private void initSequence(Collection<Map.Entry<String, List<Map.Entry<Integer, Long>>>> namePartitionSequenceList, RepairingHandler handler) {
        for (Map.Entry<String, List<Map.Entry<Integer, Long>>> entry : namePartitionSequenceList) {
            for (Map.Entry<Integer, Long> subEntry : entry.getValue()) {
                int partitionID = subEntry.getKey();
                long partitionSequence = subEntry.getValue();
                handler.initSequence(partitionID, partitionSequence);
            }
        }
    }

    protected static class MetadataHolder {
        private Collection<Map.Entry<Integer, UUID>> partitionUuidList;
        private Collection<Map.Entry<String, List<Map.Entry<Integer, Long>>>> namePartitionSequenceList;

        protected MetadataHolder() {
        }

        public void setMetadata(Collection<Map.Entry<Integer, UUID>> partitionUuidList, Collection<Map.Entry<String, List<Map.Entry<Integer, Long>>>> namePartitionSequenceList) {
            this.namePartitionSequenceList = namePartitionSequenceList;
            this.partitionUuidList = partitionUuidList;
        }

        public Collection<Map.Entry<Integer, UUID>> getPartitionUuidList() {
            return this.partitionUuidList;
        }

        public Collection<Map.Entry<String, List<Map.Entry<Integer, Long>>>> getNamePartitionSequenceList() {
            return this.namePartitionSequenceList;
        }
    }
}

