/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.dynamicconfig;

import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.cluster.impl.ClusterTopologyChangedException;
import com.hazelcast.internal.dynamicconfig.AbstractDynamicConfigOperation;
import com.hazelcast.internal.dynamicconfig.ClusterWideConfigurationService;
import com.hazelcast.internal.dynamicconfig.ConfigCheckMode;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.ExceptionAction;
import java.io.IOException;

public class AddDynamicConfigOperation
extends AbstractDynamicConfigOperation {
    private IdentifiedDataSerializable config;
    private int memberListVersion;

    public AddDynamicConfigOperation() {
    }

    public AddDynamicConfigOperation(IdentifiedDataSerializable config, int memberListVersion) {
        this.config = config;
        this.memberListVersion = memberListVersion;
    }

    @Override
    public void run() throws Exception {
        int currentMemberListVersion;
        ClusterWideConfigurationService service = (ClusterWideConfigurationService)this.getService();
        service.registerConfigLocally(this.config, ConfigCheckMode.THROW_EXCEPTION);
        ClusterService clusterService = this.getNodeEngine().getClusterService();
        if (clusterService.isMaster() && (currentMemberListVersion = clusterService.getMemberListVersion()) != this.memberListVersion) {
            throw new ClusterTopologyChangedException(String.format("Current member list version %d does not match expected %d", currentMemberListVersion, this.memberListVersion));
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeObject(this.config);
        out.writeInt(this.memberListVersion);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.config = (IdentifiedDataSerializable)in.readObject();
        this.memberListVersion = in.readInt();
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public ExceptionAction onInvocationException(Throwable throwable) {
        return throwable instanceof ClusterTopologyChangedException ? ExceptionAction.THROW_EXCEPTION : super.onInvocationException(throwable);
    }
}

