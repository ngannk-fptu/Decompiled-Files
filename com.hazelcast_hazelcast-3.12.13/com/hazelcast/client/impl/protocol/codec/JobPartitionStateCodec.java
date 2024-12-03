/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.protocol.codec;

import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.client.impl.protocol.codec.AddressCodec;
import com.hazelcast.client.impl.protocol.util.ParameterUtil;
import com.hazelcast.mapreduce.JobPartitionState;
import com.hazelcast.mapreduce.impl.task.JobPartitionStateImpl;
import com.hazelcast.nio.Address;

public final class JobPartitionStateCodec {
    private JobPartitionStateCodec() {
    }

    public static JobPartitionState decode(ClientMessage clientMessage) {
        Address address = AddressCodec.decode(clientMessage);
        String state = clientMessage.getStringUtf8();
        return new JobPartitionStateImpl(address, JobPartitionState.State.valueOf(state));
    }

    public static void encode(JobPartitionState jobPartitionState, ClientMessage clientMessage) {
        AddressCodec.encode(jobPartitionState.getOwner(), clientMessage);
        clientMessage.set(jobPartitionState.getState().name());
    }

    public static int calculateDataSize(JobPartitionState jobPartitionState) {
        int dataSize = AddressCodec.calculateDataSize(jobPartitionState.getOwner());
        return dataSize += ParameterUtil.calculateDataSize(jobPartitionState.getState().name());
    }
}

