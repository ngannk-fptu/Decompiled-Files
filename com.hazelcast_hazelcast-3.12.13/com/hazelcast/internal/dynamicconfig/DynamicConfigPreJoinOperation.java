/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.dynamicconfig;

import com.hazelcast.internal.dynamicconfig.AbstractDynamicConfigOperation;
import com.hazelcast.internal.dynamicconfig.ClusterWideConfigurationService;
import com.hazelcast.internal.dynamicconfig.ConfigCheckMode;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

public class DynamicConfigPreJoinOperation
extends AbstractDynamicConfigOperation {
    private IdentifiedDataSerializable[] configs;
    private ConfigCheckMode configCheckMode;

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"})
    public DynamicConfigPreJoinOperation(IdentifiedDataSerializable[] configs, ConfigCheckMode configCheckMode) {
        this.configs = configs;
        this.configCheckMode = configCheckMode;
    }

    public DynamicConfigPreJoinOperation() {
    }

    @Override
    public void run() throws Exception {
        ClusterWideConfigurationService service = (ClusterWideConfigurationService)this.getService();
        for (IdentifiedDataSerializable config : this.configs) {
            service.registerConfigLocally(config, this.configCheckMode);
        }
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        out.writeInt(this.configs.length);
        for (IdentifiedDataSerializable config : this.configs) {
            out.writeObject(config);
        }
        out.writeUTF(this.configCheckMode.name());
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        int size = in.readInt();
        this.configs = new IdentifiedDataSerializable[size];
        for (int i = 0; i < size; ++i) {
            this.configs[i] = (IdentifiedDataSerializable)in.readObject();
        }
        this.configCheckMode = ConfigCheckMode.valueOf(in.readUTF());
    }

    @Override
    public int getId() {
        return 6;
    }
}

