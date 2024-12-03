/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.internal.nearcache.impl.invalidation.BatchNearCacheInvalidation;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.internal.nearcache.impl.invalidation.SingleNearCacheInvalidation;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.util.HashUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AbstractBaseNearCacheInvalidationListener {
    private final int batchOrderKey;

    public AbstractBaseNearCacheInvalidationListener(String localMemberUuid, long correlationId) {
        this.batchOrderKey = HashUtil.hashCode(localMemberUuid, correlationId);
    }

    protected abstract ClientMessage encodeBatchInvalidation(String var1, List<Data> var2, List<String> var3, List<UUID> var4, List<Long> var5);

    protected abstract ClientMessage encodeSingleInvalidation(String var1, Data var2, String var3, UUID var4, long var5);

    protected abstract void sendMessageWithOrderKey(ClientMessage var1, Object var2);

    protected abstract boolean canSendInvalidation(Invalidation var1);

    protected final void sendInvalidation(Invalidation invalidation) {
        if (invalidation instanceof BatchNearCacheInvalidation) {
            ExtractedParams params = this.extractParams((BatchNearCacheInvalidation)invalidation);
            ClientMessage message = this.encodeBatchInvalidation(invalidation.getName(), params.keys, params.sourceUuids, params.partitionUuids, params.sequences);
            this.sendMessageWithOrderKey(message, this.batchOrderKey);
            return;
        }
        if (invalidation instanceof SingleNearCacheInvalidation) {
            if (this.canSendInvalidation(invalidation)) {
                ClientMessage message = this.encodeSingleInvalidation(invalidation.getName(), invalidation.getKey(), invalidation.getSourceUuid(), invalidation.getPartitionUuid(), invalidation.getSequence());
                this.sendMessageWithOrderKey(message, invalidation.getKey());
            }
            return;
        }
        throw new IllegalArgumentException("Unknown invalidation message type " + invalidation);
    }

    private ExtractedParams extractParams(BatchNearCacheInvalidation batch) {
        List<Invalidation> invalidations = batch.getInvalidations();
        int size = invalidations.size();
        ArrayList<Data> keys = new ArrayList<Data>(size);
        ArrayList<String> sourceUuids = new ArrayList<String>(size);
        ArrayList<UUID> partitionUuids = new ArrayList<UUID>(size);
        ArrayList<Long> sequences = new ArrayList<Long>(size);
        for (Invalidation invalidation : invalidations) {
            if (!this.canSendInvalidation(invalidation)) continue;
            keys.add(invalidation.getKey());
            sourceUuids.add(invalidation.getSourceUuid());
            partitionUuids.add(invalidation.getPartitionUuid());
            sequences.add(invalidation.getSequence());
        }
        return new ExtractedParams(keys, sourceUuids, partitionUuids, sequences);
    }

    private static final class ExtractedParams {
        private final List<Data> keys;
        private final List<String> sourceUuids;
        private final List<UUID> partitionUuids;
        private final List<Long> sequences;

        ExtractedParams(List<Data> keys, List<String> sourceUuids, List<UUID> partitionUuids, List<Long> sequences) {
            this.keys = keys;
            this.sourceUuids = sourceUuids;
            this.partitionUuids = partitionUuids;
            this.sequences = sequences;
        }
    }
}

