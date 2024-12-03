/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.spi.impl.eventservice.impl.operations;

import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.cluster.impl.ClusterTopologyChangedException;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.impl.AllowedDuringPassiveState;
import com.hazelcast.spi.impl.SpiDataSerializerHook;
import java.io.IOException;

abstract class AbstractRegistrationOperation
extends Operation
implements AllowedDuringPassiveState,
IdentifiedDataSerializable,
Versioned {
    private int memberListVersion = -1;

    AbstractRegistrationOperation() {
    }

    AbstractRegistrationOperation(int memberListVersion) {
        this.memberListVersion = memberListVersion;
    }

    @Override
    public final void run() throws Exception {
        this.runInternal();
        this.checkMemberListVersion();
    }

    protected abstract void runInternal() throws Exception;

    private void checkMemberListVersion() {
        int currentMemberListVersion;
        ClusterService clusterService = this.getNodeEngine().getClusterService();
        if (clusterService.isMaster() && (currentMemberListVersion = clusterService.getMemberListVersion()) != this.memberListVersion) {
            throw new ClusterTopologyChangedException(String.format("Current member list version %d does not match expected %d", currentMemberListVersion, this.memberListVersion));
        }
    }

    @Override
    protected final void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.memberListVersion);
        this.writeInternalImpl(out);
    }

    protected abstract void writeInternalImpl(ObjectDataOutput var1) throws IOException;

    @Override
    protected final void readInternal(ObjectDataInput in) throws IOException {
        this.memberListVersion = in.readInt();
        this.readInternalImpl(in);
    }

    protected abstract void readInternalImpl(ObjectDataInput var1) throws IOException;

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        return throwable instanceof ClusterTopologyChangedException ? ExceptionAction.THROW_EXCEPTION : super.onInvocationException(throwable);
    }

    @Override
    public int getFactoryId() {
        return SpiDataSerializerHook.F_ID;
    }
}

