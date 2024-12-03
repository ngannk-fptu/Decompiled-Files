/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.operation;

import com.hazelcast.config.WanReplicationConfig;
import com.hazelcast.internal.management.operation.AbstractManagementOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;

public class AddWanConfigLegacyOperation
extends AbstractManagementOperation
implements Versioned {
    private WanReplicationConfig wanReplicationConfig;

    public AddWanConfigLegacyOperation() {
    }

    public AddWanConfigLegacyOperation(WanReplicationConfig wanReplicationConfig) {
        this.wanReplicationConfig = wanReplicationConfig;
    }

    @Override
    public void run() throws Exception {
        this.getNodeEngine().getWanReplicationService().addWanReplicationConfigLocally(this.wanReplicationConfig);
        this.getLogger().info("Appended WAN config with name " + this.wanReplicationConfig.getName());
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        this.wanReplicationConfig.writeData(out);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.wanReplicationConfig = new WanReplicationConfig();
        this.wanReplicationConfig.readData(in);
    }

    @Override
    public String getServiceName() {
        return "hz:core:wanReplicationService";
    }

    @Override
    public int getId() {
        return 4;
    }
}

